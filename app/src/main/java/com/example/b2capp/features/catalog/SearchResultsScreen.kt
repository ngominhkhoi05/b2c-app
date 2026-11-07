package com.example.b2capp.features.catalog

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.TableRows
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.b2capp.core.ui.navigation.AppBottomBar
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
 * Model cho sản phẩm hiển thị trong kết quả tìm kiếm
 */
data class SearchProductResultItem(
    val id: String,
    val name: String,
    val imageRes: Int,
    val price: Long,
    val originalPrice: Long,
    val discountPercent: Int,
    val rating: Float,
    val reviewCountText: String,
    val location: String,
    val topBadgeText: String,
    val topBadgeBg: Color,
    val featureBadgeText: String,
    val featureBadgeType: SearchBadgeType
)

enum class SearchBadgeType {
    FREESHIP_XTRA,
    CHINH_HANG,
    HANG_MOI
}

/**
 * Màn hình Kết quả tìm kiếm (Search Results Screen)
 * Thiết kế chính xác 100% theo SearchResultsScreen_013d5fde.png:
 * 1. Top Bar: Nút Back, Ô tìm kiếm "áo thun nam" (có icon kính lúp & nút xóa x), nút Lọc kèm chấm đỏ, giỏ hàng kèm badge 3.
 * 2. Thanh Sort tabs: Liên quan, Mới nhất, Bán chạy (đang chọn với vạch đỏ), Giá ↑, Giá ↓.
 * 3. Hàng Filter chips:
 *    - Đang lọc: Áo thun nam ✕
 *    - ⚡ Freeship Xtra
 *    - ★ 4★ trở lên
 *    - Shopee Mall
 * 4. Header đếm kết quả: Tìm thấy 1.240 kết quả cho "áo thun nam" | icon chuyển dạng bảng/lưới.
 * 5. Lưới 2 cột 6 sản phẩm:
 *    - Áo thun nam Cotton Compact thoáng mát... (MALL, -40%, 149.000₫, 4.9 (3.4k), TP. HCM)
 *    - Áo phông nam form rộng Unisex phong cách Hàn... (Yêu thích+, -30%, 119.000₫, 4.8 (8.9k), Hà Nội)
 *    - Áo Polo nam ngắn tay vải Pique gai cá sấu cao cấp (MALL, -25%, 189.000₫, 4.9 (1.8k), TP. HCM)
 *    - Áo thun thể thao nam Pro Dry thoáng khí co giãn 4 (Chính hãng, -50%, 99.000₫, 5.0 (5.2k), Đà Nẵng)
 *    - Áo thun kẻ sọc Vintage form regular fit chất đũi... (Yêu thích, -15%, 135.000₫, 4.7 (620), TP. HCM)
 *    - Áo thun in họa tiết Typography phản quang (MALL, -35%, 165.000₫, 4.9 (2.1k), Hà Nội)
 * 6. Nút "Xem thêm 1.234 sản phẩm ˇ".
 * 7. Vòng xoay đồng bộ "Đang tự động đồng bộ ưu đãi tốt nhất".
 * 8. BottomBar 5 tabs.
 */
@Composable
fun SearchResultsScreen(
    query: String,
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit,
    onNavigateToCart: () -> Unit,
    onProductClick: (ProductItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchInput by remember { mutableStateOf(query) }
    var selectedSortTab by remember { mutableStateOf("Bán chạy") }
    var isFreeshipFilterActive by remember { mutableStateOf(true) }
    var isStarFilterActive by remember { mutableStateOf(false) }
    var isQueryFilterActive by remember { mutableStateOf(true) }
    var showAdvancedFilterSheet by remember { mutableStateOf(false) }
    var activeFilterCriteria by remember { mutableStateOf(AdvancedFilterCriteria()) }

    val focusManager = LocalFocusManager.current

    // Danh sách 6 sản phẩm chính xác theo ảnh
    val productsList = remember {
        listOf(
            SearchProductResultItem(
                id = "sr_1",
                name = "Áo thun nam Cotton Compact thoáng mát siêu mềm mịn",
                imageRes = R.drawable.product_white_tshirt_1793955938236,
                price = 149000L,
                originalPrice = 249000L,
                discountPercent = 40,
                rating = 4.9f,
                reviewCountText = "3.4k",
                location = "TP. HCM",
                topBadgeText = "MALL",
                topBadgeBg = Color(0xFFB82414),
                featureBadgeText = "Freeship Xtra",
                featureBadgeType = SearchBadgeType.FREESHIP_XTRA
            ),
            SearchProductResultItem(
                id = "sr_2",
                name = "Áo phông nam form rộng Unisex phong cách Hàn Quốc",
                imageRes = R.drawable.tshirt_black_streetwear_1793958196938,
                price = 119000L,
                originalPrice = 170000L,
                discountPercent = 30,
                rating = 4.8f,
                reviewCountText = "8.9k",
                location = "Hà Nội",
                topBadgeText = "",
                topBadgeBg = Color.Transparent,
                featureBadgeText = "",
                featureBadgeType = SearchBadgeType.FREESHIP_XTRA
            ),
            SearchProductResultItem(
                id = "sr_3",
                name = "Áo Polo nam ngắn tay vải Pique gai cá sấu cao cấp",
                imageRes = R.drawable.polo_navy_folded_1793957349647,
                price = 189000L,
                originalPrice = 250000L,
                discountPercent = 25,
                rating = 4.9f,
                reviewCountText = "1.8k",
                location = "TP. HCM",
                topBadgeText = "MALL",
                topBadgeBg = Color(0xFFB82414),
                featureBadgeText = "Chính hãng",
                featureBadgeType = SearchBadgeType.CHINH_HANG
            ),
            SearchProductResultItem(
                id = "sr_4",
                name = "Áo thun thể thao nam Pro Dry thoáng khí co giãn 4",
                imageRes = R.drawable.tshirt_sport_heather_grey_1793958213522,
                price = 99000L,
                originalPrice = 199000L,
                discountPercent = 50,
                rating = 5.0f,
                reviewCountText = "5.2k",
                location = "Đà Nẵng",
                topBadgeText = "Chính hãng",
                topBadgeBg = Color(0xFF28537D),
                featureBadgeText = "Freeship Xtra",
                featureBadgeType = SearchBadgeType.FREESHIP_XTRA
            ),
            SearchProductResultItem(
                id = "sr_5",
                name = "Áo thun kẻ sọc Vintage form regular fit chất đũi mềm",
                imageRes = R.drawable.tshirt_striped_vintage_1793958230121,
                price = 135000L,
                originalPrice = 160000L,
                discountPercent = 15,
                rating = 4.7f,
                reviewCountText = "620",
                location = "TP. HCM",
                topBadgeText = "",
                topBadgeBg = Color.Transparent,
                featureBadgeText = "Hàng mới",
                featureBadgeType = SearchBadgeType.HANG_MOI
            ),
            SearchProductResultItem(
                id = "sr_6",
                name = "Áo thun in họa tiết Typography phản quang nổi bật",
                imageRes = R.drawable.tshirt_olive_pocket_1793958249431,
                price = 165000L,
                originalPrice = 255000L,
                discountPercent = 35,
                rating = 4.9f,
                reviewCountText = "2.1k",
                location = "Hà Nội",
                topBadgeText = "MALL",
                topBadgeBg = Color(0xFFB82414),
                featureBadgeText = "Freeship Xtra",
                featureBadgeType = SearchBadgeType.FREESHIP_XTRA
            )
        )
    }

    val sortTabs = listOf("Liên quan", "Mới nhất", "Bán chạy", "Giá ↑", "Giá ↓")

    val sortedProducts = remember(selectedSortTab, productsList, activeFilterCriteria) {
        val filtered = productsList.filter { product ->
            val matchPrice = product.price in activeFilterCriteria.minPrice..activeFilterCriteria.maxPrice
            val matchMall = !activeFilterCriteria.isShopeeMall || product.topBadgeText == "MALL"
            val matchFreeship = !activeFilterCriteria.isFreeShipping || product.featureBadgeType == SearchBadgeType.FREESHIP_XTRA
            val matchStar = product.rating >= (activeFilterCriteria.starRatingMin.toFloat() - 0.5f)
            matchPrice && matchMall && matchFreeship && matchStar
        }
        val listToSort = if (filtered.isEmpty()) productsList else filtered
        when (selectedSortTab) {
            "Giá ↑" -> listToSort.sortedBy { it.price }
            "Giá ↓" -> listToSort.sortedByDescending { it.price }
            "Mới nhất" -> listToSort.reversed()
            else -> listToSort // Bán chạy hoặc liên quan
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_results_screen"),
        bottomBar = {
            AppBottomBar(
                selectedTab = uiState.currentTab,
                onTabSelected = { tabId ->
                    viewModel.closeSearchResults()
                    viewModel.selectTab(tabId)
                },
                cartBadgeCount = uiState.totalCartCount
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA))
        ) {
            // 1. Top Bar: Nút Back, Thanh tìm kiếm, Nút Filter, Giỏ hàng
            Surface(
                color = SurfaceCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Nút Back mũi tên trái
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

                        // Ô tìm kiếm có icon search & nút xóa
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

                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                BasicTextField(
                                    value = searchInput,
                                    onValueChange = { searchInput = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        fontSize = 13.5.sp,
                                        color = TextPrimary
                                    ),
                                    cursorBrush = SolidColor(CoralPrimary),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    keyboardActions = KeyboardActions(
                                        onSearch = {
                                            focusManager.clearFocus()
                                            viewModel.openSearchResults(searchInput)
                                        }
                                    )
                                )
                            }

                            if (searchInput.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.Filled.Cancel,
                                    contentDescription = "Xóa",
                                    tint = Color(0xFF9CA3AF),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { searchInput = "" }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Nút lọc có chấm đỏ
                        Box(contentAlignment = Alignment.TopEnd) {
                            IconButton(
                                onClick = { showAdvancedFilterSheet = true },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Tune,
                                    contentDescription = "Bộ lọc",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            // Chấm đỏ chỉ thị đang có bộ lọc hoạt động
                            Box(
                                modifier = Modifier
                                    .padding(top = 8.dp, end = 8.dp)
                                    .size(6.dp)
                                    .background(Color(0xFFBA2211), CircleShape)
                            )
                        }

                        // Nút giỏ hàng có badge 3
                        Box(contentAlignment = Alignment.TopEnd) {
                            IconButton(
                                onClick = onNavigateToCart,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ShoppingCart,
                                    contentDescription = "Giỏ hàng",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            if (uiState.totalCartCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp, end = 4.dp)
                                        .size(16.dp)
                                        .background(Color(0xFFBA2211), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = uiState.totalCartCount.toString(),
                                        color = Color.White,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // 2. Thanh Sort Tabs (Liên quan, Mới nhất, Bán chạy, Giá ↑, Giá ↓)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        sortTabs.forEach { tabName ->
                            val isTabSelected = tabName == selectedSortTab

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { selectedSortTab = tabName }
                                    .padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = tabName,
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isTabSelected) Color(0xFFBA2211) else TextSecondary
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Box(
                                    modifier = Modifier
                                        .width(if (isTabSelected) 28.dp else 0.dp)
                                        .height(2.dp)
                                        .background(
                                            if (isTabSelected) Color(0xFFBA2211) else Color.Transparent,
                                            RoundedCornerShape(1.dp)
                                        )
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                    // 3. Hàng Filter Chips (cuộn ngang)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Chip 1: Đang lọc: Áo thun nam ✕
                        if (isQueryFilterActive) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFFFF1EE))
                                    .border(0.8.dp, Color(0xFFF87171), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Đang lọc: $searchInput",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFBA2211)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Hủy lọc",
                                        tint = Color(0xFFBA2211),
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clickable { isQueryFilterActive = false }
                                    )
                                }
                            }
                        }

                        // Chip 2: ⚡ Freeship Xtra
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isFreeshipFilterActive) Color(0xFFE6F7F5) else Color(0xFFF3F4F6))
                                .clickable { isFreeshipFilterActive = !isFreeshipFilterActive }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.FlashOn,
                                    contentDescription = null,
                                    tint = Color(0xFF1E8275),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Freeship Xtra",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1E8275)
                                )
                            }
                        }

                        // Chip 3: ★ 4★ trở lên
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isStarFilterActive) Color(0xFFFEF3C7) else Color(0xFFF3F4F6))
                                .clickable { isStarFilterActive = !isStarFilterActive }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = StarRatingGold,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "4★ trở lên",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF4B5563)
                                )
                            }
                        }

                        // Chip 4: Shopee Mall
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFF3F4F6))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Shopee Mall",
                                fontSize = 11.5.sp,
                                color = Color(0xFF4B5563)
                            )
                        }

                        // Chip 5: Nơi bán: Hà Nội / TP.HCM
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFF3F4F6))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "TP. HCM & Hà Nội",
                                fontSize = 11.5.sp,
                                color = Color(0xFF4B5563)
                            )
                        }
                    }
                }
            }

            // Danh sách cuộn sản phẩm
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 4. Header: Tìm thấy 1.240 kết quả cho "áo thun nam" | Nút đổi layout
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Tìm thấy ",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "1.240",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = " kết quả cho ",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "\"$searchInput\"",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Icon(
                            imageVector = Icons.Outlined.TableRows,
                            contentDescription = "Chuyển chế độ xem",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // 5. Lưới 2 cột các sản phẩm kết quả tìm kiếm
                val productRows = sortedProducts.chunked(2)
                items(productRows.size) { rowIndex ->
                    val rowItems = productRows[rowIndex]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { product ->
                            val productItem = remember(product.id) {
                                ProductItem(
                                    id = product.id,
                                    name = product.name,
                                    imageUrl = "",
                                    price = product.price,
                                    originalPrice = product.originalPrice,
                                    rating = product.rating,
                                    soldCount = 3400,
                                    isFreeShipping = product.featureBadgeType == SearchBadgeType.FREESHIP_XTRA,
                                    shopName = if (product.topBadgeText == "MALL") "Coolmate Official Store" else "Thời Trang Unisex",
                                    shopLocation = product.location
                                )
                            }
                            ProductCard(
                                product = productItem,
                                onClick = { onProductClick(productItem) },
                                badgeText = product.topBadgeText,
                                badgeColor = product.topBadgeBg,
                                imageRes = product.imageRes,
                                locationText = product.location,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // 6. Nút "Xem thêm 1.234 sản phẩm ˇ"
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceCard)
                            .border(1.dp, BorderDivider, RoundedCornerShape(8.dp))
                            .clickable { /* Tải thêm */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Xem thêm 1.234 sản phẩm",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBA2211)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ˇ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBA2211)
                            )
                        }
                    }
                }

                // 7. Đồng bộ ưu đãi footer
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Vòng xoay đỏ nhỏ
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = Color(0xFFBA2211),
                            strokeWidth = 1.8.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Đang tự động đồng bộ ưu đãi tốt nhất",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    // Hiển thị BottomSheet Bộ lọc tìm kiếm nâng cao (Khớp 100% AdvancedFiltersScreen_1917256d.png)
    if (showAdvancedFilterSheet) {
        AdvancedFiltersBottomSheet(
            initialCriteria = activeFilterCriteria,
            onDismissRequest = { showAdvancedFilterSheet = false },
            onApplyFilter = { criteria ->
                activeFilterCriteria = criteria
                showAdvancedFilterSheet = false
                viewModel.showToast("Đã áp dụng bộ lọc nâng cao!")
            }
        )
    }
}


