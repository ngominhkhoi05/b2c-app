package com.example.b2capp.features.checkout

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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import com.example.b2capp.data.remote.dto.CartItemDto
import com.example.b2capp.data.remote.dto.shopDisplayName
import com.example.b2capp.features.cart.VoucherSelectionBottomSheet
import com.example.b2capp.features.checkout.components.PaymentBreakdownSection
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import java.text.NumberFormat
import java.util.Locale

/**
 * Màn hình Thanh toán đơn hàng (Checkout Screen)
 * Gắn kết 100% dữ liệu API thật từ Backend:
 * - Địa chỉ nhận hàng từ API: cho phép Đổi, Thêm mới, Sửa địa chỉ
 * - Danh sách sản phẩm được chọn từ Giỏ hàng (Cart Items) phân nhóm theo shop
 * - Mã giảm giá / Voucher từ API: tự động chọn voucher giảm sâu nhất
 * - Phương thức thanh toán: COD hoặc VNPAY-QR
 * - Chi tiết thanh toán & tính toán chi phí chính xác
 */
@Composable
fun CheckoutScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPaymentMethod by remember { mutableStateOf(uiState.checkoutSelectedPaymentMethod) }
    var sellerNote by remember { mutableStateOf(uiState.checkoutSellerNote) }
    var showVoucherSelectionSheet by remember { mutableStateOf(false) }

    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale("vi", "VN")) }

    val currentAddress = uiState.activeCheckoutAddress
    val selectedCartItems = uiState.checkoutCartItems

    // Tính toán subtotal dựa trên sản phẩm thật đang được chọn
    val itemsSubtotal = uiState.orderPreview?.subtotal?.toLong()
        ?: uiState.checkoutItemsSubtotal

    // Phí vận chuyển (lấy từ preview server hoặc mặc định 30.000đ)
    val shippingFee = uiState.orderPreview?.shippingFee?.toLong() ?: 30000L

    // Giảm giá từ voucher (lấy từ preview server hoặc tính theo selectedVoucher)
    val calculatedVoucherDiscount = uiState.selectedVoucher?.let {
        viewModel.computeDiscountAmount(it, itemsSubtotal.toDouble()).toLong()
    } ?: 0L

    val voucherDiscount = uiState.orderPreview?.discountAmount?.toLong()
        ?: calculatedVoucherDiscount

    // Tổng tiền thanh toán cuối cùng
    val finalTotal = uiState.orderPreview?.totalAmount?.toLong()
        ?: (itemsSubtotal + shippingFee - voucherDiscount).coerceAtLeast(0L)

    val totalSavings = voucherDiscount

    // Nhóm sản phẩm theo nhà bán (shop)
    val groupedItems = remember(selectedCartItems) {
        selectedCartItems.groupBy { it.shopDisplayName }
    }

    LaunchedEffect(Unit) {
        viewModel.refreshOrderPreview()
    }

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
            // 2. Thẻ Địa chỉ nhận hàng (API thật)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openAddressBook() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Dải viền phong bì bưu điện ở mép trên
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.5.dp)
                                .background(Color(0xFFBA2211))
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                if (currentAddress != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = currentAddress.recipientName,
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF111827)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = currentAddress.phoneNumber,
                                            fontSize = 13.sp,
                                            color = Color(0xFF6B7280)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (currentAddress.isDefault) {
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
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(Color(0xFFF3F4F6))
                                                .padding(horizontal = 6.dp, vertical = 1.5.dp)
                                        ) {
                                            Text(
                                                text = currentAddress.addressType,
                                                fontSize = 10.5.sp,
                                                color = Color(0xFF4B5563)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(5.dp))

                                    Text(
                                        text = currentAddress.fullAddress,
                                        fontSize = 12.5.sp,
                                        color = Color(0xFF4B5563),
                                        lineHeight = 17.sp
                                    )

                                    if (!currentAddress.deliveryNote.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Ghi chú: ${currentAddress.deliveryNote}",
                                            fontSize = 11.5.sp,
                                            color = Color(0xFF9CA3AF)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Các nút thao tác nhanh: Đổi / Thêm mới / Sửa
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Đổi địa chỉ",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavySecondary,
                                            modifier = Modifier.clickable { viewModel.openAddressBook() }
                                        )
                                        Text(
                                            text = "•",
                                            fontSize = 12.sp,
                                            color = Color(0xFFD1D5DB)
                                        )
                                        Text(
                                            text = "Sửa",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E5B94),
                                            modifier = Modifier.clickable { viewModel.openAddressEditor(currentAddress) }
                                        )
                                        Text(
                                            text = "•",
                                            fontSize = 12.sp,
                                            color = Color(0xFFD1D5DB)
                                        )
                                        Text(
                                            text = "+ Thêm mới",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CoralPrimary,
                                            modifier = Modifier.clickable { viewModel.openAddressEditor(null) }
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Chưa có địa chỉ nhận hàng",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CoralPrimary
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "Nhấn vào đây để thêm địa chỉ giao hàng của bạn",
                                        fontSize = 12.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { viewModel.openAddressEditor(null) },
                                        colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Thêm địa chỉ ngay",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
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
            }

            // 3. Thẻ Sản phẩm đơn hàng (Nhóm theo Shop từ Cart thật)
            if (selectedCartItems.isEmpty()) {
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
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Chưa có sản phẩm nào được chọn trong giỏ hàng",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onBackClick,
                                colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary)
                            ) {
                                Text("Quay lại giỏ hàng", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            } else {
                groupedItems.forEach { (shopName, shopCartItems) ->
                    item(key = "shop_$shopName") {
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
                                    modifier = Modifier.fillMaxWidth(),
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
                                        text = shopName,
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

                                // Danh sách sản phẩm của Shop
                                shopCartItems.forEachIndexed { index, cartItem ->
                                    if (index > 0) {
                                        HorizontalDivider(
                                            color = Color(0xFFF3F4F6),
                                            thickness = 0.8.dp,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        )
                                    }
                                    CheckoutProductRow(cartItem = cartItem, currencyFormatter = currencyFormatter)
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
                                                text = "₫${currencyFormatter.format(shippingFee)}",
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
                                                text = "Nhận hàng sau 2-4 ngày",
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
                                        onValueChange = {
                                            sellerNote = it
                                            viewModel.setCheckoutSellerNote(it)
                                        },
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
                }
            }

            // 4. Thẻ Mã giảm giá sàn / Voucher (Tự động chọn giảm sâu nhất từ API)
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

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Mã giảm giá sàn / Voucher",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )

                            if (uiState.selectedVoucher != null) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = uiState.selectedVoucher.name.orEmpty(),
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF6B7280),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Hiển thị badge voucher đã chọn
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (uiState.selectedVoucher != null && voucherDiscount > 0) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color(0xFFE6F7F5))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "-₫${currencyFormatter.format(voucherDiscount)} (${uiState.selectedVoucher.code.orEmpty()})",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00897B)
                                    )
                                }
                            } else {
                                Text(
                                    text = if (uiState.isPromotionsLoading) "Đang tìm mã..." else "Chọn hoặc nhập mã",
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

                        // Lựa chọn 1: COD (Thanh toán khi nhận hàng)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPaymentMethod = "COD"
                                    viewModel.setCheckoutPaymentMethod("COD")
                                },
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
                                    onClick = {
                                        selectedPaymentMethod = "COD"
                                        viewModel.setCheckoutPaymentMethod("COD")
                                    },
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
                                .clickable {
                                    selectedPaymentMethod = "VNPAY"
                                    viewModel.setCheckoutPaymentMethod("VNPAY")
                                },
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
                                    onClick = {
                                        selectedPaymentMethod = "VNPAY"
                                        viewModel.setCheckoutPaymentMethod("VNPAY")
                                    },
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

            // 6. Thẻ Chi tiết thanh toán
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
                                text = "Chi tiết thanh toán",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )

                            if (uiState.isPreviewLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = CoralPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        PaymentBreakdownSection(
                            itemsSubtotalFormatted = "₫${currencyFormatter.format(itemsSubtotal)}",
                            shippingFeeFormatted = "₫${currencyFormatter.format(shippingFee)}",
                            shippingDiscountFormatted = "-₫0",
                            voucherDiscountFormatted = if (voucherDiscount > 0) "-₫${currencyFormatter.format(voucherDiscount)}" else null,
                            finalTotalFormatted = "₫${currencyFormatter.format(finalTotal)}"
                        )
                    }
                }
            }

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
                        if (totalSavings > 0) {
                            Text(
                                text = "🏷 Tiết kiệm ₫${currencyFormatter.format(totalSavings)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00897B)
                            )
                        }
                    }

                    // Nút ĐẶT HÀNG
                    Box(
                        modifier = Modifier
                            .height(46.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (uiState.isPlacingOrder) Color(0xFF888888) else Color(0xFFBA2211))
                            .clickable(enabled = !uiState.isPlacingOrder) {
                                viewModel.completeOrderPlacement(
                                    paymentMethod = selectedPaymentMethod,
                                    sellerNote = sellerNote
                                )
                            }
                            .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isPlacingOrder) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
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
    }

    // BottomSheet Chọn Mã Giảm Giá / Voucher (API thật)
    if (showVoucherSelectionSheet) {
        VoucherSelectionBottomSheet(
            promotions = uiState.availablePromotions,
            selectedVoucher = uiState.selectedVoucher,
            orderSubtotal = itemsSubtotal.toDouble(),
            isLoading = uiState.isPromotionsLoading,
            onDismissRequest = { showVoucherSelectionSheet = false },
            onSelectVoucher = { chosen ->
                viewModel.selectVoucher(chosen)
                showVoucherSelectionSheet = false
            },
            onApplyCustomCode = { code ->
                viewModel.applyVoucherByCode(code) { success, _ ->
                    if (success) {
                        showVoucherSelectionSheet = false
                    }
                }
            },
            calculateDiscount = { promo, subtotal ->
                viewModel.computeDiscountAmount(promo, subtotal)
            }
        )
    }
}

/**
 * Hiển thị một dòng sản phẩm thực tế trong giỏ hàng tại màn hình Checkout
 */
@Composable
private fun CheckoutProductRow(
    cartItem: CartItemDto,
    currencyFormatter: NumberFormat,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp))
                .background(Color(0xFFF3F4F6))
        ) {
            val fallbackRes = R.drawable.folded_black_tshirt_1793956682242
            if (!cartItem.thumbnailUrl.isNullOrBlank()) {
                AsyncImage(
                    model = cartItem.thumbnailUrl,
                    contentDescription = cartItem.productName,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = fallbackRes),
                    error = painterResource(id = fallbackRes),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = fallbackRes),
                    contentDescription = cartItem.productName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cartItem.productName ?: "Sản phẩm",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            if (!cartItem.variantName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF3F4F6))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Phân loại: ${cartItem.variantName}",
                        fontSize = 11.5.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val unitPrice = cartItem.priceSnapshot ?: 0.0
                Text(
                    text = "₫${currencyFormatter.format(unitPrice.toLong())}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoralPrimary
                )

                Text(
                    text = "x${cartItem.quantity}",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}
