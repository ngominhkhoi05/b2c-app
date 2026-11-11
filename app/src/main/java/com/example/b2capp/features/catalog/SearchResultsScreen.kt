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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
    var isFreeshipFilterActive by remember { mutableStateOf(false) }
    var isStarFilterActive by remember { mutableStateOf(false) }
    var isMallFilterActive by remember { mutableStateOf(false) }
    var isLocationFilterActive by remember { mutableStateOf(false) }
    var isQueryFilterActive by remember { mutableStateOf(true) }
    var showAdvancedFilterSheet by remember { mutableStateOf(false) }
    var activeFilterCriteria by remember { mutableStateOf(AdvancedFilterCriteria()) }

    val focusManager = LocalFocusManager.current

    // Hàm tiện ích thực hiện tìm kiếm kết hợp toàn bộ tiêu chí lọc
    fun triggerSearch(
        targetQuery: String = searchInput,
        sortTab: String = selectedSortTab,
        criteria: AdvancedFilterCriteria = activeFilterCriteria,
        starActive: Boolean = isStarFilterActive
    ) {
        val (sortBy, sortDir) = when (sortTab) {
            "Mới nhất" -> Pair("created", "desc")
            "Bán chạy" -> Pair("sold", "desc")
            "Giá ↑" -> Pair("price", "asc")
            "Giá ↓" -> Pair("price", "desc")
            else -> Pair("created", "desc")
        }
        val effectiveRating = if (starActive) 4.0f else criteria.starRatingMin?.toFloat()
        viewModel.performSearch(
            query = targetQuery,
            sortBy = sortBy,
            sortDir = sortDir,
            minPrice = criteria.minPrice,
            maxPrice = criteria.maxPrice,
            minRating = effectiveRating,
            brand = criteria.selectedBrands.firstOrNull()
        )
    }

    androidx.compose.runtime.LaunchedEffect(query) {
        searchInput = query
        triggerSearch(targetQuery = query)
    }

    val sortTabs = listOf("Liên quan", "Mới nhất", "Bán chạy", "Giá ↑", "Giá ↓")

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_results_screen"),
        bottomBar = {
            AppBottomBar(
                selectedTab = uiState.currentTab,
                onTabSelected = { tabId ->
                    viewModel.closeSearchResults()
                    viewModel.reloadTab(tabId)
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
                                    onValueChange = {
                                        searchInput = it
                                        viewModel.onSearchQueryInputChanged(it)
                                    },
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
                                            triggerSearch(targetQuery = searchInput)
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
                                        .clickable {
                                            searchInput = ""
                                            viewModel.onSearchQueryInputChanged("")
                                            triggerSearch(targetQuery = "")
                                        }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Nút lọc có chấm đỏ chỉ thị bộ lọc đang hoạt động
                        val hasActiveFilters = activeFilterCriteria.isActive || isFreeshipFilterActive || isStarFilterActive || isMallFilterActive || isLocationFilterActive
                        Box(contentAlignment = Alignment.TopEnd) {
                            IconButton(
                                onClick = { showAdvancedFilterSheet = true },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Tune,
                                    contentDescription = "Bộ lọc",
                                    tint = if (hasActiveFilters) CoralPrimary else TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            if (hasActiveFilters) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 8.dp, end = 8.dp)
                                        .size(6.dp)
                                        .background(Color(0xFFBA2211), CircleShape)
                                )
                            }
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
                                    .clickable {
                                        selectedSortTab = tabName
                                        triggerSearch(sortTab = tabName)
                                    }
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
                        // Chip 1: Đang lọc: Từ khóa ✕
                        if (searchInput.isNotBlank()) {
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
                                            .clickable {
                                                searchInput = ""
                                                viewModel.onSearchQueryInputChanged("")
                                                triggerSearch(targetQuery = "")
                                            }
                                    )
                                }
                            }
                        }

                        // Chip 2: ⚡ Freeship Xtra
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isFreeshipFilterActive) Color(0xFFE6F7F5) else Color(0xFFF3F4F6))
                                .border(
                                    0.8.dp,
                                    if (isFreeshipFilterActive) Color(0xFF1E8275) else Color.Transparent,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { isFreeshipFilterActive = !isFreeshipFilterActive }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.FlashOn,
                                    contentDescription = null,
                                    tint = if (isFreeshipFilterActive) Color(0xFF1E8275) else Color(0xFF6B7280),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Freeship Xtra",
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isFreeshipFilterActive) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isFreeshipFilterActive) Color(0xFF1E8275) else Color(0xFF4B5563)
                                )
                            }
                        }

                        // Chip 3: ★ 4★ trở lên
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isStarFilterActive) Color(0xFFFEF3C7) else Color(0xFFF3F4F6))
                                .border(
                                    0.8.dp,
                                    if (isStarFilterActive) Color(0xFFF59E0B) else Color.Transparent,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    isStarFilterActive = !isStarFilterActive
                                    triggerSearch(starActive = isStarFilterActive)
                                }
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
                                    fontWeight = if (isStarFilterActive) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isStarFilterActive) Color(0xFFB45309) else Color(0xFF4B5563)
                                )
                            }
                        }

                        // Chip 4: Shopee Mall
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isMallFilterActive) Color(0xFFFEECE8) else Color(0xFFF3F4F6))
                                .border(
                                    0.8.dp,
                                    if (isMallFilterActive) Color(0xFFBA2211) else Color.Transparent,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { isMallFilterActive = !isMallFilterActive }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Shopee Mall",
                                fontSize = 11.5.sp,
                                fontWeight = if (isMallFilterActive) FontWeight.Bold else FontWeight.Medium,
                                color = if (isMallFilterActive) Color(0xFFBA2211) else Color(0xFF4B5563)
                            )
                        }

                        // Chip 5: Nơi bán: Hà Nội / TP.HCM
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isLocationFilterActive) Color(0xFFEFF6FF) else Color(0xFFF3F4F6))
                                .border(
                                    0.8.dp,
                                    if (isLocationFilterActive) Color(0xFF3B82F6) else Color.Transparent,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { isLocationFilterActive = !isLocationFilterActive }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "TP. HCM & Hà Nội",
                                fontSize = 11.5.sp,
                                fontWeight = if (isLocationFilterActive) FontWeight.Bold else FontWeight.Medium,
                                color = if (isLocationFilterActive) Color(0xFF1D4ED8) else Color(0xFF4B5563)
                            )
                        }
                    }
                }
            }

            // 5. Danh sách sản phẩm kết quả tìm kiếm: lấy hoàn toàn từ API backend (không dùng dữ liệu mock)
            val rawProducts: List<ProductItem> = uiState.searchResults

            // Lọc bổ sung theo các tiêu chí client-side
            val effectiveProducts = remember(
                rawProducts,
                isFreeshipFilterActive,
                isMallFilterActive,
                isStarFilterActive,
                isLocationFilterActive,
                activeFilterCriteria
            ) {
                rawProducts.filter { product ->
                    val matchFreeship = !isFreeshipFilterActive || product.isFreeShipping
                    val matchMall = !isMallFilterActive || (product.rating >= 4.9f || product.shopName.contains("Mall", ignoreCase = true) || product.shopName.contains("Official", ignoreCase = true))
                    val matchStar = !isStarFilterActive || product.rating >= 4.0f
                    val matchLocation = !isLocationFilterActive || (product.shopLocation.contains("Hà Nội", ignoreCase = true) || product.shopLocation.contains("HCM", ignoreCase = true))
                    val matchCategory = activeFilterCriteria.selectedCategories.isEmpty() ||
                            activeFilterCriteria.selectedCategories.any { cat ->
                                product.category.contains(cat, ignoreCase = true) ||
                                product.name.contains(cat, ignoreCase = true)
                            }
                    matchFreeship && matchMall && matchStar && matchLocation && matchCategory
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
                            val countText = if (uiState.isSearching) "..." else if (uiState.searchResults.isNotEmpty()) uiState.searchTotalElements.toString() else "0"
                            Text(
                                text = "Tìm thấy ",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = countText,
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

                if (uiState.isSearching) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(36.dp),
                                color = CoralPrimary
                            )
                        }
                    }
                } else if (effectiveProducts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 50.dp, horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = null,
                                    tint = Color(0xFF9CA3AF),
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Không tìm thấy sản phẩm phù hợp",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Hãy thử tìm kiếm với từ khóa khác hoặc điều chỉnh bộ lọc",
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        isFreeshipFilterActive = false
                                        isStarFilterActive = false
                                        isMallFilterActive = false
                                        isLocationFilterActive = false
                                        activeFilterCriteria = AdvancedFilterCriteria()
                                        triggerSearch(criteria = AdvancedFilterCriteria(), starActive = false)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Xóa tất cả bộ lọc",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    )
                                }
                            }
                        }
                    }
                } else {
                    val productRows = effectiveProducts.chunked(2)
                    items(productRows.size) { rowIndex ->
                        val rowItems = productRows[rowIndex]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowItems.forEach { productItem ->
                                ProductCard(
                                    product = productItem,
                                    onClick = { onProductClick(productItem) },
                                    badgeText = if (productItem.rating >= 4.9f) "MALL" else if (productItem.soldCount > 100) "Yêu thích" else null,
                                    badgeColor = if (productItem.rating >= 4.9f) Color(0xFFB82414) else if (productItem.soldCount > 100) CoralPrimary else null,
                                    locationText = productItem.shopLocation ?: "TP. HCM",
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // 6. Nút "Xem thêm sản phẩm" khi có nhiều kết quả
                val searchTotal = uiState.searchTotalElements.toInt()
                if (!uiState.isSearching && searchTotal > effectiveProducts.size && effectiveProducts.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceCard)
                                .border(1.dp, BorderDivider, RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.performSearch(
                                        query = searchInput,
                                        page = 1
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Xem thêm ${searchTotal - effectiveProducts.size} sản phẩm",
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
                if (criteria.isFreeShipping) isFreeshipFilterActive = true
                if (criteria.isShopeeMall) isMallFilterActive = true
                if (criteria.starRatingMin != null && criteria.starRatingMin >= 4) isStarFilterActive = true
                viewModel.showToast("Đã áp dụng bộ lọc nâng cao!")
                triggerSearch(criteria = criteria, starActive = isStarFilterActive)
            }
        )
    }
}


