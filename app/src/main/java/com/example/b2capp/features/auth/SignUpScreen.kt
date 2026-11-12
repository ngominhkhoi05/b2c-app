package com.example.b2capp.features.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary

/**
 * Biểu tượng Google 'G' 4 màu chuẩn thương hiệu rendered sắc nét
 */
@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier.size(20.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.22f
        val inset = stroke / 2f
        val rect = Rect(inset, inset, w - inset, h - inset)

        // Vòng cung Đỏ (Top)
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 195f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = rect.topLeft,
            size = rect.size,
            style = Stroke(width = stroke)
        )
        // Vòng cung Vàng (Left)
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 125f,
            sweepAngle = 70f,
            useCenter = false,
            topLeft = rect.topLeft,
            size = rect.size,
            style = Stroke(width = stroke)
        )
        // Vòng cung Xanh Lá (Bottom)
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 35f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = rect.topLeft,
            size = rect.size,
            style = Stroke(width = stroke)
        )
        // Vòng cung Xanh Dương (Right)
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = 330f,
            sweepAngle = 65f,
            useCenter = false,
            topLeft = rect.topLeft,
            size = rect.size,
            style = Stroke(width = stroke)
        )
        // Thanh ngang Xanh Dương
        drawLine(
            color = Color(0xFF4285F4),
            start = Offset(w * 0.45f, h * 0.5f),
            end = Offset(w * 0.95f, h * 0.5f),
            strokeWidth = stroke
        )
    }
}

/**
 * Biểu tượng Apple Logo vector sắc nét rendered qua Canvas
 */
@Composable
fun AppleLogoIcon(
    modifier: Modifier = Modifier.size(19.dp),
    color: Color = Color.Black
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Lá táo phía trên
        val leafPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.53f, h * 0.20f)
            cubicTo(w * 0.53f, h * 0.08f, w * 0.64f, 0f, w * 0.74f, 0f)
            cubicTo(w * 0.74f, h * 0.12f, w * 0.63f, h * 0.21f, w * 0.53f, h * 0.20f)
            close()
        }
        drawPath(path = leafPath, color = color)

        // Thân quả táo kèm vết cắn bên phải
        val bodyPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.70f, h * 0.26f)
            cubicTo(w * 0.58f, h * 0.26f, w * 0.52f, h * 0.32f, w * 0.45f, h * 0.32f)
            cubicTo(w * 0.38f, h * 0.32f, w * 0.31f, h * 0.26f, w * 0.21f, h * 0.26f)
            cubicTo(w * 0.08f, h * 0.26f, 0f, h * 0.36f, 0f, h * 0.55f)
            cubicTo(0f, h * 0.74f, w * 0.17f, h * 1.0f, w * 0.30f, h * 1.0f)
            cubicTo(w * 0.39f, h * 1.0f, w * 0.42f, h * 0.94f, w * 0.52f, h * 0.94f)
            cubicTo(w * 0.62f, h * 0.94f, w * 0.65f, h * 1.0f, w * 0.74f, h * 1.0f)
            cubicTo(w * 0.87f, h * 1.0f, w * 0.96f, h * 0.81f, w * 1.0f, h * 0.69f)
            cubicTo(w * 0.88f, h * 0.64f, w * 0.87f, h * 0.47f, w * 0.99f, h * 0.39f)
            cubicTo(w * 0.92f, h * 0.30f, w * 0.80f, h * 0.26f, w * 0.70f, h * 0.26f)
            close()
        }
        drawPath(path = bodyPath, color = color)
    }
}

/**
 * Màn hình Tạo Tài Khoản Mới (SignUpScreen.kt)
 * Thiết kế chính xác 100% theo ảnh SignUpScreen_cf5beffa.png:
 * 1. Top Bar: Icon Back bên trái, Pill Badge "B2C MARKET" góc phải.
 * 2. Header Title: "Tạo tài khoản mới" & Subtitle "Đăng ký nhanh chóng chỉ trong vài bước" bên ngoài Card.
 * 3. Card trắng bo góc 16.dp:
 *    - Họ và tên, Email, Mật khẩu (kèm info ⓘ), Xác nhận mật khẩu (tích xanh ✔).
 *    - Checkbox Điều khoản dùng AnnotatedString chuẩn không bị nhảy chữ xuống dòng xấu.
 *    - Nút bấm đỏ "ĐĂNG KÝ →".
 * 4. Bên ngoài Card:
 *    - Dải phân cách "HOẶC ĐĂNG KÝ VỚI" & 2 nút Google (Icon 4 màu) + Apple hiển thị rõ ràng.
 * 5. Bottom Bar: "Đã có tài khoản? Đăng nhập".
 */
@Composable
fun SignUpScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackToSignIn: () -> Unit,
    onSignUpSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(true) }

    val isPasswordMatching = password.isNotEmpty() && password == confirmPassword

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("sign_up_screen"),
        topBar = {
            Surface(
                color = SurfaceCard,
                shadowElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
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

                    // Pill Badge B2C MARKET góc phải
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFFDECEE))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingBag,
                            contentDescription = null,
                            tint = Color(0xFFC82311),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "B2C MARKET",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC82311),
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        text = "Đã có tài khoản? ",
                        fontSize = 13.5.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Đăng nhập",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC82311),
                        modifier = Modifier.clickable(onClick = onBackToSignIn)
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
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // 2. Header Title bên ngoài Card
            item {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Tạo tài khoản mới",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Đăng ký nhanh chóng chỉ trong vài bước",
                    fontSize = 13.5.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))
            }

            // 3. Thẻ Form Card Trắng Bo 16.dp
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
                        // Trường 1: Họ và tên
                        Text(
                            text = "Họ và tên",
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
                                .background(Color.White)
                                .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Box(modifier = Modifier.weight(1f)) {
                                if (fullName.isEmpty()) {
                                    Text(
                                        text = "Ví dụ: Nguyễn Văn A",
                                        fontSize = 14.sp,
                                        color = Color(0xFF9CA3AF)
                                    )
                                }
                                BasicTextField(
                                    value = fullName,
                                    onValueChange = { fullName = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        fontSize = 14.sp,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    cursorBrush = SolidColor(Color(0xFFC82311))
                                )
                            }

                            if (fullName.isNotEmpty()) {
                                IconButton(
                                    onClick = { fullName = "" },
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

                        // Trường 2: Email
                        Text(
                            text = "Email",
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
                                .background(Color.White)
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
                                if (email.isEmpty()) {
                                    Text(
                                        text = "name@example.com",
                                        fontSize = 14.sp,
                                        color = Color(0xFF9CA3AF)
                                    )
                                }
                                BasicTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    textStyle = TextStyle(
                                        fontSize = 14.sp,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    cursorBrush = SolidColor(Color(0xFFC82311))
                                )
                            }

                            if (email.isNotEmpty()) {
                                IconButton(
                                    onClick = { email = "" },
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

                        // Trường 3: Mật khẩu
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
                                .background(Color.White)
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
                                        text = "Nhập mật khẩu của bạn",
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
                                    cursorBrush = SolidColor(Color(0xFFC82311))
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

                        Spacer(modifier = Modifier.height(6.dp))

                        // Subtext hướng dẫn ⓘ Tối thiểu 8 ký tự, bao gồm chữ và số
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Tối thiểu 8 ký tự, bao gồm chữ và số",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Trường 4: Xác nhận mật khẩu
                        Text(
                            text = "Xác nhận mật khẩu",
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
                                .background(Color.White)
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
                                if (confirmPassword.isEmpty()) {
                                    Text(
                                        text = "Nhập lại mật khẩu",
                                        fontSize = 14.sp,
                                        color = Color(0xFF9CA3AF)
                                    )
                                }
                                BasicTextField(
                                    value = confirmPassword,
                                    onValueChange = { confirmPassword = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    textStyle = TextStyle(
                                        fontSize = 14.sp,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    cursorBrush = SolidColor(Color(0xFFC82311))
                                )
                            }

                            if (isPasswordMatching) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Mật khẩu khớp",
                                    tint = Color(0xFF0E8345),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Checkbox điều khoản & chính sách (dùng AnnotatedString trong 1 Text duy nhất để ngắt dòng đẹp)
                        val termsAnnotatedString = buildAnnotatedString {
                            append("Tôi đồng ý với ")
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFF0E3B64),
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Điều khoản dịch vụ")
                            }
                            append(" và ")
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFF0E3B64),
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Chính sách bảo mật")
                            }
                            append(" của B2C")
                        }

                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { agreeToTerms = !agreeToTerms }
                        ) {
                            Checkbox(
                                checked = agreeToTerms,
                                onCheckedChange = { agreeToTerms = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFFC82311)
                                ),
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(top = 1.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = termsAnnotatedString,
                                fontSize = 12.5.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Hiển thị thông báo lỗi nếu có
                        if (!uiState.authErrorMessage.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = uiState.authErrorMessage,
                                color = Color(0xFFC82311),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Nút ĐĂNG KÝ →
                        Button(
                            onClick = {
                                if (fullName.isBlank()) {
                                    viewModel.showErrorToast("Vui lòng nhập họ và tên của bạn")
                                    return@Button
                                }
                                if (email.isBlank()) {
                                    viewModel.showErrorToast("Vui lòng nhập địa chỉ email")
                                    return@Button
                                }
                                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                                    viewModel.showErrorToast("Định dạng email không hợp lệ")
                                    return@Button
                                }
                                if (password.length < 8) {
                                    viewModel.showErrorToast("Mật khẩu phải có tối thiểu 8 ký tự")
                                    return@Button
                                }
                                if (password != confirmPassword) {
                                    viewModel.showErrorToast("Mật khẩu xác nhận không khớp")
                                    return@Button
                                }
                                if (!agreeToTerms) {
                                    viewModel.showErrorToast("Vui lòng đồng ý với Điều khoản dịch vụ!")
                                    return@Button
                                }

                                viewModel.registerWithApi(
                                    fullName = fullName,
                                    email = email,
                                    password = password,
                                    onSuccess = onSignUpSuccess
                                )
                            },
                            enabled = !uiState.isAuthLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("submit_signup_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFC82311),
                                disabledContainerColor = Color(0xFFC82311).copy(alpha = 0.6f)
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
                                        text = "ĐĂNG KÝ",
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
            }

            // 4. Phần "HOẶC ĐĂNG KÝ VỚI" & 2 Nút Mạng xã hội bên ngoài Card
            item {
                Spacer(modifier = Modifier.height(20.dp))

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
                        text = "HOẶC ĐĂNG KÝ VỚI",
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

                Spacer(modifier = Modifier.height(16.dp))

                // Hàng 2 Nút Đăng ký bằng Google (dùng Logo 4 màu) & Apple
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Nút Google (đã làm mờ và vô hiệu hoá)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF3F4F6))
                            .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
                            .alpha(0.4f),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GoogleLogoIcon(modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Google",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                        }
                    }

                    // Nút Apple (đã làm mờ và vô hiệu hoá)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
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

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
