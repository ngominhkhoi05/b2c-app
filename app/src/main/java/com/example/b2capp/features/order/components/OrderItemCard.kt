package com.example.b2capp.features.order.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.R
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary

/**
 * Dữ liệu hiển thị thẻ đơn hàng
 */
data class OrderItemCardData(
    val orderId: String,
    val rawOrderId: Long? = null,
    val shopName: String,
    val isMall: Boolean = true,
    val statusText: String,
    val statusColor: Color = Color(0xFF1E8275),
    val statusBgColor: Color = Color(0xFFE6F7F5),
    val imageRes: Int = R.drawable.folded_black_tshirt_1793956682242,
    val imageUrl: String? = null,
    val productName: String,
    val variantText: String,
    val quantityText: String,
    val priceText: String,
    val deliveryBannerText: String? = null,
    val deliveryBannerIconTint: Color = Color(0xFF1E8275),
    val totalCountText: String,
    val totalPriceText: String
)

/**
 * Thẻ hiển thị một đơn hàng trong danh sách đơn mua
 */
@Composable
fun OrderItemCard(
    data: OrderItemCardData,
    onShopClick: () -> Unit,
    actionsSlot: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Shop Name & Status Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (data.isMall) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFB82414), RoundedCornerShape(3.dp))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Mall",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = "${data.shopName} >",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavySecondary,
                        modifier = Modifier.clickable(onClick = onShopClick)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(data.statusBgColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = data.statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = data.statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Thông tin sản phẩm đại diện
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                if (!data.imageUrl.isNullOrBlank()) {
                    SubcomposeAsyncImage(
                        model = data.imageUrl,
                        contentDescription = data.productName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp)),
                        loading = {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .background(Color(0xFFF3F4F6)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = CoralPrimary
                                )
                            }
                        },
                        error = {
                            Image(
                                painter = painterResource(id = data.imageRes),
                                contentDescription = data.productName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                    )
                } else {
                    Image(
                        painter = painterResource(id = data.imageRes),
                        contentDescription = data.productName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = data.productName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF3F4F6), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = data.variantText,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = data.quantityText,
                            fontSize = 11.5.sp,
                            color = TextSecondary
                        )

                        Text(
                            text = data.priceText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Banner tiến trình vận chuyển (nếu có)
            if (data.deliveryBannerText != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF8F9FA))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocalShipping,
                            contentDescription = null,
                            tint = data.deliveryBannerIconTint,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = data.deliveryBannerText,
                            fontSize = 11.5.sp,
                            color = Color(0xFF4B5563),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hàng Thành tiền
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = data.totalCountText,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 1.dp)
                )
                Text(
                    text = data.totalPriceText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBA2211)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Khối nút hành động
            actionsSlot()
        }
    }
}

