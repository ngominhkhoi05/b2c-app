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
 * 7. BottomBar 5 tabs.
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
    var selectedSortTab by remember { mutableStateOf("Liên quan") }
    var priceSortAsc by remember { mutableStateOf<Boolean?>(null) } // true: asc, false: desc, null: inactive
    var isQueryFilterActive by remember { mutableStateOf(true) }
    var showAdvancedFilterSheet by remember { mutableStateOf(false) }
    var activeFilterCriteria by remember { mutableStateOf(AdvancedFilterCriteria()) }

    val focusManager = LocalFocusManager.current

    // Hàm tiện ích thực hiện tìm kiếm kết hợp toàn bộ tiêu chí lọc và sắp xếp
    fun triggerSearch(
        targetQuery: String = searchInput,
        sortTab: String = selectedSortTab,
        isPriceAsc: Boolean? = priceSortAsc,
        criteria: AdvancedFilterCriteria = activeFilterCriteria
    ) {
        val (sortBy, sortDir) = when (sortTab) {
            "Mới nhất" -> Pair("created", "desc")
            "Bán chạy" -> Pair("sold", "desc")
            "Giá" -> if (isPriceAsc == false) Pair("price", "desc") else Pair("price", "asc")
            "Giá ↑" -> Pair("price", "asc")
            "Giá ↓" -> Pair("price", "desc")
            "Liên quan" -> Pair("relevance", "desc")
            else -> Pair("relevance", "desc")
        }
        val effectiveRating = criteria.starRatingMin?.toFloat()
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

    val sortTabs = listOf("Liên quan", "Mới nhất", "Bán chạy", "Giá")

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_results_screen"),
        topBar = {
            // 1. Top Bar & Header: Gọn gàng, tránh double padding status bar, giảm chiều cao các hàng
            Surface(
                color = SurfaceCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Nút Back mũi tên trái
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.size(38.dp)
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
                                .height(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF0F2F5))
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Tìm kiếm",
                                tint = TextSecondary,
                                modifier = Modifier.size(17.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

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

                        Spacer(modifier = Modifier.width(2.dp))

                        // Nút lọc có chấm đỏ chỉ thị bộ lọc đang hoạt động
                        val hasActiveFilters = activeFilterCriteria.isActive
                        Box(contentAlignment = Alignment.TopEnd) {
                            IconButton(
                                onClick = { showAdvancedFilterSheet = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Tune,
                                    contentDescription = "Bộ lọc",
                                    tint = if (hasActiveFilters) CoralPrimary else TextPrimary,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                            if (hasActiveFilters) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 7.dp, end = 7.dp)
                                        .size(6.dp)
                                        .background(Color(0xFFBA2211), CircleShape)
                                )
                            }
                        }

                        // Nút giỏ hàng có badge
                        Box(contentAlignment = Alignment.TopEnd) {
                            IconButton(
                                onClick = onNavigateToCart,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ShoppingCart,
                                    contentDescription = "Giỏ hàng",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                            if (uiState.totalCartCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 3.dp, end = 3.dp)
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

                    // 2. Thanh Sort Tabs (Liên quan, Mới nhất, Bán chạy, Giá ⇅)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        sortTabs.forEach { tabName ->
                            val isTabSelected = tabName == selectedSortTab

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        if (tabName == "Giá") {
                                            if (selectedSortTab == "Giá") {
                                                // Đang ở tab Giá: bấm tiếp sẽ đảo chiều giá thấp <-> cao
                                                val nextAsc = !(priceSortAsc ?: true)
                                                priceSortAsc = nextAsc
                                                triggerSearch(sortTab = "Giá", isPriceAsc = nextAsc)
                                            } else {
                                                selectedSortTab = "Giá"
                                                val nextAsc = true
                                                priceSortAsc = nextAsc
                                                triggerSearch(sortTab = "Giá", isPriceAsc = nextAsc)
                                            }
                                        } else {
                                            selectedSortTab = tabName
                                            priceSortAsc = null
                                            triggerSearch(sortTab = tabName, isPriceAsc = null)
                                        }
                                    }
                                    .padding(vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = tabName,
                                        fontSize = 13.sp,
                                        fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isTabSelected) Color(0xFFBA2211) else TextSecondary
                                    )

                                    if (tabName == "Giá") {
                                        Spacer(modifier = Modifier.width(2.dp))
                                        val arrow = when {
                                            !isTabSelected -> "⇅"
                                            priceSortAsc == false -> "↓"
                                            else -> "↑"
                                        }
                                        Text(
                                            text = arrow,
                                            fontSize = 12.sp,
                                            fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isTabSelected) Color(0xFFBA2211) else TextSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                Box(
                                    modifier = Modifier
                                        .width(if (isTabSelected) 24.dp else 0.dp)
                                        .height(2.dp)
                                        .background(
                                            if (isTabSelected) Color(0xFFBA2211) else Color.Transparent,
                                            RoundedCornerShape(1.dp)
                                        )
                                )
                            }
                        }
                    }

                    // 3. Hàng Filter Chips chỉ hiển thị khi người dùng đã chọn điều kiện ở popup lọc
                    if (activeFilterCriteria.isActive) {
                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Chip: Khoảng giá
                            if (activeFilterCriteria.minPrice != null || activeFilterCriteria.maxPrice != null) {
                                val priceLabel = when {
                                    activeFilterCriteria.selectedPricePreset != null -> activeFilterCriteria.selectedPricePreset!!
                                    activeFilterCriteria.minPrice != null && activeFilterCriteria.maxPrice != null ->
                                        "${formatShortPriceVnd(activeFilterCriteria.minPrice!!)} – ${formatShortPriceVnd(activeFilterCriteria.maxPrice!!)}"
                                    activeFilterCriteria.minPrice != null -> "Từ ${formatShortPriceVnd(activeFilterCriteria.minPrice!!)}"
                                    else -> "Đến ${formatShortPriceVnd(activeFilterCriteria.maxPrice!!)}"
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFFF1EE))
                                        .border(0.8.dp, Color(0xFFF87171), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Giá: $priceLabel",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFBA2211)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Xóa lọc giá",
                                            tint = Color(0xFFBA2211),
                                            modifier = Modifier
                                                .size(11.dp)
                                                .clickable {
                                                    val updated = activeFilterCriteria.copy(
                                                        minPrice = null,
                                                        maxPrice = null,
                                                        selectedPricePreset = null
                                                    )
                                                    activeFilterCriteria = updated
                                                    triggerSearch(criteria = updated)
                                                }
                                        )
                                    }
                                }
                            }

                            // Chip: Thương hiệu
                            activeFilterCriteria.selectedBrands.forEach { brand ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFFF1EE))
                                        .border(0.8.dp, Color(0xFFF87171), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = brand,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFBA2211)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Xóa thương hiệu $brand",
                                            tint = Color(0xFFBA2211),
                                            modifier = Modifier
                                                .size(11.dp)
                                                .clickable {
                                                    val updated = activeFilterCriteria.copy(
                                                        selectedBrands = activeFilterCriteria.selectedBrands - brand
                                                    )
                                                    activeFilterCriteria = updated
                                                    triggerSearch(criteria = updated)
                                                }
                                        )
                                    }
                                }
                            }

                            // Chip: Đánh giá sao
                            if (activeFilterCriteria.starRatingMin != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFEF3C7))
                                        .border(0.8.dp, Color(0xFFF59E0B), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = StarRatingGold,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Từ ${activeFilterCriteria.starRatingMin}★ trở lên",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFB45309)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Xóa lọc sao",
                                            tint = Color(0xFFB45309),
                                            modifier = Modifier
                                                .size(11.dp)
                                                .clickable {
                                                    val updated = activeFilterCriteria.copy(starRatingMin = null)
                                                    activeFilterCriteria = updated
                                                    triggerSearch(criteria = updated)
                                                }
                                        )
                                    }
                                }
                            }

                            // Nút xóa tất cả nếu có điều kiện lọc
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF3F4F6))
                                    .clickable {
                                        val updated = AdvancedFilterCriteria()
                                        activeFilterCriteria = updated
                                        triggerSearch(criteria = updated)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Xóa tất cả",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }
                    }
                }
            }
        },
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

            // 5. Danh sách sản phẩm kết quả tìm kiếm: lấy hoàn toàn từ API backend (không dùng dữ liệu mock)
            val rawProducts: List<ProductItem> = uiState.searchResults

            // Lọc và sắp xếp theo tab + tiêu chí lọc client-side
            val effectiveProducts = remember(
                rawProducts,
                activeFilterCriteria,
                selectedSortTab,
                priceSortAsc
            ) {
                val starMin = activeFilterCriteria.starRatingMin
                val pMin = activeFilterCriteria.minPrice
                val pMax = activeFilterCriteria.maxPrice
                val brands = activeFilterCriteria.selectedBrands

                val filtered = rawProducts.filter { product ->
                    val matchStar = starMin == null || product.rating >= starMin.toFloat()
                    val matchPriceMin = pMin == null || product.price >= pMin
                    val matchPriceMax = pMax == null || product.price <= pMax
                    val matchBrand = brands.isEmpty() || brands.any { brand ->
                        product.name.contains(brand, ignoreCase = true) || product.shopName.contains(brand, ignoreCase = true)
                    }
                    matchStar && matchPriceMin && matchPriceMax && matchBrand
                }

                when (selectedSortTab) {
                    "Mới nhất" -> filtered.sortedByDescending { it.id.toLongOrNull() ?: 0L }
                    "Bán chạy" -> filtered.sortedByDescending { it.soldCount }
                    "Giá" -> if (priceSortAsc == false) filtered.sortedByDescending { it.price } else filtered.sortedBy { it.price }
                    "Giá ↑" -> filtered.sortedBy { it.price }
                    "Giá ↓" -> filtered.sortedByDescending { it.price }
                    else -> filtered // "Liên quan" giữ nguyên thứ tự FTS relevance từ backend
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
                                        activeFilterCriteria = AdvancedFilterCriteria()
                                        triggerSearch(criteria = AdvancedFilterCriteria())
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

                item {
                    Spacer(modifier = Modifier.height(16.dp))
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
                triggerSearch(criteria = criteria)
            }
        )
    }
}

private fun formatShortPriceVnd(amount: Long): String {
    return if (amount >= 1_000_000) {
        val millions = amount / 1_000_000.0
        if (millions == millions.toLong().toDouble()) "${millions.toLong()}tr" else "${String.format(Locale.US, "%.1f", millions)}tr"
    } else if (amount >= 1_000) {
        "${amount / 1_000}k"
    } else {
        "${amount}₫"
    }
}


