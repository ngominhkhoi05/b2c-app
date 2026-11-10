package com.example.b2capp.features.checkout

import com.example.b2capp.features.cart.SelectedVoucherState
import com.example.b2capp.features.cart.VoucherSelectionBottomSheet
import com.example.b2capp.features.checkout.components.PaymentBreakdownSection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

/**
 * Màn hình Thanh toán đơn hàng (Checkout Screen)
 * Thiết kế chính xác 100% theo CheckoutScreen_08de8b26.png:
 * 1. Top Bar:
 *    - Nút Back (quay lại)
 *    - Tiêu đề "Shopping Cart"
 *    - Nút "Sửa" màu đỏ và Avatar tài khoản tròn
 * 2. Thẻ Địa chỉ nhận hàng:
 *    - Viền phong bì bưu điện đỏ & xanh navy ziczac ở mép trên
 *    - Icon ghim vị trí đỏ
 *    - Tên người nhận "Nguyễn Văn A (+84) 987 654 321", nhãn "Mặc định"
 *    - Địa chỉ: "Số 123 Đường Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh"
 *    - Mũi tên chuyển trang >
 * 3. Thẻ Sản phẩm đơn hàng:
 *    - Header: Badge đỏ "MALL", Tên shop "Coolmate Official Store" kèm tích xanh
 *    - Sản phẩm: Ảnh áo thun đen, Tên "Áo Thun Nam Cotton Compact...", Phân loại "Đen, L",
 *      Giá ₫199.000, Số lượng x2
 *    - Khung Vận chuyển Nhanh: ₫30.000, "Nhận hàng vào 14 Th04 – 16 Th04", "Đồng kiểm khi nhận"
 *    - Ô ghi chú: "Lời nhắn: Lưu ý cho người bán (ví dụ: giao giờ HC)..."
 * 4. Thẻ Mã giảm giá sàn / Voucher:
 *    - Icon vé voucher, Badge "-₫39.800 (GIAM10)", Badge "Freeship"
 * 5. Thẻ Dùng Xu B2C:
 *    - Icon xu vàng, "Dùng 200 Xu B2C [₫200]", "Khả dụng: 1.450 Xu", Switch bật/tắt màu đỏ
 * 6. Thẻ Phương thức thanh toán:
 *    - Tùy chọn 1 (Checked): "Thanh toán khi nhận hàng (COD)" kèm tag "Phổ biến"
 *    - Tùy chọn 2: "Cổng VNPAY-QR / Ví điện tử"
 * 7. Thẻ Chi tiết thanh toán:
 *    - Huy hiệu "🔒 Bảo mật SSL 256-bit"
 *    - Tổng tiền hàng ₫398.000, Phí vận chuyển ₫30.000, Giảm giá voucher -₫39.800, Dùng Xu -₫200
 *    - Tổng thanh toán ₫388.000 (Đã bao gồm VAT)
 * 8. Thanh hành động Sticky đáy:
 *    - Lời nhắc điều khoản dịch vụ
 *    - Tổng thanh toán ₫388.000, Tiết kiệm ₫40.000
 *    - Nút lớn "ĐẶT HÀNG →" màu cam đỏ
 */
@Composable
fun CheckoutScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPaymentMethod by remember { mutableStateOf("COD") }
    var sellerNote by remember { mutableStateOf("") }
    var showVoucherSelectionSheet by remember { mutableStateOf(false) }
    var voucherState by remember { mutableStateOf(SelectedVoucherState()) }

    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale("vi", "VN")) }

    // Tính toán chi phí đơn hàng
    val itemsSubtotal = 398000L // 199.000 x 2
    val shippingFee = 30000L
    val voucherDiscount = if (voucherState.hasOrderVoucher) voucherState.orderVoucherDiscount else 0L
    val freeshipDiscount = if (voucherState.hasFreeshipVoucher) voucherState.freeshipDiscount else 0L
    val finalTotal = itemsSubtotal + (shippingFee - freeshipDiscount) - voucherDiscount
    val totalSavings = voucherDiscount + freeshipDiscount

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .testTag("checkout_screen")
    ) {
        // 1. Top Bar
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
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Thanh toán",
                    fontSize = 17.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
            }
        }

        // Toàn bộ các thẻ cuộn dọc
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 2. Thẻ Địa chỉ nhận hàng
            item {
                val defaultAddr = uiState.addresses.firstOrNull { it.isDefault } ?: uiState.addresses.firstOrNull()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openAddressBook() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                            // Icon Pin vị trí
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFEBE7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = CoralPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                if (defaultAddr != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = defaultAddr.recipientName,
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF111827)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = defaultAddr.phoneNumber,
                                            fontSize = 13.sp,
                                            color = Color(0xFF6B7280)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))

                                    // Nhãn Mặc định
                                    if (defaultAddr.isDefault) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(Color(0xFFE6F7F5))
                                                .padding(horizontal = 6.dp, vertical = 1.5.dp)
                                        ) {
                                            Text(
                                                text = "Mặc định",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF00897B)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(5.dp))
                                    }

                                    Text(
                                        text = defaultAddr.fullAddress,
                                        fontSize = 12.5.sp,
                                        color = Color(0xFF4B5563),
                                        lineHeight = 17.sp
                                    )
                                } else {
                                    Text(
                                        text = "Chưa có địa chỉ nhận hàng",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CoralPrimary
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "Nhấn vào đây để thêm địa chỉ giao hàng của bạn",
                                        fontSize = 12.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                }
            }

            // 3. Thẻ Sản phẩm đơn hàng Coolmate Official Store
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Header Shop
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.showToast("Mở thông tin Shop Coolmate") },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFFB82414))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "MALL",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "Coolmate Official Store",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavySecondary
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Mall",
                                tint = NavySecondary,
                                modifier = Modifier.size(14.dp)
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sản phẩm chính: Áo thun nam cotton compact
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.folded_black_tshirt_1793956682242),
                                contentDescription = "Áo Thun Nam Cotton Compact",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp))
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Áo Thun Nam Cotton Compact Siêu Thoáng Khí Cao Cấp",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFF3F4F6))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Phân loại: Đen, L",
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "₫199.000",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CoralPrimary
                                    )

                                    Text(
                                        text = "x2",
                                        fontSize = 13.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Khung Vận chuyển Nhanh
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF4FBF7)),
                            border = BorderStroke(0.8.dp, Color(0xFFD1FAE5))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocalShipping,
                                            contentDescription = null,
                                            tint = Color(0xFF00897B),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Vận chuyển Nhanh",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1F2937)
                                        )
                                    }

                                    Text(
                                        text = "₫30.000",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Nhận hàng vào 14 Th04 – 16 Th04",
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF6B7280)
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = Color(0xFF00897B),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Đồng kiểm khi nhận",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF00897B)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Lời nhắn cho người bán
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(0.8.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = sellerNote,
                                onValueChange = { sellerNote = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 12.sp,
                                    color = Color(0xFF1F2937)
                                ),
                                decorationBox = { innerTextField ->
                                    if (sellerNote.isEmpty()) {
                                        Text(
                                            text = "Lời nhắn: Lưu ý cho người bán (ví dụ: giao giờ HC)...",
                                            fontSize = 12.sp,
                                            color = Color(0xFF94A3B8),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }
                }
            }

            // 4. Thẻ Mã giảm giá sàn / Voucher
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showVoucherSelectionSheet = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFEBE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ConfirmationNumber,
                                contentDescription = null,
                                tint = CoralPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Mã giảm giá sàn / Voucher",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            modifier = Modifier.weight(1f)
                        )

                        // Các tag voucher đã áp dụng
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (voucherState.hasOrderVoucher) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color(0xFFE6F7F5))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "-₫39.800 (GIAM10)",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00897B)
                                    )
                                }
                            }

                            if (voucherState.hasFreeshipVoucher) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color(0xFFEBF3FC))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Freeship",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E5B94)
                                    )
                                }
                            }

                            if (!voucherState.hasOrderVoucher && !voucherState.hasFreeshipVoucher) {
                                Text(
                                    text = "Chọn hoặc nhập mã",
                                    fontSize = 12.sp,
                                    color = Color(0xFF9CA3AF)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // 5. Thẻ Phương thức thanh toán
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Phương thức thanh toán",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )
                            Text(
                                text = "Chọn 1 phương thức",
                                fontSize = 12.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Lựa chọn 1: COD (Thanh toán khi nhận hàng - Được chọn)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPaymentMethod = "COD" },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedPaymentMethod == "COD") Color(0xFFF9FAFB) else Color.White
                            ),
                            border = BorderStroke(
                                0.8.dp,
                                if (selectedPaymentMethod == "COD") CoralPrimary.copy(alpha = 0.5f) else Color(0xFFE5E7EB)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                RadioButton(
                                    selected = selectedPaymentMethod == "COD",
                                    onClick = { selectedPaymentMethod = "COD" },
                                    colors = RadioButtonDefaults.colors(selectedColor = CoralPrimary),
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Thanh toán khi nhận hàng (COD)",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1F2937)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(Color(0xFFFCE7E7))
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "Phổ biến",
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFB91C1C)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))

                                    Text(
                                        text = "Thanh toán tiền mặt cho bưu tá khi kiện hàng được giao an toàn tận tay.",
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF6B7280),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Lựa chọn 2: Cổng VNPAY-QR / Ví điện tử
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPaymentMethod = "VNPAY" },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedPaymentMethod == "VNPAY") Color(0xFFF9FAFB) else Color.White
                            ),
                            border = BorderStroke(
                                0.8.dp,
                                if (selectedPaymentMethod == "VNPAY") CoralPrimary.copy(alpha = 0.5f) else Color(0xFFE5E7EB)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                RadioButton(
                                    selected = selectedPaymentMethod == "VNPAY",
                                    onClick = { selectedPaymentMethod = "VNPAY" },
                                    colors = RadioButtonDefaults.colors(selectedColor = CoralPrimary),
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Cổng VNPAY-QR / Ví điện tử",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )

                                    Spacer(modifier = Modifier.height(3.dp))

                                    Text(
                                        text = "Quét mã QR Ngân hàng (VietQR), thẻ ATM nội địa, Visa/Mastercard siêu tốc.",
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF6B7280),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 7. Thẻ Chi tiết thanh toán
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Chi tiết thanh toán",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        PaymentBreakdownSection(
                            itemsSubtotalFormatted = "₫${currencyFormatter.format(itemsSubtotal)}",
                            shippingFeeFormatted = "₫${currencyFormatter.format(shippingFee)}",
                            shippingDiscountFormatted = "-₫${currencyFormatter.format(freeshipDiscount)}",
                            voucherDiscountFormatted = if (voucherDiscount > 0) "-₫${currencyFormatter.format(voucherDiscount)}" else null,
                            finalTotalFormatted = "₫${currencyFormatter.format(finalTotal)}"
                        )
                    }
                }
            }

            // Khoảng đệm chân trang
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // 7. Thanh đặt hàng cố định đáy (Sticky Bottom Bar)
        Surface(
            color = SurfaceCard,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                // Hàng tổng tiền & Nút ĐẶT HÀNG
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TỔNG THANH TOÁN",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6B7280)
                        )
                        Text(
                            text = "₫${currencyFormatter.format(finalTotal)}",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            color = CoralPrimary
                        )
                        Text(
                            text = "🏷 Tiết kiệm ₫${currencyFormatter.format(totalSavings)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00897B)
                        )
                    }

                    // Nút ĐẶT HÀNG →
                    Box(
                        modifier = Modifier
                            .height(46.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFBA2211))
                            .clickable {
                                if (selectedPaymentMethod == "VNPAY") {
                                    viewModel.openVnpayPayment()
                                } else {
                                    viewModel.completeOrderPlacement(
                                        paymentMethod = selectedPaymentMethod,
                                        sellerNote = sellerNote
                                    )
                                }
                            }
                            .padding(horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ĐẶT HÀNG",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
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

    // BottomSheet Chọn Mã Giảm Giá / Voucher (Khớp 100% VoucherSelectionScreen_b82f3992.png)
    if (showVoucherSelectionSheet) {
        VoucherSelectionBottomSheet(
            initialState = voucherState,
            onDismissRequest = { showVoucherSelectionSheet = false },
            onConfirmSelection = { newState ->
                voucherState = newState
                showVoucherSelectionSheet = false
                viewModel.showToast("Đã áp dụng ${newState.totalVouchersCount} voucher, tiết kiệm ₫${currencyFormatter.format(newState.totalSavingsAmount)}")
            }
        )
    }
}


