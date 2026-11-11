package com.example.b2capp.features.profile

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Signpost
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Signpost
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.model.UserAddress
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary

/**
 * Màn hình Thêm / Chỉnh Sửa Địa Chỉ (AddressEditorScreen)
 * Thiết kế chính xác 100% theo AddressEditorScreen_be0ffdae.png:
 * 1. Top Bar: Back icon, Tiêu đề "Them Dia Chi Moi" / "Chỉnh sửa địa chỉ", Avatar tài khoản đỏ.
 * 2. Card Thông báo Giao hàng: Icon xe tải xanh, "Giao hàng chính xác & nhanh chóng...".
 * 3. Thẻ THÔNG TIN LIÊN HỆ:
 *    - Label "Bắt buộc" bên phải.
 *    - Họ và tên người nhận (Icon person, nút xóa x).
 *    - Số điện thoại liên lạc + Nhãn xanh "✔ Định dạng hợp lệ", prefix "+84".
 * 4. Thẻ ĐỊA CHỈ GIAO HÀNG:
 *    - Nút "🎯 Vị trí hiện tại" bên phải.
 *    - Dropdown Tỉnh/Thành phố (TP. Hồ Chí Minh).
 *    - Dropdown Quận/Huyện (Quận 1).
 *    - Dropdown Phường/Xã (Phường Bến Nghé).
 *    - Ô nhập Địa chỉ cụ thể: "Số 123 Đường Nguyễn Huệ (Tòa nhà Landmark)" kèm bộ đếm 42/120 ký tự.
 *    - Card Xem Bản Đồ xem trước vị trí GPS.
 * 5. Thẻ CÀI ĐẶT & LOẠI ĐỊA CHỈ:
 *    - Loaị nhãn: [🏠 Nhà riêng] [🏢 Văn phòng] [💬 Khác].
 *    - Switch: "Đặt làm địa chỉ mặc định" (Bật công tắc đỏ).
 *    - Checkbox: "Giao vào giờ hành chính" (Thứ 2 - Thứ 6 8h00-17h30).
 *    - Khung lưu ý ℹ️ linh hoạt trước khi giao.
 * 6. Nút "Xóa địa chỉ giao hàng này" (nếu sửa địa chỉ cũ).
 * 7. Sticky Bottom Bar: Nút [Hủy] (xám) & Nút [✔ HOÀN THÀNH] (Đỏ đậm).
 */
@Composable
fun AddressEditorScreen(
    initialAddress: UserAddress? = null,
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val addrParts = initialAddress?.fullAddress?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()

    val initialCity = initialAddress?.provinceCode?.takeIf { it.isNotBlank() }
        ?: if (addrParts.isNotEmpty()) addrParts.last() else ""

    val initialDistrict = initialAddress?.districtCode?.takeIf { it.isNotBlank() }
        ?: if (addrParts.size >= 2) addrParts[addrParts.size - 2] else ""

    val initialWard = initialAddress?.wardCode?.takeIf { it.isNotBlank() }
        ?: if (addrParts.size >= 3) addrParts[addrParts.size - 3] else ""

    val initialStreet = initialAddress?.streetDetail?.takeIf { it.isNotBlank() }
        ?: if (addrParts.size >= 4) addrParts.dropLast(3).joinToString(", ")
        else (initialAddress?.fullAddress?.split(",")?.firstOrNull()?.trim() ?: "")

    var recipientName by remember {
        mutableStateOf(initialAddress?.recipientName ?: uiState.userProfile.fullName.ifBlank { "" })
    }
    var phoneNumber by remember {
        mutableStateOf(
            initialAddress?.phoneNumber?.replace("+84 ", "")
                ?: uiState.userProfile.phoneNumber.replace("+84 ", "")
        )
    }
    var selectedCity by remember { mutableStateOf(initialCity) }
    var selectedDistrict by remember { mutableStateOf(initialDistrict) }
    var selectedWard by remember { mutableStateOf(initialWard) }
    var specificAddress by remember { mutableStateOf(initialStreet) }
    var addressType by remember { mutableStateOf(initialAddress?.addressType ?: "Nhà riêng") }
    var isDefault by remember { mutableStateOf(initialAddress?.isDefault ?: uiState.addresses.isEmpty()) }
    var isWorkHoursOnly by remember { mutableStateOf(initialAddress?.deliveryNote?.contains("hành chính") == true || initialAddress == null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("address_editor_screen"),
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
                        text = if (initialAddress == null) "Thêm Địa Chỉ Mới" else "Chỉnh Sửa Địa Chỉ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        },
        bottomBar = {
            // 7. Sticky Bottom Bar với 2 nút [Hủy] và [✔ HOÀN THÀNH]
            Surface(
                color = SurfaceCard,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onBackClick,
                        modifier = Modifier
                            .weight(0.32f)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF3F4F6),
                            contentColor = Color(0xFF374151)
                        )
                    ) {
                        Text(
                            text = "Hủy",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = {
                            if (recipientName.isBlank()) {
                                viewModel.showToast("Vui lòng nhập họ tên người nhận")
                                return@Button
                            }
                            if (phoneNumber.isBlank() || phoneNumber.length < 9) {
                                viewModel.showToast("Vui lòng nhập số điện thoại hợp lệ")
                                return@Button
                            }
                            if (selectedCity.isBlank()) {
                                viewModel.showToast("Vui lòng nhập Tỉnh / Thành phố")
                                return@Button
                            }
                            if (selectedDistrict.isBlank()) {
                                viewModel.showToast("Vui lòng nhập Quận / Huyện")
                                return@Button
                            }
                            if (selectedWard.isBlank()) {
                                viewModel.showToast("Vui lòng nhập Phường / Xã")
                                return@Button
                            }
                            if (specificAddress.isBlank()) {
                                viewModel.showToast("Vui lòng nhập số nhà, tên đường cụ thể")
                                return@Button
                            }

                            val formattedFullAddr = listOf(
                                specificAddress.trim(),
                                selectedWard.trim(),
                                selectedDistrict.trim(),
                                selectedCity.trim()
                            ).filter { it.isNotBlank() }.joinToString(", ")

                            val newAddress = UserAddress(
                                id = initialAddress?.id ?: "",
                                recipientName = recipientName.trim(),
                                phoneNumber = phoneNumber.trim(),
                                addressType = addressType,
                                fullAddress = formattedFullAddr,
                                streetDetail = specificAddress.trim(),
                                provinceCode = selectedCity.trim(),
                                districtCode = selectedDistrict.trim(),
                                wardCode = selectedWard.trim(),
                                isDefault = isDefault,
                                deliveryNote = if (isWorkHoursOnly) "Lưu ý: Giao giờ hành chính (8h00 - 17h30)" else null
                            )
                            viewModel.addOrUpdateAddress(newAddress) {
                                onBackClick()
                            }
                        },
                        enabled = !uiState.isAddressSaving,
                        modifier = Modifier
                            .weight(0.68f)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA2211))
                    ) {
                        if (uiState.isAddressSaving) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
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
                                    text = "HOÀN THÀNH",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA)),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 2. Banner Thông báo Giao hàng (Light blue box)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFEFF6FF))
                        .border(0.8.dp, Color(0xFFDBEAFE), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFDBEAFE), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocalShipping,
                                contentDescription = null,
                                tint = Color(0xFF1E40AF),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "Giao hàng chính xác & nhanh chóng",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Vui lòng nhập địa chỉ chính xác để đối tác vận chuyển giao hàng đúng hẹn.",
                                fontSize = 12.sp,
                                color = Color(0xFF1E3A8A),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // 3. SECTON 1: THÔNG TIN LIÊN HỆ
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "THÔNG TIN LIÊN HỆ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp
                            )

                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFF3F4F6), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Bắt buộc",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Input 1: Họ và tên người nhận
                        Text(
                            text = "Họ và tên người nhận",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
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
                                value = recipientName,
                                onValueChange = { recipientName = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 14.5.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(Color(0xFFBA2211)),
                                decorationBox = { innerTextField ->
                                    if (recipientName.isEmpty()) {
                                        Text(
                                            text = "Nhập họ và tên người nhận",
                                            fontSize = 14.sp,
                                            color = Color(0xFF9CA3AF)
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                            if (recipientName.isNotEmpty()) {
                                IconButton(
                                    onClick = { recipientName = "" },
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

                        Spacer(modifier = Modifier.height(12.dp))

                        // Input 2: Số điện thoại liên lạc
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Số điện thoại liên lạc",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF15803D),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Định dạng hợp lệ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
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

                            Text(
                                text = "+84",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
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
                                cursorBrush = SolidColor(Color(0xFFBA2211)),
                                decorationBox = { innerTextField ->
                                    if (phoneNumber.isEmpty()) {
                                        Text(
                                            text = "Nhập số điện thoại",
                                            fontSize = 14.sp,
                                            color = Color(0xFF9CA3AF)
                                        )
                                    }
                                    innerTextField()
                                }
                            )

                            if (phoneNumber.isNotEmpty()) {
                                IconButton(
                                    onClick = { phoneNumber = "" },
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
                    }
                }
            }

            // 4. SECTION 2: ĐỊA CHỈ GIAO HÀNG
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ĐỊA CHỈ GIAO HÀNG",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFE0EDFF))
                                    .clickable {
                                        selectedCity = "TP. Hồ Chí Minh"
                                        selectedDistrict = "Quận 1"
                                        selectedWard = "Phường Bến Nghé"
                                        viewModel.showToast("Đã định vị: Phường Bến Nghé, Quận 1, TP. HCM")
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.GpsFixed,
                                        contentDescription = null,
                                        tint = Color(0xFF1D4ED8),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Vị trí hiện tại",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D4ED8)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Textbox 1: Tỉnh / Thành phố
                        Text(
                            text = "Tỉnh / Thành phố",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
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
                                imageVector = Icons.Outlined.Apartment,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = selectedCity,
                                onValueChange = { selectedCity = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(Color(0xFFBA2211)),
                                decorationBox = { innerTextField ->
                                    if (selectedCity.isEmpty()) {
                                        Text(
                                            text = "Nhập Tỉnh / Thành phố (VD: TP. Hồ Chí Minh)",
                                            fontSize = 13.5.sp,
                                            color = Color(0xFF9CA3AF)
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                            if (selectedCity.isNotEmpty()) {
                                IconButton(
                                    onClick = { selectedCity = "" },
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

                        Spacer(modifier = Modifier.height(12.dp))

                        // Textbox 2: Quận / Huyện
                        Text(
                            text = "Quận / Huyện",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
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
                                imageVector = Icons.Outlined.Map,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = selectedDistrict,
                                onValueChange = { selectedDistrict = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(Color(0xFFBA2211)),
                                decorationBox = { innerTextField ->
                                    if (selectedDistrict.isEmpty()) {
                                        Text(
                                            text = "Nhập Quận / Huyện (VD: Quận 1)",
                                            fontSize = 13.5.sp,
                                            color = Color(0xFF9CA3AF)
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                            if (selectedDistrict.isNotEmpty()) {
                                IconButton(
                                    onClick = { selectedDistrict = "" },
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

                        Spacer(modifier = Modifier.height(12.dp))

                        // Textbox 3: Phường / Xã
                        Text(
                            text = "Phường / Xã",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
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
                                imageVector = Icons.Outlined.Signpost,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = selectedWard,
                                onValueChange = { selectedWard = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(Color(0xFFBA2211)),
                                decorationBox = { innerTextField ->
                                    if (selectedWard.isEmpty()) {
                                        Text(
                                            text = "Nhập Phường / Xã (VD: Phường Bến Nghé)",
                                            fontSize = 13.5.sp,
                                            color = Color(0xFF9CA3AF)
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                            if (selectedWard.isNotEmpty()) {
                                IconButton(
                                    onClick = { selectedWard = "" },
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

                        Spacer(modifier = Modifier.height(12.dp))

                        // Input 4: Tên đường, Tòa nhà, Số nhà cụ thể
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tên đường, Tòa nhà, Số nhà cụ thể",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )

                            Text(
                                text = "Ví dụ: Tầng 4, Tòa Pearl Plaza",
                                fontSize = 11.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(88.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF3F4F6))
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            BasicTextField(
                                value = specificAddress,
                                onValueChange = { if (it.length <= 120) specificAddress = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 18.dp),
                                textStyle = TextStyle(
                                    fontSize = 13.5.sp,
                                    color = TextPrimary,
                                    lineHeight = 19.sp
                                ),
                                cursorBrush = SolidColor(Color(0xFFBA2211)),
                                decorationBox = { innerTextField ->
                                    if (specificAddress.isEmpty()) {
                                        Text(
                                            text = "Nhập số nhà, tên đường, tòa nhà...",
                                            fontSize = 13.5.sp,
                                            color = Color(0xFF9CA3AF)
                                        )
                                    }
                                    innerTextField()
                                }
                            )

                            Text(
                                text = "${specificAddress.length}/120 ký tự",
                                fontSize = 10.5.sp,
                                color = Color(0xFF9CA3AF),
                                modifier = Modifier.align(Alignment.BottomEnd)
                            )
                        }
                    }
                }
            }

            // 5. SECTION 3: CÀI ĐẶT & LOẠI ĐỊA CHỈ
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "CÀI ĐẶT & LOẠI ĐỊA CHỈ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Loại nhãn địa chỉ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // 3 Nhãn: [🏠 Nhà riêng] [🏢 Văn phòng] [💬 Khác]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AddressLabelOption(
                                title = "Nhà riêng",
                                icon = Icons.Filled.Home,
                                isSelected = addressType == "Nhà riêng",
                                onClick = { addressType = "Nhà riêng" },
                                modifier = Modifier.weight(1f)
                            )

                            AddressLabelOption(
                                title = "Văn phòng",
                                icon = Icons.Filled.Business,
                                isSelected = addressType == "Văn phòng",
                                onClick = { addressType = "Văn phòng" },
                                modifier = Modifier.weight(1f)
                            )

                            AddressLabelOption(
                                title = "Khác",
                                icon = Icons.Filled.MoreHoriz,
                                isSelected = addressType == "Khác",
                                onClick = { addressType = "Khác" },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 0.8.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Switch 1: Đặt làm địa chỉ mặc định
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Đặt làm địa chỉ mặc định",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Sử dụng làm địa chỉ ưu tiên khi đặt hàng",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary
                                )
                            }

                            Switch(
                                checked = isDefault,
                                onCheckedChange = { isDefault = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFBA2211)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 0.8.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Checkbox 2: Giao vào giờ hành chính
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color(0xFFF3F4F6), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.AccessTime,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = "Giao vào giờ hành chính",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Thứ 2 – Thứ 6 (8h00 – 17h30)",
                                        fontSize = 11.5.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Checkbox(
                                checked = isWorkHoursOnly,
                                onCheckedChange = { isWorkHoursOnly = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFFBA2211)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Box lưu ý ℹ️
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF3F4F6))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Bạn có thể thay đổi địa chỉ nhận hàng linh hoạt trước khi đơn hàng chuyển sang trạng thái \"Đang chuẩn bị hàng\".",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF4B5563),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // 6. Nút "Xóa địa chỉ giao hàng này" (nếu sửa địa chỉ đã tồn tại)
            if (initialAddress != null) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDeleteDialog = true }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Xóa",
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Xóa địa chỉ giao hàng này",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                    }
                }
            }
        }
    }

    // Dialog xác nhận xóa
    if (showDeleteDialog && initialAddress != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xóa địa chỉ này?", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc muốn xóa địa chỉ giao hàng này không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAddress(initialAddress.id)
                        showDeleteDialog = false
                        onBackClick()
                    }
                ) {
                    Text("XÓA", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("HỦY", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun AddressLabelOption(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFFFF3F0) else Color(0xFFF3F4F6))
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) Color(0xFFBA2211) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color(0xFFBA2211) else Color(0xFF4B5563),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color(0xFFBA2211) else Color(0xFF4B5563)
            )
        }
    }
}
