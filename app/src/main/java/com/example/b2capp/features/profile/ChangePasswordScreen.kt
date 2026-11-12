package com.example.b2capp.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary

/**
 * Màn hình Đổi Mật Khẩu (ChangePasswordScreen)
 * Thiết kế chính xác 100% theo ChangePasswordScreen_4c148bb4.png:
 * 1. Top Bar: Back icon, Tiêu đề "Đổi Mật Khẩu", Avatar tài khoản đỏ.
 * 2. Thẻ Thông Báo Bảo Vệ Tài Khoản:
 *    - Icon khiên xanh, "Bảo vệ tài khoản của bạn. Thường xuyên đổi mật khẩu giúp bảo vệ thông tin thanh toán...".
 * 3. Thẻ Form Đổi Mật Khẩu:
 *    - Mật khẩu hiện tại (Icon Lock, Link "Quên mật khẩu?", nút bật/tắt ẩn hiện mắt).
 *    - Mật khẩu mới (Icon Key, nút ẩn/hiện mắt).
 *    - Thanh chỉ số Độ an toàn: "● Rất mạnh" + 3 thanh xanh lá.
 *    - Danh sách tiêu chuẩn mật khẩu (✔ Tối thiểu 8 ký tự, ✔ Có chữ hoa & thường, ✔ Chứa ít nhất 1 chữ số hoặc ký tự đặc biệt).
 *    - Xác nhận mật khẩu mới + Nhãn "✔ Khớp mật khẩu" (xanh lá).
 * 4. Thẻ Tùy chọn Đăng xuất thiết bị khác:
 *    - Khuyên dùng khi nghi ngờ rò rỉ tài khoản.
 * 5. Nút Cập Nhật Mật Khẩu:
 *    - Nút đỏ "↻ CẬP NHẬT MẬT KHẨU".
 *    - Subtext "🛡 Được bảo vệ bởi mã hóa chuẩn SSL 256–bit".
 */
@Composable
fun ChangePasswordScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Kiểm tra tiêu chuẩn mật khẩu mới
    val hasMinLength = newPassword.length >= 8
    val hasUpperAndLower = newPassword.any { it.isUpperCase() } && newPassword.any { it.isLowerCase() }
    val hasDigitOrSpecial = newPassword.any { it.isDigit() || !it.isLetterOrDigit() }
    val isPasswordMatching = newPassword.isNotEmpty() && newPassword == confirmPassword

    // Tính toán mức độ mạnh của mật khẩu mới
    val criteriaCount = (if (hasMinLength) 1 else 0) +
            (if (hasUpperAndLower) 1 else 0) +
            (if (hasDigitOrSpecial) 1 else 0)

    val (strengthLabel, strengthColor, activeBars) = when {
        newPassword.isEmpty() -> Triple("Chưa nhập", Color(0xFF9CA3AF), 0)
        criteriaCount == 1 -> Triple("Yếu", Color(0xFFDC2626), 1)
        criteriaCount == 2 -> Triple("Trung bình", Color(0xFFD97706), 2)
        else -> Triple("Rất mạnh", Color(0xFF15803D), 3)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("change_password_screen"),
        topBar = {
            Surface(
                color = SurfaceCard,
                shadowElevation = 0.5.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Đổi Mật Khẩu",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA)),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 2. Thẻ Thông Báo Bảo Vệ Tài Khoản
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFEFF6FF))
                        .border(0.8.dp, Color(0xFFDBEAFE), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFDBEAFE), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Security,
                                contentDescription = null,
                                tint = Color(0xFF1E40AF),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Bảo vệ tài khoản của bạn",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Thường xuyên đổi mật khẩu giúp bảo vệ thông tin thanh toán và đơn hàng của bạn tốt hơn.",
                                fontSize = 12.sp,
                                color = Color(0xFF1E3A8A),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // 3. Thẻ Form Đổi Mật Khẩu
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Field 1: Mật khẩu hiện tại
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mật khẩu hiện tại",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )

                            Text(
                                text = "Quên mật khẩu?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBA2211),
                                modifier = Modifier.clickable {
                                    viewModel.openForgotPassword()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF3F4F6))
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))

                            BasicTextField(
                                value = oldPassword,
                                onValueChange = { oldPassword = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                textStyle = TextStyle(
                                    fontSize = 14.5.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(Color(0xFFBA2211)),
                                decorationBox = { innerTextField ->
                                    if (oldPassword.isEmpty()) {
                                        Text(
                                            text = "Nhập mật khẩu hiện tại",
                                            fontSize = 13.5.sp,
                                            color = Color(0xFF9CA3AF)
                                        )
                                    }
                                    innerTextField()
                                }
                            )

                            IconButton(
                                onClick = { oldPasswordVisible = !oldPasswordVisible },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (oldPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = "Ẩn hiện mật khẩu",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Field 2: Mật khẩu mới
                        Text(
                            text = "Mật khẩu mới",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF3F4F6))
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Key,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))

                            BasicTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                textStyle = TextStyle(
                                    fontSize = 14.5.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(Color(0xFFBA2211)),
                                decorationBox = { innerTextField ->
                                    if (newPassword.isEmpty()) {
                                        Text(
                                            text = "Nhập mật khẩu mới (tối thiểu 8 ký tự)",
                                            fontSize = 13.5.sp,
                                            color = Color(0xFF9CA3AF)
                                        )
                                    }
                                    innerTextField()
                                }
                            )

                            IconButton(
                                onClick = { newPasswordVisible = !newPasswordVisible },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (newPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = "Ẩn hiện mật khẩu",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Thanh chỉ số độ an toàn
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Độ an toàn:",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(strengthColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = strengthLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = strengthColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // 3 Thanh tiến trình phản hồi theo độ mạnh mật khẩu
                        val activeBarColor = when (activeBars) {
                            1 -> Color(0xFFDC2626)
                            2 -> Color(0xFFF59E0B)
                            3 -> Color(0xFF16A34A)
                            else -> Color(0xFFE5E7EB)
                        }
                        val inactiveBarColor = Color(0xFFE5E7EB)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (activeBars >= 1) activeBarColor else inactiveBarColor)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (activeBars >= 2) activeBarColor else inactiveBarColor)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (activeBars >= 3) activeBarColor else inactiveBarColor)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Checklist danh sách tiêu chuẩn
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF3F4F6))
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                RequirementItem(text = "Tối thiểu 8 ký tự", isChecked = hasMinLength)
                                RequirementItem(text = "Có chữ hoa (A–Z) và chữ thường (a–z)", isChecked = hasUpperAndLower)
                                RequirementItem(text = "Chứa ít nhất 1 chữ số hoặc ký tự đặc biệt", isChecked = hasDigitOrSpecial)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Field 3: Xác nhận mật khẩu mới
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Xác nhận mật khẩu mới",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )

                            if (isPasswordMatching) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF15803D),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Khớp mật khẩu",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF3F4F6))
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.VerifiedUser,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))

                            BasicTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                textStyle = TextStyle(
                                    fontSize = 14.5.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(Color(0xFFBA2211)),
                                decorationBox = { innerTextField ->
                                    if (confirmPassword.isEmpty()) {
                                        Text(
                                            text = "Nhập lại mật khẩu mới",
                                            fontSize = 13.5.sp,
                                            color = Color(0xFF9CA3AF)
                                        )
                                    }
                                    innerTextField()
                                }
                            )

                            IconButton(
                                onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = "Ẩn hiện mật khẩu",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Nút Cập Nhật Mật Khẩu
            item {
                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        enabled = !uiState.isChangingPassword,
                        onClick = {
                            val cleanOld = oldPassword.trim()
                            val cleanNew = newPassword.trim()
                            val cleanConfirm = confirmPassword.trim()

                            if (cleanOld.isBlank()) {
                                viewModel.showErrorToast("Vui lòng nhập mật khẩu hiện tại")
                                return@Button
                            }
                            if (cleanNew.isBlank()) {
                                viewModel.showErrorToast("Vui lòng nhập mật khẩu mới")
                                return@Button
                            }
                            if (cleanNew.length < 8) {
                                viewModel.showErrorToast("Mật khẩu mới phải có tối thiểu 8 ký tự")
                                return@Button
                            }
                            if (cleanNew == cleanOld) {
                                viewModel.showErrorToast("Mật khẩu mới không được trùng với mật khẩu hiện tại")
                                return@Button
                            }
                            if (cleanConfirm.isBlank()) {
                                viewModel.showErrorToast("Vui lòng xác nhận mật khẩu mới")
                                return@Button
                            }
                            if (cleanNew != cleanConfirm) {
                                viewModel.showErrorToast("Mật khẩu xác nhận không khớp!")
                                return@Button
                            }
                            viewModel.changePassword(
                                oldPass = cleanOld,
                                newPass = cleanNew,
                                confirmPass = cleanConfirm
                            ) { success ->
                                if (success) {
                                    onBackClick()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFBA2211),
                            disabledContainerColor = Color(0xFFBA2211).copy(alpha = 0.6f)
                        )
                    ) {
                        if (uiState.isChangingPassword) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ĐANG CẬP NHẬT...",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Cập nhật",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CẬP NHẬT MẬT KHẨU",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Được bảo vệ bởi mã hóa chuẩn SSL 256–bit",
                            fontSize = 11.5.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RequirementItem(
    text: String,
    isChecked: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = if (isChecked) Color(0xFF16A34A) else Color(0xFF9CA3AF),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 11.5.sp,
            color = if (isChecked) Color(0xFF15803D) else Color(0xFF6B7280),
            fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
