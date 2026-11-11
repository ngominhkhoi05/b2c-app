package com.example.b2capp.core.network

import android.util.Log
import com.example.b2capp.data.local.TokenManager
import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.dto.ChatMessageDto
import com.example.b2capp.data.remote.dto.RefreshTokenRequestDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min
import kotlin.math.pow

data class StompTopicMessage(
    val destination: String,
    val body: String
)

data class RealtimeOrderEvent(
    val type: String,
    val orderId: Long,
    val status: String,
    val reason: String? = null
)

data class RealtimeChatEvent(
    val type: String,
    val conversationId: Long,
    val sellerId: Long? = null,
    val shopName: String? = null,
    val shopAvatar: String? = null,
    val buyerUnreadCount: Int? = null,
    val sellerUnreadCount: Int? = null,
    val readerRole: String? = null,
    val message: ChatMessageDto? = null
)

/**
 * Quản lý kết nối STOMP 1.2 over WebSocket (/ws) cho ứng dụng Mobile:
 * - Chỉ kết nối khi người dùng đã đăng nhập hợp lệ.
 * - Trước khi mở kết nối hoặc khi tự động reconnect: kiểm tra hạn JWT Access Token.
 *   + Nếu Access Token đã hết hạn (hoặc sắp hết hạn trong 30s), tự động gọi API Refresh Token.
 *   + Nếu Refresh Token cũng hết hạn/không hợp lệ, gọi clearAuth() để đăng xuất và dừng hẳn WebSocket.
 * - Hỗ trợ Heartbeat 10s/10s khớp với WebSocketConfig trên Spring Boot Backend.
 * - Hỗ trợ Auto-Reconnect với Exponential Backoff khi rớt mạng.
 */
object StompWebSocketManager {

    private const val TAG = "StompWebSocket"
    private const val HEARTBEAT_INTERVAL_MS = 10_000L
    private const val BASE_RECONNECT_DELAY_MS = 1_500L
    private const val MAX_RECONNECT_DELAY_MS = 30_000L

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val wsOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.MILLISECONDS) // WebSocket giữ kết nối liên tục
            .writeTimeout(15, TimeUnit.SECONDS)
            .pingInterval(20, TimeUnit.SECONDS)
            .build()
    }

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _orderEvents = MutableSharedFlow<RealtimeOrderEvent>(extraBufferCapacity = 32)
    val orderEvents: SharedFlow<RealtimeOrderEvent> = _orderEvents.asSharedFlow()

    private val _chatEvents = MutableSharedFlow<RealtimeChatEvent>(extraBufferCapacity = 32)
    val chatEvents: SharedFlow<RealtimeChatEvent> = _chatEvents.asSharedFlow()

    private val _rawMessages = MutableSharedFlow<StompTopicMessage>(extraBufferCapacity = 64)
    val rawMessages: SharedFlow<StompTopicMessage> = _rawMessages.asSharedFlow()

    // Map lưu destination -> subscriptionId (ví dụ: "/topic/orders/5" -> "sub-1")
    private val activeSubscriptions = ConcurrentHashMap<String, String>()
    private val subIdCounter = AtomicInteger(1)

    @Volatile
    private var webSocket: WebSocket? = null

    @Volatile
    private var isConnecting = false

    @Volatile
    private var isExplicitlyClosed = false

    @Volatile
    private var connectedToken: String? = null

    private var heartbeatJob: Job? = null
    private var reconnectJob: Job? = null
    private var reconnectAttempt = 0

    init {
        // Tự động kết nối lại nếu Base URL thay đổi tại runtime
        NetworkConfig.addOnBaseUrlChangeListener {
            if (!isExplicitlyClosed && isUserLoggedInSafe()) {
                reconnectWithLatestConfig()
            }
        }
    }

    /**
     * Khởi tạo kết nối STOMP cho người dùng đã đăng nhập và đăng ký các kênh mặc định của userId
     */
    fun connectForUser(userId: Long) {
        subscribe("/topic/orders/$userId")
        subscribe("/topic/chat/user/$userId")
        isExplicitlyClosed = false
        connectIfNeeded()
    }

    /**
     * Đăng ký lắng nghe thêm một STOMP destination (ví dụ: "/topic/order/{orderId}")
     */
    fun subscribe(destination: String) {
        val subId = activeSubscriptions.getOrPut(destination) {
            "sub-${subIdCounter.getAndIncrement()}"
        }
        if (_isConnected.value) {
            sendSubscribeFrame(destination, subId)
        }
    }

    /**
     * Hủy lắng nghe một STOMP destination
     */
    fun unsubscribe(destination: String) {
        val subId = activeSubscriptions.remove(destination) ?: return
        if (_isConnected.value) {
            val frame = buildString {
                append("UNSUBSCRIBE\n")
                append("id:$subId\n")
                append("\n")
                append("\u0000")
            }
            try {
                webSocket?.send(frame)
            } catch (_: Exception) {}
        }
    }

    /**
     * Xử lý khi Access Token thay đổi từ TokenManager
     */
    fun onAccessTokenChanged(newToken: String?) {
        if (newToken.isNullOrBlank()) {
            // Người dùng đã đăng xuất hoặc phiên hết hạn hoàn toàn
            disconnect()
            return
        }
        if (connectedToken != null && connectedToken != newToken && _isConnected.value) {
            // Token vừa được xoay vòng (Token Rotation) -> kết nối lại với token mới
            reconnectWithLatestConfig()
        } else if (!_isConnected.value && !isConnecting && !isExplicitlyClosed) {
            connectIfNeeded()
        }
    }

    /**
     * Ngắt kết nối WebSocket hoàn toàn (gọi khi Logout hoặc phiên đăng nhập hết hạn)
     */
    @Synchronized
    fun disconnect(clearSubscriptions: Boolean = true) {
        isExplicitlyClosed = true
        isConnecting = false
        reconnectAttempt = 0
        reconnectJob?.cancel()
        reconnectJob = null
        heartbeatJob?.cancel()
        heartbeatJob = null
        connectedToken = null

        if (clearSubscriptions) {
            activeSubscriptions.clear()
        }

        val currentWs = webSocket
        webSocket = null
        if (currentWs != null) {
            try {
                if (_isConnected.value) {
                    currentWs.send("DISCONNECT\n\n\u0000")
                }
                currentWs.close(1000, "User logged out or session ended")
            } catch (_: Exception) {}
        }
        _isConnected.value = false
    }

    private fun reconnectWithLatestConfig() {
        scope.launch {
            closeCurrentSocketOnly()
            isExplicitlyClosed = false
            reconnectAttempt = 0
            connectInternal()
        }
    }

    private fun connectIfNeeded() {
        if (_isConnected.value || isConnecting || isExplicitlyClosed) return
        scope.launch {
            connectInternal()
        }
    }

    private suspend fun connectInternal() {
        if (_isConnected.value || isConnecting || isExplicitlyClosed) return

        val tokenManager = try {
            TokenManager.getInstance()
        } catch (_: Exception) {
            return
        }

        // 1. Nếu người dùng chưa đăng nhập -> tuyệt đối không kết nối WebSocket
        if (!tokenManager.isLoggedIn()) {
            disconnect()
            return
        }

        isConnecting = true

        // 2. Kiểm tra tính hợp lệ và hạn của Access Token; nếu hết hạn thì chủ động Refresh Token
        val validToken = ensureValidAccessToken(tokenManager)
        if (validToken.isNullOrBlank()) {
            isConnecting = false
            return
        }

        // Đảm bảo luôn có subscription theo userId hiện tại
        tokenManager.getUserId()?.let { uid ->
            activeSubscriptions.putIfAbsent("/topic/orders/$uid", "sub-${subIdCounter.getAndIncrement()}")
            activeSubscriptions.putIfAbsent("/topic/chat/user/$uid", "sub-${subIdCounter.getAndIncrement()}")
        }

        val wsUrl = buildWebSocketUrl(validToken)
        val request = try {
            Request.Builder()
                .url(wsUrl)
                .header("Authorization", "Bearer $validToken")
                .build()
        } catch (e: Exception) {
            Log.e(TAG, "URL WebSocket không hợp lệ: $wsUrl", e)
            isConnecting = false
            return
        }

        connectedToken = validToken
        try {
            webSocket = wsOkHttpClient.newWebSocket(request, createWebSocketListener(validToken))
        } catch (e: Exception) {
            isConnecting = false
            scheduleReconnect()
        }
    }

    /**
     * Đảm bảo Access Token còn hạn trước khi mở kết nối WebSocket.
     * Nếu Access Token đã hết hạn (hoặc còn dưới 30s), tự động gọi API refresh-token.
     * Nếu Refresh Token cũng hết hạn/lỗi 401 -> xóa auth và dừng kết nối.
     */
    private fun ensureValidAccessToken(tokenManager: TokenManager): String? {
        val currentToken = tokenManager.getAccessToken()
        if (currentToken.isNullOrBlank()) {
            return null
        }

        // Nếu token vẫn còn hạn (trừ hao 30 giây) -> dùng luôn
        if (!tokenManager.isTokenExpired(currentToken, leewaySeconds = 30L)) {
            return currentToken
        }

        // Token đã hết hạn -> kiểm tra Refresh Token
        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            Log.w(TAG, "Access token hết hạn và không có Refresh Token -> xóa phiên")
            tokenManager.clearAuth()
            disconnect()
            return null
        }

        return synchronized(this) {
            // Kiểm tra lại xem thread khác (như TokenAuthenticator) đã refresh chưa
            val latestToken = tokenManager.getAccessToken()
            if (!latestToken.isNullOrBlank() &&
                latestToken != currentToken &&
                !tokenManager.isTokenExpired(latestToken, leewaySeconds = 30L)
            ) {
                return@synchronized latestToken
            }

            try {
                Log.d(TAG, "Access token hết hạn, đang gọi refresh-token trước khi kết nối WebSocket...")
                val response = ApiClient.unauthenticatedAuthApiService
                    .refreshTokenSync(RefreshTokenRequestDto(refreshToken))
                    .execute()

                if (response.isSuccessful) {
                    val authData = response.body()?.data
                    if (authData != null && authData.accessToken.isNotBlank()) {
                        tokenManager.updateTokens(
                            accessToken = authData.accessToken,
                            refreshToken = authData.refreshToken
                        )
                        Log.i(TAG, "Refresh token cho WebSocket thành công!")
                        return@synchronized authData.accessToken
                    }
                }

                if (response.code() == 401 || response.code() == 403) {
                    Log.w(TAG, "Refresh token hết hạn (${response.code()}), đăng xuất người dùng.")
                    tokenManager.clearAuth()
                    disconnect()
                }
                null
            } catch (e: Exception) {
                Log.e(TAG, "Không thể refresh token do lỗi mạng: ${e.message}")
                null
            }
        }
    }

    private fun createWebSocketListener(token: String) = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "WebSocket opened, gửi STOMP CONNECT frame...")
            val connectFrame = buildString {
                append("CONNECT\n")
                append("accept-version:1.1,1.2\n")
                append("heart-beat:10000,10000\n")
                append("Authorization:Bearer $token\n")
                append("\n")
                append("\u0000")
            }
            webSocket.send(connectFrame)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            if (text == "\n" || text.isBlank()) {
                // Server heartbeat
                return
            }
            val frames = text.split("\u0000")
            for (rawFrame in frames) {
                val trimmed = rawFrame.trimStart('\n', '\r')
                if (trimmed.isBlank()) continue
                handleStompFrame(trimmed)
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(code, reason)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket closed: code=$code, reason=$reason")
            isConnecting = false
            _isConnected.value = false
            heartbeatJob?.cancel()
            if (!isExplicitlyClosed && isUserLoggedInSafe()) {
                scheduleReconnect()
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.w(TAG, "WebSocket failure: ${t.message}")
            isConnecting = false
            _isConnected.value = false
            heartbeatJob?.cancel()
            if (!isExplicitlyClosed && isUserLoggedInSafe()) {
                scheduleReconnect()
            }
        }
    }

    private fun handleStompFrame(frameText: String) {
        val normalized = frameText.replace("\r\n", "\n")
        val headerEndIndex = normalized.indexOf("\n\n")
        val headerPart = if (headerEndIndex >= 0) normalized.substring(0, headerEndIndex) else normalized
        val bodyPart = if (headerEndIndex >= 0) normalized.substring(headerEndIndex + 2) else ""

        val lines = headerPart.split("\n")
        if (lines.isEmpty()) return

        val command = lines[0].trim()
        val headers = mutableMapOf<String, String>()
        for (i in 1 until lines.size) {
            val line = lines[i]
            val colonIdx = line.indexOf(':')
            if (colonIdx > 0) {
                val key = line.substring(0, colonIdx).trim()
                val value = line.substring(colonIdx + 1).trim()
                headers.putIfAbsent(key, value)
            }
        }

        when (command) {
            "CONNECTED" -> {
                Log.i(TAG, "STOMP CONNECTED thành công!")
                isConnecting = false
                _isConnected.value = true
                reconnectAttempt = 0
                startHeartbeat()
                resubscribeAll()
            }

            "MESSAGE" -> {
                val destination = headers["destination"].orEmpty()
                if (destination.isNotBlank() && bodyPart.isNotBlank()) {
                    _rawMessages.tryEmit(StompTopicMessage(destination, bodyPart))
                    dispatchParsedEvent(destination, bodyPart)
                }
            }

            "ERROR" -> {
                val errMsg = headers["message"] ?: bodyPart
                Log.w(TAG, "STOMP ERROR frame: $errMsg")
                isConnecting = false
                _isConnected.value = false
                closeCurrentSocketOnly()
                if (!isExplicitlyClosed && isUserLoggedInSafe()) {
                    scheduleReconnect()
                }
            }
        }
    }

    private fun dispatchParsedEvent(destination: String, body: String) {
        try {
            if (destination.startsWith("/topic/orders/") || destination.startsWith("/topic/order/")) {
                parseOrderEvent(body)?.let { _orderEvents.tryEmit(it) }
            } else if (destination.startsWith("/topic/chat/")) {
                parseChatEvent(body)?.let { _chatEvents.tryEmit(it) }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Lỗi parse STOMP message tại $destination: ${e.message}")
        }
    }

    internal fun parseOrderEvent(jsonStr: String): RealtimeOrderEvent? {
        return try {
            val root = JSONObject(jsonStr)
            val type = root.optString("type", "ORDER_STATUS_UPDATED")
            val payload = root.optJSONObject("payload") ?: root
            val orderId = payload.optLong("orderId", -1L)
            val status = payload.optString("status", "")
            val reason = payload.optString("reason", "").takeIf { it.isNotBlank() }
            if (orderId > 0 && status.isNotBlank()) {
                RealtimeOrderEvent(
                    type = type,
                    orderId = orderId,
                    status = status,
                    reason = reason
                )
            } else null
        } catch (_: Exception) {
            null
        }
    }

    internal fun parseChatEvent(jsonStr: String): RealtimeChatEvent? {
        return try {
            val root = JSONObject(jsonStr)
            val type = root.optString("type", "NEW_MESSAGE")
            val convId = root.optLong("conversationId", -1L)
            if (convId <= 0) return null

            val sellerId = if (root.has("sellerId") && !root.isNull("sellerId")) root.optLong("sellerId") else null
            val shopName = root.optString("shopName", "").takeIf { it.isNotBlank() }
            val shopAvatar = root.optString("shopAvatar", "").takeIf { it.isNotBlank() }
            val buyerUnread = if (root.has("buyerUnreadCount") && !root.isNull("buyerUnreadCount")) root.optInt("buyerUnreadCount") else null
            val sellerUnread = if (root.has("sellerUnreadCount") && !root.isNull("sellerUnreadCount")) root.optInt("sellerUnreadCount") else null
            val readerRole = root.optString("readerRole", "").takeIf { it.isNotBlank() }

            val msgObj = root.optJSONObject("message")
            val messageDto = if (msgObj != null) {
                ChatMessageDto(
                    id = msgObj.optLong("id", System.currentTimeMillis()),
                    conversationId = msgObj.optLong("conversationId", convId),
                    senderId = if (msgObj.has("senderId") && !msgObj.isNull("senderId")) msgObj.optLong("senderId") else null,
                    senderType = msgObj.optString("senderType", "SELLER"),
                    senderName = msgObj.optString("senderName", "").takeIf { it.isNotBlank() },
                    content = msgObj.optString("content", ""),
                    messageType = msgObj.optString("messageType", "TEXT"),
                    attachmentUrl = msgObj.optString("attachmentUrl", "").takeIf { it.isNotBlank() },
                    refProductId = if (msgObj.has("refProductId") && !msgObj.isNull("refProductId")) msgObj.optLong("refProductId") else null,
                    refOrderId = if (msgObj.has("refOrderId") && !msgObj.isNull("refOrderId")) msgObj.optLong("refOrderId") else null,
                    isRead = msgObj.optBoolean("isRead", false),
                    readAt = msgObj.optString("readAt", "").takeIf { it.isNotBlank() },
                    createdAt = msgObj.optString("createdAt", "").takeIf { it.isNotBlank() }
                )
            } else null

            RealtimeChatEvent(
                type = type,
                conversationId = convId,
                sellerId = sellerId,
                shopName = shopName,
                shopAvatar = shopAvatar,
                buyerUnreadCount = buyerUnread,
                sellerUnreadCount = sellerUnread,
                readerRole = readerRole,
                message = messageDto
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun resubscribeAll() {
        for ((destination, subId) in activeSubscriptions) {
            sendSubscribeFrame(destination, subId)
        }
    }

    private fun sendSubscribeFrame(destination: String, subId: String) {
        val frame = buildString {
            append("SUBSCRIBE\n")
            append("id:$subId\n")
            append("destination:$destination\n")
            append("\n")
            append("\u0000")
        }
        try {
            webSocket?.send(frame)
        } catch (_: Exception) {}
    }

    private fun startHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch {
            while (isActive && _isConnected.value) {
                delay(HEARTBEAT_INTERVAL_MS)
                if (_isConnected.value) {
                    try {
                        webSocket?.send("\n")
                    } catch (_: Exception) {}
                }
            }
        }
    }

    private fun scheduleReconnect() {
        if (isExplicitlyClosed || !isUserLoggedInSafe()) return
        if (reconnectJob?.isActive == true) return

        val expDelay = (BASE_RECONNECT_DELAY_MS * 2.0.pow(reconnectAttempt.toDouble())).toLong()
        val delayMs = min(expDelay, MAX_RECONNECT_DELAY_MS)
        reconnectAttempt++

        reconnectJob = scope.launch {
            delay(delayMs)
            if (!isExplicitlyClosed && isUserLoggedInSafe()) {
                connectInternal()
            }
        }
    }

    private fun closeCurrentSocketOnly() {
        heartbeatJob?.cancel()
        heartbeatJob = null
        val currentWs = webSocket
        webSocket = null
        isConnecting = false
        _isConnected.value = false
        try {
            currentWs?.close(1000, "Reconnecting")
        } catch (_: Exception) {}
    }

    private fun buildWebSocketUrl(token: String): String {
        val baseUrl = NetworkConfig.getBaseUrl().trimEnd('/')
        val wsBase = when {
            baseUrl.startsWith("https://", ignoreCase = true) ->
                "wss://" + baseUrl.substring("https://".length)
            baseUrl.startsWith("http://", ignoreCase = true) ->
                "ws://" + baseUrl.substring("http://".length)
            else -> "ws://$baseUrl"
        }
        val encodedToken = try {
            URLEncoder.encode(token, "UTF-8")
        } catch (_: Exception) {
            token
        }
        return "$wsBase/ws?token=$encodedToken"
    }

    private fun isUserLoggedInSafe(): Boolean {
        return try {
            TokenManager.getInstance().isLoggedIn()
        } catch (_: Exception) {
            false
        }
    }
}

