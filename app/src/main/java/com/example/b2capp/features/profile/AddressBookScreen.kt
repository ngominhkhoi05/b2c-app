package com.example.b2capp.features.profile

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.model.UserAddress
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary

/**
 * Màn hình Sổ Địa Chỉ / Địa Chỉ Của Tôi (AddressBookScreen)
 * Thiết kế chính xác 100% theo AddressBookScreen_049a18cb.png:
 * 1. Top Bar: Back icon, Tiêu đề "Địa Chỉ Của Tôi", Avatar tài khoản đỏ.
 * 2. Banner Thông báo Phí vận chuyển: Nền hồng cam nhạt với icon vị trí pin.
 * 3. Thẻ Địa chỉ Mặc định:
 *    - Thanh viền trên đỏ cam đậm.
 *    - Nguyễn Văn A | 0987 654 321 + Icon tích đỏ đã chọn.
 *    - Tag MẶC ĐỊNH (đỏ nhạt) & Tag Nhà riêng (xám).
 *    - Địa chỉ chi tiết: Số 123 Đường Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh.
 *    - Thẻ lưu ý: Giao giờ hành chính, gọi trước 15 phút.
 *    - Chấm đỏ "Địa chỉ mặc định hiện tại" + Nút Chỉnh sửa (xanh).
 * 4. Thẻ Địa chỉ Văn phòng:
 *    - Nguyễn Văn A | 0987 654 321 + Tag Văn phòng (xanh dương).
 *    - Nút "Thiết lập mặc định", "Chỉnh sửa", "Xóa".
 * 5. Thẻ Địa chỉ Nhà riêng (Trần Thị B - Mẹ):
 *    - Trần Thị B (Mẹ) | 0912 345 678 + Tag Nhà riêng.
 *    - Nút "Thiết lập mặc định", "Chỉnh sửa", "Xóa".
 * 6. Thẻ "Định vị địa chỉ nhanh":
 *    - Icon GPS xanh nhạt, tự động điền phường/xã, nút "Dò tìm".
 * 7. Bottom Bar Cố định:
 *    - Nút đỏ nổi bật "+ + THÊM ĐỊA CHỈ MỚI".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressBookScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showAddressSheet by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<UserAddress?>(null) }
    var addressToDelete by remember { mutableStateOf<UserAddress?>(null) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("address_book_screen"),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Quay lại",
                                tint = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "Địa Chỉ Của Tôi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    IconButton(onClick = { viewModel.loadAddresses(isRefresh = true) }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Làm mới",
                            tint = TextPrimary
                        )
                    }
                }
            }
        },
        bottomBar = {
            // 7. Bottom Bar cố định nút "+ + THÊM ĐỊA CHỈ MỚI"
            Surface(
                color = SurfaceCard,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.openAddressEditor(null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA2211))
                    ) {
                        Text(
                            text = "+ THÊM ĐỊA CHỈ MỚI",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
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
            // 2. Banner Thông Báo Phí Vận Chuyển
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFF3F0))
                        .border(0.8.dp, Color(0xFFFDE8E4), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFFFE5DF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFFBA2211),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Địa chỉ nhận hàng dùng để tính phí vận chuyển và nhận hàng nhanh chóng.",
                            fontSize = 12.5.sp,
                            color = Color(0xFF4B5563),
                            lineHeight = 17.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Trạng thái đang tải (Loading State)
            if (uiState.isAddressLoading && uiState.addresses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = Color(0xFFBA2211),
                                modifier = Modifier.size(36.dp),
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Đang tải danh sách địa chỉ...",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Trạng thái lỗi tải dữ liệu (Error State)
            if (uiState.addressErrorMessage != null && uiState.addresses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Không thể tải danh sách địa chỉ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.addressErrorMessage ?: "Vui lòng kiểm tra lại kết nối mạng",
                                fontSize = 12.sp,
                                color = Color(0xFFB91C1C),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.loadAddresses(isRefresh = true) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA2211))
                            ) {
                                Text("Thử lại", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Trạng thái trống (Empty State)
            if (!uiState.isAddressLoading && uiState.addresses.isEmpty() && uiState.addressErrorMessage == null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color(0xFFFFF3F0), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFFBA2211),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Bạn chưa có địa chỉ nhận hàng nào",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Thêm địa chỉ giao hàng để đặt hàng và nhận hàng nhanh chóng nhất.",
                                fontSize = 12.5.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // 3. Danh sách Địa chỉ (Loop qua uiState.addresses)
            items(uiState.addresses) { address ->
                val isSelectedForCheckout = uiState.showCheckoutScreen && (address.id == uiState.activeCheckoutAddress?.id)
                AddressCardItem(
                    address = address,
                    isSelectedForCheckout = isSelectedForCheckout,
                    onSelect = {
                        if (uiState.showCheckoutScreen) {
                            viewModel.selectCheckoutAddress(address.id)
                            viewModel.closeAddressBook()
                        }
                    },
                    onSetDefault = {
                        viewModel.setDefaultAddress(address.id)
                    },
                    onEdit = {
                        viewModel.openAddressEditor(address)
                    },
                    onDelete = {
                        addressToDelete = address
                    }
                )
            }

            // 6. Thẻ "Định vị địa chỉ nhanh" (GPS)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFE0EDFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.GpsFixed,
                                contentDescription = "GPS",
                                tint = Color(0xFF1D4ED8),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Định vị địa chỉ nhanh",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tự động điền phường/xã qua định vị GPS tiện lợi",
                                fontSize = 11.5.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF3F4F6))
                                .clickable {
                                    viewModel.showToast("Đang lấy vị trí GPS hiện tại...")
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Dò tìm",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF374151)
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog xác nhận xóa địa chỉ
    if (addressToDelete != null) {
        AlertDialog(
            onDismissRequest = { addressToDelete = null },
            title = {
                Text(
                    text = "Xóa địa chỉ này?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "Bạn có chắc chắn muốn xóa địa chỉ của ${addressToDelete?.recipientName} khỏi sổ địa chỉ không?",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        addressToDelete?.let { viewModel.deleteAddress(it.id) }
                        addressToDelete = null
                    }
                ) {
                    Text("XÓA", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { addressToDelete = null }) {
                    Text("HỦY", color = TextSecondary)
                }
            }
        )
    }

    // Bottom Sheet Form Thêm / Sửa Địa Chỉ
    if (showAddressSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddressSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = SurfaceCard
        ) {
            AddressFormSheetContent(
                initialAddress = editingAddress,
                onSave = { newAddr ->
                    viewModel.addOrUpdateAddress(newAddr)
                    showAddressSheet = false
                },
                onClose = { showAddressSheet = false }
            )
        }
    }
}

@Composable
fun AddressCardItem(
    address: UserAddress,
    isSelectedForCheckout: Boolean = false,
    onSelect: (() -> Unit)? = null,
    onSetDefault: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onSelect != null) Modifier.clickable { onSelect() } else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelectedForCheckout) 2.dp else 0.5.dp),
        border = if (isSelectedForCheckout) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFBA2211)) else null
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Thanh viền đỏ phía trên cho địa chỉ mặc định hoặc đang chọn
            if (address.isDefault || isSelectedForCheckout) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.5.dp)
                        .background(if (isSelectedForCheckout) Color(0xFFBA2211) else Color(0xFFE53935))
                )
            }

            Column(modifier = Modifier.padding(14.dp)) {
                // Header: Họ tên + SĐT + Tích chọn đỏ (nếu mặc định)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = address.recipientName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "|",
                            fontSize = 13.sp,
                            color = Color(0xFFD1D5DB)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = address.phoneNumber,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }

                    if (isSelectedForCheckout) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFFECE9))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Đang chọn thanh toán",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBA2211)
                            )
                        }
                    } else if (address.isDefault) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(Color(0xFFBA2211), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Mặc định",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Các thẻ Tag (MẶC ĐỊNH, Nhà riêng, Văn phòng)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (address.isDefault) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFF3F0), RoundedCornerShape(3.dp))
                                .border(0.6.dp, Color(0xFFFDE8E4), RoundedCornerShape(3.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "MẶC ĐỊNH",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBA2211)
                            )
                        }
                    }

                    val (tagBg, tagText, tagIcon) = when (address.addressType) {
                        "Văn phòng" -> Triple(Color(0xFFE0EDFF), Color(0xFF1D4ED8), Icons.Filled.Business)
                        else -> Triple(Color(0xFFF3F4F6), Color(0xFF4B5563), Icons.Filled.Home)
                    }

                    Box(
                        modifier = Modifier
                            .background(tagBg, RoundedCornerShape(3.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = tagIcon,
                                contentDescription = null,
                                tint = tagText,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = address.addressType,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = tagText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Nội dung địa chỉ chi tiết
                Text(
                    text = address.fullAddress,
                    fontSize = 13.5.sp,
                    color = TextPrimary,
                    lineHeight = 19.sp
                )

                // Lưu ý giao hàng (nếu có)
                if (!address.deliveryNote.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF8F9FA))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = address.deliveryNote,
                                fontSize = 11.5.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // Footer Actions: Thiết lập mặc định / Chỉnh sửa / Xóa
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (address.isDefault) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(Color(0xFFBA2211), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Địa chỉ mặc định hiện tại",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBA2211)
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onSetDefault() }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = Color(0xFFBA2211),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Thiết lập mặc định",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBA2211)
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onEdit() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Chỉnh sửa",
                                tint = Color(0xFF1E5B94),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Chỉnh sửa",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1E5B94)
                            )
                        }

                        if (!address.isDefault) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onDelete() }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Xóa",
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Xóa",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFD32F2F)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddressFormSheetContent(
    initialAddress: UserAddress?,
    onSave: (UserAddress) -> Unit,
    onClose: () -> Unit
) {
    var name by remember { mutableStateOf(initialAddress?.recipientName ?: "") }
    var phone by remember { mutableStateOf(initialAddress?.phoneNumber ?: "") }
    var addressType by remember { mutableStateOf(initialAddress?.addressType ?: "Nhà riêng") }
    var fullAddress by remember { mutableStateOf(initialAddress?.fullAddress ?: "") }
    var deliveryNote by remember { mutableStateOf(initialAddress?.deliveryNote ?: "") }
    var isDefault by remember { mutableStateOf(initialAddress?.isDefault ?: false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (initialAddress == null) "Thêm Địa Chỉ Mới" else "Chỉnh Sửa Địa Chỉ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Đóng",
                    tint = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Họ và tên người nhận") },
            placeholder = { Text("Ví dụ: Nguyễn Văn A") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFBA2211),
                focusedLabelColor = Color(0xFFBA2211)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Số điện thoại") },
            placeholder = { Text("Ví dụ: 0987 654 321") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFBA2211),
                focusedLabelColor = Color(0xFFBA2211)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = fullAddress,
            onValueChange = { fullAddress = it },
            label = { Text("Địa chỉ chi tiết (Số nhà, tên đường, Phường/Xã, Quận/Huyện...)") },
            placeholder = { Text("Ví dụ: Số 123 Đường Nguyễn Huệ, P. Bến Nghé, Q.1, TP. HCM") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFBA2211),
                focusedLabelColor = Color(0xFFBA2211)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = deliveryNote,
            onValueChange = { deliveryNote = it },
            label = { Text("Ghi chú giao hàng (không bắt buộc)") },
            placeholder = { Text("Ví dụ: Giao giờ hành chính, gọi trước 15 phút") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFBA2211),
                focusedLabelColor = Color(0xFFBA2211)
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Loại địa chỉ:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("Nhà riêng", "Văn phòng").forEach { type ->
                val isSelected = addressType == type
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFFFFF3F0) else Color(0xFFF3F4F6))
                        .border(
                            width = if (isSelected) 1.dp else 0.dp,
                            color = if (isSelected) Color(0xFFBA2211) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { addressType = type }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = type,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFFBA2211) else Color(0xFF374151)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Đặt làm địa chỉ mặc định",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Switch(
                checked = isDefault,
                onCheckedChange = { isDefault = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFFBA2211)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isBlank() || phone.isBlank() || fullAddress.isBlank()) return@Button
                val newAddr = UserAddress(
                    id = initialAddress?.id ?: "addr_${System.currentTimeMillis()}",
                    recipientName = name,
                    phoneNumber = phone,
                    addressType = addressType,
                    fullAddress = fullAddress,
                    isDefault = isDefault,
                    deliveryNote = deliveryNote.ifBlank { null }
                )
                onSave(newAddr)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA2211))
        ) {
            Text(
                text = "HOÀN THÀNH",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
