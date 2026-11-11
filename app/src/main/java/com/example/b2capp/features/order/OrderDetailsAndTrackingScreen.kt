package com.example.b2capp.features.order

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
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.R
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import com.example.b2capp.core.ui.components.AppButton
import com.example.b2capp.features.order.components.OrderMetaRow
import com.example.b2capp.features.order.components.PaymentDetailLine
import com.example.b2capp.features.order.components.TimelineStepItem
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import java.text.NumberFormat
import java.util.Locale

/**
 * Màn hình Chi Tiết Đơn Hàng & Lộ Trình Vận Đơn (Order Details & Tracking Screen)
 * - Tự động gọi API GET /api/v1/buyer/orders/{id} khi mở màn hình
 * - Bắt buộc chờ tải dữ liệu thật từ API, không hiển thị dữ liệu mock khi đang tải
 * - Lộ trình đơn hàng:
 *   + COD: Đơn hàng đặt hàng thành công -> Người bán chuẩn bị hàng -> Đang vận chuyển -> Giao hàng thành công
 *   + VNPay chưa thanh toán: Đơn hàng đặt hàng thành công -> Chờ thanh toán -> Người bán chuẩn bị hàng -> Đang vận chuyển -> Giao hàng thành công
 *   + VNPay đã thanh toán: Đơn hàng đặt hàng thành công -> Đã thanh toán -> Người bán chuẩn bị hàng -> Đang vận chuyển -> Giao hàng thành công
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

    // Dialog xác nhận hủy đơn hàng
    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("Tôi muốn cập nhật lại địa chỉ nhận hàng") }

    // Dialog yêu cầu trả hàng / hoàn tiền
    var showReturnDialog by remember { mutableStateOf(false) }
    var returnReasonKey by remember { mutableStateOf("DAMAGED") }
    var returnDescription by remember { mutableStateOf("") }

    val detail = uiState.activeOrderDetail
    val resolvedNumericId = detail?.id
        ?: orderId.toLongOrNull()
        ?: uiState.orders.firstOrNull { it.orderCode == orderId || "#${it.orderCode}" == orderId }?.id

    // Tự động gọi API lấy chi tiết đơn hàng nếu chưa có dữ liệu đúng của đơn này
    LaunchedEffect(orderId, resolvedNumericId) {
        if (resolvedNumericId != null && detail?.id != resolvedNumericId && !uiState.isOrderDetailLoading) {
            viewModel.loadOrderDetail(resolvedNumericId)
        }
    }

    // Đăng ký nhận cập nhật trạng thái đơn hàng theo thời gian thực qua WebSocket (/topic/order/{id})
    DisposableEffect(resolvedNumericId) {
        val targetId = resolvedNumericId
        if (targetId != null) {
            viewModel.subscribeOrderDetailRealtime(targetId)
        }
        onDispose {
            if (targetId != null) {
                viewModel.unsubscribeOrderDetailRealtime(targetId)
            }
        }
    }

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
                        text = "Chi tiết đơn hàng",
                        fontSize = 17.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                }

                if (resolvedNumericId != null) {
                    IconButton(
                        onClick = { viewModel.loadOrderDetail(resolvedNumericId) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Làm mới chi tiết",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Khi chưa load xong API: bắt người dùng chờ, tuyệt đối không hiển thị dữ liệu mock
        if (uiState.isOrderDetailLoading || (detail == null && uiState.orderDetailErrorMessage == null)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = CoralPrimary,
                        modifier = Modifier.size(40.dp),
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Đang tải thông tin đơn hàng...",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }
            return@Column
        }

        // Khi có lỗi tải API và chưa có dữ liệu chi tiết
        if (detail == null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.orderDetailErrorMessage ?: "Không tải được thông tin đơn hàng",
                            color = Color(0xFF991B1B),
                            fontSize = 13.5.sp,
                            textAlign = TextAlign.Center
                        )
                        if (resolvedNumericId != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            AppButton(
                                text = "Thử lại",
                                onClick = { viewModel.loadOrderDetail(resolvedNumericId) },
                                modifier = Modifier.width(120.dp)
                            )
                        }
                    }
                }
            }
            return@Column
        }

        // Từ đây trở xuống: detail != null (Dữ liệu thật từ API Backend)
        val rawId = detail.id
        val displayOrderCode = detail.orderCode ?: detail.id.toString()

        val status = detail.status?.uppercase() ?: ""
        val isPending = status == "PENDING"
        val isCancelled = status == "CANCELLED"
        val isDelivered = status == "DELIVERED"
        val isCompleted = status == "COMPLETED"
        val isDeliveredOrCompleted = isDelivered || isCompleted
        val isShipping = status == "SHIPPING"
        val isConfirmed = status == "CONFIRMED" || status == "PACKED"
        val isReturnRequested = status == "RETURN_REQUESTED"
        val isReturned = status == "RETURNED"

        val isCod = detail.paymentMethod.equals("COD", ignoreCase = true)
        val isPaid = detail.paymentStatus.equals("PAID", ignoreCase = true) || isDeliveredOrCompleted

        // Visuals của Hero Status Banner dựa trên trạng thái thật
        val bannerVisuals = when (status) {
            "PENDING" -> when {
                isCod -> BannerVisuals(
                    bg = Color(0xFF1E3A8A),
                    tagText = "• NGƯỜI BÁN CHUẨN BỊ HÀNG",
                    tagColor = Color(0xFF93C5FD),
                    title = "Người bán đang chuẩn bị đơn hàng (COD)",
                    subtitle = "Đơn hàng đã đặt thành công và được chuyển cho người bán chuẩn bị",
                    icon = Icons.Outlined.Inventory2
                )
                isPaid -> BannerVisuals(
                    bg = Color(0xFF065F46),
                    tagText = "• ĐÃ THANH TOÁN",
                    tagColor = Color(0xFFA7F3D0),
                    title = "Đơn hàng đã thanh toán thành công",
                    subtitle = "Đơn hàng đã được chuyển cho người bán tiếp nhận và chuẩn bị hàng",
                    icon = Icons.Default.Verified
                )
                else -> BannerVisuals(
                    bg = Color(0xFF854D0E),
                    tagText = "• CHỜ THANH TOÁN",
                    tagColor = Color(0xFFFDE68A),
                    title = "Đơn hàng đang chờ thanh toán",
                    subtitle = "Vui lòng hoàn tất thanh toán để đơn hàng được chuyển cho người bán",
                    icon = Icons.Outlined.AccessTime
                )
            }
            "CONFIRMED" -> BannerVisuals(
                bg = Color(0xFF1E3A8A),
                tagText = "• ĐÃ XÁC NHẬN",
                tagColor = Color(0xFF93C5FD),
                title = "Người bán đang chuẩn bị hàng",
                subtitle = "Đơn hàng đã được người bán xác nhận và chuẩn bị đóng gói",
                icon = Icons.Default.Verified
            )
            "PACKED" -> BannerVisuals(
                bg = Color(0xFF4338CA),
                tagText = "• ĐÃ ĐÓNG GÓI",
                tagColor = Color(0xFFC7D2FE),
                title = "Đơn hàng đã được đóng gói",
                subtitle = "Đang chờ bàn giao cho đơn vị vận chuyển",
                icon = Icons.Outlined.Inventory2
            )
            "SHIPPING" -> BannerVisuals(
                bg = Color(0xFF2C4C70),
                tagText = "• ĐANG GIAO HÀNG",
                tagColor = Color(0xFF2DD4BF),
                title = "Đơn hàng đang được giao đến bạn",
                subtitle = "Kiện hàng đang trên đường vận chuyển tới địa chỉ nhận hàng",
                icon = Icons.Default.LocalShipping
            )
            "DELIVERED" -> BannerVisuals(
                bg = Color(0xFF065F46),
                tagText = "• ĐÃ GIAO HÀNG",
                tagColor = Color(0xFFA7F3D0),
                title = "Kiện hàng đã được giao tới bạn",
                subtitle = "Vui lòng kiểm tra hàng và xác nhận đã nhận hàng",
                icon = Icons.Default.CheckCircle
            )
            "COMPLETED" -> BannerVisuals(
                bg = Color(0xFF047857),
                tagText = "• HOÀN TẤT ĐƠN HÀNG",
                tagColor = Color(0xFFA7F3D0),
                title = "Đơn hàng đã hoàn thành",
                subtitle = "Cảm ơn bạn đã tin tưởng mua sắm!",
                icon = Icons.Default.CheckCircle
            )
            "RETURN_REQUESTED" -> BannerVisuals(
                bg = Color(0xFF9A3412),
                tagText = "• ĐÃ YÊU CẦU TRẢ HÀNG",
                tagColor = Color(0xFFFED7AA),
                title = "Đang xử lý yêu cầu trả hàng / hoàn tiền",
                subtitle = if (!detail.returnReason.isNullOrBlank()) "Lý do: ${detail.returnReason}" else "Đang chờ người bán phản hồi",
                icon = Icons.Outlined.ErrorOutline
            )
            "RETURNED" -> BannerVisuals(
                bg = Color(0xFF4C1D95),
                tagText = "• ĐÃ TRẢ HÀNG & HOÀN TIỀN",
                tagColor = Color(0xFFDDD6FE),
                title = "Yêu cầu trả hàng đã được chấp thuận",
                subtitle = "Đơn hàng đã hoàn tất thủ tục trả hàng và hoàn tiền.",
                icon = Icons.Default.CheckCircle
            )
            "CANCELLED" -> BannerVisuals(
                bg = Color(0xFF7F1D1D),
                tagText = "• ĐÃ HỦY ĐƠN HÀNG",
                tagColor = Color(0xFFFECACA),
                title = "Đơn hàng đã bị hủy",
                subtitle = if (!detail.cancelReason.isNullOrBlank()) "Lý do: ${detail.cancelReason}" else "Đơn hàng đã hủy",
                icon = Icons.Default.Cancel
            )
            else -> BannerVisuals(
                bg = Color(0xFF2C4C70),
                tagText = "• $status",
                tagColor = Color(0xFF5EEAD4),
                title = "Đơn hàng #$displayOrderCode",
                subtitle = "Trạng thái: $status",
                icon = Icons.Default.LocalShipping
            )
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
                                text = " • Chi tiết đơn hàng",
                                fontSize = 11.5.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "#$displayOrderCode",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF111827)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Sao chép mã đơn",
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier
                                    .size(15.dp)
                                    .clickable {
                                        viewModel.showSuccessToast("Đã sao chép mã đơn hàng #$displayOrderCode")
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
                                viewModel.openShopChatForOrder(detail)
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

            // 3. Hero Status Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = bannerVisuals.bg)
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
                                    imageVector = bannerVisuals.icon,
                                    contentDescription = null,
                                    tint = bannerVisuals.tagColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = bannerVisuals.tagText,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = bannerVisuals.tagColor
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = bannerVisuals.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = bannerVisuals.subtitle,
                                    fontSize = 12.5.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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

                        Spacer(modifier = Modifier.height(14.dp))

                        val createdTime = detail.createdAt ?: ""
                        val confirmedTime = detail.confirmedAt ?: ""
                        val shippedTime = detail.shippedAt ?: ""
                        val deliveredTime = detail.deliveredAt ?: detail.completedAt ?: ""
                        val cancelledTime = detail.cancelledAt ?: detail.updatedAt ?: ""

                        val hasPassedSellerPrep = isShipping || isDeliveredOrCompleted || isReturnRequested || isReturned
                        // Đơn hàng chỉ chuyển cho người bán khi:
                        // - Là COD (đặt thành công là chuyển ngay cho người bán)
                        // - Hoặc là VNPay nhưng ĐÃ THANH TOÁN (isPaid == true) hoặc đã qua trạng thái xác nhận/giao hàng
                        val isTransferredToSeller = !isCancelled && (isCod || isPaid || isConfirmed || hasPassedSellerPrep)
                        val isSellerPreparingNow = isTransferredToSeller && !hasPassedSellerPrep

                        // Bước 1: Đơn hàng đặt hàng thành công (luôn hiển thị đầu tiên)
                        TimelineStepItem(
                            stepIcon = Icons.Default.Check,
                            iconBg = Color(0xFF059669),
                            iconTint = Color.White,
                            title = "Đơn hàng đặt hàng thành công",
                            time = createdTime,
                            description = "Đơn hàng #$displayOrderCode đã được tạo thành công.",
                            isLineActive = true,
                            isLast = false
                        )

                        // Bước 2 (chỉ dành cho VNPay / thanh toán trực tuyến):
                        // Nếu chưa thanh toán: hiển thị "Chờ thanh toán" trước khi chuyển cho người bán
                        // Nếu đã thanh toán: hiển thị "Đã thanh toán" rồi mới chuyển sang Người bán chuẩn bị hàng
                        if (!isCod) {
                            val isWaitingPayment = !isPaid && !isCancelled && !isConfirmed && !hasPassedSellerPrep
                            val isPaymentDone = isPaid || isConfirmed || hasPassedSellerPrep

                            TimelineStepItem(
                                stepIcon = if (isPaymentDone) Icons.Default.Check else Icons.Outlined.AccessTime,
                                iconBg = when {
                                    isPaymentDone -> Color(0xFF059669)
                                    isWaitingPayment -> Color(0xFFD97706)
                                    else -> Color(0xFFD1D5DB)
                                },
                                iconTint = Color.White,
                                title = if (isPaymentDone) "Đã thanh toán thành công" else "Chờ thanh toán",
                                time = if (isPaymentDone) (detail.confirmedAt ?: detail.updatedAt ?: "") else if (isWaitingPayment) "Đang chờ" else "",
                                description = when {
                                    isPaymentDone -> "Thanh toán VNPay hoàn tất. Đơn hàng đã được chuyển cho người bán."
                                    isWaitingPayment -> "Vui lòng hoàn tất thanh toán VNPay để đơn hàng được chuyển cho người bán."
                                    else -> "Đơn hàng chưa hoàn tất thanh toán."
                                },
                                isLineActive = isPaymentDone || isCancelled,
                                isLast = false,
                                isTitleBold = isWaitingPayment,
                                titleColor = when {
                                    isWaitingPayment -> Color(0xFFB45309)
                                    isPaymentDone -> Color(0xFF111827)
                                    else -> Color(0xFF9CA3AF)
                                }
                            )
                        }

                        if (isCancelled) {
                            // Nếu đơn đã hủy: hiển thị bước Đã hủy đơn hàng kết thúc lộ trình
                            TimelineStepItem(
                                stepIcon = Icons.Default.Cancel,
                                iconBg = Color(0xFFDC2626),
                                iconTint = Color.White,
                                title = "Đã hủy đơn hàng",
                                time = cancelledTime,
                                description = if (!detail.cancelReason.isNullOrBlank()) {
                                    "Lý do: ${detail.cancelReason}"
                                } else {
                                    "Đơn hàng đã bị hủy."
                                },
                                isLineActive = false,
                                isLast = true,
                                isTitleBold = true,
                                titleColor = Color(0xFFDC2626)
                            )
                        } else {
                            // Bước: Người bán chuẩn bị hàng
                            TimelineStepItem(
                                stepIcon = when {
                                    hasPassedSellerPrep -> Icons.Default.Check
                                    isSellerPreparingNow -> Icons.Outlined.Inventory2
                                    else -> Icons.Outlined.Inventory2
                                },
                                iconBg = when {
                                    hasPassedSellerPrep -> Color(0xFF059669)
                                    isSellerPreparingNow -> Color(0xFF2563EB)
                                    else -> Color(0xFFD1D5DB)
                                },
                                iconTint = Color.White,
                                title = "Người bán chuẩn bị hàng",
                                time = when {
                                    hasPassedSellerPrep -> confirmedTime
                                    isSellerPreparingNow -> confirmedTime.ifBlank { "Đang chuẩn bị" }
                                    else -> ""
                                },
                                description = when {
                                    hasPassedSellerPrep -> "Người bán đã chuẩn bị xong và bàn giao kiện hàng cho đơn vị vận chuyển."
                                    status == "PACKED" -> "Người bán đã đóng gói kiện hàng, chờ bàn giao cho đơn vị vận chuyển."
                                    status == "CONFIRMED" -> "Người bán đã xác nhận đơn hàng và đang đóng gói sản phẩm."
                                    isSellerPreparingNow -> "Đơn hàng đã chuyển cho người bán tiếp nhận và chuẩn bị hàng."
                                    else -> "Đơn hàng sẽ được chuyển cho người bán sau khi thanh toán hoàn thành."
                                },
                                isLineActive = hasPassedSellerPrep,
                                isLast = false,
                                isTitleBold = isSellerPreparingNow,
                                titleColor = when {
                                    isSellerPreparingNow -> Color(0xFF1D4ED8)
                                    hasPassedSellerPrep -> Color(0xFF111827)
                                    else -> Color(0xFF9CA3AF)
                                }
                            )

                            // Bước: Đang vận chuyển
                            val hasPassedShipping = isDeliveredOrCompleted || isReturnRequested || isReturned
                            TimelineStepItem(
                                stepIcon = if (hasPassedShipping) Icons.Default.Check else Icons.Default.LocalShipping,
                                iconBg = when {
                                    hasPassedShipping -> Color(0xFF059669)
                                    isShipping -> Color(0xFF2563EB)
                                    else -> Color(0xFFD1D5DB)
                                },
                                iconTint = Color.White,
                                title = "Đang vận chuyển",
                                time = when {
                                    isShipping -> shippedTime.ifBlank { "Đang giao" }
                                    hasPassedShipping -> shippedTime
                                    else -> ""
                                },
                                description = when {
                                    hasPassedShipping -> "Đơn vị vận chuyển đã giao kiện hàng tới địa chỉ nhận."
                                    isShipping -> "Kiện hàng đang trên đường giao tới địa chỉ nhận hàng của bạn."
                                    else -> "Đơn vị vận chuyển sẽ tiếp nhận kiện hàng sau khi người bán chuẩn bị xong."
                                },
                                isLineActive = hasPassedShipping,
                                isLast = false,
                                isTitleBold = isShipping,
                                titleColor = when {
                                    isShipping -> Color(0xFF1D4ED8)
                                    hasPassedShipping -> Color(0xFF111827)
                                    else -> Color(0xFF9CA3AF)
                                }
                            )

                            // Bước: Giao hàng thành công
                            val hasReturnStep = isReturnRequested || isReturned
                            TimelineStepItem(
                                stepIcon = if (hasPassedShipping) Icons.Default.CheckCircle else null,
                                iconBg = if (hasPassedShipping) Color(0xFF059669) else Color(0xFFD1D5DB),
                                iconTint = Color.White,
                                title = "Giao hàng thành công",
                                time = if (hasPassedShipping) deliveredTime else "",
                                description = if (hasPassedShipping) {
                                    "Kiện hàng đã được giao thành công tới người nhận."
                                } else {
                                    "Chờ giao hàng tới người nhận."
                                },
                                isLineActive = hasReturnStep,
                                isLast = !hasReturnStep,
                                isTitleBold = isDeliveredOrCompleted,
                                titleColor = if (hasPassedShipping) Color(0xFF059669) else Color(0xFF9CA3AF)
                            )

                            // Bước trả hàng / hoàn tiền (nếu có)
                            if (hasReturnStep) {
                                TimelineStepItem(
                                    stepIcon = if (isReturned) Icons.Default.CheckCircle else Icons.Outlined.ErrorOutline,
                                    iconBg = if (isReturned) Color(0xFF4C1D95) else Color(0xFFD97706),
                                    iconTint = Color.White,
                                    title = if (isReturned) "Đã trả hàng & hoàn tiền" else "Yêu cầu trả hàng / hoàn tiền",
                                    time = (if (isReturned) detail.returnApprovedAt else detail.returnRequestedAt) ?: "",
                                    description = if (isReturned) {
                                        "Yêu cầu trả hàng và hoàn tiền đã hoàn tất."
                                    } else {
                                        "Đang xử lý yêu cầu trả hàng: ${detail.returnReason ?: ""}"
                                    },
                                    isLineActive = false,
                                    isLast = true,
                                    isTitleBold = true,
                                    titleColor = if (isReturned) Color(0xFF4C1D95) else Color(0xFFB45309)
                                )
                            }
                        }
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
                                text = detail.receiverName ?: "",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            if (!detail.receiverPhone.isNullOrBlank()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = detail.receiverPhone,
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }

                        if (!detail.shippingAddress.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = detail.shippingAddress,
                                fontSize = 12.5.sp,
                                color = Color(0xFF4B5563),
                                lineHeight = 17.sp
                            )
                        }

                        if (!detail.note.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "🕒 Lời nhắn: ${detail.note}",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF335C85)
                                )
                            }
                        }
                    }
                }
            }

            // 6. Card Danh sách sản phẩm trong đơn hàng
            val itemsList = detail.items ?: emptyList()
            if (itemsList.isNotEmpty()) {
                item {
                    val shopName = detail.shopName
                        ?: uiState.orders.firstOrNull { it.id == detail.id }?.shopName
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
                            if (!shopName.isNullOrBlank()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.openShopChatForOrder(detail, shopName) },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Storefront,
                                            contentDescription = null,
                                            tint = NavySecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = shopName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavySecondary
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
                            }

                            // Lặp qua từng sản phẩm trong đơn
                            itemsList.forEachIndexed { index, orderItem ->
                                if (index > 0) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 0.8.dp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    if (!orderItem.imageUrl.isNullOrBlank()) {
                                        SubcomposeAsyncImage(
                                            model = orderItem.imageUrl,
                                            contentDescription = orderItem.productName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp)),
                                            error = {
                                                Image(
                                                    painter = painterResource(id = R.drawable.folded_black_tshirt_1793956682242),
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.size(64.dp)
                                                )
                                            }
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = R.drawable.folded_black_tshirt_1793956682242),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp))
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = orderItem.productName ?: "",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF111827),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        if (!orderItem.variantName.isNullOrBlank()) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(Color(0xFFF3F4F6))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Phân loại: ${orderItem.variantName}",
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
                                            Text(
                                                text = "Số lượng: x${orderItem.quantity}",
                                                fontSize = 12.5.sp,
                                                color = Color(0xFF6B7280)
                                            )

                                            Text(
                                                text = "₫${currencyFormatter.format(orderItem.totalPrice ?: ((orderItem.unitPrice ?: 0.0) * orderItem.quantity))}",
                                                fontSize = 14.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CoralPrimary
                                            )
                                        }

                                        // Nút/Badge đánh giá riêng cho từng sản phẩm khi đơn đã giao
                                        if (isDeliveredOrCompleted) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (orderItem.isReviewed == true) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(Color(0xFFE8F5E9))
                                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                                    ) {
                                                        Text(
                                                            text = "✓ Đã đánh giá",
                                                            fontSize = 11.5.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = Color(0xFF2E7D32)
                                                        )
                                                    }
                                                } else {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .border(1.dp, Color(0xFFBA2211), RoundedCornerShape(4.dp))
                                                            .background(Color(0xFFFFF5F5))
                                                            .clickable {
                                                                viewModel.openWriteReviewForOrderDetail(detail, orderItem)
                                                            }
                                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                                    ) {
                                                        Text(
                                                            text = "Viết đánh giá",
                                                            fontSize = 11.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFFBA2211)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
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
                                imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
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

                        val subtotalVal = detail.subtotal ?: detail.totalAmount ?: 0.0
                        val shippingFeeVal = detail.shippingFee ?: 0.0
                        val discountVal = (detail.discountAmount ?: 0.0) + (detail.platformDiscount ?: 0.0)
                        val totalVal = detail.totalAmount ?: (subtotalVal + shippingFeeVal - discountVal)

                        PaymentDetailLine(label = "Tổng tiền hàng", value = "₫${currencyFormatter.format(subtotalVal)}")
                        Spacer(modifier = Modifier.height(6.dp))
                        PaymentDetailLine(label = "Phí vận chuyển", value = "₫${currencyFormatter.format(shippingFeeVal)}")

                        if (discountVal > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            PaymentDetailLine(
                                label = "Giảm giá khuyến mãi",
                                value = "-₫${currencyFormatter.format(discountVal)}",
                                valueColor = Color(0xFF059669)
                            )
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
                                text = "₫${currencyFormatter.format(totalVal)}",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Black,
                                color = CoralPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Phương thức thanh toán
                        val method = detail.paymentMethod ?: ""

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
                                    if (method.isNotBlank()) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (method.equals("VNPAY", ignoreCase = true)) Color(0xFFEBF3FC) else Color(0xFFF3F4F6))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = method,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (method.equals("VNPAY", ignoreCase = true)) Color(0xFF005BAA) else NavySecondary
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Column {
                                        Text(
                                            text = if (method.equals("VNPAY", ignoreCase = true)) "Cổng VNPAY-QR" else "Thanh toán khi nhận hàng (COD)",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF111827)
                                        )
                                        Text(
                                            text = if (isPaid) "Trạng thái: Đã thanh toán" else "Trạng thái: Chưa thanh toán",
                                            fontSize = 10.5.sp,
                                            color = if (isPaid) Color(0xFF059669) else Color(0xFFD97706)
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isPaid) Color(0xFFE6F7F5) else Color(0xFFFEF3C7))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isPaid) Icons.Default.Check else Icons.Outlined.AccessTime,
                                            contentDescription = null,
                                            tint = if (isPaid) Color(0xFF00897B) else Color(0xFFD97706),
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = if (isPaid) "Hoàn tất" else "Chờ thanh toán",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPaid) Color(0xFF00897B) else Color(0xFFD97706)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 8. Khung thông số đơn hàng (chỉ hiển thị thông tin thật từ API)
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
                        OrderMetaRow(label = "Mã đơn hàng", value = displayOrderCode)
                        if (!detail.createdAt.isNullOrBlank()) {
                            OrderMetaRow(label = "Thời gian đặt hàng", value = detail.createdAt)
                        }
                        if (!detail.paymentMethod.isNullOrBlank()) {
                            OrderMetaRow(label = "Phương thức thanh toán", value = detail.paymentMethod)
                            OrderMetaRow(label = "Trạng thái thanh toán", value = if (isPaid) "Đã thanh toán" else "Chưa thanh toán")
                        }
                        if (!detail.confirmedAt.isNullOrBlank()) {
                            OrderMetaRow(label = "Thời gian xác nhận", value = detail.confirmedAt)
                        }
                        if (!detail.shippedAt.isNullOrBlank()) {
                            OrderMetaRow(label = "Thời gian giao vận chuyển", value = detail.shippedAt)
                        }
                        if (!detail.deliveredAt.isNullOrBlank()) {
                            OrderMetaRow(label = "Thời gian nhận hàng", value = detail.deliveredAt)
                        }
                        if (!detail.completedAt.isNullOrBlank()) {
                            OrderMetaRow(label = "Thời gian hoàn tất", value = detail.completedAt)
                        }
                        if (!detail.cancelledAt.isNullOrBlank()) {
                            OrderMetaRow(label = "Thời gian hủy đơn", value = detail.cancelledAt)
                        }
                    }
                }
            }

            // 8.1 Card Thông tin Yêu cầu Trả hàng / Hoàn tiền nếu có
            if (isReturnRequested || isReturned || !detail.returnReason.isNullOrBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.ErrorOutline,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "YÊU CẦU TRẢ HÀNG / HOÀN TIỀN",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                            }
                            if (!detail.returnReason.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Lý do: ${detail.returnReason}",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF78350F)
                                )
                            }
                            if (!detail.returnDescription.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Chi tiết: ${detail.returnDescription}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF92400E)
                                )
                            }
                            if (!detail.returnAdminNote.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Ghi chú xử lý: ${detail.returnAdminNote}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF1E3A8A)
                                )
                            }
                        }
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
                if (isPending) {
                    // Đơn PENDING:
                    // 1. Nếu là COD: Hiển thị [Hủy đơn hàng] & [Liên hệ Shop]
                    // 2. Nếu là VNPay:
                    //    - Chưa thanh toán (!isPaid): Hiển thị [Hủy đơn hàng] & [THANH TOÁN NGAY]
                    //    - Đã thanh toán (isPaid): Hiển thị [Hủy đơn hàng] & [Liên hệ Shop]
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFEE2E2))
                            .clickable { showCancelDialog = true }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Hủy đơn hàng",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }

                    if (!isCod && !isPaid) {
                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFBA2211))
                                .clickable {
                                    viewModel.retryOrderPayment(rawId)
                                }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "THANH TOÁN NGAY",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE0EDFF))
                                .clickable {
                                    viewModel.openShopChatForOrder(detail)
                                }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Liên hệ Shop",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E5B94)
                            )
                        }
                    }
                } else if (isConfirmed) {
                    // Đơn CONFIRMED/PACKED: [Hủy đơn] & [Liên hệ Shop]
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFEE2E2))
                            .clickable { showCancelDialog = true }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Hủy đơn hàng",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFBFDBFE))
                            .clickable { viewModel.openShopChatForOrder(detail) }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Liên hệ Shop",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF)
                        )
                    }
                } else if (isShipping || isDelivered) {
                    // Đơn SHIPPING hoặc DELIVERED: [Liên hệ Shop] & [Trả hàng nếu DELIVERED] & [ĐÃ NHẬN ĐƯỢC HÀNG]
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFBFDBFE))
                            .clickable { viewModel.openShopChatForOrder(detail) },
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
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (uiState.isCompletingOrder) Color(0xFF888888) else Color(0xFFBA2211))
                            .clickable(enabled = !uiState.isCompletingOrder) {
                                viewModel.confirmOrderDelivery(rawId)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isCompletingOrder) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
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
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                } else if (isCompleted) {
                    // Đơn COMPLETED: [Mua lại] & [ĐÁNH GIÁ]
                    val isReorderingThis = uiState.reorderingOrderId == rawId
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE0EDFF))
                            .clickable(enabled = !isReorderingThis) {
                                viewModel.reorderFromOrder(rawId)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isReorderingThis) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color(0xFF1E5B94),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Mua lại",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E5B94)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFBA2211))
                            .clickable {
                                viewModel.openWriteReviewForOrderDetail(detail)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ĐÁNH GIÁ",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    // CANCELLED, RETURN_REQUESTED, RETURNED
                    val isReorderingThis = uiState.reorderingOrderId == rawId
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFBFDBFE))
                            .clickable { viewModel.openShopChatForOrder(detail) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Liên hệ Shop",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFBA2211))
                            .clickable(enabled = !isReorderingThis) {
                                viewModel.reorderFromOrder(rawId)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isReorderingThis) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Mua lại sản phẩm",
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

    // Dialog xác nhận hủy đơn hàng
    if (showCancelDialog && detail != null) {
        val displayCode = detail.orderCode ?: detail.id.toString()
        val reasons = listOf(
            "Tôi muốn cập nhật lại địa chỉ nhận hàng",
            "Tôi muốn đổi sản phẩm hoặc áp dụng voucher khác",
            "Tìm thấy giá tốt hơn ở nơi khác",
            "Đổi ý, không muốn mua nữa",
            "Thời gian giao hàng quá lâu"
        )

        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Text(
                    text = "Xác nhận hủy đơn hàng",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NavySecondary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Bạn có chắc chắn muốn hủy đơn hàng #$displayCode không?",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Chọn lý do hủy đơn:",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavySecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    reasons.forEach { r ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { cancelReason = r }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = cancelReason == r,
                                onClick = { cancelReason = r },
                                colors = RadioButtonDefaults.colors(selectedColor = CoralPrimary),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = r,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        viewModel.cancelOrder(detail.id, cancelReason)
                    }
                ) {
                    Text("Xác nhận hủy", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Giữ lại đơn", color = TextSecondary)
                }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = SurfaceCard
        )
    }

    // Dialog xác nhận yêu cầu trả hàng / hoàn tiền
    if (showReturnDialog && detail != null) {
        val displayCode = detail.orderCode ?: detail.id.toString()
        val returnReasons = listOf(
            "DAMAGED" to "Hàng bị hỏng, móp méo hoặc vỡ khi vận chuyển",
            "WRONG_ITEM" to "Người bán gửi sai sản phẩm / màu / size",
            "NOT_AS_DESCRIBED" to "Sản phẩm không đúng với mô tả trên app",
            "CHANGED_MIND" to "Đổi ý, không còn nhu cầu sử dụng",
            "OTHER" to "Lý do khác"
        )

        AlertDialog(
            onDismissRequest = { showReturnDialog = false },
            title = {
                Text(
                    text = "Yêu cầu Trả hàng / Hoàn tiền",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NavySecondary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Vui lòng chọn lý do yêu cầu trả hàng cho đơn #$displayCode:",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    returnReasons.forEach { (key, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { returnReasonKey = key }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = returnReasonKey == key,
                                onClick = { returnReasonKey = key },
                                colors = RadioButtonDefaults.colors(selectedColor = CoralPrimary),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = returnDescription,
                        onValueChange = { returnDescription = it },
                        label = { Text("Mô tả chi tiết (tùy chọn)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CoralPrimary,
                            focusedLabelColor = CoralPrimary
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showReturnDialog = false
                        viewModel.requestOrderReturn(detail.id, returnReasonKey, returnDescription.ifBlank { null })
                    },
                    enabled = !uiState.isRequestingReturn
                ) {
                    if (uiState.isRequestingReturn) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Gửi yêu cầu", color = CoralPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showReturnDialog = false }) {
                    Text("Đóng", color = TextSecondary)
                }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = SurfaceCard
        )
    }

    // Dialog thông báo khi sản phẩm trong đơn hàng không còn tồn tại hoặc đã ngừng bán
    if (uiState.reorderErrorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissReorderError() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Không thể mua lại đơn hàng",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = NavySecondary
                    )
                }
            },
            text = {
                Text(
                    text = uiState.reorderErrorMessage,
                    fontSize = 13.5.sp,
                    color = TextPrimary,
                    lineHeight = 19.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissReorderError() }) {
                    Text(
                        text = "Đã hiểu",
                        color = CoralPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = SurfaceCard
        )
    }
}

private data class BannerVisuals(
    val bg: Color,
    val tagText: String,
    val tagColor: Color,
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
