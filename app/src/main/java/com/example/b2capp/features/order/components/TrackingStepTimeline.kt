package com.example.b2capp.features.order.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary

/**
 * Item từng bước trong Timeline lộ trình (Chi tiết đơn hàng)
 */
@Composable
fun TimelineStepItem(
    stepIcon: ImageVector?,
    iconBg: Color,
    iconTint: Color,
    title: String,
    time: String,
    description: String,
    driverTag: String? = null,
    isLineActive: Boolean = false,
    isLast: Boolean = false,
    isTitleBold: Boolean = false,
    titleColor: Color = Color(0xFF111827)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Cột icon & đường nối xám dọc
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                if (stepIcon != null) {
                    Icon(
                        imageVector = stepIcon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(13.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD1D5DB))
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(if (driverTag != null) 64.dp else 46.dp)
                        .background(if (isLineActive) Color(0xFF3B82F6) else Color(0xFFE5E7EB))
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Cột nội dung chi tiết
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = if (isTitleBold) FontWeight.Bold else FontWeight.SemiBold,
                    color = titleColor
                )
                Text(
                    text = time,
                    fontSize = 11.5.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF6B7280),
                lineHeight = 16.5.sp
            )

            if (driverTag != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFBFDBFE))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "📄 $driverTag",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

/**
 * Dòng chi tiết phí trong thẻ Thông tin thanh toán
 */
@Composable
fun PaymentDetailLine(
    icon: ImageVector? = null,
    label: String,
    value: String,
    valueColor: Color = Color(0xFF374151)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF00897B),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = label,
                fontSize = 12.5.sp,
                color = Color(0xFF6B7280)
            )
        }

        Text(
            text = value,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

/**
 * Dòng thông số đơn hàng
 */
@Composable
fun OrderMetaRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )
    }
}

/**
 * Bước theo dõi vận chuyển rút gọn (Màn hình Danh sách Đơn hàng)
 */
@Composable
fun TrackingStep(
    title: String,
    subtitle: String,
    time: String,
    isActive: Boolean
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 3.dp)
                .size(10.dp)
                .background(if (isActive) Color(0xFF2563EB) else Color(0xFFD1D5DB), CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontSize = 12.5.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isActive) Color(0xFF1E3A8A) else TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = TextSecondary
            )
            Text(
                text = time,
                fontSize = 10.5.sp,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}

