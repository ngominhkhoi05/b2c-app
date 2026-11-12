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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
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
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.b2capp.core.model.ProductVariantItem
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.designsystem.theme.BackgroundApp
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.StarRatingGold
import com.example.b2capp.core.ui.components.StarRatingBar
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import com.example.b2capp.data.remote.dto.PromotionDto
import com.example.b2capp.features.cart.VoucherSelectionBottomSheet
import java.text.NumberFormat
import java.util.Locale

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
 * 5. Chọn phân loại màu sắc & kích cỡ (nếu có tùy chọn phân loại từ API).
 * 6. Thông tin Shop Coolmate Official (Mall, Chat, Xem Shop, Chỉ số 4.9/5.0, 99%, 185).
 * 7. Chi tiết sản phẩm (Chất liệu, Xuất xứ, Kiểu dáng, Họa tiết, Gửi hàng từ).
 * 8. Mô tả sản phẩm với nút Xem thêm chi tiết.
 * 9. Đánh giá sản phẩm với bình luận mẫu và ảnh chụp feedback.
 * 10. Cam kết 100% Chính Hãng, Giao Nhanh 2h, Đổi Trả 60 Ngày.
 * 11. Thanh hành động Sticky đáy màn hình: Chat, Giỏ hàng, THÊM VÀO GIỎ, MUA NGAY.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun ProductDetailsScreen(
    product: ProductItem,
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasDynamicOptions = product.options.isNotEmpty()

    val selectedOptionValues = remember(product.id, product.options) {
        val map = mutableStateMapOf<Long, Long>()
        product.options.forEach { opt ->
            opt.values.firstOrNull()?.id?.let { firstId ->
                map[opt.id] = firstId
            }
        }
        map
    }

    val currentVariant = remember(product.variants, selectedOptionValues.toMap()) {
        if (hasDynamicOptions) {
            product.findVariantByOptionValues(selectedOptionValues.values)
        } else {
            product.variants.firstOrNull()
        }
    }

    val numericProductId = product.id.toLongOrNull()
    val isWishlisted = numericProductId != null && uiState.wishlistProductIds.contains(numericProductId)
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var showVariantSheet by remember { mutableStateOf(false) }
    var showVoucherSelectionSheet by remember { mutableStateOf(false) }
    var variantSheetMode by remember { mutableStateOf(VariantSheetMode.ADD_TO_CART) }

    val displayImages = remember(product.id, product.images) {
        if (product.images.isNotEmpty()) product.images else listOfNotNull(product.imageUrl.ifBlank { null })
    }
    val pagerState = rememberPagerState(pageCount = { displayImages.size.coerceAtLeast(1) })

    val effectivePrice = (currentVariant?.price ?: product.price).toDouble()
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale("vi", "VN")) }

    val topVouchers = remember(uiState.availablePromotions, effectivePrice) {
        uiState.availablePromotions
            .filter { promo ->
                val isActive = promo.status == null || promo.status.equals("ACTIVE", ignoreCase = true)
                val hasStock = (promo.remainingQuantity ?: 1) > 0
                val meetsMinOrder = effectivePrice >= (promo.minOrderValue ?: 0.0)
                isActive && hasStock && meetsMinOrder
            }
            .map { promo ->
                val discount = viewModel.computeDiscountAmount(promo, effectivePrice)
                Pair(promo, discount)
            }
            .filter { it.second > 0.0 }
            .sortedByDescending { it.second }
            .take(3)
    }

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
                    // Slider ảnh động
                    if (displayImages.isNotEmpty()) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            AsyncImage(
                                model = displayImages[page],
                                contentDescription = "${product.name} - ảnh ${page + 1}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.product_white_tshirt_1793955938236),
                            contentDescription = product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Nút điều khiển bên trên (Overlaid controls)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Góc trái: Nút Back
                        TranslucentIconButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            onClick = onBackClick
                        )

                        // Góc phải: Nút Tim, Nút Túi xách mở Wishlist, Nút Giỏ hàng
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TranslucentIconButton(
                                icon = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Yêu thích",
                                iconTint = if (isWishlisted) Color(0xFFD32F2F) else Color.White,
                                onClick = {
                                    if (numericProductId != null) {
                                        viewModel.toggleWishlist(numericProductId, product.name)
                                    } else {
                                        viewModel.showToast("Đã lưu vào danh sách yêu thích!")
                                    }
                                }
                            )

                            // Nút túi xách mở trang sản phẩm yêu thích (Wishlist)
                            TranslucentIconButton(
                                icon = Icons.Outlined.ShoppingBag,
                                contentDescription = "Sản phẩm yêu thích",
                                onClick = { viewModel.openWishlist() }
                            )

                            // Nút giỏ hàng có Badge số lượng
                            BadgedBox(
                                badge = {
                                    if (uiState.totalCartCount > 0) {
                                        Badge(containerColor = CoralPrimary) {
                                            Text("${uiState.totalCartCount}", color = Color.White, fontSize = 9.sp)
                                        }
                                    }
                                }
                            ) {
                                TranslucentIconButton(
                                    icon = Icons.Outlined.ShoppingCart,
                                    contentDescription = "Giỏ hàng",
                                    onClick = onNavigateToCart
                                )
                            }
                        }
                    }

                    // Badge chỉ số ảnh động ở góc dưới phải
                    if (displayImages.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(14.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${pagerState.currentPage + 1} / ${displayImages.size}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
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
                    val displayPrice = currentVariant?.formattedPrice ?: product.formattedPrice
                    val displayOriginalPrice = currentVariant?.formattedOriginalPrice ?: product.formattedOriginalPrice
                    val displayDiscountPercent = currentVariant?.discountPercent ?: product.discountPercent

                    // Hàng giá tiền & giảm giá
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = displayPrice,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )

                        if (displayOriginalPrice != null) {
                            Text(
                                text = displayOriginalPrice,
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }

                        if (displayDiscountPercent != null && displayDiscountPercent > 0) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFFEBE7), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "-${displayDiscountPercent}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFD32F2F)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tên sản phẩm động
                    Text(
                        text = product.name,
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
                            val hasRating = product.rating > 0f
                            if (hasRating) {
                                Text(
                                    text = String.format(java.util.Locale.US, "%.1f", product.rating),
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )

                                // Sao vàng theo điểm thực tế (hỗ trợ nửa sao)
                                StarRatingBar(
                                    rating = product.rating,
                                    starSize = 14.dp,
                                    showRatingText = false,
                                    testTag = "product_detail_rating_stars"
                                )
                            } else {
                                Text(
                                    text = "Chưa có đánh giá",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            Text(
                                text = "|",
                                color = BorderDivider,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )

                            Text(
                                text = product.formattedSoldCount,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // 3. Dải ƯU ĐÃI Voucher (Lấy từ API thật - hiển thị 3 voucher tốt nhất hoặc ẩn nếu không có)
            if (topVouchers.isNotEmpty()) {
                item {
                    HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 8.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showVoucherSelectionSheet = true }
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
                            topVouchers.forEachIndexed { index, (voucher, discount) ->
                                val discountText = if (discount >= 1000) {
                                    "Giảm ₫${currencyFormatter.format(discount.toLong())}"
                                } else {
                                    "Giảm ₫${discount.toInt()}"
                                }
                                val isFreeship = voucher.discountType.equals("FREESHIP", ignoreCase = true)
                                VoucherTag(
                                    text = discountText,
                                    isRed = !isFreeship,
                                    isGreen = isFreeship,
                                    showTicket = index == 0
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Xem tất cả ưu đãi",
                            tint = TextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                    }

                    HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 8.dp)
                }
            }

            // 4. Chọn phân loại (Chỉ hiển thị khi sản phẩm có tùy chọn phân loại thực tế từ API)
            if (hasDynamicOptions) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        val currentSummary = currentVariant?.variantName ?: product.options.mapNotNull { opt ->
                            val valId = selectedOptionValues[opt.id]
                            opt.values.find { it.id == valId }?.value
                        }.joinToString(", ")

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
                                text = if (currentSummary.isNotBlank()) "$currentSummary >" else ">",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        product.options.forEach { option ->
                            val selectedValId = selectedOptionValues[option.id]
                            val selectedValue = option.values.find { it.id == selectedValId }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = option.name,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                                if (selectedValue != null) {
                                    Text(
                                        text = selectedValue.value,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = CoralPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                option.values.forEach { optionValue ->
                                    val isValSelected = optionValue.id == selectedValId

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isValSelected) Color(0xFFFFF3F0) else Color(0xFFF0F2F5))
                                            .border(
                                                width = if (isValSelected) 1.dp else 0.dp,
                                                color = if (isValSelected) Color(0xFFD32F2F) else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { selectedOptionValues[option.id] = optionValue.id }
                                            .padding(horizontal = 10.dp, vertical = 7.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (optionValue.imageUrl != null) {
                                                AsyncImage(
                                                    model = optionValue.imageUrl,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(14.dp)
                                                        .clip(CircleShape)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                            }
                                            Text(
                                                text = optionValue.value,
                                                fontSize = 12.sp,
                                                fontWeight = if (isValSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isValSelected) Color(0xFFD32F2F) else TextPrimary
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 8.dp)
                }
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
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Logo Coolmate + Tên Shop + Trạng thái hoạt động
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.openShopScreen(
                                        shopName = product.shopName,
                                        sellerId = product.sellerId,
                                        shopLocation = product.shopLocation,
                                        shopSlug = product.shopSlug,
                                        shopLogoUrl = product.shopLogoUrl
                                    )
                                }
                        ) {
                            val shopTitle = product.shopName.ifBlank { "Coolmate Official" }
                            val shopInitials = shopTitle.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2).joinToString("").ifBlank { "CM" }

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0E3B64))
                                    .border(1.5.dp, Color(0xFFE5E7EB), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = shopInitials,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = shopTitle,
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

                                Spacer(modifier = Modifier.height(3.dp))

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

                        // Nút Xem Shop & Chat
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .height(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(1.dp, NavySecondary, RoundedCornerShape(6.dp))
                                    .clickable {
                                        viewModel.openShopScreen(
                                            shopName = product.shopName,
                                            sellerId = product.sellerId,
                                            shopLocation = product.shopLocation,
                                            shopSlug = product.shopSlug,
                                            shopLogoUrl = product.shopLogoUrl
                                        )
                                    }
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Xem Shop",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavySecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .height(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(1.dp, Color(0xFFD32F2F), RoundedCornerShape(6.dp))
                                    .clickable { viewModel.openShopChat(shopName = product.shopName, product = product, sellerId = product.sellerId) }
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.ChatBubbleOutline,
                                        contentDescription = null,
                                        tint = Color(0xFFD32F2F),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Chat",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD32F2F)
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

            // 6. Mô tả sản phẩm
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

                    val descText = if (product.description.isNotBlank()) {
                        product.description
                    } else {
                        "${product.name} là sản phẩm chính hãng cao cấp, phân phối độc quyền bởi ${product.shopName}. Hỗ trợ đổi trả trong 7 ngày, bảo hành toàn diện và cam kết chất lượng tuyệt đối."
                    }
                    Text(
                        text = descText,
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
                val totalReviews = if (uiState.productReviewsTotalCount > 0) uiState.productReviewsTotalCount else product.ratingCount
                val ratingScore = if (product.rating > 0f) String.format(java.util.Locale.US, "★ %.1f/5", product.rating) else "Chưa có sao"
                val reviewSummaryText = if (totalReviews > 0) "($totalReviews nhận xét)" else "(Chưa có nhận xét)"

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openProductReviewsScreen() },
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
                                    text = ratingScore,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = reviewSummaryText,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        if (totalReviews > 0 || uiState.productReviews.isNotEmpty()) {
                            Text(
                                text = "Xem tất cả >",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NavySecondary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable { viewModel.openProductReviewsScreen() }
                                    .padding(vertical = 4.dp, horizontal = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Trạng thái tải hoặc nội dung đánh giá thật từ API
                    if (uiState.isProductReviewsLoading) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                            border = BorderStroke(0.8.dp, BorderDivider)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.material3.CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp,
                                    color = CoralPrimary
                                )
                            }
                        }
                    } else if (uiState.productReviews.isEmpty()) {
                        // Hiển thị thông báo không có đánh giá khi API trả về rỗng
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                            border = BorderStroke(0.8.dp, BorderDivider)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFD1D5DB),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Chưa có đánh giá nào cho sản phẩm này",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Hãy là người đầu tiên mua và chia sẻ cảm nhận nhé!",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        // Hiển thị review thật đầu tiên lấy từ API
                        val latestReview = uiState.productReviews.first()
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.openProductReviewsScreen() },
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
                                        if (!latestReview.userAvatar.isNullOrBlank()) {
                                            AsyncImage(
                                                model = latestReview.userAvatar,
                                                contentDescription = latestReview.username,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .background(latestReview.avatarBgColor, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = latestReview.initial,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF4338CA),
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = latestReview.username,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Row {
                                                val starCount = latestReview.rating.coerceIn(1, 5)
                                                repeat(starCount) {
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
                                        text = latestReview.date,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                if (latestReview.comment.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = latestReview.comment,
                                        fontSize = 12.5.sp,
                                        color = Color(0xFF333333),
                                        lineHeight = 17.sp,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // Ảnh đánh giá thật từ API nếu có
                                if (latestReview.imageUrls.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        latestReview.imageUrls.take(4).forEach { imgUrl ->
                                            Box(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .border(0.8.dp, BorderDivider, RoundedCornerShape(6.dp))
                                            ) {
                                                AsyncImage(
                                                    model = imgUrl,
                                                    contentDescription = "Review image",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                        }
                                    }
                                }

                                if (latestReview.variantInfo.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = latestReview.variantInfo,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                // Phản hồi của shop nếu có
                                if (!latestReview.sellerResponse.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFF3F4F6), RoundedCornerShape(6.dp))
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = "Phản hồi người bán: ${latestReview.sellerResponse}",
                                            fontSize = 11.5.sp,
                                            color = Color(0xFF4B5563),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 8. Sticky Bottom Action Bar (Chat, Giỏ hàng, Thêm vào giỏ, Mua ngay)
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
                        .clickable { viewModel.openShopChat(shopName = product.shopName, product = product, sellerId = product.sellerId) },
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

                // Nút Giỏ hàng (không hiển thị số lượng)
                Column(
                    modifier = Modifier
                        .width(52.dp)
                        .clickable(onClick = onNavigateToCart),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Giỏ hàng",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
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
                initialSelectedOptionValues = selectedOptionValues.toMap(),
                initialQuantity = 1,
                mode = variantSheetMode,
                onDismissRequest = { showVariantSheet = false },
                onConfirm = { color, size, qty, mode ->
                    showVariantSheet = false
                    if (mode == VariantSheetMode.BUY_NOW) {
                        viewModel.buyNow(
                            product = product,
                            variant = currentVariant,
                            quantity = qty,
                            color = color
                        )
                    } else {
                        viewModel.addProductToCart(
                            product = product,
                            color = color,
                            size = size,
                            quantity = qty
                        )
                    }
                },
                onConfirmVariant = { chosenVariant, selectedSummary, chosenQuantity, mode ->
                    chosenVariant?.optionValueIds?.forEach { valId ->
                        product.options.find { opt -> opt.values.any { it.id == valId } }?.let { opt ->
                            selectedOptionValues[opt.id] = valId
                        }
                    }
                    showVariantSheet = false
                    if (mode == VariantSheetMode.BUY_NOW) {
                        viewModel.buyNow(
                            product = product,
                            variant = chosenVariant ?: currentVariant,
                            quantity = chosenQuantity,
                            color = selectedSummary
                        )
                    } else {
                        viewModel.addProductToCart(
                            product = product,
                            color = selectedSummary,
                            size = "",
                            quantity = chosenQuantity,
                            variant = chosenVariant
                        )
                    }
                }
            )
        }

        // Popup ModalBottomSheet chọn mã giảm giá / voucher
        if (showVoucherSelectionSheet) {
            val effectivePrice = (currentVariant?.price ?: product.price).toDouble()
            VoucherSelectionBottomSheet(
                promotions = uiState.availablePromotions,
                selectedVoucher = uiState.selectedVoucher,
                orderSubtotal = effectivePrice,
                isLoading = uiState.isPromotionsLoading,
                onDismissRequest = { showVoucherSelectionSheet = false },
                onSelectVoucher = { chosen ->
                    viewModel.selectVoucher(chosen)
                    showVoucherSelectionSheet = false
                },
                onApplyCustomCode = { code ->
                    viewModel.applyVoucherByCode(code) { success, _ ->
                        if (success) {
                            showVoucherSelectionSheet = false
                        }
                    }
                },
                calculateDiscount = { promo, subtotal ->
                    viewModel.computeDiscountAmount(promo, subtotal)
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
