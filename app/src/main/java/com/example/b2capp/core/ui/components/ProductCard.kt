package com.example.b2capp.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.b2capp.model.ProductItem
import com.example.ui.theme.B2CAppTheme
import com.example.ui.theme.BackgroundApp
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.CoralPrimaryContainer
import com.example.ui.theme.FreeShippingAccent
import com.example.ui.theme.StarRatingGold
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Thẻ sản phẩm chuẩn B2C Buyer App:
 * - Kích thước lưới chuẩn (~170.dp hoặc fillMaxWidth trong Adaptive Grid)
 * - Ảnh 1:1 bo góc trên 12.dp (Coil SubcomposeAsyncImage)
 * - Tên sản phẩm tối đa 2 dòng (14sp)
 * - Hàng giá: Giá hiện tại nổi bật (#EE4D2D, 16sp Bold), Giá gốc gạch ngang mờ
 * - Hàng phụ: Điểm sao (#FFB800) kèm số lượng "Đã bán X"
 * - Nền #FFFFFF, bo góc 12.dp, viền 1.dp #E5E7EB hoặc shadow nhẹ
 */
@Composable
fun ProductCard(
    product: ProductItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "product_card_${product.id}"
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = BorderDivider,
                shape = RoundedCornerShape(12.dp)
            )
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Ảnh tỷ lệ 1:1, bo góc trên 12.dp kèm Tag Giảm giá & Freeship
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(BackgroundApp)
            ) {
                SubcomposeAsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                    loading = {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
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
                                .matchParentSize()
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

                // Tag Freeship góc trái dưới của ảnh
                if (product.isFreeShipping) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(6.dp)
                            .background(
                                color = FreeShippingAccent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Freeship Xtra",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Tag Giảm Giá % ở góc phải trên
                product.discountPercent?.let { discount ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(
                                color = CoralPrimaryContainer,
                                shape = RoundedCornerShape(bottomStart = 8.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "-$discount%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CoralPrimary
                        )
                    }
                }
            }

            // 2. Nội dung chi tiết thẻ: Tên, Giá, Đánh giá & Đã bán
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                // Tên sản phẩm tối đa 2 dòng (14sp)
                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                    modifier = Modifier.height(36.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Hàng giá: Giá hiện tại (#EE4D2D, 16sp Bold) & Giá gạch ngang nếu có sale
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = product.formattedPrice,
                        fontSize = 16.sp,
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

                // Hàng phụ: Điểm sao (#FFB800) kèm số lượng "Đã bán X"
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

                    Text(
                        text = product.formattedSoldCount,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
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
