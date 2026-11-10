package com.example.b2capp.features.order

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.StarRatingGold
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.core.ui.components.AppButton
import com.example.b2capp.core.ui.components.EmptyStateView
import com.example.b2capp.core.ui.components.ProductCard
import com.example.b2capp.data.remote.dto.OrderSummaryDto
import com.example.b2capp.features.order.components.OrderItemCard
import com.example.b2capp.features.order.components.OrderItemCardData
import com.example.b2capp.features.order.components.TrackingStep
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import java.text.NumberFormat
import java.util.Locale

/**
 * Màn hình Đơn mua của tôi (My Orders Screen) - Kết nối API Backend:
 * - Gọi GET /api/v1/buyer/orders để lấy danh sách đơn hàng thực tế của Buyer
 * - Hỗ trợ lọc theo các Tab trạng thái: Tất cả, Chờ thanh toán, Đang chuẩn bị, Đang giao, Đánh giá, Đã hủy
 * - Tìm kiếm nhanh theo mã đơn hàng, tên gian hàng, tên sản phẩm
 * - Thao tác Hủy đơn hàng (PATCH /api/v1/buyer/orders/{id}/cancel) với Dialog chọn lý do
 * - Thao tác Thanh toán lại đơn hàng PENDING qua VNPay (POST /api/v1/buyer/orders/{id}/pay)
 * - Mở chi tiết đơn hàng (GET /api/v1/buyer/orders/{id}) khi click vào thẻ đơn hàng
 */
@Composable
fun OrdersScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onExploreShop: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(uiState.selectedOrdersTab) }
    var searchQuery by remember { mutableStateOf("") }

    // Dialog đánh giá sản phẩm
    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewRating by remember { mutableIntStateOf(5) }

    // Dialog xem nhanh lộ trình vận chuyển
    var showTrackingDialog by remember { mutableStateOf(false) }

    // Dialog xác nhận hủy đơn hàng
    var showCancelDialog by remember { mutableStateOf(false) }
    var targetCancelOrder by remember { mutableStateOf<OrderSummaryDto?>(null) }
    var cancelReason by remember { mutableStateOf("Tôi muốn cập nhật lại địa chỉ nhận hàng") }

    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale("vi", "VN")) }

    val statusTabs = listOf(
        "Tất cả",
        "Chờ thanh toán",
        "Đang chuẩn bị",
        "Đang giao",
        "Đánh giá",
        "Đã hủy"
    )

    // Tự động tải đơn hàng từ API khi vào màn hình
    LaunchedEffect(Unit) {
        if (uiState.isLoggedIn) {
            viewModel.loadOrders(selectedTab)
        }
    }

    // Lọc danh sách đơn hàng theo Tab và Từ khóa tìm kiếm
    val filteredOrders = remember(uiState.orders, selectedTab, searchQuery) {
        uiState.orders.filter { order ->
            val status = order.status?.uppercase() ?: ""
            val matchesTab = when (selectedTab) {
                "Chờ thanh toán" -> status == "PENDING"
                "Đang chuẩn bị" -> status == "CONFIRMED" || status == "PACKED"
                "Đang giao" -> status == "SHIPPING"
                "Đánh giá" -> status == "DELIVERED" || status == "COMPLETED"
                "Đã hủy" -> status == "CANCELLED" || status == "RETURNED" || status == "RETURN_REQUESTED"
                else -> true // "Tất cả"
            }

            val query = searchQuery.trim().lowercase()
            val matchesQuery = query.isBlank() ||
                    (order.orderCode?.lowercase()?.contains(query) == true) ||
                    (order.shopName?.lowercase()?.contains(query) == true) ||
                    (order.firstProductName?.lowercase()?.contains(query) == true) ||
                    ("ord-${order.id}".contains(query))

            matchesTab && matchesQuery
        }
    }

    // Đếm số đơn chờ thanh toán cho Badge đỏ
    val pendingCount = remember(uiState.orders) {
        uiState.orders.count { it.status.equals("PENDING", ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F8))
            .testTag("orders_screen")
    ) {
        // 1. Top Bar
        Surface(
            color = SurfaceCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onExploreShop,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Quay lại",
                                tint = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "Đơn hàng của bạn",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Nút làm mới dữ liệu từ API
                        IconButton(
                            onClick = { viewModel.loadOrders(selectedTab, isRefresh = true) },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = "Làm mới đơn hàng",
                                tint = TextPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.openShopMessages() },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = "Tin nhắn với Shop",
                                tint = TextPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // 2. Ô tìm kiếm & Nút Filter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0F2F5))
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Tìm kiếm",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                color = TextPrimary
                            ),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Tìm theo tên Shop, ID đơn hoặc SP...",
                                        fontSize = 12.5.sp,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                innerTextField()
                            }
                        )

                        if (searchQuery.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Xóa tìm kiếm",
                                tint = TextSecondary,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { searchQuery = "" }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0F2F5))
                            .clickable {
                                viewModel.showToast("Bộ lọc đơn hàng đang được áp dụng")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Tune,
                            contentDescription = "Bộ lọc",
                            tint = NavySecondary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 3. Thanh Tab trạng thái đơn hàng (cuộn ngang)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    statusTabs.forEach { tabName ->
                        val isTabSelected = tabName == selectedTab
                        val hasPendingBadge = tabName == "Chờ thanh toán" && pendingCount > 0

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    selectedTab = tabName
                                    viewModel.selectOrdersTab(tabName)
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = tabName,
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isTabSelected) Color(0xFFBA2211) else TextSecondary
                                )

                                if (hasPendingBadge) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .offset(y = (-2).dp)
                                            .size(16.dp)
                                            .background(Color(0xFFBA2211), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$pendingCount",
                                            color = Color.White,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            lineHeight = 9.5.sp,
                                            style = TextStyle(
                                                platformStyle = PlatformTextStyle(includeFontPadding = false),
                                                lineHeightStyle = LineHeightStyle(
                                                    alignment = LineHeightStyle.Alignment.Center,
                                                    trim = LineHeightStyle.Trim.Both
                                                )
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Vạch gạch chân chỉ thị tab đang chọn
                            Box(
                                modifier = Modifier
                                    .width(if (isTabSelected) 32.dp else 0.dp)
                                    .height(2.5.dp)
                                    .background(
                                        if (isTabSelected) Color(0xFFBA2211) else Color.Transparent,
                                        RoundedCornerShape(1.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }

        // Nội dung danh sách đơn hàng
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Khi đang tải đơn hàng từ API
            if (uiState.isOrdersLoading && !uiState.isOrdersRefreshing) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = CoralPrimary,
                                modifier = Modifier.size(36.dp),
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Đang tải danh sách đơn hàng...",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else if (uiState.ordersErrorMessage != null && uiState.orders.isEmpty()) {
                // Khi có lỗi từ API và không có đơn hàng cục bộ
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = uiState.ordersErrorMessage ?: "Không thể nạp danh sách đơn hàng",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Vui lòng kiểm tra lại kết nối mạng hoặc thử lại",
                            fontSize = 12.5.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        AppButton(
                            text = "Thử lại",
                            onClick = { viewModel.loadOrders(selectedTab) },
                            modifier = Modifier.width(140.dp)
                        )
                    }
                }
            } else if (filteredOrders.isEmpty()) {
                // Trạng thái trống: Không có đơn hàng nào
                item {
                    EmptyStateView(
                        icon = Icons.Outlined.Inventory2,
                        title = "Chưa có đơn hàng nào",
                        description = if (searchQuery.isNotBlank()) {
                            "Không tìm thấy đơn hàng phù hợp với \"$searchQuery\"."
                        } else {
                            "Bạn chưa có đơn hàng nào trong mục \"$selectedTab\"."
                        },
                        actionText = "Khám phá sản phẩm",
                        onActionClick = onExploreShop,
                        modifier = Modifier.padding(vertical = 30.dp)
                    )
                }
            } else {
                // Render danh sách đơn hàng thực tế từ API Backend
                items(filteredOrders, key = { it.id }) { order ->
                    val status = order.status?.uppercase() ?: "PENDING"
                    val (statusLabel, statusColor, statusBgColor) = getOrderStatusVisuals(status)

                    val deliveryBanner = when (status) {
                        "PENDING" -> "Vui lòng thanh toán sớm để đơn hàng không bị tự động hủy."
                        "CONFIRMED" -> "Người bán đã xác nhận và đang đóng gói sản phẩm của bạn."
                        "PACKED" -> "Kiện hàng đã được đóng gói kỹ lưỡng và bàn giao đơn vị vận chuyển."
                        "SHIPPING" -> "Đang trên đường giao bởi SPX Express tới địa chỉ nhận hàng của bạn."
                        "DELIVERED", "COMPLETED" -> "Kiện hàng đã được giao thành công tới bạn. Cảm ơn bạn đã tin dùng!"
                        "CANCELLED" -> "Đơn hàng đã được hủy thành công."
                        "RETURN_REQUESTED" -> "Yêu cầu trả hàng/hoàn tiền đang được xử lý."
                        "RETURNED" -> "Đơn hàng đã hoàn trả thành công."
                        else -> null
                    }

                    OrderItemCard(
                        data = OrderItemCardData(
                            orderId = order.orderCode ?: "ORD-${order.id}",
                            rawOrderId = order.id,
                            shopName = order.shopName ?: "Gian hàng B2C",
                            isMall = true,
                            statusText = statusLabel,
                            statusColor = statusColor,
                            statusBgColor = statusBgColor,
                            imageRes = R.drawable.folded_black_tshirt_1793956682242,
                            imageUrl = order.firstProductThumbnail,
                            productName = order.firstProductName ?: "Đơn hàng #${order.orderCode ?: order.id}",
                            variantText = if (!order.firstVariantName.isNullOrBlank()) "Phân loại: ${order.firstVariantName}" else "Phân loại tiêu chuẩn",
                            quantityText = if ((order.totalItemsCount ?: 1) > 1) "x1 • và ${(order.totalItemsCount ?: 1) - 1} SP khác" else "x1",
                            priceText = "₫${currencyFormatter.format(order.totalAmount ?: 0.0)}",
                            deliveryBannerText = deliveryBanner,
                            deliveryBannerIconTint = statusColor,
                            totalCountText = "Thành tiền (${order.totalItemsCount ?: 1} sản phẩm): ",
                            totalPriceText = "₫${currencyFormatter.format(order.totalAmount ?: 0.0)}"
                        ),
                        onShopClick = {
                            viewModel.openOrderDetailsTrackingScreen(order.orderCode ?: order.id.toString())
                        },
                        actionsSlot = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                when (status) {
                                    "PENDING" -> {
                                        // 2 Nút: [Hủy đơn], [THANH TOÁN NGAY]
                                        Box(
                                            modifier = Modifier
                                                .height(34.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFF3F4F6))
                                                .clickable {
                                                    targetCancelOrder = order
                                                    showCancelDialog = true
                                                }
                                                .padding(horizontal = 14.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Hủy đơn",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFFD32F2F)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Box(
                                            modifier = Modifier
                                                .height(34.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFBA2211))
                                                .clickable {
                                                    viewModel.retryOrderPayment(order.id)
                                                }
                                                .padding(horizontal = 14.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "THANH TOÁN NGAY",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    "CONFIRMED", "PACKED" -> {
                                        // 2 Nút: [Hủy đơn], [Xem chi tiết]
                                        Box(
                                            modifier = Modifier
                                                .height(34.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFF3F4F6))
                                                .clickable {
                                                    targetCancelOrder = order
                                                    showCancelDialog = true
                                                }
                                                .padding(horizontal = 14.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Hủy đơn",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFFD32F2F)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Box(
                                            modifier = Modifier
                                                .height(34.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFE0EDFF))
                                                .clickable {
                                                    viewModel.openOrderDetailsTrackingScreen(order.orderCode ?: order.id.toString())
                                                }
                                                .padding(horizontal = 14.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Xem chi tiết",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E5B94)
                                            )
                                        }
                                    }

                                    "SHIPPING" -> {
                                        // 2 Nút: [Xem lộ trình], [Đã nhận được hàng]
                                        Box(
                                            modifier = Modifier
                                                .height(34.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFEBF3FA))
                                                .clickable {
                                                    viewModel.openOrderDetailsTrackingScreen(order.orderCode ?: order.id.toString())
                                                }
                                                .padding(horizontal = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Xem lộ trình",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF1E5B94)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Box(
                                            modifier = Modifier
                                                .height(34.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFE6F7F5))
                                                .clickable {
                                                    viewModel.showSuccessToast("Cảm ơn bạn đã xác nhận đã nhận hàng!")
                                                }
                                                .padding(horizontal = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Đã nhận được hàng",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E8275)
                                            )
                                        }
                                    }

                                    "DELIVERED", "COMPLETED" -> {
                                        // 2 Nút: [Mua lại], [ĐÁNH GIÁ]
                                        Box(
                                            modifier = Modifier
                                                .height(34.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFE0EDFF))
                                                .clickable {
                                                    viewModel.openCheckoutScreen()
                                                }
                                                .padding(horizontal = 14.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Mua lại",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E5B94)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Box(
                                            modifier = Modifier
                                                .height(34.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFBA2211))
                                                .clickable {
                                                    viewModel.openWriteReview()
                                                }
                                                .padding(horizontal = 14.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "ĐÁNH GIÁ",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    else -> {
                                        // CANCELLED, RETURNED -> [Mua lại], [Xem chi tiết]
                                        Box(
                                            modifier = Modifier
                                                .height(34.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFF3F4F6))
                                                .clickable {
                                                    viewModel.openOrderDetailsTrackingScreen(order.orderCode ?: order.id.toString())
                                                }
                                                .padding(horizontal = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Xem chi tiết",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = NavySecondary
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Box(
                                            modifier = Modifier
                                                .height(34.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFE0EDFF))
                                                .clickable {
                                                    viewModel.openCheckoutScreen()
                                                }
                                                .padding(horizontal = 14.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Mua lại",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E5B94)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }

            // 7. KHU VỰC "CÓ THỂ BẠN CŨNG THÍCH"
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.ThumbUp,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Có thể bạn cũng thích",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Text(
                        text = "Gợi ý riêng cho bạn",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Lưới 2 sản phẩm gợi ý
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val socksProduct = remember {
                        ProductItem(
                            id = "rec_socks",
                            name = "Combo 3 Đôi Vớ Cổ Trung Thể Thao Khử Mùi",
                            imageUrl = "",
                            price = 79000L,
                            rating = 4.9f,
                            soldCount = 2500
                        )
                    }
                    ProductCard(
                        product = socksProduct,
                        imageRes = R.drawable.socks_sport_pack_1793957699009,
                        customDiscountTag = "-35%",
                        onClick = {
                            viewModel.openProductDetailsScreen(socksProduct)
                        },
                        modifier = Modifier.weight(1f)
                    )

                    val chargerProduct = remember {
                        ProductItem(
                            id = "rec_charger",
                            name = "Củ Sạc Nhanh Anker Nano II 65W GaN Siêu Nhỏ",
                            imageUrl = "",
                            price = 450000L,
                            rating = 5.0f,
                            soldCount = 1800
                        )
                    }
                    ProductCard(
                        product = chargerProduct,
                        imageRes = R.drawable.anker_gan_charger_1793957714558,
                        customDiscountTag = "-20%",
                        onClick = {
                            viewModel.openProductDetailsScreen(chargerProduct)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 8. Footer cuối trang
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Inventory2,
                        contentDescription = null,
                        tint = Color(0xFFD1D5DB),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Bạn đã xem hết các đơn mua gần đây",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

    // Dialog xác nhận hủy đơn hàng
    if (showCancelDialog && targetCancelOrder != null) {
        val order = targetCancelOrder!!
        val reasons = listOf(
            "Tôi muốn cập nhật lại địa chỉ nhận hàng",
            "Tôi muốn đổi sản phẩm hoặc áp dụng voucher khác",
            "Tìm thấy giá tốt hơn ở nơi khác",
            "Đổi ý, không muốn mua nữa",
            "Thời gian giao hàng quá lâu"
        )

        AlertDialog(
            onDismissRequest = {
                showCancelDialog = false
                targetCancelOrder = null
            },
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
                        text = "Bạn có chắc chắn muốn hủy đơn hàng #${order.orderCode ?: order.id} không?",
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
                        val orderId = order.id
                        showCancelDialog = false
                        targetCancelOrder = null
                        viewModel.cancelOrder(orderId, cancelReason)
                    }
                ) {
                    Text("Xác nhận hủy", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        targetCancelOrder = null
                    }
                ) {
                    Text("Giữ lại đơn", color = TextSecondary)
                }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = SurfaceCard
        )
    }

    // Dialog đánh giá sao đơn hàng
    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = {
                Text(
                    text = "Đánh giá đơn hàng",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NavySecondary
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Hãy chia sẻ cảm nhận của bạn về sản phẩm nhé!",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "$i sao",
                                tint = if (i <= reviewRating) StarRatingGold else Color(0xFFE5E7EB),
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { reviewRating = i }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showReviewDialog = false
                        viewModel.showSuccessToast("Cảm ơn bạn đã gửi đánh giá $reviewRating sao!")
                    }
                ) {
                    Text("Gửi đánh giá", color = CoralPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Để sau", color = TextSecondary)
                }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = SurfaceCard
        )
    }

    // Dialog lộ trình vận chuyển SPX Express
    if (showTrackingDialog) {
        AlertDialog(
            onDismissRequest = { showTrackingDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocalShipping,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lộ trình SPX Express",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = NavySecondary
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TrackingStep(
                        title = "Đang giao hàng",
                        subtitle = "Tài xế đang phát hàng tới địa chỉ nhận của bạn",
                        time = "Hôm nay",
                        isActive = true
                    )
                    TrackingStep(
                        title = "Đã đến kho phân loại",
                        subtitle = "Đơn hàng đã được xuất kho trung chuyển",
                        time = "Hôm qua",
                        isActive = false
                    )
                    TrackingStep(
                        title = "Shop đã bàn giao kiện hàng",
                        subtitle = "Lấy hàng thành công từ người bán",
                        time = "Trước đó",
                        isActive = false
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTrackingDialog = false }) {
                    Text("Đóng", color = CoralPrimary, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = SurfaceCard
        )
    }
}

/**
 * Hàm helper định dạng trạng thái đơn hàng: Text, Màu chữ, Màu nền
 */
private fun getOrderStatusVisuals(status: String): Triple<String, Color, Color> {
    return when (status) {
        "PENDING" -> Triple("CHỜ THANH TOÁN", Color(0xFFD97706), Color(0xFFFEF3C7))
        "CONFIRMED" -> Triple("ĐÃ XÁC NHẬN", Color(0xFF1D4ED8), Color(0xFFE0EDFF))
        "PACKED" -> Triple("ĐANG CHUẨN BỊ", Color(0xFF4338CA), Color(0xFFEEF2FF))
        "SHIPPING" -> Triple("ĐANG GIAO HÀNG", Color(0xFF0284C7), Color(0xFFE0F2FE))
        "DELIVERED" -> Triple("GIAO THÀNH CÔNG", Color(0xFF1E8275), Color(0xFFE6F7F5))
        "COMPLETED" -> Triple("HOÀN THÀNH", Color(0xFF1E8275), Color(0xFFE6F7F5))
        "CANCELLED" -> Triple("ĐÃ HỦY", Color(0xFFDC2626), Color(0xFFFEE2E2))
        "RETURN_REQUESTED" -> Triple("YÊU CẦU TRẢ HÀNG", Color(0xFF7C3AED), Color(0xFFF3E8FF))
        "RETURNED" -> Triple("ĐÃ TRẢ HÀNG", Color(0xFF6B7280), Color(0xFFF3F4F6))
        else -> Triple(status, NavySecondary, Color(0xFFF3F4F6))
    }
}
