package com.example.b2capp.features.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import kotlinx.coroutines.delay

/**
 * Màn hình Cổng Thanh Toán VNPAY-QR / Sandbox Payment
 * Thiết kế chính xác 100% theo VnpaySandboxPaymentScreen__1__61098749.png:
 * 1. Top Bar:
 *    - Nút Mũi tên quay lại
 *    - Tiêu đề "Thanh toán VNPAY-QR", phụ đề "Giao dịch bảo mật 100%"
 *    - Icon trợ giúp (?) và nút Đóng (X)
 * 2. Header Đơn hàng & Thương hiệu:
 *    - Avatar CM + Tên "Coolmate Official Store" + Tích xanh verified
 *    - Mã đơn hàng trực tuyến #ORD-20261105
 *    - Logo VNPAY góc phải
 * 3. Thẻ Thông tin thanh toán:
 *    - Mã đơn hàng: ORD-20261105-0088
 *    - Số tiền thanh toán: ₫388.200 (màu đỏ cam 22sp in đậm), tag "✔ Miễn phí giao dịch"
 *    - Khung đếm ngược thời gian hủ đơn tự động "⏳ Giao dịch tự động hủy sau: 14:59"
 * 4. Thanh chọn phương thức thanh toán chính:
 *    - [VNPAY-QR] (Được chọn, viền nhô) | [Thẻ ATM / Bank] | [Thẻ Quốc tế]
 * 5. Khung Mã QR & Thao tác chuyển khoản:
 *    - Tiêu đề step "1 Quét mã qua App Ngân hàng / Ví", pill "Hỗ trợ 40+ App"
 *    - Khung vuông QR code VNPAY chính xác với tâm logo VNPAY QR PAY và vạch laser đỏ
 *    - Chú thích: Quét mã VNPAY-QR bằng Ngân hàng hoặc Ví VNPAY
 *    - Nút màu đỏ lớn "MỞ APP NGÂN HÀNG THANH TOÁN"
 *    - Row nút "Tải mã QR / Lưu vào máy" & nút Share
 *    - Quy trình 3 bước: 1 Mở App -> 2 Chọn Quét QR -> 3 Xác nhận thanh toán
 *    - Mục "Ngân hàng hỗ trợ thanh toán" (40+ Ngân hàng & Ví): Lưới 8 Ngân hàng tiêu biểu: Vietcombank, Techcombank, MB Bank, BIDV, VPBank, ACB, VietinBank, +34 Khác
 * 6. Các phương thức khác:
 *    - Thẻ ATM & Tài khoản ngân hàng (Napas nội địa, Internet Banking 42 ngân hàng)
 *    - Thẻ thanh toán Quốc tế (3D Secure: Visa, MasterCard, JCB, UnionPay, American Express)
 * 7. Nút HỦY GIAO DỊCH & QUAY LẠI ĐƠN HÀNG
 * 8. Footer chứng nhận bảo mật & hotline CSKH VNPAY:
 *    - 🛡 PCI-DSS Level 1 | 🛡 NAPAS Switch | 🛡 SBV Licensed
 *    - Hotline hỗ trợ 24/7: 1900 55 55 77 | hotrovnpay@vnpay.vn
 */
@Composable
fun VnpaySandboxPaymentScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMainTab by remember { mutableIntStateOf(0) } // 0: VNPAY-QR, 1: Thẻ ATM/Bank, 2: Thẻ Quốc tế
    var countdownSeconds by remember { mutableIntStateOf(899) } // 14:59

    // Đếm ngược thời gian giao dịch
    LaunchedEffect(Unit) {
        while (countdownSeconds > 0) {
            delay(1000)
            countdownSeconds--
        }
    }

    val minutes = countdownSeconds / 60
    val seconds = countdownSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7))
            .testTag("vnpay_payment_screen")
    ) {
        // 1. Top Bar Header
        Surface(
            color = SurfaceCard,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Column {
                        Text(
                            text = "Thanh toán VNPAY–QR",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        Text(
                            text = "Giao dịch bảo mật 100%",
                            fontSize = 11.5.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.showToast("Trung tâm trợ giúp VNPAY 1900 55 55 77") },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Trợ giúp",
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 2. Thông tin thương hiệu Coolmate Official Store & VNPAY Header
        Surface(
            color = SurfaceCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5E7EB)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "CM",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Coolmate Official Store",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = Color(0xFF00897B),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Đơn hàng trực tuyến #ORD-20261105",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                // Logo VNPAY
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "VN",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF005BAA)
                    )
                    Text(
                        text = "PAY",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE31B23)
                    )
                }
            }
        }

        HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 0.8.dp)

        // Toàn bộ các thẻ cuộn dọc
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 3. Thẻ Mã đơn hàng & Số tiền thanh toán
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(0.8.dp, Color(0xFFE5E7EB))
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
                                text = "Mã đơn hàng:",
                                fontSize = 13.5.sp,
                                color = Color(0xFF6B7280)
                            )
                            Text(
                                text = "ORD-20261105-0088",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Số tiền thanh toán:",
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B7280)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF00897B),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Miễn phí giao dịch",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF00897B)
                                    )
                                }
                            }

                            Text(
                                text = "₫388.200",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFE31B23)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Khung tự động hủy giao dịch sau 14:59
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFF1F2))
                                .border(0.8.dp, Color(0xFFFECDD3), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.HourglassTop,
                                        contentDescription = null,
                                        tint = Color(0xFFBE123C),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Giao dịch tự động hủy sau:",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1F2937)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White)
                                        .border(0.8.dp, Color(0xFFFDA4AF), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = formattedTime,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFBE123C)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Thanh chọn tab phương thức thanh toán chính
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE2E8F0).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        PaymentMainTabButton(
                            title = "VNPAY–QR",
                            icon = Icons.Default.QrCodeScanner,
                            isSelected = selectedMainTab == 0,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedMainTab = 0 }
                        )

                        PaymentMainTabButton(
                            title = "Thẻ ATM / Bank",
                            icon = Icons.Default.CreditCard,
                            isSelected = selectedMainTab == 1,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedMainTab = 1 }
                        )

                        PaymentMainTabButton(
                            title = "Thẻ Quốc tế",
                            icon = Icons.Default.AccountBalanceWallet,
                            isSelected = selectedMainTab == 2,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedMainTab = 2 }
                        )
                    }
                }
            }

            // 5. Khung Mã QR & Thao tác chuyển khoản
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(0.8.dp, Color(0xFFE5E7EB))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Step Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF005BAA)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "1",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Quét mã qua App Ngân hàng / Ví",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE6F7F5))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Hỗ trợ 40+ App",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00897B)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Khung chứa Mã QR Code VNPAY chân thực
                        Box(
                            modifier = Modifier
                                .size(230.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.5.dp, Color(0xFF005BAA), RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Hình vuông mô phỏng QR Code VNPAY với 3 góc định vị & tâm logo
                            VnpayQrCodeGraphic()
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Quét mã VNPAY–QR bằng Ngân hàng hoặc Ví VNPAY",
                            fontSize = 12.5.sp,
                            color = Color(0xFF374151),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Nút lớn "MỞ APP NGÂN HÀNG THANH TOÁN"
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFB82414))
                                .clickable {
                                    viewModel.completeVnpayPayment()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "MỞ APP NGÂN HÀNG THANH TOÁN",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Hàng nút Tải mã QR / Lưu & Share
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(1.dp, Color(0xFF005BAA), RoundedCornerShape(6.dp))
                                    .clickable {
                                        viewModel.showToast("Đã lưu hình ảnh VNPAY-QR vào bộ sưu tập!")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = null,
                                        tint = Color(0xFF005BAA),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Tải mã QR / Lưu vào máy",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF005BAA)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp))
                                    .clickable {
                                        viewModel.showToast("Đã chia sẻ liên kết thanh toán")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Chia sẻ",
                                    tint = Color(0xFF6B7280),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Quy trình 3 bước hướng dẫn
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            InstructionStepItem(
                                step = "1",
                                title = "Mở App Ngân hàng\nhoặc Ví VNPAY"
                            )
                            InstructionStepItem(
                                step = "2",
                                title = "Chọn tính năng \"Quét\nQR\""
                            )
                            InstructionStepItem(
                                step = "3",
                                title = "Xác nhận thanh toán"
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Danh sách Ngân hàng hỗ trợ thanh toán
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = null,
                                    tint = Color(0xFF005BAA),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Ngân hàng hỗ trợ thanh toán",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                            }
                            Text(
                                text = "40+ Ngân hàng & Ví",
                                fontSize = 11.5.sp,
                                color = Color(0xFF6B7280)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Lưới 8 Ngân hàng hỗ trợ tiêu biểu
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                BankLogoBox(
                                    name = "Vietcombank",
                                    code = "VCB",
                                    bgColor = Color(0xFFE8F5E9),
                                    textColor = Color(0xFF1B5E20),
                                    modifier = Modifier.weight(1f)
                                )
                                BankLogoBox(
                                    name = "Techcombank",
                                    code = "TCB",
                                    bgColor = Color(0xFFFFEBEE),
                                    textColor = Color(0xFFC62828),
                                    modifier = Modifier.weight(1f)
                                )
                                BankLogoBox(
                                    name = "MB Bank",
                                    code = "MB",
                                    bgColor = Color(0xFFE8EAF6),
                                    textColor = Color(0xFF1A237E),
                                    modifier = Modifier.weight(1f)
                                )
                                BankLogoBox(
                                    name = "BIDV",
                                    code = "BIDV",
                                    bgColor = Color(0xFFE0F2F1),
                                    textColor = Color(0xFF004D40),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                BankLogoBox(
                                    name = "VPBank",
                                    code = "VPB",
                                    bgColor = Color(0xFFE8F5E9),
                                    textColor = Color(0xFF2E7D32),
                                    modifier = Modifier.weight(1f)
                                )
                                BankLogoBox(
                                    name = "ACB",
                                    code = "ACB",
                                    bgColor = Color(0xFFE3F2FD),
                                    textColor = Color(0xFF1565C0),
                                    modifier = Modifier.weight(1f)
                                )
                                BankLogoBox(
                                    name = "VietinBank",
                                    code = "CTG",
                                    bgColor = Color(0xFFE0F7FA),
                                    textColor = Color(0xFF00838F),
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFF3F4F6))
                                        .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+34 Khác",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. PHƯƠNG THỨC KHÁC
            item {
                Text(
                    text = "PHƯƠNG THỨC KHÁC",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280),
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
            }

            // Thẻ ATM & Tài khoản ngân hàng
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedMainTab = 1 },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(0.8.dp, Color(0xFFE5E7EB))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = Color(0xFF1E5B94),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Thẻ ATM & Tài khoản ngân hàng",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Napas nội địa, Internet Banking 42 ngân hàng",
                                fontSize = 11.5.sp,
                                color = Color(0xFF6B7280)
                            )
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

            // Thẻ thanh toán Quốc tế (Visa, MasterCard, JCB, 3D Secure)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedMainTab = 2 },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(0.8.dp, Color(0xFFE5E7EB))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFF1F2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = Color(0xFFBE123C),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Thẻ thanh toán Quốc tế",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color(0xFFEBF3FC))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "3D Secure",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E5B94)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Visa, MasterCard, JCB, UnionPay, American Express",
                                fontSize = 11.5.sp,
                                color = Color(0xFF6B7280)
                            )
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

            // 7. Nút HỦY GIAO DỊCH & QUAY LẠI ĐƠN HÀNG
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .clickable {
                            onBackClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color(0xFF4B5563),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HỦY GIAO DỊCH & QUAY LẠI ĐƠN HÀNG",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4B5563)
                        )
                    }
                }
            }

            // 8. Footer chứng nhận bảo mật & Hotline VNPAY
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SecurityBadgeItem(text = "PCI-DSS Level 1")
                        SecurityBadgeItem(text = "NAPAS Switch")
                        SecurityBadgeItem(text = "SBV Licensed")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Hotline hỗ trợ 24/7: 1900 55 55 77 | hotrovnpay@vnpay.vn",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4B5563)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Bản quyền thuộc VNPAY • Đảm bảo an toàn theo tiêu chuẩn quốc tế",
                        fontSize = 10.5.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }
        }
    }
}

/**
 * Nút chuyển tab phương thức thanh toán chính VNPAY
 */
@Composable
private fun PaymentMainTabButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color(0xFF005BAA) else Color(0xFF6B7280),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color(0xFF005BAA) else Color(0xFF6B7280)
            )
        }
    }
}

/**
 * Đồ họa QR Code VNPAY giả lập chân thực
 */
@Composable
private fun VnpayQrCodeGraphic() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Khung QR Code với vạch đỏ giữa
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QrCornerSquare()
                QrCornerSquare()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color(0xFFE31B23))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QrCornerSquare()
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFF111827), RoundedCornerShape(4.dp))
                )
            }
        }

        // Tâm Logo VNPAY QR PAY
        Card(
            shape = RoundedCornerShape(6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(0.8.dp, Color(0xFF005BAA))
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "VN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF005BAA)
                    )
                    Text(
                        text = "PAY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE31B23)
                    )
                }
                Text(
                    text = "QR PAY",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF374151)
                )
            }
        }
    }
}

/**
 * Góc vuông QR Code
 */
@Composable
private fun QrCornerSquare() {
    Box(
        modifier = Modifier
            .size(42.dp)
            .background(Color(0xFF005BAA), RoundedCornerShape(6.dp))
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, RoundedCornerShape(3.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF005BAA), RoundedCornerShape(2.dp))
            )
        }
    }
}

/**
 * Bước hướng dẫn quy trình 3 bước
 */
@Composable
private fun InstructionStepItem(
    step: String,
    title: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B5563)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 10.5.sp,
            color = Color(0xFF4B5563),
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

/**
 * Ô Logo Ngân Hàng hỗ trợ
 */
@Composable
private fun BankLogoBox(
    name: String,
    code: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(0.8.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .clickable { /* Chọn bank */ },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = code,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = textColor
            )
            Text(
                text = name,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Item huy hiệu bảo mật footer
 */
@Composable
private fun SecurityBadgeItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = Color(0xFF00897B),
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF4B5563)
        )
    }
}
