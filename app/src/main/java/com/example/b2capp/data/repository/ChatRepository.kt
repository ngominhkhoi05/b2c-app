package com.example.b2capp.data.repository

import androidx.compose.ui.graphics.Color
import com.example.R
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
     * Gửi tin nhắn mới tới Shop
     */
    suspend fun sendMessage(
        conversationId: Long? = null,
        sellerId: Long? = null,
        content: String
    ): Result<ChatMessageDto> = withContext(Dispatchers.IO) {
        try {
            val targetId = conversationId ?: 0L
            val request = SendMessageRequestDto(
                conversationId = conversationId,
                sellerId = sellerId,
                content = content
            )
            val response = apiServiceHolder().sendMessage(targetId, request)
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
        val isFromUser = dto.senderType == "BUYER" || (currentUserId != null && dto.senderId == currentUserId)
        val formattedTime = formatTime(dto.createdAt)
        val isCoolmate = shopName.contains("Coolmate", ignoreCase = true)

        return DetailedChatMessage(
            id = "msg_${dto.id}",
            text = dto.content,
            time = formattedTime,
            isFromUser = isFromUser,
            senderAvatarRes = if (!isFromUser && isCoolmate) R.drawable.coolmate_shop_avatar_1793960155632 else null,
            avatarLetter = if (!isFromUser) avatarLetter else null,
            avatarBg = if (!isFromUser) avatarBg else null
        )
    }

    /**
     * Chuyển đổi từ DTO Backend sang UI Model hiển thị trên ShopMessagesScreen
     */
    fun mapDtoToShopConversation(dto: ConversationDto): ShopConversation {
        val name = dto.otherPartyName?.takeIf { it.isNotBlank() } ?: "Shop đối tác #${dto.otherPartyId ?: dto.id}"
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
            lastMessage = dto.lastMessage?.takeIf { it.isNotBlank() } ?: "Chào bạn, shop có thể hỗ trợ gì cho bạn?",
            isLastMessageBold = unread > 0,
            prefixIconType = prefixType,
            unreadCount = unread,
            checkStatus = if (unread > 0) CheckStatus.NONE else CheckStatus.DOUBLE_CHECK_READ
        )
    }

    /**
     * Trích xuất chữ cái đại diện ngắn gọn cho avatar Shop
     */
    private fun extractInitials(shopName: String): String {
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
    private fun generateAvatarColor(shopName: String): Color {
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
    private fun formatTime(isoString: String?): String {
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
         * Danh sách hội thoại mẫu dự phòng khi chưa đăng nhập hoặc mạng không khả dụng
         */
        fun getDefaultConversations(): List<ShopConversation> {
            return listOf(
                ShopConversation(
                    id = "conv_coolmate",
                    numericId = 1L,
                    sellerId = 1L,
                    shopName = "Coolmate Official Store",
                    avatarLetter = "AURA",
                    avatarBg = Color(0xFFF0FDF4),
                    isOnline = true,
                    tagText = "Mall",
                    tagBg = Color(0xFFB82414),
                    time = "10:30",
                    isTimeRed = true,
                    lastMessage = "Dạ sản phẩm này shop còn size L bạn nhé!",
                    isLastMessageBold = true,
                    unreadCount = 2
                ),
                ShopConversation(
                    id = "conv_logitech",
                    numericId = 4L,
                    sellerId = 4L,
                    shopName = "Logitech Flagship Store",
                    avatarLetter = "logi",
                    avatarBg = Color(0xFFE5E7EB),
                    isOnline = true,
                    tagText = "Mall",
                    tagBg = Color(0xFFB82414),
                    time = "09:15",
                    isTimeRed = true,
                    lastMessage = "[Hình ảnh] Bạn kiểm tra mã đơn hàng #OF...",
                    isLastMessageBold = true,
                    prefixIconType = PrefixIconType.IMAGE,
                    unreadCount = 1
                ),
                ShopConversation(
                    id = "conv_anker",
                    numericId = 5L,
                    sellerId = 5L,
                    shopName = "Anker Audio Flagship",
                    avatarLetter = "ANKER",
                    avatarBg = Color(0xFFEFF6FF),
                    isOnline = false,
                    tagText = "Chính hãng",
                    tagBg = Color(0xFF28537D),
                    time = "Hôm qua",
                    lastMessage = "Cảm ơn bạn đã đặt hàng. Đơn sẽ sớm được bàn...",
                    checkStatus = CheckStatus.DOUBLE_CHECK_READ
                ),
                ShopConversation(
                    id = "conv_minimalist",
                    numericId = 99L,
                    sellerId = 99L,
                    shopName = "Nội Thất Minimalist Living",
                    avatarLetter = "Nội Thất",
                    avatarBg = Color(0xFFFFFBEB),
                    isOnline = true,
                    time = "15 Th10",
                    lastMessage = "Dạ shop đã gửi tặng voucher giảm giá 20k...",
                    prefixIconType = PrefixIconType.VOUCHER,
                    checkStatus = CheckStatus.SINGLE_CHECK
                ),
                ShopConversation(
                    id = "conv_locknlock",
                    numericId = 3L,
                    sellerId = 3L,
                    shopName = "Gia Dụng Thông Minh Lock&Lock",
                    avatarLetter = "L&L",
                    avatarBg = Color(0xFFF3F4F6),
                    isOnline = false,
                    tagText = "Mall",
                    tagBg = Color(0xFFB82414),
                    time = "12 Th10",
                    lastMessage = "Bạn có hài lòng với máy tăm nước vừa nhận khô...",
                    checkStatus = CheckStatus.DOUBLE_CHECK_READ
                ),
                ShopConversation(
                    id = "conv_unisex",
                    numericId = 101L,
                    sellerId = 101L,
                    shopName = "Thời Trang Unisex Studio",
                    avatarLetter = "STUDIO",
                    avatarBg = Color(0xFF1E1E1E),
                    isOnline = false,
                    time = "05 Th10",
                    lastMessage = "Áo polo form rộng hiện đã restock đủ màu bạn...",
                    checkStatus = CheckStatus.SINGLE_CHECK
                )
            )
        }

        /**
         * Danh sách tin nhắn mặc định theo từng Shop (dự phòng ngoại tuyến hoặc cuộc hội thoại mẫu)
         */
        fun getDefaultMessagesForShop(
            shopName: String,
            avatarLetter: String? = null,
            avatarBg: Color? = null
        ): List<DetailedChatMessage> {
            val isCoolmate = shopName.contains("Coolmate", ignoreCase = true)
            val isLogitech = shopName.contains("Logitech", ignoreCase = true)
            val isAnker = shopName.contains("Anker", ignoreCase = true)
            val isMinimalist = shopName.contains("Minimalist", ignoreCase = true) || shopName.contains("Nội Thất", ignoreCase = true)
            val isLock = shopName.contains("Lock", ignoreCase = true)

            val letter = avatarLetter ?: "SP"
            val bg = avatarBg ?: Color(0xFFF0FDF4)

            return when {
                isCoolmate -> listOf(
                    DetailedChatMessage(
                        id = "msg_1",
                        text = "Shop tư vấn giúp mình mẫu này với ạ!",
                        time = "10:28",
                        isFromUser = true
                    ),
                    DetailedChatMessage(
                        id = "msg_2",
                        text = "Chào bạn! Cảm ơn bạn đã quan tâm đến Coolmate. Shop có thể hỗ trợ gì cho bạn về mẫu Áo Thun Cotton Compact này ạ?",
                        time = "10:29",
                        isFromUser = false,
                        senderAvatarRes = R.drawable.support_agent_avatar_1793960174892
                    ),
                    DetailedChatMessage(
                        id = "msg_3",
                        text = "Shop ơi cho mình hỏi mình cao 1m73 nặng 70kg thì mặc size L vừa hay cần lên XL ạ?",
                        time = "10:29",
                        isFromUser = true
                    ),
                    DetailedChatMessage(
                        id = "msg_4",
                        text = "Dạ với chiều cao 1m73 và 70kg bạn chọn size L là chuẩn form vừa vặn, thoải mái luôn bạn nhé! Chất vải cotton compact 100% co giãn 4 chiều và thấm hút mồ hôi rất tốt ạ. ✨",
                        time = "10:30",
                        isFromUser = false,
                        senderAvatarRes = R.drawable.coolmate_shop_avatar_1793960155632
                    ),
                    DetailedChatMessage(
                        id = "msg_5",
                        text = "Shop gửi tặng bạn Voucher giảm giá ₫20.000 độc quyền cho đơn hàng từ ₫299.000 áp dụng hôm nay ạ!",
                        time = "10:30",
                        isFromUser = false,
                        isVoucher = true,
                        voucherValue = "₫20.000",
                        senderAvatarRes = R.drawable.coolmate_shop_avatar_1793960155632
                    )
                )
                isLogitech -> listOf(
                    DetailedChatMessage(
                        id = "msg_l1",
                        text = "Shop kiểm tra giúp mình mã đơn hàng #OF38291 với ạ, đã giao chưa shop?",
                        time = "09:12",
                        isFromUser = true
                    ),
                    DetailedChatMessage(
                        id = "msg_l2",
                        text = "[Hình ảnh] Bạn kiểm tra mã đơn hàng #OF38291: Kiện hàng bàn phím cơ G Pro X đã được đóng gói và bàn giao cho bưu cục hỏa tốc sáng nay nhé!",
                        time = "09:15",
                        isFromUser = false,
                        avatarLetter = letter,
                        avatarBg = bg
                    )
                )
                isAnker -> listOf(
                    DetailedChatMessage(
                        id = "msg_a1",
                        text = "Củ sạc Anker GaN 65W này có sạc nhanh được cho cả Macbook Pro và iPhone không shop?",
                        time = "Hôm qua",
                        isFromUser = true
                    ),
                    DetailedChatMessage(
                        id = "msg_a2",
                        text = "Cảm ơn bạn đã đặt hàng. Đơn sẽ sớm được bàn giao cho đơn vị vận chuyển. Củ sạc hỗ trợ chuẩn PD 65W sạc mượt mà đồng thời cả laptop và điện thoại bạn nhé!",
                        time = "Hôm qua",
                        isFromUser = false,
                        avatarLetter = letter,
                        avatarBg = bg
                    )
                )
                isMinimalist -> listOf(
                    DetailedChatMessage(
                        id = "msg_m1",
                        text = "Bàn làm việc gỗ sồi tự nhiên này kích thước thực tế bao nhiêu vậy shop?",
                        time = "15 Th10",
                        isFromUser = true
                    ),
                    DetailedChatMessage(
                        id = "msg_m2",
                        text = "Dạ shop đã gửi tặng voucher giảm giá 20k cho bạn. Bàn có kích thước 120x60x75cm chuẩn công thái học bạn nha!",
                        time = "15 Th10",
                        isFromUser = false,
                        isVoucher = true,
                        voucherValue = "₫20.000",
                        avatarLetter = letter,
                        avatarBg = bg
                    )
                )
                isLock -> listOf(
                    DetailedChatMessage(
                        id = "msg_lk1",
                        text = "Máy tăm nước dùng thích lắm shop ơi, có vòi xịt thay thế không ạ?",
                        time = "12 Th10",
                        isFromUser = true
                    ),
                    DetailedChatMessage(
                        id = "msg_lk2",
                        text = "Bạn có hài lòng với máy tăm nước vừa nhận không ạ? Bên shop luôn có sẵn bộ 4 đầu tăm nước thay thế chính hãng Lock&Lock nhé!",
                        time = "12 Th10",
                        isFromUser = false,
                        avatarLetter = letter,
                        avatarBg = bg
                    )
                )
                else -> listOf(
                    DetailedChatMessage(
                        id = "msg_gen1",
                        text = "Chào bạn! Cảm ơn bạn đã quan tâm đến $shopName. Shop có thể hỗ trợ tư vấn sản phẩm hoặc đơn hàng gì cho bạn hôm nay ạ?",
                        time = "Vừa xong",
                        isFromUser = false,
                        avatarLetter = letter,
                        avatarBg = bg
                    )
                )
            }
        }
    }
}

