package com.example.b2capp.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.b2capp.core.designsystem.theme.B2CAppTheme
import com.example.b2capp.core.designsystem.theme.BackgroundApp
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.CoralPrimaryContainer
import com.example.b2capp.core.designsystem.theme.ErrorRed
import com.example.b2capp.core.designsystem.theme.FreeShippingAccent
import com.example.b2capp.core.designsystem.theme.StarRatingGold
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import com.example.b2capp.core.model.ProductItem

/**
 * Thẻ sản phẩm dùng chung chuẩn B2C Buyer App:
 * - Tái sử dụng xuyên suốt toàn app: Home, Catalog/Search, Orders, Wishlist.
 * - Ảnh 1:1 linh hoạt (URL qua Coil hoặc Image Resource).
 * - Hỗ trợ các loại Badge: Freeship Xtra, Yêu thích, Mall, Giảm giá %.
 * - Hỗ trợ nút hành động Yêu thích (Wishlist heart) hoặc Bottom Action tuỳ biến.
 */
@Composable
fun ProductCard(
    product: ProductItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeText: String? = null,
    badgeColor: Color? = null,
    imageRes: Int? = null,
    showWishlistAction: Boolean = false,
    isWishlisted: Boolean = false,
    onWishlistClick: (() -> Unit)? = null,
    locationText: String? = null,
    customDiscountTag: String? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    testTag: String = "product_card_${product.id}"
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .border(
                width = 0.8.dp,
                color = BorderDivider.copy(alpha = 0.7f),
                shape = RoundedCornerShape(10.dp)
            )
            .testTag(testTag),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Ảnh tỷ lệ 1:1 kèm các Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                    .background(BackgroundApp)
            ) {
                if (imageRes != null) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    SubcomposeAsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(BackgroundApp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ShoppingBag,
                                    contentDescription = null,
                                    tint = BorderDivider,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFF1F3F5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ShoppingBag,
                                    contentDescription = null,
                                    tint = BorderDivider,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    )
                }

                // Huy hiệu góc trên trái: Badge tuỳ biến hoặc Freeship / Yêu thích / Mall
                if (badgeText != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(
                                color = badgeColor ?: FreeShippingAccent,
                                shape = RoundedCornerShape(bottomEnd = 6.dp, topStart = 10.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else if (product.isFreeShipping) {
                    FreeShippingBadge(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(6.dp)
                    )
                }

                // Góc trên phải: Nút Wishlist HOẶC Tag giảm giá
                if (showWishlistAction) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(28.dp)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                            .clickable { onWishlistClick?.invoke() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Yêu thích",
                            tint = if (isWishlisted) ErrorRed else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else if (customDiscountTag != null) {
                    DiscountTextBadge(
                        discountText = customDiscountTag,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                    )
                } else {
                    product.discountPercent?.let { discount ->
                        DiscountBadge(
                            percent = discount,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        )
                    }
                }
            }

            // 2. Nội dung thông tin sản phẩm
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                // Tên sản phẩm hiển thị chuẩn 2 hàng, tự động xuống dòng và có dấu chấm lửng ở cuối nếu vượt quá 2 hàng
                Text(
                    text = product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Giá bán nổi bật & giá gốc
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = product.formattedPrice,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoralPrimary
                    )

                    product.formattedOriginalPrice?.let { origPrice ->
                        Text(
                            text = origPrice,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Đánh giá sao & Đã bán / Địa điểm
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Đánh giá",
                            tint = StarRatingGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = String.format(java.util.Locale.US, "%.1f", product.rating),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }

                    if (locationText != null) {
                        Text(
                            text = locationText,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    } else {
                        Text(
                            text = product.formattedSoldCount,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                bottomAction?.let { action ->
                    Spacer(modifier = Modifier.height(6.dp))
                    action()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductCardPreview() {
    val sampleProduct = ProductItem(
        id = "1",
        name = "Áo Thun Polo Nam Phối Bo Cổ Dệt Basic Co Giãn Thoáng Khí Cao Cấp",
        imageUrl = "",
        price = 149000L,
        originalPrice = 220000L,
        rating = 4.9f,
        soldCount = 3800,
        isFreeShipping = true
    )

    B2CAppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ProductCard(
                product = sampleProduct,
                onClick = {},
                modifier = Modifier.width(170.dp)
            )
        }
    }
}
