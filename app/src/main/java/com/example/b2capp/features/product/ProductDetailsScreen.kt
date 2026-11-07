package com.example.b2capp.features.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.b2capp.core.designsystem.theme.BackgroundApp
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.StarRatingGold
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary

/**
 * Màn hình Chi Tiết Sản Phẩm (Product Details Screen)
 * Thiết kế chính xác 100% theo ProductDetailsScreen_8fc97796.png:
 * 1. Ảnh sản phẩm lớn tỷ lệ thời trang với các nút tròn mờ đè lên:
 *    - Back arrow <
 *    - Tag MALL CHÍNH HÃNG đỏ
 *    - Icon Share, Icon Tim đỏ yêu thích, Icon Giỏ hàng kèm badge đỏ số 3
 *    - Huy hiệu chỉ số ảnh 1 / 6
 * 2. Cụm Giá tiền: ₫199.000 – ₫249.000, giá gốc ₫350.000, tag -29%, Freeship Xtra Toàn Quốc, Đổi trả 60 ngày.
 * 3. Tên sản phẩm, Đánh giá 4.9 sao (342 Đánh giá | 1.5k Đã bán), nút bookmark.
 * 4. Dải ƯU ĐÃI: Giảm ₫15k, Giảm 10%, Freeship 200k.
 * 5. Chọn phân loại màu sắc (Trắng Tinh Khôi, Đen Huyền Bí, Xanh Navy) & kích cỡ (M, L, XL, 2XL).
 * 6. Thông tin Shop Coolmate Official (Mall, Chat, Xem Shop, Chỉ số 4.9/5.0, 99%, 185).
 * 7. Chi tiết sản phẩm (Chất liệu, Xuất xứ, Kiểu dáng, Họa tiết, Gửi hàng từ).
 * 8. Mô tả sản phẩm với nút Xem thêm chi tiết.
 * 9. Đánh giá sản phẩm với bình luận mẫu và ảnh chụp feedback.
 * 10. Cam kết 100% Chính Hãng, Giao Nhanh 2h, Đổi Trả 60 Ngày.
 * 11. Thanh hành động Sticky đáy màn hình: Chat, Giỏ hàng, THÊM VÀO GIỎ, MUA NGAY.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    product: ProductItem,
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedColor by remember { mutableStateOf("Đen Huyền Bí") }
    var selectedSize by remember { mutableStateOf("M") }
    var isFavorite by remember { mutableStateOf(true) }
    var isBookmarked by remember { mutableStateOf(false) }
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var showVariantSheet by remember { mutableStateOf(false) }
    var variantSheetMode by remember { mutableStateOf(VariantSheetMode.ADD_TO_CART) }

    val colorsList = listOf("Trắng Tinh Khôi", "Đen Huyền Bí", "Xanh Navy")
    val sizesList = listOf("M", "L", "XL", "2XL")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCard)
            .testTag("product_details_screen")
    ) {
        // Nội dung cuộn toàn màn hình
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 68.dp) // Dành khoảng trống cho thanh mua hàng dính đáy
        ) {
            // 1. Khung ảnh sản phẩm Hero lớn kèm các nút điều khiển đè lên
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.9f)
                        .background(Color(0xFFF7F8FA))
                ) {
                    // Ảnh sản phẩm áo thun trắng treo móc
                    Image(
                        painter = painterResource(id = R.drawable.product_white_tshirt_1793955938236),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Nút điều khiển bên trên (Overlaid controls)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Góc trái: Nút Back + Tag MALL CHÍNH HÃNG
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TranslucentIconButton(
                                icon = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Quay lại",
                                onClick = onBackClick
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFB82414), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "MALL CHÍNH HÃNG",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        // Góc phải: Nút Share, Nút Tim, Nút Giỏ hàng
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TranslucentIconButton(
                                icon = Icons.Outlined.Share,
                                contentDescription = "Chia sẻ",
                                onClick = {}
                            )

                            TranslucentIconButton(
                                icon = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Yêu thích",
                                iconTint = if (isFavorite) Color(0xFFD32F2F) else Color.White,
                                onClick = { isFavorite = !isFavorite }
                            )

                            // Nút giỏ hàng có badge đỏ số 3
                            Box(contentAlignment = Alignment.TopEnd) {
                                TranslucentIconButton(
                                    icon = Icons.Outlined.ShoppingBag,
                                    contentDescription = "Giỏ hàng",
                                    onClick = onNavigateToCart
                                )

                                if (uiState.totalCartCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .offset(x = 4.dp, y = (-4).dp)
                                            .size(17.dp)
                                            .background(Color(0xFFD32F2F), CircleShape)
                                            .border(1.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (uiState.totalCartCount > 99) "99+" else uiState.totalCartCount.toString(),
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Badge chỉ số ảnh 1 / 6 ở góc dưới phải
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(14.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "1 / 6",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // 2. Khu vực Giá tiền & Tiêu đề
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Hàng giá tiền & giảm giá
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "₫199.000 – ₫249.000",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )

                        Text(
                            text = "₫350.000",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textDecoration = TextDecoration.LineThrough
                        )

                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFEBE7), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "-29%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFD32F2F)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Hàng tag Freeship & Đổi trả
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .background(Color(0xFFE6F7F5), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FlashOn,
                                contentDescription = null,
                                tint = Color(0xFF1E8275),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Freeship Xtra Toàn Quốc",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E8275)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF0F2F5), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Đổi trả 60 ngày",
                                fontSize = 11.sp,
                                color = Color(0xFF555555)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tên sản phẩm
                    Text(
                        text = "Áo Thun Nam Cotton Compact Siêu Thoáng Khí Cao Cấp Chống Nhăn Co Giãn",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Hàng sao đánh giá & đã bán
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.clickable { viewModel.openProductReviewsScreen() }
                        ) {
                            Text(
                                text = "4.9",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            // 5 sao vàng
                            Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                                repeat(5) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = StarRatingGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Text(
                                text = "|",
                                color = BorderDivider,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )

                            Text(
                                text = "342 Đánh giá",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "|",
                                color = BorderDivider,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )

                            Text(
                                text = "1.5k Đã bán",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        // Bookmark lưu
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Lưu sản phẩm",
                            tint = if (isBookmarked) CoralPrimary else TextSecondary,
                            modifier = Modifier
                                .size(22.dp)
                                .clickable { isBookmarked = !isBookmarked }
                        )
                    }
                }
            }

            // 3. Dải ƯU ĐÃI Voucher
            item {
                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 8.dp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Mở danh sách voucher */ }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ƯU ĐÃI:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        VoucherTag(text = "Giảm ₫15k", isRed = true, showTicket = true)
                        VoucherTag(text = "Giảm 10%", isRed = true)
                        VoucherTag(text = "Freeship 200k", isGreen = true)
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                }

                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 8.dp)
            }

            // 4. Chọn phân loại (Màu sắc & Kích cỡ)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                variantSheetMode = VariantSheetMode.SELECT_ONLY
                                showVariantSheet = true
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Chọn phân loại",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = "$selectedColor, Size $selectedSize >",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Lựa chọn màu sắc
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        colorsList.forEach { colorName ->
                            val isColorSelected = colorName == selectedColor
                            val dotColor = when (colorName) {
                                "Trắng Tinh Khôi" -> Color.White
                                "Đen Huyền Bí" -> Color(0xFF1E1E1E)
                                else -> Color(0xFF1C3A5A)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isColorSelected) Color(0xFFFFF3F0) else Color(0xFFF0F2F5))
                                    .border(
                                        width = if (isColorSelected) 1.dp else 0.dp,
                                        color = if (isColorSelected) Color(0xFFD32F2F) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedColor = colorName }
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Chấm màu
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(dotColor, CircleShape)
                                            .border(1.dp, if (colorName == "Trắng Tinh Khôi") Color(0xFFCCCCCC) else Color.Transparent, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = colorName,
                                        fontSize = 12.sp,
                                        fontWeight = if (isColorSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isColorSelected) Color(0xFFD32F2F) else TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Lựa chọn kích cỡ
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Kích cỡ:",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        sizesList.forEach { sizeName ->
                            val isSizeSelected = sizeName == selectedSize

                            Box(
                                modifier = Modifier
                                    .size(width = 38.dp, height = 28.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSizeSelected) Color(0xFFFFF3F0) else Color(0xFFF0F2F5))
                                    .border(
                                        width = if (isSizeSelected) 1.dp else 0.dp,
                                        color = if (isSizeSelected) Color(0xFFD32F2F) else Color.Transparent,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .clickable { selectedSize = sizeName },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sizeName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSizeSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSizeSelected) Color(0xFFD32F2F) else TextPrimary
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 8.dp)
            }

            // 5. Thông tin Shop Coolmate Official
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Logo Coolmate + Tên Shop + Trạng thái hoạt động
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0E3B64))
                                    .border(1.5.dp, Color(0xFFE5E7EB), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "CM",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Coolmate Official",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavySecondary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFB82414), RoundedCornerShape(3.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "Mall",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(Color(0xFF10B981), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Đang hoạt động",
                                        fontSize = 11.sp,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }
                        }

                        // 2 Nút Chat & Xem Shop
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(1.dp, Color(0xFFD32F2F), RoundedCornerShape(6.dp))
                                    .clickable { viewModel.openShopChat(product.shopName, product) }
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.ChatBubbleOutline,
                                        contentDescription = null,
                                        tint = Color(0xFFD32F2F),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Chat",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD32F2F)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(1.dp, NavySecondary, RoundedCornerShape(6.dp))
                                    .clickable { /* Xem Shop */ }
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Storefront,
                                        contentDescription = null,
                                        tint = NavySecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Xem Shop",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavySecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 Ô chỉ số Shop (Đánh giá, Phản hồi, Sản phẩm)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ShopStatBox(
                            value = "4.9 / 5.0",
                            label = "12.4k Đánh giá",
                            modifier = Modifier.weight(1f)
                        )
                        ShopStatBox(
                            value = "99%",
                            label = "Phản hồi vài phút",
                            valueColor = Color(0xFFD32F2F),
                            modifier = Modifier.weight(1f)
                        )
                        ShopStatBox(
                            value = "185",
                            label = "Sản phẩm",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 8.dp)
            }

            // 6. Chi tiết sản phẩm (Thông số kỹ thuật)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Chi tiết sản phẩm",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SpecRow(label = "Chất liệu", value = "100% Cotton Compact 4 chiều mềm mịn")
                    SpecRow(label = "Xuất xứ", value = "Việt Nam (Tự hào sản xuất)")
                    SpecRow(label = "Kiểu dáng", value = "Regular Fit chuẩn dáng nam giới")
                    SpecRow(label = "Họa tiết", value = "Trơn Basic tối giản thanh lịch")
                    SpecRow(label = "Gửi hàng từ", value = "Kho Quận Thanh Xuân, Hà Nội")
                }

                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 8.dp)
            }

            // 7. Mô tả sản phẩm
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Mô tả sản phẩm",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Áo thun nam Cotton Compact là sự kết hợp hoàn hảo giữa sợi bông tự nhiên cao cấp và công nghệ dệt nén sợi siêu bền. Bề mặt vải mềm mịn gấp 2 lần cotton thông thường, thấm hút mồ hôi tối đa và không bai xù khi giặt máy.",
                        fontSize = 13.sp,
                        color = Color(0xFF444444),
                        lineHeight = 19.sp,
                        maxLines = if (isDescriptionExpanded) 20 else 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.clickable { isDescriptionExpanded = !isDescriptionExpanded },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isDescriptionExpanded) "Thu gọn" else "Xem thêm chi tiết",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFD32F2F)
                        )
                        Icon(
                            imageVector = if (isDescriptionExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 8.dp)
            }

            // 8. Đánh giá sản phẩm
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Đánh giá sản phẩm",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "★ 4.9/5",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(342 nhận xét có hình ảnh)",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Text(
                            text = "Xem tất cả >",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NavySecondary,
                            modifier = Modifier.clickable { viewModel.openProductReviewsScreen() }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Thẻ đánh giá mẫu
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                        border = BorderStroke(0.8.dp, BorderDivider)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(Color(0xFFE0E7FF), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "H",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4338CA),
                                            fontSize = 13.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "hoang_minh_92",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Row {
                                            repeat(5) {
                                                Icon(
                                                    imageVector = Icons.Filled.Star,
                                                    contentDescription = null,
                                                    tint = StarRatingGold,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Text(
                                    text = "2 ngày trước",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Chất vải cotton compact mềm mát cực kỳ, form áo chuẩn regular mặc lên tôn dáng lắm. Giao hàng Hà Nội chỉ mất 1 ngày. Sẽ ủng hộ shop thêm màu xanh navy nữa!",
                                fontSize = 12.5.sp,
                                color = Color(0xFF333333),
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Ảnh feedback nhỏ kèm thẻ
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(1.dp, BorderDivider, RoundedCornerShape(6.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.product_white_tshirt_1793955938236),
                                    contentDescription = "Feedback",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Phân loại: Trắng, Size L",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 8.dp)
            }

            // 9. Cam kết an tâm mua sắm (100% Chính Hãng, Giao Nhanh 2h, Đổi Trả 60 Ngày)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssuranceItem(text = "100% Chính Hãng")
                    AssuranceItem(text = "Giao Nhanh 2h")
                    AssuranceItem(text = "Đổi Trả 60 Ngày")
                }
            }
        }

        // 10. Sticky Bottom Action Bar (Chat, Giỏ hàng, Thêm vào giỏ, Mua ngay)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .shadow(elevation = 12.dp)
                .border(0.8.dp, BorderDivider),
            color = SurfaceCard
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(64.dp)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nút Chat
                Column(
                    modifier = Modifier
                        .width(48.dp)
                        .clickable { viewModel.openShopChat(product.shopName, product) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Chat",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Chat",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }

                // Nút Giỏ hàng
                Column(
                    modifier = Modifier
                        .width(52.dp)
                        .clickable(onClick = onNavigateToCart),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = "Giỏ hàng",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        if (uiState.totalCartCount > 0) {
                            Box(
                                modifier = Modifier
                                    .offset(x = 6.dp, y = (-4).dp)
                                    .size(15.dp)
                                    .background(Color(0xFFD32F2F), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (uiState.totalCartCount > 99) "99+" else uiState.totalCartCount.toString(),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        text = "Giỏ hàng",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Nút "Thêm Vào Giỏ" (Nền hồng đào, viền đỏ, chữ đỏ)
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFFEBE7))
                        .border(1.dp, Color(0xFFD32F2F).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .clickable {
                            variantSheetMode = VariantSheetMode.ADD_TO_CART
                            showVariantSheet = true
                        }
                        .testTag("btn_add_to_cart_full"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Thêm Vào Giỏ",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Nút "MUA NGAY" (Nền đỏ đậm, chữ trắng)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFBA2211))
                        .clickable {
                            variantSheetMode = VariantSheetMode.BUY_NOW
                            showVariantSheet = true
                        }
                        .testTag("btn_buy_now_full"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "MUA NGAY",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Popup ModalBottomSheet chọn phân loại (Product Variant Selection Screen)
        // Hiển thị khi ấn nút Thêm Vào Giỏ, Mua Ngay hoặc dòng Chọn phân loại
        if (showVariantSheet) {
            ProductVariantBottomSheet(
                product = product,
                initialColor = if (selectedColor.contains("Trắng")) "Trắng" else "Đen",
                initialSize = selectedSize,
                initialQuantity = 2,
                mode = variantSheetMode,
                onDismissRequest = { showVariantSheet = false },
                onConfirm = { chosenColor, chosenSize, chosenQuantity, mode ->
                    selectedColor = if (chosenColor == "Trắng") "Trắng Tinh Khôi" else "Đen Huyền Bí"
                    selectedSize = chosenSize
                    viewModel.addProductToCart(
                        product = product,
                        color = chosenColor,
                        size = chosenSize,
                        quantity = chosenQuantity
                    )
                    showVariantSheet = false
                    if (mode == VariantSheetMode.BUY_NOW) {
                        viewModel.openCheckoutScreen()
                    }
                }
            )
        }
    }
}

@Composable
private fun TranslucentIconButton(
    icon: ImageVector,
    contentDescription: String,
    iconTint: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(Color.Black.copy(alpha = 0.45f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(19.dp)
        )
    }
}

@Composable
private fun VoucherTag(
    text: String,
    isRed: Boolean = false,
    isGreen: Boolean = false,
    showTicket: Boolean = false
) {
    val borderColor = when {
        isRed -> Color(0xFFF87171)
        isGreen -> Color(0xFF26AA99)
        else -> BorderDivider
    }

    val textColor = when {
        isRed -> Color(0xFFD32F2F)
        isGreen -> Color(0xFF26AA99)
        else -> TextPrimary
    }

    Box(
        modifier = Modifier
            .border(0.8.dp, borderColor, RoundedCornerShape(3.dp))
            .background(Color.White, RoundedCornerShape(3.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showTicket) {
                Icon(
                    imageVector = Icons.Filled.ConfirmationNumber,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
            }
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@Composable
private fun ShopStatBox(
    value: String,
    label: String,
    valueColor: Color = TextPrimary,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFFF8F9FA), RoundedCornerShape(6.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SpecRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.width(96.dp)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AssuranceItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(Color(0xFF26AA99), CircleShape)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text,
            fontSize = 11.5.sp,
            color = Color(0xFF555555)
        )
    }
}
