package com.example.b2capp.features.chat

import com.example.b2capp.features.chat.components.ChatBubbleRow
import com.example.b2capp.features.chat.components.ShopConversationItemRow

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.StarRatingGold
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Model cho từng hội thoại tin nhắn với Shop
 */
data class ShopConversation(
    val id: String,
    val shopName: String,
    val avatarLetter: String,
    val avatarBg: Color,
    val isOnline: Boolean,
    val tagText: String? = null,
    val tagBg: Color = Color(0xFFB82414),
    val time: String,
    val isTimeRed: Boolean = false,
    val lastMessage: String,
    val isLastMessageBold: Boolean = false,
    val prefixIconType: PrefixIconType = PrefixIconType.NONE,
    val unreadCount: Int = 0,
    val checkStatus: CheckStatus = CheckStatus.NONE
)

enum class PrefixIconType {
    NONE,
    IMAGE,
    VOUCHER
}

enum class CheckStatus {
    NONE,
    SINGLE_CHECK,
    DOUBLE_CHECK_READ
}

/**
 * Model tin nhắn trong chi tiết cuộc trò chuyện
 */
data class ChatMessage(
    val id: String,
    val senderName: String,
    val text: String,
    val time: String,
    val isFromUser: Boolean
)

/**
 * Màn hình Tin Nhắn Với Shop (Shop Messages Screen)
 * Thiết kế chính xác 100% theo ShopMessagesScreen_cafb5486.png:
 * 1. Top Bar: Nút Back, "Tin Nhắn Với Shop", nút tìm kiếm.
 * 2. Ô tìm kiếm: "Tìm kiếm tin nhắn, tên Shop..."
 * 3. Hàng Filter pills:
 *    - "Tất cả (12)" (nền đỏ cam đậm, chữ trắng)
 *    - "Chưa đọc ● (3)" (chấm tròn đỏ)
 *    - "Official Mall"
 *    - "Hỗ trợ 24/7" (kèm dấu tích)
 * 4. Khối đặc biệt "Trợ lý B2C 24/7 (AI Bot)":
 *    - Nền xanh phấn nhạt, icon robot/tai nghe navy, chấm xanh online.
 *    - "Trợ lý B2C 24/7" kèm tag "AI Bot" đỏ.
 *    - "Giải đáp khiếu nại & tư vấn đơn hàng" | "Vừa xong >".
 * 5. Tiêu đề "HỘP THOẠI GẦN ĐÂY" kèm nút "Bộ lọc hoạt động".
 * 6. Danh sách 6 shop:
 *    - Coolmate Official Store (Mall, 10:30, "Dạ sản phẩm này shop còn size L bạn nhé!", badge đỏ 2)
 *    - Logitech Flagship Store (Mall, 09:15, "[Hình ảnh] Bạn kiểm tra mã đơn hàng #OF...", badge đỏ 1)
 *    - Anker Audio Flagship (Chính hãng, Hôm qua, "Cảm ơn bạn đã đặt hàng...", tích đôi xanh)
 *    - Nội Thất Minimalist Living (15 Th10, voucher 20k, tích đơn xám)
 *    - Gia Dụng Thông Minh Lock&Lock (Mall, 12 Th10, tích đôi xanh)
 *    - Thời Trang Unisex Studio (05 Th10, restock đủ màu, tích đơn xám)
 * 7. Khung chat chi tiết tương tác khi chạm vào bất kỳ cuộc trò chuyện nào.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopMessagesScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterPill by remember { mutableStateOf("Tất cả (12)") }
    var activeChatConversation by remember { mutableStateOf<ShopConversation?>(null) }

    val filterPills = listOf(
        "Tất cả (12)",
        "Chưa đọc ● (3)",
        "Official Mall",
        "Hỗ trợ 24/7"
    )

    // Danh sách 6 hội thoại chuẩn theo ảnh
    val conversations = remember {
        listOf(
            ShopConversation(
                id = "conv_coolmate",
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

    // Lọc danh sách theo pill và từ khóa tìm kiếm
    val filteredConversations = remember(selectedFilterPill, searchQuery, conversations) {
        conversations.filter { conv ->
            val matchQuery = searchQuery.isBlank() ||
                    conv.shopName.contains(searchQuery, ignoreCase = true) ||
                    conv.lastMessage.contains(searchQuery, ignoreCase = true)

            val matchPill = when (selectedFilterPill) {
                "Chưa đọc ● (3)" -> conv.unreadCount > 0
                "Official Mall" -> conv.tagText == "Mall"
                else -> true
            }

            matchQuery && matchPill
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .testTag("shop_messages_screen")
    ) {
        // 1. Top Bar
        Surface(
            color = SurfaceCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Quay lại",
                                tint = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "Tin Nhắn Với Shop",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    IconButton(
                        onClick = { /* Tìm kiếm */ },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Tìm kiếm",
                            tint = TextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // 2. Ô tìm kiếm
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF0F2F5))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 13.5.sp,
                            color = TextPrimary
                        ),
                        cursorBrush = SolidColor(CoralPrimary),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Tìm kiếm tin nhắn, tên Shop...",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            }
                            innerTextField()
                        }
                    )

                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Filled.Cancel,
                            contentDescription = "Xóa",
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { searchQuery = "" }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 3. Hàng Filter Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    filterPills.forEach { pill ->
                        val isSelected = pill == selectedFilterPill

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) Color(0xFFBA2211) else Color(0xFFF0F2F5))
                                .clickable { selectedFilterPill = pill }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (pill.contains("●")) {
                                    Text(
                                        text = "Chưa đọc ",
                                        fontSize = 12.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color(0xFF4B5563)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(if (isSelected) Color.White else Color(0xFFBA2211), CircleShape)
                                    )
                                    Text(
                                        text = " (3)",
                                        fontSize = 12.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color(0xFF4B5563)
                                    )
                                } else {
                                    Text(
                                        text = pill,
                                        fontSize = 12.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color(0xFF4B5563)
                                    )
                                }

                                if (pill == "Hỗ trợ 24/7") {
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else Color(0xFF1E8275),
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // Danh sách nội dung
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 4. Banner Trợ lý B2C 24/7 (AI Bot)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            viewModel.openShopChat(
                                shopName = "Trợ lý B2C 24/7 (AI Bot)"
                            )
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF2FC)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon Trợ lý bo tròn mềm
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF2A527A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.HeadsetMic,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )

                            // Chấm xanh online góc dưới phải
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(10.dp)
                                    .background(Color(0xFF10B981), CircleShape)
                                    .border(1.5.dp, Color.White, CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Trợ lý B2C 24/7",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E3A8A)
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFFFEBE7), RoundedCornerShape(3.dp))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "AI Bot",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFBA2211)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Giải đáp khiếu nại & tư vấn đơn hàng",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Vừa xong",
                                fontSize = 11.sp,
                                color = Color(0xFF3B82F6)
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color(0xFF3B82F6),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // 5. Tiêu đề HỘP THOẠI GẦN ĐÂY
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HỘP THOẠI GẦN ĐÂY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280),
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { /* Toggle bộ lọc */ }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Tune,
                            contentDescription = null,
                            tint = Color(0xFF1E5B94),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Bộ lọc hoạt động",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E5B94)
                        )
                    }
                }
            }

            // 6. Danh sách 6 cuộc trò chuyện dạng Card trắng có Divider mảnh
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.8.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        filteredConversations.forEachIndexed { index, conv ->
                            ShopConversationItemRow(
                                conversation = conv,
                                onClick = { viewModel.openShopChat(conv.shopName) }
                            )

                            if (index < filteredConversations.size - 1) {
                                HorizontalDivider(
                                    color = Color(0xFFF3F4F6),
                                    thickness = 0.8.dp,
                                    modifier = Modifier.padding(start = 66.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet Chat trực tuyến chi tiết với Shop
    activeChatConversation?.let { conv ->
        ConversationChatModal(
            conversation = conv,
            onDismissRequest = { activeChatConversation = null }
        )
    }
}



/**
 * Modal cửa sổ Chat trực tiếp với Shop khi chọn một cuộc trò chuyện
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversationChatModal(
    conversation: ShopConversation,
    onDismissRequest: () -> Unit
) {
    var messageInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val chatMessages = remember {
        mutableStateListOf(
            ChatMessage("m1", conversation.shopName, "Xin chào bạn! Shop có thể hỗ trợ gì cho bạn ạ?", "10:25", false),
            ChatMessage("m2", "Tôi", "Sản phẩm áo thun compact này mình 1m72 nặng 68kg mặc size L vừa không shop?", "10:28", true),
            ChatMessage("m3", conversation.shopName, conversation.lastMessage, conversation.time, false)
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = SurfaceCard,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Chat Header
            Surface(
                color = SurfaceCard,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Đóng",
                            tint = TextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(conversation.avatarBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = conversation.avatarLetter.take(2),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavySecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = conversation.shopName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        if (conversation.isOnline) Color(0xFF10B981) else Color(0xFF9CA3AF),
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (conversation.isOnline) "Đang hoạt động" else "Hoạt động gần đây",
                                fontSize = 11.sp,
                                color = if (conversation.isOnline) Color(0xFF10B981) else TextSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .border(0.8.dp, CoralPrimary, RoundedCornerShape(6.dp))
                            .clickable { /* Xem shop */ }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Xem Shop",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoralPrimary
                        )
                    }
                }
            }

            // Danh sách tin nhắn
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFF5F6F8))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(chatMessages, key = { it.id }) { msg ->
                    ChatBubbleRow(message = msg)
                }
            }

            // Thanh nhập tin nhắn dính đáy
            Surface(
                color = SurfaceCard,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF0F2F5))
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 13.5.sp,
                                color = TextPrimary
                            ),
                            cursorBrush = SolidColor(CoralPrimary),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (messageInput.isNotBlank()) {
                                        val textToSend = messageInput
                                        messageInput = ""
                                        chatMessages.add(
                                            ChatMessage(
                                                id = "user_${System.currentTimeMillis()}",
                                                senderName = "Tôi",
                                                text = textToSend,
                                                time = "Vừa xong",
                                                isFromUser = true
                                            )
                                        )
                                        scope.launch {
                                            delay(100)
                                            listState.animateScrollToItem(chatMessages.size - 1)
                                            // Phản hồi mẫu tự động từ Shop
                                            delay(1000)
                                            chatMessages.add(
                                                ChatMessage(
                                                    id = "shop_${System.currentTimeMillis()}",
                                                    senderName = conversation.shopName,
                                                    text = "Shop đã nhận được tin nhắn của bạn và sẽ phản hồi trong giây lát nhé!",
                                                    time = "Vừa xong",
                                                    isFromUser = false
                                                )
                                            )
                                            listState.animateScrollToItem(chatMessages.size - 1)
                                        }
                                    }
                                }
                            ),
                            decorationBox = { innerTextField ->
                                if (messageInput.isEmpty()) {
                                    Text(
                                        text = "Nhập tin nhắn với ${conversation.shopName}...",
                                        fontSize = 12.5.sp,
                                        color = TextSecondary
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (messageInput.isNotBlank()) {
                                val textToSend = messageInput
                                messageInput = ""
                                chatMessages.add(
                                    ChatMessage(
                                        id = "user_${System.currentTimeMillis()}",
                                        senderName = "Tôi",
                                        text = textToSend,
                                        time = "Vừa xong",
                                        isFromUser = true
                                    )
                                )
                                scope.launch {
                                    delay(100)
                                    listState.animateScrollToItem(chatMessages.size - 1)
                                    delay(1000)
                                    chatMessages.add(
                                        ChatMessage(
                                            id = "shop_${System.currentTimeMillis()}",
                                            senderName = conversation.shopName,
                                            text = "Shop đã nhận được tin nhắn của bạn và sẽ phản hồi trong giây lát nhé!",
                                            time = "Vừa xong",
                                            isFromUser = false
                                        )
                                    )
                                    listState.animateScrollToItem(chatMessages.size - 1)
                                }
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFBA2211), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Gửi",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}


