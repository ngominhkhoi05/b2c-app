package com.example.b2capp.features.cart.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.ErrorRed
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary

/**
 * Thanh tổng kết thanh toán cố định ở đáy giỏ hàng (Sticky Bottom Checkout Bar)
 * Đã bỏ nút chọn "Tất cả", phần tổng tiền được tinh chỉnh trực quan, nổi bật và đẹp mắt.
 */
@Composable
fun CartBottomSummaryBar(
    currentShopLabel: String = "",
    formattedTotalPrice: String,
    formattedSavings: String = "",
    totalItemsCount: Int,
    isEditMode: Boolean = false,
    onCheckoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    isAllSelected: Boolean = true,
    onSelectAllClick: () -> Unit = {}
) {
    Surface(
        color = SurfaceCard,
        shadowElevation = 8.dp,
        border = BorderStroke(0.8.dp, BorderDivider),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Phần hiển thị Tổng tiền tinh chỉnh đẹp mắt
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tổng thanh toán",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                    if (totalItemsCount > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "($totalItemsCount)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = formattedTotalPrice,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CoralPrimary
                    )
                    if (formattedSavings.isNotBlank() && formattedSavings != "₫0") {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tiết kiệm $formattedSavings",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF00897B),
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Nút MUA HÀNG / XÓA
            Box(
                modifier = Modifier
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isEditMode) ErrorRed else CoralPrimary
                    )
                    .clickable(onClick = onCheckoutClick)
                    .padding(horizontal = 22.dp)
                    .testTag("btn_cart_checkout"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isEditMode) {
                        "XÓA ($totalItemsCount)"
                    } else {
                        if (totalItemsCount > 0) "MUA HÀNG ($totalItemsCount)" else "MUA HÀNG"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.4.sp
                )
            }
        }
    }
}
