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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val attachedProduct: ProductItem? = null,
    val isVoucher: Boolean = false,
    val voucherValue: String? = null
)

/**
 * Màn hình Tin Nhắn Với Shop (Shop Chat Screen)
 * Thiết kế chính xác 100% theo ShopChatScreen_a0957472.png:
 * 1. Top Bar:
 *    - Nút Back
 *    - Avatar Coolmate tròn kèm badge đỏ "MALL" ở đáy avatar
 *    - Tên Shop "Coolmate Official St" kèm tích xanh xác minh verified
 *    - Trạng thái "● Đang hoạt động" màu xanh lá
 *    - Nút viền "Xem Shop" bên phải
 * 2. Widget sản phẩm ghim (Pinned Product):
 *    - Ảnh áo thun đen kèm badge "Xtra"
 *    - Tag "FREESHIP XTRA", Tên sản phẩm "Áo Thun Cott...", Giá ₫199.000 (gốc ₫299.000 gạch ngang)
 *    - Nút pill "Gửi link" màu hồng/cam nhạt
 * 3. Hàng câu hỏi gợi ý nhanh (Suggestion Chips):
 *    - "Tư vấn chọn size?", "Còn màu đen size L không?", "Thời gian giao hàng bao lâu?",...
 * 4. Luồng tin nhắn:
 *    - Pill ngày tháng "Hôm nay, 14:15"
 *    - Tin nhắn 1 (User): Bóng cam đỏ bo góc, chứa thẻ sản phẩm đính kèm "Áo Thun Cc ₫199.000",
 *      văn bản "Shop tư vấn giúp mình mẫu này với ạ!", giờ 14:15 kèm ✓✓
 *    - Tin nhắn 2 (Shop): Avatar nhân viên hỗ trợ, bóng trắng viền mỏng "Chào bạn! Cảm ơn bạn...", 14:15
 *    - Tin nhắn 3 (User): "Shop ơi cho mình hỏi mình cao 1m73 nặng 70kg thì mặc size L vừa hay cần lên XL ạ?", 14:16 ✓✓
 *    - Tin nhắn 4 (Shop): Avatar Coolmate C, bóng trắng "Dạ với chiều cao 1m73 và 70kg bạn chọn size L là chuẩn form...", 14:17
 *    - Tin nhắn 5 (Voucher ưu đãi): Thẻ voucher giảm giá độc quyền
 * 5. Thanh nhập tin nhắn dính đáy:
 *    - Icon (+), Camera, Mặt cười cảm xúc (Sticker)
 *    - Ô nhập bo tròn pill "Nhập tin nhắn..." tích hợp icon Micro
 *    - Nút Gửi tròn màu cam đỏ
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

    // Sản phẩm hiện tại đang được ghim
    val currentProduct = chatInfo.product ?: uiState.products.firstOrNull()

    // Khởi tạo danh sách tin nhắn mẫu chuẩn xác như trong ảnh ShopChatScreen_a0957472.png
    val messages = remember {
        mutableStateListOf(
            DetailedChatMessage(
                id = "msg_1",
                text = "Shop tư vấn giúp mình mẫu này với ạ!",
                time = "14:15",
                isFromUser = true,
                attachedProduct = currentProduct
            ),
            DetailedChatMessage(
                id = "msg_2",
                text = "Chào bạn! Cảm ơn bạn đã quan tâm đến Coolmate. Shop có thể hỗ trợ gì cho bạn về mẫu Áo Thun Cotton Compact này ạ?",
                time = "14:15",
                isFromUser = false,
                senderAvatarRes = R.drawable.support_agent_avatar_1793960174892
            ),
            DetailedChatMessage(
                id = "msg_3",
                text = "Shop ơi cho mình hỏi mình cao 1m73 nặng 70kg thì mặc size L vừa hay cần lên XL ạ?",
                time = "14:16",
                isFromUser = true
            ),
            DetailedChatMessage(
                id = "msg_4",
                text = "Dạ với chiều cao 1m73 và 70kg bạn chọn size L là chuẩn form vừa vặn, thoải mái luôn bạn nhé! Chất vải cotton compact 100% co giãn 4 chiều và thấm hút mồ hôi rất tốt ạ. ✨",
                time = "14:17",
                isFromUser = false,
                senderAvatarRes = R.drawable.coolmate_shop_avatar_1793960155632
            ),
            DetailedChatMessage(
                id = "msg_5",
                text = "Shop gửi tặng bạn Voucher giảm giá ₫20.000 độc quyền cho đơn hàng từ ₫299.000 áp dụng hôm nay ạ!",
                time = "14:18",
                isFromUser = false,
                isVoucher = true,
                voucherValue = "₫20.000",
                senderAvatarRes = R.drawable.coolmate_shop_avatar_1793960155632
            )
        )
    }

    // Tự động cuộn xuống tin nhắn cuối cùng khi có tin mới
    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size - 1)
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
            time = "14:20",
            isFromUser = true,
            attachedProduct = productAttachment
        )
        messages.add(userMsg)
        inputText = ""

        // Giả lập shop phản hồi tự nhiên thông minh
        coroutineScope.launch {
            delay(1000)
            val replyText = when {
                content.contains("size", ignoreCase = true) || content.contains("chiều cao", ignoreCase = true) ->
                    "Dạ sản phẩm may theo bảng size chuẩn nam Việt Nam. Nếu bạn thích mặc rộng thoải mái có thể tăng lên 1 size nhé ạ!"
                content.contains("màu đen", ignoreCase = true) || content.contains("màu", ignoreCase = true) ->
                    "Dạ màu đen hiện kho bên mình đang còn sẵn size M, L, XL ạ. Bạn có thể nhấn đặt ngay nhé!"
                content.contains("giao hàng", ignoreCase = true) || content.contains("bao lâu", ignoreCase = true) ->
                    "Dạ đơn hàng nội thành Hà Nội & TP.HCM giao hỏa tốc 2-4h hoặc 1-2 ngày, các tỉnh thành khác 2-3 ngày bạn nha!"
                content.contains("kiểm tra", ignoreCase = true) ->
                    "Dạ chính sách của shop là khách được đồng kiểm trước khi nhận hàng và đổi trả miễn phí trong 60 ngày ạ!"
                productAttachment != null ->
                    "Dạ mẫu Áo Thun Cotton Compact này là dòng Best Seller của shop đó ạ, vải rất mát và không nhăn!"
                else ->
                    "Cảm ơn bạn! Chuyên viên tư vấn Coolmate đã ghi nhận và sẽ phản hồi chi tiết ngay cho bạn nhé ạ! ❤️"
            }

            messages.add(
                DetailedChatMessage(
                    id = "shop_${System.currentTimeMillis()}",
                    text = replyText,
                    time = "14:21",
                    isFromUser = false,
                    senderAvatarRes = R.drawable.coolmate_shop_avatar_1793960155632
                )
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .testTag("shop_chat_screen")
    ) {
        // 1. Top Bar chuẩn ảnh
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

                // Avatar Coolmate tròn kèm badge MALL ở chân avatar
                Box(
                    modifier = Modifier.size(44.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.coolmate_shop_avatar_1793960155632),
                        contentDescription = chatInfo.shopName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                    )

                    // Badge MALL đỏ chữ trắng ở dưới cùng avatar
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

                Spacer(modifier = Modifier.width(10.dp))

                // Cột Tên Shop + Tích xanh + Trạng thái hoạt động
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Coolmate Official St",
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
                            viewModel.showToast("Đang mở trang chủ gian hàng Coolmate Official")
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

        // 2. Widget Sản phẩm đang được ghim (Pinned Product Strip)
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
                // Ảnh sản phẩm có badge Xtra
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF3F4F6))
                        .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.folded_black_tshirt_1793956682242),
                        contentDescription = "Áo Thun Cotton",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Badge Xtra
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

                // Cột thông tin sản phẩm
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Áo Thun Cott...",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "₫199.000",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoralPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "₫299.000",
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF),
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                // Nút "Gửi link"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFFEBE7))
                        .clickable {
                            sendMessage(
                                content = "Shop ơi mình quan tâm sản phẩm này ạ!",
                                productAttachment = currentProduct
                            )
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = null,
                            tint = CoralPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Gửi link",
                            fontSize = 12.sp,
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
            // Mốc ngày "Hôm nay, 14:15"
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
                            text = "Hôm nay, 14:15",
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
                    UserChatBubble(message = msg)
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
                                viewModel.showToast("Đã chọn ảnh mẫu từ thư viện")
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
                            viewModel.showToast("Mở camera chụp ảnh thực tế")
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
