package com.example.b2capp.features.profile

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.R
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.core.ui.components.ProductCard
import com.example.b2capp.data.remote.dto.WishlistItemDto
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel

/**
 * Màn hình Sản Phẩm Yêu Thích (WishlistScreen)
 * Kết nối trực tiếp API Backend Spring Boot (/api/v1/wishlist):
 * - Tự động đồng bộ danh sách sản phẩm yêu thích của người dùng đã xác thực.
 * - Hỗ trợ Xóa sản phẩm đơn lẻ hoặc chọn xóa hàng loạt (Bulk remove).
 * - Hỗ trợ Thêm sản phẩm vào giỏ hàng hoặc bấm vào xem chi tiết sản phẩm.
 * - Bộ lọc nhanh: Tất cả, Đang giảm giá, Còn hàng, Sắp hết hàng.
 * - Tự động cập nhật số lượng Badge trên Trang cá nhân (AccountScreen).
 */

data class WishlistItemData(
    val id: String,
    val productId: Long,
    val name: String,
    val price: Long,
    val originalPrice: Long,
    val discountPercent: String,
    val badgeText: String?,
    val isRedBadge: Boolean = false,
    val rating: Float = 5.0f,
    val soldCount: String = "100+",
    val imageUrl: String = "",
    val imageRes: Int? = null,
    val isOutOfStock: Boolean = false
)

@Composable
fun WishlistScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit = {},
    onProductClick: (ProductItem) -> Unit = { product -> viewModel.openProductDetailsScreen(product) },
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    var isDemoEmpty by remember { mutableStateOf(false) }
    var isSelectionMode by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf("Mới lưu gần đây") }

    val selectedProductIds = remember { mutableStateListOf<Long>() }

    // Tự động tải Wishlist từ API khi người dùng vào màn hình
    LaunchedEffect(Unit) {
        if (uiState.isLoggedIn) {
            viewModel.loadWishlist(isRefresh = false)
        }
    }

    // Danh sách sản phẩm hiển thị: Ưu tiên dữ liệu thật từ API
    val currentItems: List<WishlistItemData> = remember(uiState.wishlistItems, uiState.isLoggedIn) {
        if (uiState.isLoggedIn && uiState.wishlistItems.isNotEmpty()) {
            uiState.wishlistItems.map { dto ->
                val min = dto.minPrice?.toLong() ?: 0L
                val max = dto.maxPrice?.toLong() ?: min
                val discount = if (max > min && max > 0) {
                    val pct = ((max - min) * 100 / max).toInt()
                    "-$pct%"
                } else {
                    "Yêu thích"
                }
                val isOutOfStock = dto.status != null && !dto.status.equals("ACTIVE", ignoreCase = true)
                WishlistItemData(
                    id = dto.id.toString(),
                    productId = dto.productId,
                    name = dto.productName,
                    price = min,
                    originalPrice = if (max > min) max else min,
                    discountPercent = discount,
                    badgeText = if (isOutOfStock) "Hết hàng" else null,
                    isRedBadge = isOutOfStock,
                    rating = 5.0f,
                    soldCount = "100+",
                    imageUrl = dto.productThumbnail ?: "",
                    imageRes = null,
                    isOutOfStock = isOutOfStock
                )
            }
        } else if (!uiState.isLoggedIn) {
            // Dữ liệu mẫu minh họa khi khách chưa đăng nhập
            listOf(
                WishlistItemData(
                    id = "wish_1",
                    productId = 1L,
                    name = "Bàn phím cơ không dây Custom Hot–swap Led...",
                    price = 450000L,
                    originalPrice = 690000L,
                    discountPercent = "-35%",
                    badgeText = null,
                    rating = 4.9f,
                    soldCount = "2.1k",
                    imageUrl = "",
                    imageRes = R.drawable.product_white_tshirt_1793955938236
                ),
                WishlistItemData(
                    id = "wish_2",
                    productId = 2L,
                    name = "Ly giữ nhiệt Inox 316 cao cấp 500ml nắp chống...",
                    price = 289000L,
                    originalPrice = 385000L,
                    discountPercent = "-25%",
                    badgeText = "Chỉ còn 2 chiếc",
                    isRedBadge = true,
                    rating = 4.8f,
                    soldCount = "890",
                    imageUrl = "",
                    imageRes = R.drawable.headphones_anker_space_one_1793957334650
                ),
                WishlistItemData(
                    id = "wish_3",
                    productId = 3L,
                    name = "Tai nghe Bluetooth 5.3 chống ồn chủ động ANC",
                    price = 590000L,
                    originalPrice = 890000L,
                    discountPercent = "Flash Sale",
                    badgeText = null,
                    rating = 5.0f,
                    soldCount = "3.4k",
                    imageUrl = "",
                    imageRes = R.drawable.anker_gan_charger_1793957714558
                ),
                WishlistItemData(
                    id = "wish_4",
                    productId = 4L,
                    name = "Túi Tote Canvas Unisex phong cách Hàn Quốc...",
                    price = 125000L,
                    originalPrice = 210000L,
                    discountPercent = "-40%",
                    badgeText = null,
                    rating = 4.7f,
                    soldCount = "5.6k",
                    imageUrl = "",
                    imageRes = R.drawable.polo_navy_folded_1793957349647
                )
            )
        } else {
            emptyList()
        }
    }

    // Đếm số lượng theo danh mục bộ lọc
    val totalCount = currentItems.size
    val discountCount = currentItems.count { it.originalPrice > it.price }
    val inStockCount = currentItems.count { !it.isOutOfStock }
    val lowStockCount = currentItems.count { it.badgeText != null }

    val filterOptions = listOf(
        "Tất cả ($totalCount)",
        "Đang giảm giá ($discountCount)",
        "Còn hàng ($inStockCount)",
        "Sắp hết hàng ($lowStockCount)"
    )

    // Lọc theo tag đang chọn
    val filteredItems = remember(currentItems, selectedFilter, selectedSort) {
        val list = when {
            selectedFilter.startsWith("Đang giảm giá") -> currentItems.filter { it.originalPrice > it.price }
            selectedFilter.startsWith("Còn hàng") -> currentItems.filter { !it.isOutOfStock }
            selectedFilter.startsWith("Sắp hết hàng") -> currentItems.filter { it.badgeText != null }
            else -> currentItems
        }
        when (selectedSort) {
            "Giá: Thấp đến Cao" -> list.sortedBy { it.price }
            "Giá: Cao đến Thấp" -> list.sortedByDescending { it.price }
            else -> list
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("wishlist_screen"),
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
                        .padding(horizontal = 10.dp, vertical = 10.dp),
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

                        Column {
                            Text(
                                text = "Sản phẩm yêu thích",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (uiState.isLoggedIn && !uiState.isWishlistLoading) {
                                Text(
                                    text = "$totalCount sản phẩm đã lưu",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Nút làm mới
                        if (uiState.isWishlistRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(2.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFFBA2211)
                            )
                        } else {
                            IconButton(onClick = { viewModel.loadWishlist(isRefresh = true) }) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Làm mới",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Thanh hành động khi đang chọn nhiều sản phẩm
            AnimatedVisibility(visible = isSelectionMode && selectedProductIds.isNotEmpty()) {
                Surface(
                    color = SurfaceCard,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Đã chọn: ${selectedProductIds.size} sản phẩm",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )

                        Button(
                            onClick = {
                                viewModel.removeMultipleFromWishlist(selectedProductIds.toList())
                                selectedProductIds.clear()
                                isSelectionMode = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA2211)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Xóa đã chọn",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
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
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Banner nhắc đăng nhập nếu khách chưa đăng nhập
            if (!uiState.isLoggedIn) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEFF6FF))
                            .border(0.8.dp, Color(0xFFDBEAFE), RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Đăng nhập tài khoản",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E40AF)
                                )
                                Text(
                                    text = "Để đồng bộ và lưu trữ sản phẩm yêu thích an toàn",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF3B82F6)
                                )
                            }
                            Button(
                                onClick = { viewModel.openSignIn() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(text = "Đăng nhập", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 3. Thanh Bộ lọc Yêu thích (cuộn ngang)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    filterOptions.forEach { filterName ->
                        val isSelected = filterName.startsWith(selectedFilter) ||
                                (selectedFilter == "Tất cả" && filterName.startsWith("Tất cả"))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Color(0xFFBA2211) else Color(0xFFF3F4F6))
                                .border(
                                    width = if (isSelected) 0.dp else 0.8.dp,
                                    color = Color(0xFFE5E7EB),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    selectedFilter = filterName.split(" ")[0]
                                }
                                .padding(horizontal = 14.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = filterName,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF374151)
                            )
                        }
                    }
                }
            }

            // 4. Thanh Điều khiển Sắp xếp & Toggle Demo Trống
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showSortMenu = true }
                        ) {
                            Text(
                                text = selectedSort,
                                fontSize = 12.5.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Mới lưu gần đây", fontSize = 13.sp) },
                                onClick = {
                                    selectedSort = "Mới lưu gần đây"
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Giá: Thấp đến Cao", fontSize = 13.sp) },
                                onClick = {
                                    selectedSort = "Giá: Thấp đến Cao"
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Giá: Cao đến Thấp", fontSize = 13.sp) },
                                onClick = {
                                    selectedSort = "Giá: Cao đến Thấp"
                                    showSortMenu = false
                                }
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { isDemoEmpty = !isDemoEmpty }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.RemoveRedEye,
                                contentDescription = null,
                                tint = Color(0xFF1E5B94),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isDemoEmpty) "Hiện danh sách" else "Xem demo trống",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1E5B94)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Text(
                            text = if (isSelectionMode) "Xong" else "Chọn (Sửa)",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBA2211),
                            modifier = Modifier.clickable {
                                isSelectionMode = !isSelectionMode
                                if (!isSelectionMode) {
                                    selectedProductIds.clear()
                                }
                            }
                        )
                    }
                }
            }

            // Trạng thái đang tải từ API (Loading State)
            if (uiState.isWishlistLoading && uiState.wishlistItems.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFBA2211),
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Đang tải danh sách yêu thích...",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Trạng thái lỗi tải dữ liệu từ API (Error State)
            if (uiState.wishlistErrorMessage != null && uiState.wishlistItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Không thể tải danh sách yêu thích",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF991B1B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.wishlistErrorMessage,
                                fontSize = 12.sp,
                                color = Color(0xFFB91C1C),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.loadWishlist(isRefresh = true) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(text = "Thử lại", fontSize = 12.5.sp)
                            }
                        }
                    }
                }
            }

            // 5. NỘI DUNG DANH SÁCH SẢN PHẨM HOẶC GIAO DIỆN TRỐNG DEMO
            if (isDemoEmpty || filteredItems.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp, horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .background(Color(0xFFFFF3F0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFBA2211),
                                modifier = Modifier.size(46.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (selectedFilter == "Tất cả")
                                "Danh sách yêu thích đang trống"
                            else
                                "Không tìm thấy sản phẩm phù hợp",
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (selectedFilter == "Tất cả")
                                "Hãy thả tim các sản phẩm bạn yêu thích để dễ dàng theo dõi biến động giá và mua sau nhé!"
                            else
                                "Thử chọn mục khác hoặc quay lại 'Tất cả' để xem toàn bộ sản phẩm đã lưu.",
                            fontSize = 12.5.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (isDemoEmpty) {
                                    isDemoEmpty = false
                                } else {
                                    onBackClick()
                                    viewModel.selectTab(0)
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA2211)),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Text(
                                text = "KHÁM PHÁ SẢN PHẨM NGAY",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                // Lưới 2 cột các sản phẩm trong Wishlist
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (i in filteredItems.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val item1 = filteredItems[i]
                                val isSelected1 = selectedProductIds.contains(item1.productId)

                                WishlistProductCard(
                                    item = item1,
                                    isSelectionMode = isSelectionMode,
                                    isSelected = isSelected1,
                                    onSelectToggle = {
                                        if (isSelected1) selectedProductIds.remove(item1.productId)
                                        else selectedProductIds.add(item1.productId)
                                    },
                                    onRemove = {
                                        viewModel.removeFromWishlist(item1.productId)
                                    },
                                    onAddToCart = {
                                        viewModel.addProductToCart(
                                            ProductItem(
                                                id = item1.productId.toString(),
                                                name = item1.name,
                                                imageUrl = item1.imageUrl,
                                                price = item1.price,
                                                rating = item1.rating,
                                                soldCount = 1000
                                            )
                                        )
                                    },
                                    onCardClick = {
                                        if (isSelectionMode) {
                                            if (isSelected1) selectedProductIds.remove(item1.productId)
                                            else selectedProductIds.add(item1.productId)
                                        } else {
                                            onProductClick(
                                                ProductItem(
                                                    id = item1.productId.toString(),
                                                    name = item1.name,
                                                    imageUrl = item1.imageUrl,
                                                    price = item1.price,
                                                    originalPrice = if (item1.originalPrice > item1.price) item1.originalPrice else null,
                                                    rating = item1.rating,
                                                    soldCount = item1.soldCount.filter { it.isDigit() }.toIntOrNull() ?: 100
                                                )
                                            )
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )

                                if (i + 1 < filteredItems.size) {
                                    val item2 = filteredItems[i + 1]
                                    val isSelected2 = selectedProductIds.contains(item2.productId)

                                    WishlistProductCard(
                                        item = item2,
                                        isSelectionMode = isSelectionMode,
                                        isSelected = isSelected2,
                                        onSelectToggle = {
                                            if (isSelected2) selectedProductIds.remove(item2.productId)
                                            else selectedProductIds.add(item2.productId)
                                        },
                                        onRemove = {
                                            viewModel.removeFromWishlist(item2.productId)
                                        },
                                        onAddToCart = {
                                            viewModel.addProductToCart(
                                                ProductItem(
                                                    id = item2.productId.toString(),
                                                    name = item2.name,
                                                    imageUrl = item2.imageUrl,
                                                    price = item2.price,
                                                    rating = item2.rating,
                                                    soldCount = 1000
                                                )
                                            )
                                        },
                                        onCardClick = {
                                            if (isSelectionMode) {
                                                if (isSelected2) selectedProductIds.remove(item2.productId)
                                                else selectedProductIds.add(item2.productId)
                                            } else {
                                                onProductClick(
                                                    ProductItem(
                                                        id = item2.productId.toString(),
                                                        name = item2.name,
                                                        imageUrl = item2.imageUrl,
                                                        price = item2.price,
                                                        originalPrice = if (item2.originalPrice > item2.price) item2.originalPrice else null,
                                                        rating = item2.rating,
                                                        soldCount = item2.soldCount.filter { it.isDigit() }.toIntOrNull() ?: 100
                                                    )
                                                )
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WishlistProductCard(
    item: WishlistItemData,
    onRemove: () -> Unit,
    onAddToCart: () -> Unit,
    onCardClick: () -> Unit,
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    onSelectToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val productItem = remember(item.id, item.imageUrl, item.price) {
        ProductItem(
            id = item.productId.toString(),
            name = item.name,
            imageUrl = item.imageUrl,
            price = item.price,
            originalPrice = if (item.originalPrice > item.price) item.originalPrice else null,
            rating = item.rating,
            soldCount = item.soldCount.filter { it.isDigit() }.toIntOrNull() ?: 100
        )
    }

    Box(modifier = modifier) {
        ProductCard(
            product = productItem,
            imageRes = item.imageRes,
            badgeText = item.badgeText,
            badgeColor = if (item.isRedBadge) Color(0xFFBA2211) else Color(0xFF1D4ED8),
            customDiscountTag = item.discountPercent,
            showWishlistAction = !isSelectionMode,
            isWishlisted = true,
            onWishlistClick = onRemove,
            onClick = onCardClick,
            bottomAction = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFFF3F0))
                        .border(0.8.dp, Color(0xFFFDE8E4), RoundedCornerShape(6.dp))
                        .clickable { onAddToCart() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingBag,
                            contentDescription = null,
                            tint = Color(0xFFBA2211),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Thêm vào giỏ",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBA2211)
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Overlay checkbox khi ở chế độ chọn nhiều
        if (isSelectionMode) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0xFFBA2211) else Color.White.copy(alpha = 0.9f))
                    .clickable { onSelectToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Đã chọn",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .border(1.5.dp, Color(0xFF9CA3AF), CircleShape)
                    )
                }
            }
        }
    }
}
