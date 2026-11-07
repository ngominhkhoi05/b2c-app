package com.example.b2capp.features.cart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary

/**
 * Thanh tổng kết thanh toán cố định ở đáy giỏ hàng (Sticky Bottom Checkout Bar)
 */
@Composable
fun CartBottomSummaryBar(
    currentShopLabel: String,
    formattedTotalPrice: String,
    formattedSavings: String,
    totalItemsCount: Int,
    isEditMode: Boolean,
    onCheckoutClick: () -> Unit,
    isAllSelected: Boolean = true,
    onSelectAllClick: () -> Unit = {}
) {
    Surface(
        color = SurfaceCard,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 10.dp)
            .border(0.8.dp, BorderDivider)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox Tất cả
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onSelectAllClick)
            ) {
                RoundCheckbox(
                    isChecked = isAllSelected,
                    onClick = onSelectAllClick
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Tất cả",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = currentShopLabel,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Cụm Tổng tiền & Tiết kiệm
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "Tổng: ",
                        fontSize = 11.5.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = formattedTotalPrice,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBA2211)
                    )
                }
                Text(
                    text = "Tiết kiệm $formattedSavings",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E8275)
                )
            }

            // Nút MUA HÀNG (X)
            Box(
                modifier = Modifier
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFBA2211))
                    .clickable(onClick = onCheckoutClick)
                    .padding(horizontal = 16.dp)
                    .testTag("btn_cart_checkout"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isEditMode) "XÓA ($totalItemsCount)" else "MUA HÀNG ($totalItemsCount)",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

