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
import androidx.compose.material.icons.outlined.ChatBubbleOutline
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

import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator

/**
 * Model cho từng hội thoại tin nhắn với Shop
 */
data class ShopConversation(
    val id: String,
    val numericId: Long? = null,
    val sellerId: Long? = null,
    val shopName: String,
    val avatarLetter: String,
    val avatarBg: Color,
    val avatarUrl: String? = null,
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
    val isFromUser: Boolean,
    val avatarLetter: String? = null,
    val avatarBg: androidx.compose.ui.graphics.Color? = null,
    val senderAvatarRes: Int? = null
)

enum class ChatFilterTab(val title: String) {
    ALL("Tất cả"),
    UNREAD("Chưa đọc"),
    READ("Đã đọc")
}

/**
 * Màn hình Tin Nhắn Với Shop (Shop Messages Screen)
 * Thiết kế chính xác 100% theo ShopMessagesScreen_cafb5486.png:
 * 1. Top Bar: Nút Back, "Tin Nhắn Với Shop", nút tìm kiếm.
 * 2. Ô tìm kiếm: "Tìm kiếm tin nhắn, tên Shop..."
 * 3. Hàng Filter pills:
 *    - "Tất cả (X)"
 *    - "Chưa đọc (Y)" kèm chấm đỏ/trắng
 *    - "Đã đọc (Z)"
 * 4. Tiêu đề "HỘP THOẠI GẦN ĐÂY" kèm nút "Bộ lọc hoạt động".
 * 5. Danh sách cuộc hội thoại thực tế từ cơ sở dữ liệu.
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
    var selectedFilterTab by remember { mutableStateOf(ChatFilterTab.ALL) }

    // Tự động kích hoạt gọi API load danh sách tin nhắn khi mở màn hình
    LaunchedEffect(Unit) {
        viewModel.loadShopConversations()
    }

    val conversations = uiState.shopConversations

    val unreadCount = remember(conversations) { conversations.count { it.unreadCount > 0 } }
    val readCount = remember(conversations) { conversations.count { it.unreadCount == 0 } }
    val totalCount = conversations.size

    // Lọc danh sách theo tab và từ khóa tìm kiếm
    val filteredConversations = remember(selectedFilterTab, searchQuery, conversations) {
        conversations.filter { conv ->
            val matchQuery = searchQuery.isBlank() ||
                    conv.shopName.contains(searchQuery, ignoreCase = true) ||
                    conv.lastMessage.contains(searchQuery, ignoreCase = true)

            val matchTab = when (selectedFilterTab) {
                ChatFilterTab.ALL -> true
                ChatFilterTab.UNREAD -> conv.unreadCount > 0
                ChatFilterTab.READ -> conv.unreadCount == 0
            }

            matchQuery && matchTab
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

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.loadShopConversations(forceRefresh = true) },
                            modifier = Modifier.size(38.dp)
                        ) {
                            if (uiState.isShopConversationsLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = CoralPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Tải lại",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
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
                    ChatFilterTab.values().forEach { tab ->
                        val isSelected = tab == selectedFilterTab
                        val count = when (tab) {
                            ChatFilterTab.ALL -> totalCount
                            ChatFilterTab.UNREAD -> unreadCount
                            ChatFilterTab.READ -> readCount
                        }
                        val label = "${tab.title} ($count)"

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) Color(0xFFBA2211) else Color(0xFFF0F2F5))
                                .clickable { selectedFilterTab = tab }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = label,
                                    fontSize = 12.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF4B5563)
                                )

                                if (tab == ChatFilterTab.UNREAD && unreadCount > 0) {
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(if (isSelected) Color.White else Color(0xFFBA2211), CircleShape)
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
            // Tiêu đề HỘP THOẠI GẦN ĐÂY
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
                        modifier = Modifier.clickable { viewModel.loadShopConversations(forceRefresh = true) }
                    ) {
                        if (uiState.isShopConversationsLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                strokeWidth = 1.5.dp,
                                color = Color(0xFF1E5B94)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Icon(
                            imageVector = Icons.Outlined.Tune,
                            contentDescription = null,
                            tint = Color(0xFF1E5B94),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (uiState.isShopConversationsLoading) "Đang cập nhật..." else "Bộ lọc hoạt động",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E5B94)
                        )
                    }
                }
            }

            // Banner thông báo lỗi nếu có
            if (uiState.shopConversationsError != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Không thể tải danh sách tin nhắn từ máy chủ",
                                fontSize = 12.sp,
                                color = Color(0xFF991B1B)
                            )
                            Text(
                                text = "Thử lại",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB91C1C),
                                modifier = Modifier.clickable { viewModel.loadShopConversations(forceRefresh = true) }
                            )
                        }
                    }
                }
            }

            // 6. Danh sách cuộc trò chuyện
            if (uiState.isShopConversationsLoading && conversations.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = CoralPrimary,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Đang tải tin nhắn...",
                                    fontSize = 12.5.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            } else if (filteredConversations.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 36.dp, horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ChatBubbleOutline,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(
                                text = if (searchQuery.isNotBlank()) {
                                    "Không tìm thấy tin nhắn phù hợp"
                                } else {
                                    when (selectedFilterTab) {
                                        ChatFilterTab.UNREAD -> "Không có tin nhắn chưa đọc"
                                        ChatFilterTab.READ -> "Không có tin nhắn đã đọc"
                                        ChatFilterTab.ALL -> "Không có dữ liệu tin nhắn"
                                    }
                                },
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF334155),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = if (searchQuery.isNotBlank()) {
                                    "Hãy thử tìm kiếm với từ khóa khác hoặc xóa ô tìm kiếm."
                                } else {
                                    when (selectedFilterTab) {
                                        ChatFilterTab.UNREAD -> "Bạn đã đọc hết tất cả tin nhắn. Khi có tin nhắn mới từ Shop, tin nhắn sẽ hiển thị tại đây."
                                        ChatFilterTab.READ -> "Chưa có cuộc trò chuyện nào được đánh dấu là đã đọc."
                                        ChatFilterTab.ALL -> "Bạn chưa có cuộc trò chuyện nào với các Shop. Khi nhắn tin với Shop, lịch sử trò chuyện sẽ được hiển thị tại đây."
                                    }
                                },
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            } else {
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
                                    onClick = {
                                        viewModel.openShopChat(
                                            shopName = conv.shopName,
                                            conversationId = conv.numericId,
                                            sellerId = conv.sellerId,
                                            avatarLetter = conv.avatarLetter,
                                            avatarBg = conv.avatarBg,
                                            avatarUrl = conv.avatarUrl,
                                            isMall = conv.tagText == "Mall"
                                        )
                                    }
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
    }
}



