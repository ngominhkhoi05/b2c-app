package com.example.b2capp.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.core.designsystem.theme.B2CAppTheme
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.CoralPrimaryPressed
import com.example.b2capp.core.designsystem.theme.NavySecondary

/**
 * Biến thể của AppButton theo thiết kế B2C:
 * - PRIMARY: Nền Cam san hô (#EE4D2D), chữ trắng.
 * - SECONDARY: Nền Xanh Navy (#0E3B64), chữ trắng.
 * - OUTLINED: Nền trong suốt, viền mảnh 1.dp #EE4D2D, chữ #EE4D2D.
 */
enum class ButtonVariant {
    PRIMARY,
    SECONDARY,
    OUTLINED
}

/**
 * AppButton dùng chung cho Buyer App tuân thủ M3 & Design System:
 * - Chiều cao tối thiểu: 48.dp
 * - Bo góc: 8.dp
 * - Xử lý trạng thái Loading hiển thị CircularProgressIndicator (20.dp)
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    leadingIcon: (@Composable () -> Unit)? = null,
    testTag: String = "app_button"
) {
    val shape = RoundedCornerShape(8.dp)
    val minHeight = 48.dp

    when (variant) {
        ButtonVariant.PRIMARY -> {
            Button(
                onClick = { if (!isLoading) onClick() },
                modifier = modifier
                    .defaultMinSize(minHeight = minHeight)
                    .testTag(testTag),
                enabled = enabled && !isLoading,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CoralPrimary,
                    contentColor = Color.White,
                    disabledContainerColor = BorderDivider,
                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                ButtonContent(
                    text = text,
                    isLoading = isLoading,
                    indicatorColor = Color.White,
                    leadingIcon = leadingIcon
                )
            }
        }
        ButtonVariant.SECONDARY -> {
            Button(
                onClick = { if (!isLoading) onClick() },
                modifier = modifier
                    .defaultMinSize(minHeight = minHeight)
                    .testTag(testTag),
                enabled = enabled && !isLoading,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavySecondary,
                    contentColor = Color.White,
                    disabledContainerColor = BorderDivider,
                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                ButtonContent(
                    text = text,
                    isLoading = isLoading,
                    indicatorColor = Color.White,
                    leadingIcon = leadingIcon
                )
            }
        }
        ButtonVariant.OUTLINED -> {
            OutlinedButton(
                onClick = { if (!isLoading) onClick() },
                modifier = modifier
                    .defaultMinSize(minHeight = minHeight)
                    .testTag(testTag),
                enabled = enabled && !isLoading,
                shape = shape,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (enabled && !isLoading) CoralPrimary else BorderDivider
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = CoralPrimary,
                    disabledContentColor = Color.Gray
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                ButtonContent(
                    text = text,
                    isLoading = isLoading,
                    indicatorColor = CoralPrimary,
                    leadingIcon = leadingIcon
                )
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    isLoading: Boolean,
    indicatorColor: Color,
    leadingIcon: (@Composable () -> Unit)?
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(20.dp)
                .testTag("app_button_loading"),
            color = indicatorColor,
            strokeWidth = 2.dp
        )
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.2.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppButtonPreview() {
    B2CAppTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp)
        ) {
            AppButton(
                text = "Mua ngay",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.PRIMARY
            )
            Spacer(modifier = Modifier.size(12.dp))
            AppButton(
                text = "Đang xử lý...",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                isLoading = true,
                variant = ButtonVariant.PRIMARY
            )
            Spacer(modifier = Modifier.size(12.dp))
            AppButton(
                text = "Xem Shop",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.SECONDARY
            )
            Spacer(modifier = Modifier.size(12.dp))
            AppButton(
                text = "Thêm vào giỏ hàng",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.OUTLINED
            )
        }
    }
}
