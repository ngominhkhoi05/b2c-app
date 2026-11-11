package com.example.b2capp.data.repository

import androidx.compose.ui.graphics.Color
import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.api.ChatApiService
import com.example.b2capp.data.remote.dto.ChatMessageDto
import com.example.b2capp.data.remote.dto.ConversationDto
import com.example.b2capp.data.remote.dto.SendMessageRequestDto
import com.example.b2capp.features.chat.CheckStatus
import com.example.b2capp.features.chat.DetailedChatMessage
import com.example.b2capp.features.chat.PrefixIconType
import com.example.b2capp.features.chat.ShopConversation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Repository phụ trách quản lý tin nhắn và danh sách hội thoại với các Shop:
 * - Gọi Backend API để lấy danh sách cuộc trò chuyện (/buyer/conversations hoặc /conversations)
 * - Lấy chi tiết tin nhắn trong một cuộc hội thoại
 * - Gửi tin nhắn mới tới Shop
 * - Chuyển đổi dữ liệu từ API DTO sang UI Model (ShopConversation)
 * - Cung cấp dữ liệu mẫu mặc định khi ngoại tuyến / chưa đăng nhập
 */
class ChatRepository(
    private val apiServiceHolder: () -> ChatApiService = { ApiClient.chatApiService }
) {

    /**
     * Tải danh sách cuộc trò chuyện với các shop của người dùng hiện tại
     * Thử gọi /api/v1/buyer/conversations, nếu không khớp role sẽ gọi /api/v1/conversations
     */
    suspend fun getConversations(page: Int = 0, size: Int = 20): Result<List<ConversationDto>> = withContext(Dispatchers.IO) {
        try {
            // Thử endpoint buyer trước
            var response = apiServiceHolder().getBuyerConversations(page, size)
            if (!response.isSuccessful && response.code() == 403) {
                // Thử endpoint chung
                response = apiServiceHolder().getConversations(page, size)
            }

            if (response.isSuccessful) {
                val list = response.body()?.data?.content ?: emptyList()
                Result.success(list)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lấy danh sách tin nhắn của một cuộc trò chuyện
     */
    suspend fun getMessages(conversationId: Long, page: Int = 0, size: Int = 50): Result<List<ChatMessageDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().getMessages(conversationId, page, size)
            if (response.isSuccessful) {
                val list = response.body()?.data?.content ?: emptyList()
                Result.success(list)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Đánh dấu cuộc hội thoại đã đọc trên server
     */
    suspend fun markConversationAsRead(conversationId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().markAsRead(conversationId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gửi tin nhắn mới tới Shop
     */
    suspend fun sendMessage(
        conversationId: Long? = null,
        sellerId: Long? = null,
        content: String,
        messageType: String = "TEXT",
        attachmentUrl: String? = null,
        refProductId: Long? = null,
        refOrderId: Long? = null
    ): Result<ChatMessageDto> = withContext(Dispatchers.IO) {
        try {
            val request = SendMessageRequestDto(
                conversationId = conversationId,
                sellerId = sellerId,
                content = content,
                messageType = messageType,
                attachmentUrl = attachmentUrl,
                refProductId = refProductId,
                refOrderId = refOrderId
            )
            val response = if (conversationId != null && conversationId > 0) {
                apiServiceHolder().sendMessage(conversationId, request)
            } else {
                apiServiceHolder().sendMessageDirect(request)
            }
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Khởi tạo hoặc tìm cuộc hội thoại với Seller
     */
    suspend fun startConversation(sellerId: Long): Result<ConversationDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().startConversation(sellerId)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Chuyển đổi DTO tin nhắn từ Backend sang UI Model DetailedChatMessage
     */
    fun mapDtoToDetailedChatMessage(
        dto: ChatMessageDto,
        currentUserId: Long?,
        shopName: String,
        avatarLetter: String? = null,
        avatarBg: Color? = null
    ): DetailedChatMessage {
        val isFromUser = dto.senderType == "BUYER" || (currentUserId != null && dto.senderId == currentUserId && dto.senderType != "SELLER")
        val formattedTime = formatTime(dto.createdAt)
        val resolvedSenderName = if (!isFromUser) {
            dto.senderName?.takeIf { it.isNotBlank() } ?: shopName
        } else {
            "Tôi"
        }

        return DetailedChatMessage(
            id = "msg_${dto.id}",
            senderName = resolvedSenderName,
            text = dto.content,
            time = formattedTime,
            isFromUser = isFromUser,
            senderAvatarRes = null,
            avatarLetter = if (!isFromUser) (avatarLetter ?: extractInitials(shopName)) else null,
            avatarBg = if (!isFromUser) (avatarBg ?: generateAvatarColor(shopName)) else null,
            attachedProduct = null,
            attachmentUrl = dto.attachmentUrl,
            isVoucher = false,
            voucherValue = null
        )
    }

    /**
     * Chuyển đổi từ DTO Backend sang UI Model hiển thị trên ShopMessagesScreen
     */
    fun mapDtoToShopConversation(dto: ConversationDto): ShopConversation {
        val name = dto.otherPartyName?.takeIf { it.isNotBlank() } ?: "Shop #${dto.otherPartyId ?: dto.id}"
        val initialLetter = extractInitials(name)
        val bgColor = generateAvatarColor(name)
        val formattedTime = formatTime(dto.lastMessageAt ?: dto.createdAt)
        val unread = dto.unreadCount ?: 0
        val isMall = name.contains("Official", ignoreCase = true) || name.contains("Mall", ignoreCase = true)

        val prefixType = when {
            dto.lastMessage?.contains("[Hình ảnh]", ignoreCase = true) == true -> PrefixIconType.IMAGE
            dto.lastMessage?.contains("voucher", ignoreCase = true) == true -> PrefixIconType.VOUCHER
            else -> PrefixIconType.NONE
        }

        return ShopConversation(
            id = "conv_${dto.id}",
            numericId = dto.id,
            sellerId = dto.otherPartyId,
            shopName = name,
            avatarLetter = initialLetter,
            avatarBg = bgColor,
            avatarUrl = dto.otherPartyAvatar,
            isOnline = true,
            tagText = if (isMall) "Mall" else null,
            tagBg = Color(0xFFB82414),
            time = formattedTime,
            isTimeRed = unread > 0,
            lastMessage = dto.lastMessage?.trim().orEmpty(),
            isLastMessageBold = unread > 0,
            prefixIconType = prefixType,
            unreadCount = unread,
            checkStatus = if (unread > 0) CheckStatus.NONE else CheckStatus.DOUBLE_CHECK_READ
        )
    }

    /**
     * Trích xuất chữ cái đại diện ngắn gọn cho avatar Shop
     */
    fun extractInitials(shopName: String): String {
        val clean = shopName.replace("Official", "", ignoreCase = true)
            .replace("Store", "", ignoreCase = true)
            .replace("Flagship", "", ignoreCase = true)
            .trim()
        val words = clean.split(" ").filter { it.isNotBlank() }
        return when {
            words.isEmpty() -> "S"
            words.size == 1 -> words[0].take(4)
            else -> (words.first().take(1) + words.last().take(1)).uppercase()
        }
    }

    /**
     * Sinh màu nền avatar hài hòa từ tên shop
     */
    fun generateAvatarColor(shopName: String): Color {
        val colors = listOf(
            Color(0xFFF0FDF4), // Xanh nhạt
            Color(0xFFEFF6FF), // Xanh dương phấn
            Color(0xFFFFFBEB), // Vàng kem
            Color(0xFFF3F4F6), // Xám thanh lịch
            Color(0xFFFDF2F8), // Hồng phấn
            Color(0xFFECFDF5)  // Mint mát mẻ
        )
        val hash = kotlin.math.abs(shopName.hashCode())
        return colors[hash % colors.size]
    }

    /**
     * Định dạng thời gian thân thiện (Vừa xong, 10:30, Hôm qua, 15 Th10...)
     */
    fun formatTime(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "Vừa xong"
        return try {
            val parsed = LocalDateTime.parse(isoString)
            val now = LocalDateTime.now()
            val minutesDiff = ChronoUnit.MINUTES.between(parsed, now)
            val hoursDiff = ChronoUnit.HOURS.between(parsed, now)
            val daysDiff = ChronoUnit.DAYS.between(parsed, now)

            when {
                minutesDiff < 5 -> "Vừa xong"
                hoursDiff < 24 && parsed.dayOfMonth == now.dayOfMonth -> parsed.format(DateTimeFormatter.ofPattern("HH:mm"))
                daysDiff == 1L -> "Hôm qua"
                daysDiff < 7 -> parsed.format(DateTimeFormatter.ofPattern("dd 'Th'MM"))
                else -> parsed.format(DateTimeFormatter.ofPattern("dd/MM"))
            }
        } catch (e: Exception) {
            if (isoString.length >= 16) {
                // Cố lấy "HH:mm" từ chuỗi "yyyy-MM-ddTHH:mm:ss"
                isoString.substring(11, 16)
            } else {
                "Vừa xong"
            }
        }
    }

    companion object {
        /**
         * Danh sách hội thoại mẫu - đã xóa bỏ để sử dụng dữ liệu thực từ Backend
         */
        fun getDefaultConversations(): List<ShopConversation> {
            return emptyList()
        }

        /**
         * Danh sách tin nhắn mặc định theo từng Shop - đã xóa bỏ để sử dụng dữ liệu thực từ Backend
         */
        fun getDefaultMessagesForShop(
            shopName: String,
            avatarLetter: String? = null,
            avatarBg: Color? = null
        ): List<DetailedChatMessage> {
            return emptyList()
        }
    }
}

