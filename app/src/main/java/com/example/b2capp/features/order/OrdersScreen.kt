package com.example.b2capp.features.order

import com.example.b2capp.features.order.components.OrderItemCard
import com.example.b2capp.features.order.components.OrderItemCardData
import com.example.b2capp.features.order.components.TrackingStep

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.example.b2capp.core.ui.components.ProductCard
import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.StarRatingGold
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

/**
 * Màn hình Đơn mua của tôi (My Orders Screen)
 * Thiết kế chính xác 100% theo MyOrdersScreen_a4cb2a91.png:
 * 1. Top Bar: Nút Back, "Shopping Cart", nút đỏ "Sửa", avatar tròn đỏ.
 * 2. Ô tìm kiếm: "Tìm theo tên Shop, ID đơn hoặc SP..." kèm nút lọc bên phải.
 * 3. Thanh Tab trạng thái:
 *    - "Tất cả" (vạch gạch chân đỏ)
 *    - "Chờ thanh toán" kèm badge đỏ số 1
 *    - "Đang chuẩn bị"
 *    - "Đang giao"
 *    - "Đánh giá"
 * 4. Đơn hàng 1 (Coolmate Official - GIAO THÀNH CÔNG):
 *    - Tag Mall đỏ, Coolmate Official >, tag GIAO THÀNH CÔNG xanh ngọc.
 *    - Áo Thun Nam Cotton Compact Siêu Thoáng Khí Cao Cấp, Phân loại: Đen, L, x2 • và 2 SP khác, ₫199.000.
 *    - "Kiện hàng đã được giao thành công tới bạn vào 08/11/2026. Cảm ơn bạn đã tin dùng!".
 *    - "Thành tiền (3 sản phẩm): ₫388.200".
 *    - 3 Nút: [Trả hàng/Hoàn tiền], [Mua lại], [ĐÁNH GIÁ] (đỏ đậm).
 * 5. Đơn hàng 2 (Anker Audio Flagship - CHỜ THANH TOÁN):
 *    - Tag Mall đỏ, Anker Audio Flagsh >, tag CHỜ THANH TOÁN vàng cam.
 *    - Tai Nghe Chống Ồn Anker Space One Không Dây Hi–Res, Phân loại: Đen Titan, Số lượng: x1, ₫890.000.
 *    - "Vui lòng thanh toán trước 23:59 hôm nay để đơn không bị tự động hủy.".
 *    - "Thành tiền (1 sản phẩm): ₫890.000".
 *    - 2 Nút: [Hủy đơn], [THANH TOÁN NGAY] (đỏ đậm).
 * 6. Đơn hàng 3 (Uniqlo Vietnam Official - ĐANG GIAO HÀNG):
 *    - Icon shop, Uniqlo Vietnam Offici >, tag ĐANG GIAO HÀNG xanh dương.
 *    - Áo Polo Dry Pique Ngắn Tay Kháng Khuẩn Nam, Phân loại: Xanh Navy / M, Số lượng: x1, ₫399.000.
 *    - "Đang trên đường giao bởi SPX Express. Tài xế: Nguyễn Văn B (0912***456) đang phát hàng tại kh...".
 *    - "Thành tiền (1 sản phẩm): ₫399.000".
 *    - 2 Nút: [Xem lộ trình], [Đã nhận được hàng] (xanh ngọc).
 * 7. Khu vực "Có thể bạn cũng thích" (Gợi ý riêng cho bạn):
 *    - Combo 3 Đôi Vớ Cổ Trung Thể Thao Khử Mùi (-35%, ₫79.000)
 *    - Củ Sạc Nhanh Anker Nano II 65W GaN Siêu Nhỏ (-20%, ₫450.000)
 * 8. Footer: "Bạn đã xem hết các đơn mua gần đây".
 */
@Composable
fun OrdersScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onExploreShop: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("Tất cả") }
    var searchQuery by remember { mutableStateOf("") }

    // Dialog đánh giá sản phẩm đơn Coolmate
    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewRating by remember { mutableIntStateOf(5) }

    // Dialog xem lộ trình vận chuyển SPX Express đơn Uniqlo
    var showTrackingDialog by remember { mutableStateOf(false) }

    val statusTabs = listOf(
        "Tất cả",
        "Chờ thanh toán",
        "Đang chuẩn bị",
        "Đang giao",
        "Đánh giá"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F8))
            .testTag("orders_screen")
    ) {
        // 1. Top Bar chuẩn theo ảnh
        Surface(
            color = SurfaceCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
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

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "Shopping Cart",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Sửa",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoralPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFFBA2211), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Tài khoản",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
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
                        Text(
                            text = "Tìm theo tên Shop, ID đơn hoặc SP...",
                            fontSize = 12.5.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0F2F5))
                            .clickable { /* Mở bộ lọc */ },
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
                        val hasPendingBadge = tabName == "Chờ thanh toán"

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedTab = tabName }
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
                                            .size(16.dp)
                                            .background(Color(0xFFBA2211), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "1",
                                            color = Color.White,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold
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
            // 4. ĐƠN HÀNG 1: Coolmate Official (GIAO THÀNH CÔNG)
            if (selectedTab == "Tất cả" || selectedTab == "Đánh giá") {
                item {
                    OrderItemCard(
                        data = OrderItemCardData(
                            orderId = "ORD-20261105-0088",
                            shopName = "Coolmate Official",
                            isMall = true,
                            statusText = "GIAO THÀNH CÔNG",
                            statusColor = Color(0xFF1E8275),
                            statusBgColor = Color(0xFFE6F7F5),
                            imageRes = R.drawable.folded_black_tshirt_1793956682242,
                            productName = "Áo Thun Nam Cotton Compact Siêu Thoáng Khí Cao Cấp",
                            variantText = "Phân loại: Đen, L",
                            quantityText = "x2 • và 2 SP khác",
                            priceText = "₫199.000",
                            deliveryBannerText = "Kiện hàng đã được giao thành công tới bạn vào 08/11/2026. Cảm ơn bạn đã tin dùng!",
                            totalCountText = "Thành tiền (3 sản phẩm): ",
                            totalPriceText = "₫388.200"
                        ),
                        onShopClick = {
                            viewModel.openOrderDetailsTrackingScreen("ORD-20261105-0088")
                        },
                        actionsSlot = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFF3F4F6))
                                        .clickable { /* Trả hàng */ }
                                        .padding(horizontal = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Trả hàng/Hoàn tiền",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF374151)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFE0EDFF))
                                        .clickable {
                                            viewModel.selectTab(2) // Chuyển sang Giỏ hàng
                                        }
                                        .padding(horizontal = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Mua lại",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E5B94)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFBA2211))
                                        .clickable { viewModel.openWriteReview() }
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
                        }
                    )
                }
            }

            // 5. ĐƠN HÀNG 2: Anker Audio Flagship (CHỜ THANH TOÁN)
            if (selectedTab == "Tất cả" || selectedTab == "Chờ thanh toán") {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFB82414), RoundedCornerShape(3.dp))
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Mall",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Anker Audio Flagsh >",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavySecondary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFFEF3C7), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "CHỜ THANH TOÁN",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Sản phẩm: Tai nghe Anker Space One
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.headphones_anker_space_one_1793957334650),
                                    contentDescription = "Tai Nghe Chống Ồn Anker",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Tai Nghe Chống Ồn Anker Space One Không Dây Hi–Res",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        lineHeight = 17.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFF3F4F6), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Phân loại: Đen Titan",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Số lượng: x1",
                                            fontSize = 11.5.sp,
                                            color = TextSecondary
                                        )

                                        Text(
                                            text = "₫890.000",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Cảnh báo thời gian thanh toán
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFFFFBEB))
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.AccessTime,
                                        contentDescription = null,
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Vui lòng thanh toán trước 23:59 hôm nay để đơn không bị tự động hủy.",
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF92400E)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Thành tiền
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "Thành tiền (1 sản phẩm): ",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(bottom = 1.dp)
                                )
                                Text(
                                    text = "₫890.000",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFBA2211)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 0.8.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            // 2 Nút: [Hủy đơn], [THANH TOÁN NGAY]
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFF3F4F6))
                                        .clickable { /* Hủy đơn */ }
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
                                            // Demo thanh toán ngay
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
                        }
                    }
                }
            }

            // 6. ĐƠN HÀNG 3: Uniqlo Vietnam Official (ĐANG GIAO HÀNG)
            if (selectedTab == "Tất cả" || selectedTab == "Đang giao") {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Storefront,
                                        contentDescription = null,
                                        tint = NavySecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Uniqlo Vietnam Offici >",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavySecondary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFE0EDFF), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "ĐANG GIAO HÀNG",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D4ED8)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Sản phẩm: Áo Polo Dry Pique
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.polo_navy_folded_1793957349647),
                                    contentDescription = "Áo Polo Dry Pique",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Áo Polo Dry Pique Ngắn Tay Kháng Khuẩn Nam",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        lineHeight = 17.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFF3F4F6), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Phân loại: Xanh Navy / M",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Số lượng: x1",
                                            fontSize = 11.5.sp,
                                            color = TextSecondary
                                        )

                                        Text(
                                            text = "₫399.000",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Lộ trình giao hàng SPX Express
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFF0F7FF))
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Filled.LocalShipping,
                                        contentDescription = null,
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Đang trên đường giao bởi SPX Express",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E3A8A)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Tài xế: Nguyễn Văn B (0912***456) đang phát hàng tại kh...",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Thành tiền
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "Thành tiền (1 sản phẩm): ",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(bottom = 1.dp)
                                )
                                Text(
                                    text = "₫399.000",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFBA2211)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 0.8.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            // 2 Nút: [Xem lộ trình], [Đã nhận được hàng]
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFEBF3FA))
                                        .clickable { viewModel.openOrderDetailsTrackingScreen("ORD-20261105-0088") }
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
                                        .clickable { /* Xác nhận nhận hàng */ }
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
                        }
                    }
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
                    // Sản phẩm gợi ý 1: Combo 3 đôi vớ
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

                    // Sản phẩm gợi ý 2: Củ sạc nhanh Anker GaN 65W
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

    // Dialog đánh giá sao đơn hàng Coolmate
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
                        text = "Hãy chia sẻ cảm nhận của bạn về sản phẩm của Coolmate Official nhé!",
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
                        subtitle = "Tài xế Nguyễn Văn B đang phát hàng tới bạn",
                        time = "09:30 - Hôm nay",
                        isActive = true
                    )
                    TrackingStep(
                        title = "Đã đến kho phân loại Hà Nội",
                        subtitle = "Đơn hàng đã được xuất kho trung chuyển",
                        time = "04:15 - Hôm nay",
                        isActive = false
                    )
                    TrackingStep(
                        title = "Shop đã bàn giao kiện hàng",
                        subtitle = "Lấy hàng thành công từ Uniqlo Vietnam",
                        time = "18:20 - Hôm qua",
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




