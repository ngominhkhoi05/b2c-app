package com.example.b2capp.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.B2CAppTheme
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * EmptyStateView - Giao diện trạng thái trống có minh họa cho Buyer App:
 * - Icon minh họa lớn màu xám nhạt trong vòng tròn nhẹ
 * - Tiêu đề thông báo (16sp Bold)
 * - Dòng mô tả (14sp #888888)
 * - Nút hành động kêu gọi CTA (ví dụ: "Tiếp tục mua sắm")
 */
@Composable
fun EmptyStateView(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.ShoppingCart,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    testTag: String = "empty_state_view"
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon minh họa lớn màu xám nhạt
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(
                    color = BorderDivider.copy(alpha = 0.4f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary.copy(alpha = 0.6f),
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tiêu đề thông báo (16sp Bold)
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dòng mô tả (14sp #888888)
        Text(
            text = description,
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Nút hành động kêu gọi (Call to Action)
        if (!actionText.isNullOrBlank() && onActionClick != null) {
            Spacer(modifier = Modifier.height(24.dp))
            AppButton(
                text = actionText,
                onClick = onActionClick,
                variant = ButtonVariant.PRIMARY,
                modifier = Modifier.testTag("${testTag}_cta_button")
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStateViewPreview() {
    B2CAppTheme {
        EmptyStateView(
            title = "Giỏ hàng của bạn đang trống",
            description = "Hãy dạo xem hàng nghìn sản phẩm ưu đãi hấp dẫn và chọn mua ngay nhé!",
            icon = Icons.Outlined.RemoveShoppingCart,
            actionText = "Tiếp tục mua sắm",
            onActionClick = {}
        )
    }
}
