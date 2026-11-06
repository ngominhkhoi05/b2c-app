package com.example.b2capp.ui

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.viewmodel.OrderSuccessInfo
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

/**
 * Màn hình Đặt Hàng Thành Công (Order Success Screen)
 * Thiết kế chính xác 100% theo OrderSuccessScreen_e1863100.png:
 * 1. Top Bar:
 *    - Chấm xanh lá `● B2C MARKET` bên trái
 *    - Nút Đóng (X) tròn xám bên phải
 * 2. Hero Celebration Graphic & Tiêu đề:
 *    - Icon tích xanh lục nằm trong ô vuông mềm bo góc mint
 *    - Các họa tiết pháo hoa confetti ngộ nghĩnh xung quanh (ngôi sao cam, xanh, thanh xám)
 *    - Tiêu đề in đậm lớn "Đặt hàng thành công!"
 *    - Lời cảm ơn: "Cảm ơn bạn đã mua sắm tại B2C Market. Đơn hàng của bạn đang được người bán tiếp nhận và chuẩn bị gửi đi."
 * 3. Card THÔNG TIN ĐƠN HÀNG:
 *    - Header: Icon sổ đơn hàng, tiêu đề "THÔNG TIN ĐƠN HÀNG", badge "Hợp lệ"
 *    - Mã đơn hàng: ORD-20261105-0088 kèm nút "Sao chép"
 *    - Phương thức: Cổng VNPAY / COD + Tag "✔ Đã thanh toán" / "Chờ thanh toán khi nhận"
 *    - Thời gian đặt: 05/11/2026 14:20
 *    - Dự kiến nhận: 14 Th04 – 16 Th04 (Đồng kiểm khi nhận hàng)
 *    - Tổng thanh toán: ₫388.200 (in đậm màu đỏ cam)
 * 4. Card ĐỊA CHỈ NHẬN HÀNG:
 *    - Icon xe tải xanh ngọc, tiêu đề "ĐỊA CHỈ NHẬN HÀNG"
 *    - Tên & SĐT: Nguyễn Văn A • 090****123
 *    - Địa chỉ: Số 45 Lê Duẩn, Phường Bến Nghé, Q.1, TP. HCM
 *    - Lời nhắn: 🔔 Cập nhật lộ trình sẽ được gửi qua mục Thông báo & SMS.
 * 5. Dòng cam kết chính sách:
 *    - 🛡 Bảo vệ người mua 100%  •  🔄 Đổi trả 15 ngày miễn phí
 * 6. Nút hành động chính:
 *    - Nút viền xanh Navy: "📄 XEM CHI TIẾT ĐƠN HÀNG"
 *    - Nút cam đỏ lớn: "🛍 TIẾP TỤC MUA SẮM"
 */
@Composable
fun OrderSuccessScreen(
    orderInfo: OrderSuccessInfo,
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale("vi", "VN")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .testTag("order_success_screen")
    ) {
        // 1. Top Bar Header
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
                // Nhãn thương hiệu B2C MARKET
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(Color(0xFF10B981), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "B2C MARKET",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF6B433B),
                        letterSpacing = 0.5.sp
                    )
                }

                // Nút X tròn xám
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3F4F6))
                        .clickable {
                            viewModel.closeOrderSuccessScreen(navigateToOrdersTab = false)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Đóng",
                        tint = Color(0xFF374151),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Toàn bộ các thẻ cuộn dọc
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 2. Celebration Hero & Title
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Đồ họa pháo hoa mừng đơn thành công
                    Box(
                        modifier = Modifier.size(110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Trang trí ngôi sao & chấm confetti xung quanh
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(top = 10.dp, start = 12.dp)
                        ) {
                            Text("✦", fontSize = 16.sp, color = Color(0xFFF97316))
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 16.dp, end = 16.dp)
                        ) {
                            Text("✦", fontSize = 18.sp, color = Color(0xFF0D9488))
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(bottom = 20.dp, end = 10.dp)
                        ) {
                            Text("╱", fontSize = 14.sp, color = Color(0xFF2DD4BF))
                        }

                        // Ô icon tích xanh lục chính giữa
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFFD1FAE5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Success",
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Đặt hàng thành công!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF111827),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Cảm ơn bạn đã mua sắm tại B2C Market. Đơn hàng của bạn đang được người bán tiếp nhận và chuẩn bị gửi đi.",
                        fontSize = 13.sp,
                        color = Color(0xFF4B5563),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.5.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            // 3. Card THÔNG TIN ĐƠN HÀNG
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
                        // Header card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.ReceiptLong,
                                    contentDescription = null,
                                    tint = NavySecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "THÔNG TIN ĐƠN HÀNG",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937),
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE6F7F5))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Hợp lệ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00897B)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Mã đơn hàng
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mã đơn hàng",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = orderInfo.orderId,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavySecondary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFF3F4F6))
                                        .clickable {
                                            viewModel.showToast("Đã sao chép mã đơn hàng #${orderInfo.orderId}")
                                        }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Sao chép",
                                        fontSize = 11.sp,
                                        color = Color(0xFF4B5563)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Phương thức thanh toán
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Phương thức",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = orderInfo.paymentMethodName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                if (orderInfo.isOnlinePaid) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFECFDF5))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF059669),
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "Đã thanh toán",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF059669)
                                            )
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFFFF7ED))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Thanh toán khi nhận",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFC2410C)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Thời gian đặt
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Thời gian đặt",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280)
                            )
                            Text(
                                text = orderInfo.orderTime,
                                fontSize = 13.sp,
                                color = Color(0xFF1F2937)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dự kiến nhận
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "Dự kiến nhận",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280)
                            )
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = orderInfo.expectedDelivery,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                                Text(
                                    text = "Đồng kiểm khi nhận hàng",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }

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
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                                Text(
                                    text = "(Đã gồm thuế & phí vận chuyển)",
                                    fontSize = 11.sp,
                                    color = Color(0xFF9CA3AF)
                                )
                            }

                            Text(
                                text = "₫${currencyFormatter.format(orderInfo.totalAmount)}",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }

            // 4. Card ĐỊA CHỈ NHẬN HÀNG
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
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0F2FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalShipping,
                                    contentDescription = null,
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(17.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ĐỊA CHỈ NHẬN HÀNG",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6B7280),
                                    letterSpacing = 0.5.sp
                                )

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = "${orderInfo.recipientName} • ${orderInfo.recipientPhoneMasked}",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = orderInfo.deliveryAddress,
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF4B5563),
                                    lineHeight = 17.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🔔 ",
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Cập nhật lộ trình sẽ được gửi qua mục Thông báo & SMS.",
                                fontSize = 11.5.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }
            }

            // 5. Cam kết chính sách
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFF0D9488),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Bảo vệ người mua 100%",
                            fontSize = 11.5.sp,
                            color = Color(0xFF4B5563)
                        )
                    }

                    Text("•", fontSize = 12.sp, color = Color(0xFFD1D5DB))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color(0xFF0D9488),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Đổi trả 15 ngày miễn phí",
                            fontSize = 11.5.sp,
                            color = Color(0xFF4B5563)
                        )
                    }
                }
            }

            // 6. Nút hành động chính
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Nút XEM CHI TIẾT ĐƠN HÀNG (Viền xanh Navy)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.2.dp, NavySecondary, RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .clickable {
                                viewModel.closeOrderSuccessScreen(navigateToOrdersTab = true)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.ReceiptLong,
                                contentDescription = null,
                                tint = NavySecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "XEM CHI TIẾT ĐƠN HÀNG",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavySecondary
                            )
                        }
                    }

                    // Nút TIẾP TỤC MUA SẮM (Cam đỏ)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CoralPrimary)
                            .clickable {
                                viewModel.closeOrderSuccessScreen(navigateToOrdersTab = false)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingBag,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TIẾP TỤC MUA SẮM",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
