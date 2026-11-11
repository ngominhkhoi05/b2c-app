package com.example.b2capp.features.chat

import com.example.b2capp.features.chat.components.ShopChatBubble
import com.example.b2capp.features.chat.components.UserChatBubble

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.model.ShopChatInfo
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.FreeShippingAccent
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Model tin nhắn trong màn hình Chat trực tiếp với Shop
 */
data class DetailedChatMessage(
    val id: String,
    val text: String,
    val time: String,
    val isFromUser: Boolean,
    val senderAvatarRes: Int? = null,
    val avatarLetter: String? = null,
    val avatarBg: Color? = null,
    val attachedProduct: ProductItem? = null,
    val attachmentUrl: String? = null,
    val isVoucher: Boolean = false,
    val voucherValue: String? = null
)

/**
 * Màn hình Tin Nhắn Với Shop (Shop Chat Screen)
 * Thiết kế chuẩn UI kết nối Backend API thực tế
 */
@Composable
fun ShopChatScreen(
    chatInfo: ShopChatInfo,
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var isLoadingMessages by remember { mutableStateOf(false) }
    var activeConversationId by remember { mutableStateOf(chatInfo.conversationId) }

    // Sản phẩm đính kèm nếu mở chat từ trang chi tiết sản phẩm
    val currentProduct = chatInfo.product

    val messages = remember { mutableStateListOf<DetailedChatMessage>() }

    // Tự động tải tin nhắn từ Backend API
    LaunchedEffect(chatInfo.conversationId, chatInfo.sellerId) {
        isLoadingMessages = true
        var targetConvId = chatInfo.conversationId

        // Nếu chưa có conversationId nhưng có sellerId -> gọi API tạo hoặc tìm hội thoại
        if (targetConvId == null && chatInfo.sellerId != null) {
            val startedId = viewModel.startOrGetConversation(chatInfo.sellerId)
            if (startedId != null) {
                targetConvId = startedId
                activeConversationId = startedId
            }
        }

        // Tải danh sách tin nhắn qua API (không dùng mock)
        messages.clear()
        if (targetConvId != null) {
            val apiMessages = viewModel.loadMessagesForConversation(
                conversationId = targetConvId,
                shopName = chatInfo.shopName,
                avatarLetter = chatInfo.avatarLetter,
                avatarBg = chatInfo.avatarBg
            )
            if (apiMessages.isNotEmpty()) {
                messages.addAll(apiMessages)
            }
        }
        isLoadingMessages = false
    }

    // Tự động cuộn xuống tin nhắn cuối cùng khi có tin mới
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Hàm gửi tin nhắn
    fun sendMessage(
        content: String,
        productAttachment: ProductItem? = null
    ) {
        if (content.isBlank() && productAttachment == null) return

        val userMsg = DetailedChatMessage(
            id = "user_${System.currentTimeMillis()}",
            text = content,
            time = "Vừa xong",
            isFromUser = true,
            attachedProduct = productAttachment
        )
        messages.add(userMsg)
        inputText = ""

        // Gửi qua API Backend
        viewModel.sendChatMessage(
            conversationId = activeConversationId,
            sellerId = chatInfo.sellerId,
            content = content,
            messageType = if (productAttachment != null) "PRODUCT" else "TEXT",
            refProductId = productAttachment?.id?.toLongOrNull()
        ) { sentDto ->
            if (activeConversationId == null && sentDto.conversationId != null) {
                activeConversationId = sentDto.conversationId
            }
        }
    }

    // Hàm gửi hình ảnh đính kèm
    fun sendImageMessage(imageUrl: String) {
        val userMsg = DetailedChatMessage(
            id = "user_${System.currentTimeMillis()}",
            text = "[Hình ảnh]",
            time = "Vừa xong",
            isFromUser = true,
            attachmentUrl = imageUrl
        )
        messages.add(userMsg)

        viewModel.sendChatMessage(
            conversationId = activeConversationId,
            sellerId = chatInfo.sellerId,
            content = "[Hình ảnh]",
            messageType = "IMAGE",
            attachmentUrl = imageUrl
        ) { sentDto ->
            if (activeConversationId == null && sentDto.conversationId != null) {
                activeConversationId = sentDto.conversationId
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .testTag("shop_chat_screen")
    ) {
        // 1. Top Bar
        Surface(
            color = SurfaceCard,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nút Back
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = NavySecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Avatar Shop tròn kèm badge MALL ở chân avatar
                val initialLetter = chatInfo.avatarLetter ?: chatInfo.shopName.take(2).uppercase()
                val bgColor = chatInfo.avatarBg ?: Color(0xFFF0FDF4)

                Box(
                    modifier = Modifier.size(44.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!chatInfo.avatarUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = chatInfo.avatarUrl,
                            contentDescription = chatInfo.shopName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(bgColor)
                                .border(1.dp, Color(0xFFE5E7EB), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (initialLetter.length > 3) initialLetter.take(3) else initialLetter,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavySecondary
                            )
                        }
                    }

                    // Badge MALL đỏ chữ trắng ở dưới cùng avatar
                    if (chatInfo.isMall) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFEE4D2D))
                                .padding(horizontal = 4.dp, vertical = 0.5.dp)
                        ) {
                            Text(
                                text = "MALL",
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Cột Tên Shop + Tích xanh + Trạng thái hoạt động
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = chatInfo.shopName,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))

                        // Tích xanh xác minh (Verified checkmark)
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF26AA99)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Verified",
                                tint = Color.White,
                                modifier = Modifier.size(9.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.5.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Đang hoạt động",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF26AA99)
                        )
                    }
                }

                // Nút "Xem Shop" viền Navy bên phải
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.2.dp, NavySecondary, RoundedCornerShape(16.dp))
                        .clickable {
                            viewModel.showToast("Đang mở trang chủ gian hàng ${chatInfo.shopName}")
                        }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = NavySecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Xem Shop",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavySecondary
                        )
                    }
                }
            }
        }

        // 2. Widget Sản phẩm đang được ghim (chỉ hiện khi có sản phẩm được ghim)
        if (currentProduct != null) {
            Surface(
                color = SurfaceCard,
                shadowElevation = 0.5.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.openProductDetailsScreen(currentProduct) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF3F4F6))
                                .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp))
                        ) {
                            if (currentProduct.imageUrl.isNotBlank()) {
                                AsyncImage(
                                    model = currentProduct.imageUrl,
                                    contentDescription = currentProduct.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.folded_black_tshirt_1793956682242),
                                    contentDescription = currentProduct.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .clip(RoundedCornerShape(bottomEnd = 4.dp, topStart = 6.dp))
                                    .background(Color(0xFF00897B))
                                    .padding(horizontal = 3.dp, vertical = 0.5.dp)
                            ) {
                                Text(
                                    text = "Xtra",
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentProduct.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentProduct.formattedPrice,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CoralPrimary
                                )
                                val origPrice = currentProduct.formattedOriginalPrice
                                if (!origPrice.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = origPrice,
                                        fontSize = 11.sp,
                                        color = Color(0xFF9CA3AF),
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFFEBE7))
                            .clickable {
                                sendMessage(
                                    content = "Shop tư vấn giúp mình sản phẩm ${currentProduct.name} này với ạ!",
                                    productAttachment = currentProduct
                                )
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Gửi link",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoralPrimary
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 0.8.dp)

        // 3. Hàng câu hỏi gợi ý nhanh (Suggestion Chips)
        Surface(
            color = SurfaceCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chip 1: Tư vấn chọn size (Có icon thước kẻ)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, CoralPrimary, RoundedCornerShape(16.dp))
                        .clickable {
                            sendMessage("Tư vấn chọn size?")
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Straighten,
                            contentDescription = null,
                            tint = CoralPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tư vấn chọn size?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF374151)
                        )
                    }
                }

                // Chip 2: Còn màu đen size L không?
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                        .clickable {
                            sendMessage("Còn màu đen size L không?")
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Còn màu đen size L không?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151)
                    )
                }

                // Chip 3: Thời gian giao hàng bao lâu?
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                        .clickable {
                            sendMessage("Thời gian giao hàng bao lâu?")
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Thời gian giao hàng bao lâu?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151)
                    )
                }

                // Chip 4: Có được kiểm tra hàng không?
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                        .clickable {
                            sendMessage("Có được kiểm tra hàng trước khi nhận không?")
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Có được kiểm tra hàng không?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151)
                    )
                }
            }
        }

        // 4. Danh sách tin nhắn trò chuyện (LazyColumn)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (isLoadingMessages && messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(26.dp),
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
            } else if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
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
                                text = "Không có dữ liệu tin nhắn",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF334155),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Bạn chưa có tin nhắn nào với ${chatInfo.shopName}. Hãy gửi tin nhắn hoặc chọn gợi ý bên trên để bắt đầu trò chuyện!",
                                fontSize = 12.5.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            } else {
                // Mốc ngày "Hôm nay"
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE2E8F0))
                                .padding(horizontal = 12.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Hôm nay",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                // Các bong bóng tin nhắn
                items(messages, key = { it.id }) { msg ->
                    if (msg.isFromUser) {
                        // Tin nhắn từ phía Người mua (Màu cam đỏ bên phải)
                        UserChatBubble(
                            message = msg,
                            onProductClick = { p -> viewModel.openProductDetailsScreen(p) }
                        )
                    } else {
                        // Tin nhắn từ phía Shop (Bong bóng trắng bên trái kèm Avatar)
                        ShopChatBubble(
                            message = msg,
                            onClaimVoucher = {
                                viewModel.showToast("Đã lưu Voucher ₫20k vào Ví Voucher của bạn!")
                            }
                        )
                    }
                }
            }
        }

        // 5. Thanh nhập tin nhắn dính đáy màn hình (Input Bar)
        Surface(
            color = SurfaceCard,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                // Menu đính kèm nhanh khi bấm dấu (+)
                AnimatedVisibility(visible = showAttachmentSheet) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9FAFB))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        AttachmentMenuItem(
                            icon = Icons.Outlined.Straighten,
                            title = "Bảng size",
                            onClick = {
                                showAttachmentSheet = false
                                sendMessage("Gửi giúp mình bảng quy đổi kích cỡ chuẩn với ạ!")
                            }
                        )
                        AttachmentMenuItem(
                            icon = Icons.Default.ConfirmationNumber,
                            title = "Mã giảm giá",
                            onClick = {
                                showAttachmentSheet = false
                                sendMessage("Shop có voucher giảm giá nào áp dụng được cho đơn này không ạ?")
                            }
                        )
                        AttachmentMenuItem(
                            icon = Icons.Default.CameraAlt,
                            title = "Ảnh chụp",
                            onClick = {
                                showAttachmentSheet = false
                                sendImageMessage("https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=500&auto=format&fit=crop&q=80")
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nút (+) Thêm tiện ích
                    IconButton(
                        onClick = { showAttachmentSheet = !showAttachmentSheet },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Thêm tiện ích",
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Nút Camera chụp ảnh
                    IconButton(
                        onClick = {
                            sendImageMessage("https://images.unsplash.com/photo-1523381210434-271e8be1f52b?w=500&auto=format&fit=crop&q=80")
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Chụp ảnh",
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Nút Sticker / Mặt cười
                    IconButton(
                        onClick = {
                            inputText += " 😊"
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SentimentSatisfiedAlt,
                            contentDescription = "Biểu cảm",
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    // Ô nhập tin nhắn hình pill
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
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
                                    sendMessage(inputText)
                                }
                            ),
                            decorationBox = { innerTextField ->
                                if (inputText.isEmpty()) {
                                    Text(
                                        text = "Nhập tin nhắn...",
                                        fontSize = 13.5.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                innerTextField()
                            }
                        )

                        // Icon Microphone thu âm
                        IconButton(
                            onClick = {
                                viewModel.showToast("Nhấn giữ để thu âm tin nhắn thoại")
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = "Ghi âm",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Nút Gửi hình tròn màu Cam đỏ
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CoralPrimary)
                            .clickable {
                                sendMessage(inputText)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Gửi",
                            tint = Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }
        }
    }
}



/**
 * Item nút chọn tiện ích trong bảng đính kèm
 */
@Composable
private fun AttachmentMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = NavySecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            color = Color(0xFF475569)
        )
    }
}
