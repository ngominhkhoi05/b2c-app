package com.example.b2capp.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

/**
 * Màn hình Chi Tiết Đơn Hàng & Lộ Trình Vận Đơn (Order Details & Tracking Screen)
 * Thiết kế chính xác 100% theo OrderDetailsAndTrackingScreen_20b4c46c.png:
 * 1. Top Bar:
 *    - Nút Back (<-)
 *    - Tiêu đề "Shopping Cart", nút văn bản đỏ "Sửa", avatar tròn đỏ
 * 2. Breadcrumb Header Đơn hàng:
 *    - "ĐƠN HÀNG • Chi tiết vận đơn"
 *    - mã đơn "#ORD-20261105-0088" kèm icon Copy
 *    - Nút pill xanh dương "🎧 Trợ giúp"
 * 3. Hero Status Banner (Xanh Navy):
 *    - Icon xe tải xanh ngọc
 *    - Trạng thái "• ĐANG GIAO HÀNG" (nhãn xanh teal)
 *    - Tiêu đề "Đơn hàng đang được giao đến bạn"
 *    - Thời gian: "🕒 Dự kiến trước 18:00, 07/11/2026"
 *    - Khung SPX Express • SPX2026VN9988 kèm nút "📋 Sao chép"
 * 4. Card Lộ trình đơn hàng (Vertical Timeline):
 *    - Tiêu đề "🔴 Lộ trình đơn hàng", link "Chi tiết >"
 *    - Bước 1: 🚚 Đang vận chuyển (08:30 06/11)
 *      Nội dung: Kiện hàng đã rời kho phân loại Tân Bình và đang trên đường giao tới bạn.
 *      Tag tài xế: "📄 Tài xế: Nguyễn Văn B (0912***456)"
 *    - Bước 2: ✔ Người bán chuẩn bị hàng (16:00 05/11)
 *      Nội dung: Kiện hàng đã được đóng gói kỹ lưỡng và bàn giao đơn vị SPX.
 *    - Bước 3: ✔ Đơn hàng đã đặt thành công (14:20 05/11)
 *      Nội dung: Đã xác thực thanh toán tức thì qua cổng VNPAY-QR.
 *    - Bước 4: ⚪ Giao hàng thành công (Dự kiến 07/11)
 *      Nội dung: Vui lòng chuẩn bị sẵn sàng nhận máy và kiểm tra kiện hàng.
 * 5. Card Địa chỉ nhận hàng:
 *    - Icon ghim đỏ "📍 Địa chỉ nhận hàng"
 *    - Người nhận: Nguyễn Văn A (+84) 987 654 321, nhãn "Mặc định"
 *    - Địa chỉ: Số 123 Đường Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh
 *    - Khung nhắc nhở: "🕒 Lưu ý: Giao giờ hành chính, gọi trước 15 phút"
 * 6. Card Sản phẩm đơn hàng Coolmate Official Store:
 *    - Badge "MALL", Tên "Coolmate Official Store" kèm icon cửa hàng & mũi tên >
 *    - Ảnh áo thun đen, Tên "Áo Thun Nam Cotton Compact Siêu Thoáng Khí Cao Cấp Chống...", Phân loại "Đen, L", Số lượng x2, Giá ₫398.000
 *    - Khung quà tặng đính kèm: "🎁 Tặng kèm: 1x Túi vải Canvas B2C Eco Limited - ₫0"
 * 7. Card Thông tin thanh toán:
 *    - Icon "🧾 Thông tin thanh toán"
 *    - Bảng chi tiết: Tổng tiền hàng ₫398.000, Phí vận chuyển ₫30.000, Giảm giá phí vận chuyển -₫30.000, Voucher đơn hàng (GIAM10) -₫39.800, Dùng 200 Xu B2C -₫200
 *    - Tổng thanh toán: ₫388.200 (In đậm đỏ 20sp)
 *    - Thẻ VNPAY: Logo VNPAY "Cổng VNPAY-QR / Mã GD: VNP1458291" + Tag "✔ Hoàn tất"
 * 8. Khung tóm tắt đơn hàng màu xám:
 *    - Mã đơn hàng: ORD-20261105-0088
 *    - Thời gian đặt hàng: 05/11/2026 14:20:15
 *    - Thời gian thanh toán: 05/11/2026 14:21:02
 *    - Hình thức giao hàng: Giao hàng Tiêu chuẩn (2-3 ngày)
 * 9. Thanh nút hành động dính đáy:
 *    - Nút xanh nhạt: "💬 Liên hệ Shop"
 *    - Nút đỏ lớn: "✔ ĐÃ NHẬN ĐƯỢC HÀNG"
 */
@Composable
fun OrderDetailsAndTrackingScreen(
    orderId: String,
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale("vi", "VN")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .testTag("order_details_tracking_screen")
    ) {
        // 1. Top Bar Header
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                        text = "Shopping Cart",
                        fontSize = 17.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Sửa",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB82414),
                        modifier = Modifier
                            .clickable { viewModel.showToast("Chế độ chỉnh sửa thông tin đơn hàng") }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFB82414)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Tài khoản",
                            tint = Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }
        }

        // Toàn bộ các phần cuộn dọc
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 2. Breadcrumb & Mã đơn hàng & Nút Trợ giúp
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ĐƠN HÀNG",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavySecondary
                            )
                            Text(
                                text = " • Chi tiết vận đơn",
                                fontSize = 11.5.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "#$orderId",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF111827)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Sao chép",
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier
                                    .size(15.dp)
                                    .clickable {
                                        viewModel.showToast("Đã sao chép mã đơn hàng #$orderId")
                                    }
                            )
                        }
                    }

                    // Nút Trợ giúp
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFBFDBFE))
                            .clickable {
                                viewModel.openShopMessages()
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HeadsetMic,
                                contentDescription = null,
                                tint = Color(0xFF1E40AF),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Trợ giúp",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF)
                            )
                        }
                    }
                }
            }

            // 3. Hero Status Banner (Xanh Navy)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2C4C70))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalShipping,
                                    contentDescription = null,
                                    tint = Color(0xFF5EEAD4),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "• ĐANG GIAO HÀNG",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF2DD4BF)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Đơn hàng đang được giao đến bạn",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "🕒 Dự kiến trước 18:00, 07/11/2026",
                                    fontSize = 12.5.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Khung mã vận đơn SPX Express
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocalShipping,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "SPX Express • SPX2026VN9988",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White.copy(alpha = 0.25f))
                                        .clickable {
                                            viewModel.showToast("Đã sao chép mã vận đơn SPX2026VN9988")
                                        }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "📋 Sao chép",
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Card Lộ trình đơn hàng (Vertical Timeline)
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFB82414))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Lộ trình đơn hàng",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                            }

                            Text(
                                text = "Chi tiết >",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1E5B94),
                                modifier = Modifier.clickable {
                                    viewModel.showToast("Mở chi tiết nhật ký vận chuyển SPX")
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Lộ trình 4 bước dọc
                        // Bước 1: Đang vận chuyển
                        TimelineStepItem(
                            stepIcon = Icons.Default.LocalShipping,
                            iconBg = Color(0xFF3B82F6),
                            iconTint = Color.White,
                            title = "Đang vận chuyển",
                            time = "08:30 06/11",
                            description = "Kiện hàng đã rời kho phân loại Tân Bình và đang trên đường giao tới bạn.",
                            driverTag = "Tài xế: Nguyễn Văn B (0912***456)",
                            isLineActive = true,
                            isTitleBold = true,
                            titleColor = Color(0xFF1D4ED8)
                        )

                        // Bước 2: Người bán chuẩn bị hàng
                        TimelineStepItem(
                            stepIcon = Icons.Default.Check,
                            iconBg = Color(0xFF059669),
                            iconTint = Color.White,
                            title = "Người bán chuẩn bị hàng",
                            time = "16:00 05/11",
                            description = "Kiện hàng đã được đóng gói kỹ lưỡng và bàn giao đơn vị SPX.",
                            isLineActive = true
                        )

                        // Bước 3: Đơn hàng đã đặt thành công
                        TimelineStepItem(
                            stepIcon = Icons.Default.Check,
                            iconBg = Color(0xFF059669),
                            iconTint = Color.White,
                            title = "Đơn hàng đã đặt thành công",
                            time = "14:20 05/11",
                            description = "Đã xác thực thanh toán tức thì qua cổng VNPAY–QR.",
                            isLineActive = false
                        )

                        // Bước 4: Giao hàng thành công (Dự kiến)
                        TimelineStepItem(
                            stepIcon = null,
                            iconBg = Color(0xFFF3F4F6),
                            iconTint = Color(0xFF9CA3AF),
                            title = "Giao hàng thành công",
                            time = "Dự kiến 07/11",
                            description = "Vui lòng chuẩn bị sẵn sàng nhận máy và kiểm tra kiện hàng.",
                            isLineActive = false,
                            isLast = true,
                            titleColor = Color(0xFF9CA3AF)
                        )
                    }
                }
            }

            // 5. Card Địa chỉ nhận hàng
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFFB82414),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Địa chỉ nhận hàng",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Nguyễn Văn A",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(+84) 987 654 321",
                                fontSize = 12.5.sp,
                                color = Color(0xFF6B7280)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFFF3F4F6))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "Mặc định",
                                    fontSize = 10.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Số 123 Đường Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh",
                            fontSize = 12.5.sp,
                            color = Color(0xFF4B5563),
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Khung lưu ý
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🕒 Lưu ý: Giao giờ hành chính, gọi trước 15 phút",
                                fontSize = 11.5.sp,
                                color = Color(0xFF335C85)
                            )
                        }
                    }
                }
            }

            // 6. Card Sản phẩm đơn hàng Coolmate Official Store
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.openShopMessages() },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
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
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = NavySecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.folded_black_tshirt_1793956682242),
                                contentDescription = "Áo thun đen",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp))
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Áo Thun Nam Cotton Compact Siêu Thoáng Khí Cao Cấp Chống...",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
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
                                        text = "Số lượng: x2",
                                        fontSize = 12.5.sp,
                                        color = Color(0xFF6B7280)
                                    )

                                    Text(
                                        text = "₫398.000",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CoralPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Thẻ quà tặng đính kèm
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFFF7ED))
                                .border(0.8.dp, Color(0xFFFFEDD5), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🎁 ", fontSize = 12.sp)
                                    Text(
                                        text = "Tặng kèm: ",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF9A3412)
                                    )
                                    Text(
                                        text = "1x Túi vải Canvas B2C Eco Limited",
                                        fontSize = 12.sp,
                                        color = Color(0xFF9A3412)
                                    )
                                }

                                Text(
                                    text = "₫0",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9A3412)
                                )
                            }
                        }
                    }
                }
            }

            // 7. Card Thông tin thanh toán
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = NavySecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Thông tin thanh toán",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        PaymentDetailLine(label = "Tổng tiền hàng", value = "₫398.000")
                        Spacer(modifier = Modifier.height(6.dp))
                        PaymentDetailLine(label = "Phí vận chuyển", value = "₫30.000")
                        Spacer(modifier = Modifier.height(6.dp))
                        PaymentDetailLine(
                            icon = Icons.Default.LocalShipping,
                            label = "Giảm giá phí vận chuyển",
                            value = "-₫30.000",
                            valueColor = Color(0xFF00897B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        PaymentDetailLine(
                            icon = Icons.Outlined.ConfirmationNumber,
                            label = "Voucher đơn hàng (GIAM10)",
                            value = "-₫39.800",
                            valueColor = Color(0xFF00897B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        PaymentDetailLine(
                            icon = Icons.Default.Redeem,
                            label = "Dùng 200 Xu B2C",
                            value = "-₫200",
                            valueColor = Color(0xFF00897B)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Tổng thanh toán
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Tổng thanh toán",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                                Text(
                                    text = "Đã bao gồm VAT & phí vận chuyển",
                                    fontSize = 11.sp,
                                    color = Color(0xFF9CA3AF)
                                )
                            }

                            Text(
                                text = "₫388.200",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Black,
                                color = CoralPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Phương thức VNPAY hoàn tất
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            border = BorderStroke(0.8.dp, Color(0xFFE2E8F0))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFEBF3FC))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "VNPAY",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF005BAA)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Cổng VNPAY–QR",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF111827)
                                        )
                                        Text(
                                            text = "Mã GD: VNP1458291",
                                            fontSize = 10.5.sp,
                                            color = Color(0xFF6B7280)
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFE6F7F5))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color(0xFF00897B),
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Hoàn tất",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF00897B)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 8. Khung thông số đơn hàng
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OrderMetaRow(label = "Mã đơn hàng", value = "ORD-20261105-0088")
                        OrderMetaRow(label = "Thời gian đặt hàng", value = "05/11/2026 14:20:15")
                        OrderMetaRow(label = "Thời gian thanh toán", value = "05/11/2026 14:21:02")
                        OrderMetaRow(label = "Hình thức giao hàng", value = "Giao hàng Tiêu chuẩn (2-3 ngày)")
                    }
                }
            }
        }

        // 9. Thanh nút hành động dính đáy
        Surface(
            color = SurfaceCard,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nút "Liên hệ Shop" xanh nhạt
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFBFDBFE))
                        .clickable {
                            viewModel.openShopMessages()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = null,
                            tint = Color(0xFF1E40AF),
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Liên hệ Shop",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF)
                        )
                    }
                }

                // Nút "ĐÃ NHẬN ĐƯỢC HÀNG" đỏ
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFBA2211))
                        .clickable {
                            viewModel.showToast("Cảm ơn bạn đã xác nhận nhận hàng thành công!")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ĐÃ NHẬN ĐƯỢC HÀNG",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Item từng bước trong Timeline lộ trình
 */
@Composable
private fun TimelineStepItem(
    stepIcon: androidx.compose.ui.graphics.vector.ImageVector?,
    iconBg: Color,
    iconTint: Color,
    title: String,
    time: String,
    description: String,
    driverTag: String? = null,
    isLineActive: Boolean = false,
    isLast: Boolean = false,
    isTitleBold: Boolean = false,
    titleColor: Color = Color(0xFF111827)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Cột icon & đường nối xám dọc
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                if (stepIcon != null) {
                    Icon(
                        imageVector = stepIcon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(13.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD1D5DB))
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(if (driverTag != null) 64.dp else 46.dp)
                        .background(if (isLineActive) Color(0xFF3B82F6) else Color(0xFFE5E7EB))
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Cột nội dung chi tiết
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = if (isTitleBold) FontWeight.Bold else FontWeight.SemiBold,
                    color = titleColor
                )
                Text(
                    text = time,
                    fontSize = 11.5.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF6B7280),
                lineHeight = 16.5.sp
            )

            if (driverTag != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFBFDBFE))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "📄 $driverTag",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

/**
 * Dòng chi tiết phí trong thẻ Thông tin thanh toán
 */
@Composable
private fun PaymentDetailLine(
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    label: String,
    value: String,
    valueColor: Color = Color(0xFF374151)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF00897B),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = label,
                fontSize = 12.5.sp,
                color = Color(0xFF6B7280)
            )
        }

        Text(
            text = value,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

/**
 * Dòng thông số đơn hàng
 */
@Composable
private fun OrderMetaRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )
    }
}
