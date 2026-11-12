package com.example.b2capp.features.auth

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.b2capp.core.auth.GoogleAuthHelper
import kotlinx.coroutines.launch
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.designsystem.theme.BackgroundApp
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary

/**
 * Màn hình Đăng Nhập (SignInScreen.kt)
 * Được thiết kế chính xác 100% theo ảnh SignInScreen_dc4dccd4.png:
 * 1. Top Bar: Nút Back vuông góc trái, tiêu đề "ĐĂNG NHẬP" chữ in hoa xám nhạt ở giữa.
 * 2. Hero Logo Badge: Khung vuông hồng nhạt bo góc với icon Túi Mua Sắm màu đỏ ở giữa.
 * 3. Tiêu đề: "Chào mừng trở lại!" và phụ đề "Đăng nhập để tiếp tục trải nghiệm mua sắm".
 * 4. Thẻ Khung Đăng Nhập màu trắng:
 *    - Trường "Email hoặc số điện thoại" kèm icon thư.
 *    - Trường "Mật khẩu" kèm icon khóa & nút xem/ẩn mắt.
 *    - Link "Quên mật khẩu?" căn phải.
 *    - Nút bấm nổi bật "ĐĂNG NHẬP →" màu đỏ cam (#BA2211) bo góc 10.dp.
 *    - Dải phân cách "HOẶC ĐĂNG NHẬP VỚI".
 *    - Hàng 2 nút đăng nhập mạng xã hội: Google & Apple.
 * 5. Chân trang Footer: "Chưa có tài khoản? Đăng ký ngay" (gạch chân màu đỏ).
 */
@Composable
fun SignInScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onSignInSuccess: () -> Unit,
    onSkipClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var emailOrPhone by remember { mutableStateOf("nguyenvana@gmail.com") }
    var password by remember { mutableStateOf("12345678") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("sign_in_screen"),
        topBar = {
            Surface(
                color = SurfaceCard,
                shadowElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ĐĂNG NHẬP",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280),
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF9FAFB)),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))

                // 2. Hero Logo Badge: Khung vuông hồng nhạt bo góc với icon túi mua sắm
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFDECEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingBag,
                        contentDescription = "Logo",
                        tint = Color(0xFFBA2211),
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Tiêu đề "Chào mừng trở lại!" & Phụ đề
                Text(
                    text = "Chào mừng trở lại!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Đăng nhập để tiếp tục trải nghiệm mua sắm",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // 4. Thẻ Khung Đăng Nhập
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(0.8.dp, Color(0xFFE5E7EB)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        // Trường 1: Email hoặc số điện thoại
                        Text(
                            text = "Email hoặc số điện thoại",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF9FAFB))
                                .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Box(modifier = Modifier.weight(1f)) {
                                if (emailOrPhone.isEmpty()) {
                                    Text(
                                        text = "Email hoặc số điện thoại",
                                        fontSize = 14.sp,
                                        color = Color(0xFF9CA3AF)
                                    )
                                }
                                BasicTextField(
                                    value = emailOrPhone,
                                    onValueChange = { emailOrPhone = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    textStyle = TextStyle(
                                        fontSize = 14.sp,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    cursorBrush = SolidColor(Color(0xFFBA2211))
                                )
                            }

                            if (emailOrPhone.isNotEmpty()) {
                                IconButton(
                                    onClick = { emailOrPhone = "" },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = "Xóa",
                                        tint = Color(0xFF9CA3AF),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Trường 2: Mật khẩu
                        Text(
                            text = "Mật khẩu",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF9FAFB))
                                .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Box(modifier = Modifier.weight(1f)) {
                                if (password.isEmpty()) {
                                    Text(
                                        text = "Mật khẩu",
                                        fontSize = 14.sp,
                                        color = Color(0xFF9CA3AF)
                                    )
                                }
                                BasicTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    textStyle = TextStyle(
                                        fontSize = 14.sp,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    cursorBrush = SolidColor(Color(0xFFBA2211))
                                )
                            }

                            IconButton(
                                onClick = { isPasswordVisible = !isPasswordVisible },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = "Ẩn/hiện mật khẩu",
                                    tint = Color(0xFF9CA3AF),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Link "Quên mật khẩu?" căn phải
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Quên mật khẩu?",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NavySecondary,
                                modifier = Modifier.clickable {
                                    viewModel.openForgotPassword()
                                }
                            )
                        }

                        // Hiển thị thông báo lỗi nếu có
                        if (!uiState.authErrorMessage.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = uiState.authErrorMessage,
                                color = Color(0xFFBA2211),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Nút ĐĂNG NHẬP →
                        Button(
                            onClick = {
                                viewModel.loginWithApi(
                                    email = emailOrPhone,
                                    password = password,
                                    onSuccess = onSignInSuccess
                                )
                            },
                            enabled = !uiState.isAuthLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("submit_login_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFBA2211),
                                disabledContainerColor = Color(0xFFBA2211).copy(alpha = 0.6f)
                            )
                        ) {
                            if (uiState.isAuthLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "ĐĂNG NHẬP",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Dải phân cách "HOẶC ĐĂNG NHẬP VỚI"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFE5E7EB),
                                thickness = 0.8.dp
                            )
                            Text(
                                text = "HOẶC ĐĂNG NHẬP VỚI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9CA3AF),
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFE5E7EB),
                                thickness = 0.8.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Hàng 2 Nút Đăng nhập bằng Google & Apple
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Nút Google
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White)
                                    .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
                                    .clickable {
                                        coroutineScope.launch {
                                            val result = GoogleAuthHelper.signInWithGoogle(context)
                                            result.fold(
                                                onSuccess = { idToken ->
                                                    viewModel.loginWithGoogle(idToken) {
                                                        onSignInSuccess()
                                                    }
                                                },
                                                onFailure = { error ->
                                                    val msg = error.message ?: "Đăng nhập Google thất bại"
                                                    if (!msg.contains("huỷ", ignoreCase = true)) {
                                                        viewModel.showErrorToast(msg)
                                                    }
                                                }
                                            )
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    GoogleLogoIcon(modifier = Modifier.size(19.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Google",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                }
                            }

                            // Nút Apple (đã làm mờ và vô hiệu hoá)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF3F4F6))
                                    .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
                                    .alpha(0.4f),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AppleLogoIcon(modifier = Modifier.size(19.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Apple",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Chân trang Footer
            item {
                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    Text(
                        text = "Chưa có tài khoản? ",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Đăng ký ngay",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBA2211),
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            viewModel.openSignUp()
                        }
                    )
                }
            }
        }
    }
}
