package com.example.b2capp.features.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.example.R
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.model.UserProfile
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Màn hình Chỉnh Sửa Hồ Sơ Cá Nhân (EditProfileScreen)
 * Thiết kế chính xác 100% theo EditProfileScreen_42608698.png:
 * 1. Top Bar: Nút Back quay lại, Tiêu đề "Edit Profile", Avatar tài khoản góc phải.
 * 2. Khối Ảnh đại diện:
 *    - Ảnh đại diện lớn bo góc tròn vuông kèm nút camera màu đỏ góc dưới phải.
 *    - Dòng chữ đỏ "Chạm để đổi ảnh đại diện".
 *    - Ghi chú: "Định dạng JPG, PNG tối đa 5MB".
 * 3. Card Thông tin Cá nhân:
 *    - Họ và tên người nhận (Icon person, nút clear x).
 *    - Số điện thoại + Prefix +84 + Nhãn xanh "✔ Đã xác thực".
 *    - Email 🔒 (Khóa bảo mật không tự ý chỉnh sửa) + Tag "Bảo mật" + Khung ghi chú ℹ️.
 *    - Giới tính: [Nam] (chọn sẵn màu đỏ nhạt) [Nữ] [Khác].
 *    - Ngày sinh + Tag "🎂 Ưu đãi sinh nhật" + Icon lịch + Popup chọn ngày.
 * 4. Card Bảo mật Dữ liệu (Light Blue Box):
 *    - Icon khiên bảo vệ xanh, "Thông tin của bạn được bảo mật tuyệt đối theo Chính sách bảo vệ dữ liệu cá nhân B2C Market".
 * 5. Nút Hành động Cố định:
 *    - Nút đỏ nổi bật "✔ LƯU THAY ĐỔI".
 *    - Subtext "🕒 Mọi thay đổi sẽ có hiệu lực ngay lập tức".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentProfile = uiState.userProfile
    val context = LocalContext.current

    var fullName by remember { mutableStateOf(currentProfile.fullName) }
    var phoneNumber by remember {
        mutableStateOf(
            currentProfile.phoneNumber.removePrefix("+84 ").removePrefix("+84").trim()
        )
    }
    var email by remember { mutableStateOf(currentProfile.email) }
    var selectedGender by remember { mutableStateOf(currentProfile.gender) }
    var birthDate by remember { mutableStateOf(currentProfile.birthDate) }

    var showDatePicker by remember { mutableStateOf(false) }

    // Tự động đồng bộ các trường trên form khi dữ liệu Profile được nạp hoặc cập nhật từ API
    LaunchedEffect(currentProfile) {
        fullName = currentProfile.fullName
        phoneNumber = currentProfile.phoneNumber.removePrefix("+84 ").removePrefix("+84").trim()
        email = currentProfile.email
        selectedGender = currentProfile.gender
        birthDate = currentProfile.birthDate
    }

    // Khi mở màn hình, đảm bảo nạp thông tin mới nhất từ API backend GET /api/v1/users/me
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }

    var isAvatarImageLoading by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            try {
                val inputStream = context.contentResolver.openInputStream(selectedUri)
                val bytes = inputStream?.use { it.readBytes() }
                val mimeType = context.contentResolver.getType(selectedUri) ?: "image/jpeg"
                if (bytes != null && bytes.isNotEmpty()) {
                    isAvatarImageLoading = true
                    viewModel.uploadAvatar(
                        bytes = bytes,
                        mimeType = mimeType,
                        fileName = "avatar_${System.currentTimeMillis()}.jpg"
                    ) { success ->
                        if (!success) {
                            isAvatarImageLoading = false
                        }
                    }
                } else {
                    viewModel.showErrorToast("Không thể đọc tệp ảnh đã chọn")
                }
            } catch (e: Exception) {
                isAvatarImageLoading = false
                viewModel.showErrorToast("Lỗi khi đọc tệp ảnh: ${e.localizedMessage}")
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("edit_profile_screen"),
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
                        text = "Chỉnh sửa thông tin",
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
            // 2. Khối Đổi Ảnh Đại Diện
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.clickable {
                            if (!uiState.isUploadingAvatar) {
                                imagePickerLauncher.launch("image/*")
                            }
                        }
                    ) {
                        if (!currentProfile.avatarUrl.isNullOrBlank()) {
                            SubcomposeAsyncImage(
                                model = currentProfile.avatarUrl,
                                contentDescription = "Ảnh đại diện",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .border(1.5.dp, Color(0xFFE5E7EB), RoundedCornerShape(18.dp)),
                                onSuccess = {
                                    isAvatarImageLoading = false
                                },
                                onError = {
                                    isAvatarImageLoading = false
                                },
                                loading = {
                                    DefaultGrayAvatar(iconSize = 48.dp)
                                },
                                error = {
                                    DefaultGrayAvatar(iconSize = 48.dp)
                                }
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .border(1.5.dp, Color(0xFFE5E7EB), RoundedCornerShape(18.dp))
                            ) {
                                DefaultGrayAvatar(iconSize = 48.dp)
                            }
                        }

                        if (uiState.isUploadingAvatar) {
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color.Black.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(28.dp),
                                    color = Color.White,
                                    strokeWidth = 2.5.dp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .offset(x = 6.dp, y = 6.dp)
                                .size(30.dp)
                                .background(Color(0xFFBA2211), CircleShape)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = "Đổi ảnh",
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (uiState.isUploadingAvatar) "Đang tải ảnh đại diện lên..." else "Chạm để đổi ảnh đại diện",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBA2211),
                        modifier = Modifier.clickable {
                            if (!uiState.isUploadingAvatar) {
                                imagePickerLauncher.launch("image/*")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Định dạng JPG, PNG tối đa 5MB",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )
                }
            }

            // 3. Card Form Thông tin Cá nhân
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Field 1: Họ và tên
                        Text(
                            text = "Họ và tên",
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
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 14.5.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(Color(0xFFBA2211)),
                                decorationBox = { innerTextField ->
                                    if (fullName.isEmpty()) {
                                        Text(
                                            text = "Nhập họ và tên",
                                            fontSize = 14.sp,
                                            color = Color(0xFF9CA3AF)
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                            if (fullName.isNotEmpty()) {
                                IconButton(
                                    onClick = { fullName = "" },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = "Xóa",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Field 2: Số điện thoại
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Số điện thoại",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )

                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFDCFCE7), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Đã xác thực",
                                        fontSize = 11.sp,
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
                                imageVector = Icons.Outlined.Phone,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFE5E7EB), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "+84",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))

                            BasicTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                textStyle = TextStyle(
                                    fontSize = 14.5.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(Color(0xFFBA2211))
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Field 3: Email 🔒 (Khóa bảo mật)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Email",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }

                            Text(
                                text = "Bảo mật",
                                fontSize = 11.5.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF3F4F6))
                                .clickable {
                                    viewModel.showToast("Liên hệ CSKH B2C Market để hỗ trợ đổi Email bảo mật")
                                }
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Email,
                                    contentDescription = null,
                                    tint = Color(0xFF9CA3AF),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = email,
                                    fontSize = 14.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }

                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = "Email khóa",
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Email liên kết bảo mật tài khoản và không thể tự ý thay đổi. Liên hệ CSKH nếu cần cập nhật.",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280),
                                lineHeight = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Field 4: Giới tính
                        Text(
                            text = "Giới tính",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf("Nam", "Nữ", "Khác").forEach { genderOption ->
                                val isSelected = selectedGender == genderOption
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFFFFF3F0) else Color(0xFFF3F4F6))
                                        .border(
                                            width = if (isSelected) 1.dp else 0.dp,
                                            color = if (isSelected) Color(0xFFBA2211) else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedGender = genderOption },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = genderOption,
                                        fontSize = 13.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color(0xFFBA2211) else Color(0xFF374151)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Field 5: Ngày sinh
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ngày sinh",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Cake,
                                    contentDescription = null,
                                    tint = Color(0xFFBA2211),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Ưu đãi sinh nhật",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFBA2211)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF3F4F6))
                                .clickable { showDatePicker = true }
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarToday,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = birthDate,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = "Chọn ngày",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // 4. Card Bảo mật Dữ liệu (Light Blue Box)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFEFF6FF))
                        .border(0.8.dp, Color(0xFFDBEAFE), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFF1E5B94), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VerifiedUser,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Thông tin của bạn được bảo mật tuyệt đối theo Chính sách bảo vệ dữ liệu cá nhân B2C Market.",
                            fontSize = 12.sp,
                            color = Color(0xFF1E3A8A),
                            lineHeight = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 5. Nút Hành động Cố định "✔ LƯU THAY ĐỔI"
            item {
                Spacer(modifier = Modifier.height(6.dp))

                val isWaitingForAvatar = uiState.isUploadingAvatar || isAvatarImageLoading
                val canSubmitProfile = !uiState.isUpdatingProfile && !isWaitingForAvatar

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        enabled = canSubmitProfile,
                        onClick = {
                            if (fullName.isBlank()) {
                                viewModel.showErrorToast("Vui lòng nhập họ và tên")
                                return@Button
                            }
                            val cleanPhone = phoneNumber.replace("\\s+".toRegex(), "")
                            if (cleanPhone.isNotBlank() && (!cleanPhone.all { it.isDigit() } || cleanPhone.length !in 9..11)) {
                                viewModel.showErrorToast("Số điện thoại không hợp lệ (cần từ 9 đến 11 chữ số)")
                                return@Button
                            }
                            val formattedPhone = if (cleanPhone.isNotBlank()) {
                                if (cleanPhone.startsWith("0")) cleanPhone else "0$cleanPhone"
                            } else ""

                            viewModel.updateUserProfile(
                                currentProfile.copy(
                                    fullName = fullName.trim(),
                                    phoneNumber = formattedPhone,
                                    email = email,
                                    gender = selectedGender,
                                    birthDate = birthDate
                                )
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
                            disabledContainerColor = Color(0xFFBA2211).copy(alpha = 0.5f)
                        )
                    ) {
                        if (uiState.isUpdatingProfile) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ĐANG LƯU...",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        } else if (isWaitingForAvatar) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ĐANG TẢI ẢNH LÊN...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LƯU THAY ĐỔI",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Mọi thay đổi sẽ có hiệu lực ngay lập tức",
                            fontSize = 11.5.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }
    }

    // Dialog chọn ngày sinh (Material 3 DatePicker)
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            birthDate = formatter.format(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("CHỌN", color = Color(0xFFBA2211), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("HỦY", color = TextSecondary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
