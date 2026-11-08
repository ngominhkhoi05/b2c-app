package com.example.b2capp.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.core.designsystem.theme.B2CAppTheme
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.ErrorRed
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary

/**
 * AppTextField dùng chung cho Buyer App:
 * - Bo góc 8.dp
 * - Nền #FFFFFF, viền mặc định #E5E7EB
 * - Khi focus: viền đổi sang #EE4D2D
 * - Khi lỗi: viền đổi sang #D32F2F kèm dòng thông báo lỗi phía dưới
 * - Hỗ trợ ẩn/hiện mật khẩu nếu isPassword = true
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    testTag: String = "app_text_field"
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor = when {
        isError -> ErrorRed
        isFocused -> CoralPrimary
        else -> BorderDivider
    }

    val borderWidth = if (isFocused || isError) 1.5.dp else 1.dp
    val shape = RoundedCornerShape(8.dp)

    Column(modifier = modifier) {
        if (!label.isNullOrBlank()) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isError) ErrorRed else TextPrimary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(color = SurfaceCard, shape = shape)
                .border(width = borderWidth, color = borderColor, shape = shape)
                .padding(horizontal = 12.dp)
                .testTag(testTag),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Box(
                    modifier = Modifier.padding(end = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    leadingIcon()
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty() && !placeholder.isNullOrBlank()) {
                    Text(
                        text = placeholder,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled,
                    singleLine = singleLine,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Normal
                    ),
                    cursorBrush = SolidColor(CoralPrimary),
                    interactionSource = interactionSource,
                    visualTransformation = if (isPassword && !passwordVisible) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions
                )
            }

            if (isPassword) {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("${testTag}_toggle_password")
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (isError && !errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                color = ErrorRed,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .testTag("${testTag}_error_message")
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppTextFieldPreview() {
    var text by remember { mutableStateOf("0912345678") }
    var password by remember { mutableStateOf("MatKhau123") }

    B2CAppTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            AppTextField(
                value = text,
                onValueChange = { text = it },
                label = "Số điện thoại",
                placeholder = "Nhập số điện thoại của bạn",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppTextField(
                value = password,
                onValueChange = { password = it },
                label = "Mật khẩu",
                placeholder = "Nhập mật khẩu",
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppTextField(
                value = "invalid-email",
                onValueChange = {},
                label = "Email",
                placeholder = "email@example.com",
                isError = true,
                errorMessage = "Email không đúng định dạng. Vui lòng kiểm tra lại.",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
