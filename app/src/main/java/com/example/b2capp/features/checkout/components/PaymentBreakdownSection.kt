package com.example.b2capp.features.checkout.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.core.designsystem.theme.CoralPrimary

/**
 * Dòng chi tiết thanh toán đơn lẻ
 */
@Composable
fun PaymentBreakdownRow(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF374151)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF6B7280)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

/**
 * Khối tóm tắt toàn bộ chi tiết thanh toán và tổng cuối cùng
 */
@Composable
fun PaymentBreakdownSection(
    itemsSubtotalFormatted: String,
    shippingFeeFormatted: String,
    shippingDiscountFormatted: String,
    voucherDiscountFormatted: String?,
    coinsDiscountFormatted: String,
    finalTotalFormatted: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Chi tiết thanh toán",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        Spacer(modifier = Modifier.height(10.dp))

        PaymentBreakdownRow(
            label = "Tổng tiền hàng",
            value = itemsSubtotalFormatted
        )
        Spacer(modifier = Modifier.height(6.dp))
        PaymentBreakdownRow(
            label = "Tổng tiền phí vận chuyển",
            value = shippingFeeFormatted
        )
        Spacer(modifier = Modifier.height(6.dp))
        PaymentBreakdownRow(
            label = "Giảm giá phí vận chuyển",
            value = shippingDiscountFormatted,
            valueColor = Color(0xFF00897B)
        )
        if (voucherDiscountFormatted != null) {
            Spacer(modifier = Modifier.height(6.dp))
            PaymentBreakdownRow(
                label = "Giảm giá voucher sàn",
                value = voucherDiscountFormatted,
                valueColor = Color(0xFF00897B)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        PaymentBreakdownRow(
            label = "Dùng Xu B2C",
            value = coinsDiscountFormatted,
            valueColor = Color(0xFF00897B)
        )

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        Spacer(modifier = Modifier.height(10.dp))

        // Hàng Tổng thanh toán
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tổng thanh toán",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = finalTotalFormatted,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CoralPrimary
                )
                Text(
                    text = "(Đã bao gồm VAT)",
                    fontSize = 10.5.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
        }
    }
}

