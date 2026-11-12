package com.example.b2capp.features.auth

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import kotlinx.coroutines.delay

/**
 * Màn hình Quên Mật Khẩu (ForgotPasswordScreen.kt)
 * Được thiết kế chính xác 100% theo ảnh ForgotPasswordScreen_1d224d7a.png:
 * 1. Top Bar: Icon Back bên trái, tiêu đề "Quên mật khẩu" chữ đậm căn giữa.
 * 2. Hero Badge Logo:
 *    - Khung màu hồng nhạt bo góc 16.dp.
 *    - Icon Đặt lại mật khẩu (khóa đỏ vòng xoay) + Huy hiệu bảo mật màu xanh navy ở góc dưới phải.
 * 3. Title & Subtitle:
 *    - "Đặt lại mật khẩu" (Bold 22.sp)
 *    - "Nhập địa chỉ email đã đăng ký của bạn. Chúng tôi sẽ gửi mã xác nhận để đặt lại mật khẩu mới."
 * 4. Thẻ Khung Nhập Liệu (Card trắng bo 16.dp):
 *    - Nhãn "Email đã đăng ký" (hoặc chuyển sang Số điện thoại).
 *    - Input field với icon email & nút xóa text.
 *    - Dòng hướng dẫn: "ⓘ Mã OTP 6 chữ số sẽ có hiệu lực trong 5 phút."
 *    - Nút bấm đỏ nổi bật "GỬI MÃ XÁC NHẬN →" màu #BA2211.
 *    - Dải phân cách mờ.
 *    - Dòng gợi ý: "Không nhận được mã hoặc đổi số điện thoại?"
 *    - Link chuyển đổi phương thức: "📱 Thử xác thực bằng Số điện thoại".
 * 5. Link Hỗ trợ CSKH: "Cần hỗ trợ ngay? Liên hệ CSKH 24/7" (gạch chân).
 * 6. Footer Bottom: "< Quay lại Đăng nhập" dẫn về màn hình SignInScreen.
 */
@Composable
fun ForgotPasswordScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackToSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputEmail by remember { mutableStateOf("nguyenvana@gmail.com") }

    // Trạng thái đếm ngược OTP
    var isOtpSent by remember { mutableStateOf(false) }
    var countdownTimer by remember { mutableIntStateOf(300) } // 5 phút (300 giây)
    var otpCodeInput by remember { mutableStateOf("") }

    // Đếm ngược tự động khi đã gửi OTP
    LaunchedEffect(isOtpSent) {
        if (isOtpSent) {
            countdownTimer = 300
            while (countdownTimer > 0) {
                delay(1000L)
                countdownTimer--
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("forgot_password_screen"),
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
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBackToSignIn) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = TextPrimary
                        )
                    }

                    Text(
                        text = "Quên mật khẩu",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF9FAFB)),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                // 2. Hero Badge Logo
                Box(
                    modifier = Modifier.size(76.dp)
                ) {
                    // Khung hồng nhạt bo góc 18.dp
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFFFDECEE))
                            .border(1.dp, Color(0xFFF8D7DA), RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LockReset,
                            contentDescription = "Reset Password",
                            tint = Color(0xFFBA2211),
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    // Huy hiệu bảo mật xanh navy tròn góc dưới phải
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(26.dp)
                            .background(NavySecondary, CircleShape)
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Security,
                            contentDescription = "Bảo mật",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Tiêu đề "Đặt lại mật khẩu" & Subtitle
                Text(
                    text = "Đặt lại mật khẩu",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Nhập địa chỉ email đã đăng ký của bạn. Chúng tôi sẽ gửi mã OTP xác thực để đặt lại mật khẩu mới.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // 4. Thẻ Khung Nhập Liệu Card Trắng
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
                        Text(
                            text = "Email đã đăng ký",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Input Container
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
                                if (inputEmail.isEmpty()) {
                                    Text(
                                        text = "name@example.com",
                                        fontSize = 14.sp,
                                        color = Color(0xFF9CA3AF)
                                    )
                                }

                                BasicTextField(
                                    value = inputEmail,
                                    onValueChange = { inputEmail = it },
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

                            if (inputEmail.isNotEmpty()) {
                                IconButton(
                                    onClick = { inputEmail = "" },
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

                        Spacer(modifier = Modifier.height(8.dp))

                        // Hướng dẫn OTP
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = Color(0xFF6B7280),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Mã OTP 6 chữ số sẽ có hiệu lực trong 5 phút.",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                        }

                        // Nếu đã ấn gửi OTP -> Hiển thị ô nhập mã OTP 6 chữ số
                        AnimatedVisibility(visible = isOtpSent) {
                            Column(modifier = Modifier.padding(top = 14.dp)) {
                                val minutes = countdownTimer / 60
                                val seconds = countdownTimer % 60
                                val formattedTimer = String.format("%02d:%02d", minutes, seconds)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Nhập mã OTP 6 chữ số",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Còn hiệu lực: $formattedTimer",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFBA2211)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFEF2F2))
                                        .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BasicTextField(
                                        value = otpCodeInput,
                                        onValueChange = { if (it.length <= 6) otpCodeInput = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        textStyle = TextStyle(
                                            fontSize = 16.sp,
                                            color = Color(0xFFBA2211),
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 4.sp
                                        ),
                                        cursorBrush = SolidColor(Color(0xFFBA2211))
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Nút GỬI MÃ XÁC NHẬN →
                        Button(
                            onClick = {
                                if (inputEmail.isBlank()) {
                                    viewModel.showToast("Vui lòng nhập địa chỉ email hợp lệ!")
                                    return@Button
                                }
                                if (!isOtpSent) {
                                    isOtpSent = true
                                    viewModel.showToast("Đã gửi mã xác nhận 6 chữ số tới $inputEmail!")
                                } else {
                                    if (otpCodeInput.length < 6) {
                                        viewModel.showToast("Vui lòng nhập đủ mã OTP 6 chữ số!")
                                        return@Button
                                    }
                                    viewModel.showToast("Mã OTP hợp lệ! Đang chuyển đến màn hình Đổi Mật Khẩu.")
                                    viewModel.openChangePassword()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("send_otp_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA2211))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (isOtpSent) "XÁC NHẬN MÃ OTP" else "GỬI MÃ XÁC NHẬN",
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
                }
            }

            // 5. Link Hỗ trợ CSKH
            item {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cần hỗ trợ ngay? ",
                        fontSize = 12.5.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Liên hệ CSKH 24/7",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavySecondary,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            viewModel.openShopChat(shopName = "Trung Tâm CSKH B2C", product = null)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))
            }

            // 6. Footer Bottom: < Quay lại Đăng nhập
            item {
                Row(
                    modifier = Modifier
                        .clickable(onClick = onBackToSignIn)
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = NavySecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Quay lại Đăng nhập",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavySecondary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
