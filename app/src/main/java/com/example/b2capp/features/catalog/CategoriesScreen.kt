package com.example.b2capp.features.catalog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material.icons.outlined.Weekend
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.StarRatingGold
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.data.remote.dto.CategoryDto
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import java.util.Locale

/**
 * Màn hình Duyệt danh mục:
 * - Bên trái: Thanh danh mục được nạp từ Database qua API (roots / categories tree)
 * - Bên phải: Các sản phẩm thuộc danh mục đó được nạp từ Database qua API
 * - Hỗ trợ lọc theo danh mục con (Subcategories) nếu danh mục có nhóm con
 * - Tương tác click sản phẩm mở chi tiết đầy đủ
 */
@Composable
fun CategoriesScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onNavigateToCart: () -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchInput by remember { mutableStateOf("") }

    // Tự động tải danh mục và lập tức load sản phẩm của danh mục đang chọn (hoặc mục đầu tiên nếu chưa chọn)
    LaunchedEffect(Unit) {
        if (uiState.browserCategories.isEmpty()) {
            val pendingCategory = uiState.selectedCategory.takeIf { it != "Tất cả" }
            viewModel.loadBrowserCategories(
                autoSelectFirst = (uiState.selectedBrowserCategory == null && pendingCategory == null),
                preferredCategoryId = uiState.selectedBrowserCategory?.id,
                preferredCategoryName = uiState.selectedBrowserCategory?.name ?: pendingCategory
            )
        } else {
            val targetCategory = uiState.selectedBrowserCategory ?: uiState.browserCategories.first()
            if (uiState.selectedBrowserCategory?.id != targetCategory.id || uiState.browserProducts.isEmpty()) {
                viewModel.selectBrowserCategory(targetCategory)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCard)
            .testTag("category_browser_screen")
    ) {
        // 1. Top Bar: Nút Back, Thanh tìm kiếm, Giỏ hàng có Badge
        CategoryTopBar(
            searchQuery = searchInput,
            onSearchChange = { searchInput = it },
            onSearchSubmit = { query ->
                viewModel.openSearchResults(query.ifBlank { uiState.selectedBrowserCategory?.name ?: "sản phẩm" })
            },
            cartBadgeCount = uiState.totalCartCount,
            onBackClick = onBackClick,
            onCartClick = onNavigateToCart
        )

        HorizontalDivider(color = BorderDivider.copy(alpha = 0.6f), thickness = 0.8.dp)

        // 2. Nội dung dạng Split View 2 cột:
        // - Cột trái: Thanh danh mục nạp từ Database
        // - Cột phải: Danh sách sản phẩm thuộc danh mục
        Row(modifier = Modifier.fillMaxSize()) {
            // Cột trái: Sidebar Danh mục
            LeftCategorySidebar(
                categories = uiState.browserCategories,
                selectedCategory = uiState.selectedBrowserCategory,
                isLoading = uiState.isBrowserCategoriesLoading,
                onCategoryClick = { cat ->
                    viewModel.selectBrowserCategory(cat)
                },
                onRetry = {
                    viewModel.refreshBrowserCategories()
                },
                modifier = Modifier
                    .width(96.dp)
                    .fillMaxHeight()
            )

            // Cột phải: Các sản phẩm thuộc danh mục đó
            RightProductsPanel(
                selectedCategory = uiState.selectedBrowserCategory,
                selectedSubCategoryId = uiState.selectedBrowserSubCategoryId,
                products = uiState.browserProducts,
                isLoading = uiState.isBrowserProductsLoading,
                onSubCategorySelect = { subId ->
                    viewModel.selectBrowserSubCategory(subId)
                },
                onProductClick = { product ->
                    viewModel.openProductDetailsScreen(product)
                },
                onRetry = {
                    val currentId = uiState.selectedBrowserSubCategoryId ?: uiState.selectedBrowserCategory?.id
                    viewModel.loadBrowserProducts(currentId)
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

/**
 * Top Bar chuẩn e-commerce
 */
@Composable
private fun CategoryTopBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    cartBadgeCount: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit
) {
    Surface(
        color = SurfaceCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nút Back
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Ô tìm kiếm bo góc kèm icon kính lúp bên phải
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .background(color = Color(0xFFF0F2F5), shape = RoundedCornerShape(8.dp))
                    .padding(start = 12.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Tìm kiếm trong danh mục...",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 13.sp,
                            color = TextPrimary
                        ),
                        cursorBrush = SolidColor(CoralPrimary),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { onSearchSubmit(searchQuery) })
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Tìm kiếm",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onSearchSubmit(searchQuery) }
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Giỏ hàng bên phải kèm Badge đỏ
            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(
                    onClick = onCartClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Giỏ hàng",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (cartBadgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .offset(x = 2.dp, y = (-2).dp)
                            .size(17.dp)
                            .background(Color(0xFFD32F2F), CircleShape)
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (cartBadgeCount > 99) "99+" else cartBadgeCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 10.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Cột trái: Thanh danh mục nạp từ Database
 */
@Composable
private fun LeftCategorySidebar(
    categories: List<CategoryDto>,
    selectedCategory: CategoryDto?,
    isLoading: Boolean,
    onCategoryClick: (CategoryDto) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFFF5F6F8))
            .border(width = 0.5.dp, color = BorderDivider.copy(alpha = 0.4f))
    ) {
        if (isLoading && categories.isEmpty()) {
            // Skeleton loading bên cột trái
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(7) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E4E8))
                    )
                    Box(
                        modifier = Modifier
                            .width(55.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE2E4E8))
                    )
                }
            }
        } else if (categories.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = onRetry) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Thử lại",
                        tint = CoralPrimary
                    )
                }
                Text(
                    text = "Tải lại",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            val listState = rememberLazyListState()

            LaunchedEffect(selectedCategory?.id) {
                val selectedIndex = categories.indexOfFirst { it.id == selectedCategory?.id }
                if (selectedIndex >= 0) {
                    listState.animateScrollToItem(selectedIndex)
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(categories, key = { it.id }) { cat ->
                    val isSelected = selectedCategory?.id == cat.id
                    SidebarItemRow(
                        category = cat,
                        isSelected = isSelected,
                        onClick = { onCategoryClick(cat) }
                    )
                }
            }
        }
    }
}

/**
 * Mục từng danh mục trên thanh Sidebar trái
 */
@Composable
private fun SidebarItemRow(
    category: CategoryDto,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) SurfaceCard else Color.Transparent
    val textColor = if (isSelected) CoralPrimary else Color(0xFF4A4A4A)
    val icon = resolveCategoryIcon(category)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Vạch đỏ/cam chỉ thị bên mép trái
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(3.5.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp))
                    .background(CoralPrimary)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon trong hình tròn
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        color = if (isSelected) Color(0xFFFEECE8) else Color(0xFFEAEDF1),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = category.name,
                    tint = if (isSelected) CoralPrimary else Color(0xFF555555),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = category.name,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }
    }
}

/**
 * Cột phải: Các sản phẩm thuộc danh mục được chọn
 */
@Composable
private fun RightProductsPanel(
    selectedCategory: CategoryDto?,
    selectedSubCategoryId: Long?,
    products: List<ProductItem>,
    isLoading: Boolean,
    onSubCategorySelect: (Long?) -> Unit,
    onProductClick: (ProductItem) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(SurfaceCard)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        // 1. Header: Tiêu đề danh mục và số lượng sản phẩm
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedCategory?.name ?: "Danh mục sản phẩm",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NavySecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${products.size} sản phẩm",
                fontSize = 11.5.sp,
                color = TextSecondary
            )
        }

        // 2. Nếu danh mục có các nhóm con (Subcategories), hiển thị thanh Chips để lọc
        val subcategories = selectedCategory?.children.orEmpty()
        if (subcategories.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Chip "Tất cả"
                item {
                    val isAllSelected = selectedSubCategoryId == null
                    FilterChip(
                        selected = isAllSelected,
                        onClick = { onSubCategorySelect(null) },
                        label = {
                            Text(
                                text = "Tất cả",
                                fontSize = 11.sp,
                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFEECE8),
                            selectedLabelColor = CoralPrimary,
                            containerColor = Color(0xFFF4F5F7),
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isAllSelected,
                            borderColor = if (isAllSelected) CoralPrimary else Color.Transparent,
                            selectedBorderColor = CoralPrimary,
                            borderWidth = 0.8.dp,
                            selectedBorderWidth = 1.dp
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(28.dp)
                    )
                }

                // Từng danh mục con
                items(subcategories, key = { it.id }) { subCat ->
                    val isSelected = selectedSubCategoryId == subCat.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSubCategorySelect(subCat.id) },
                        label = {
                            Text(
                                text = subCat.name,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFEECE8),
                            selectedLabelColor = CoralPrimary,
                            containerColor = Color(0xFFF4F5F7),
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) CoralPrimary else Color.Transparent,
                            selectedBorderColor = CoralPrimary,
                            borderWidth = 0.8.dp,
                            selectedBorderWidth = 1.dp
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 3. Khối hiển thị sản phẩm
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = CoralPrimary,
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Đang tải sản phẩm từ hệ thống...",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )
                }
            } else if (products.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFFF5F6F8), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingBag,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.7f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Chưa có sản phẩm nào",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Các sản phẩm thuộc danh mục này sẽ sớm được cập nhật.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    IconButton(
                        onClick = onRetry,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Tải lại",
                            tint = CoralPrimary
                        )
                    }
                }
            } else {
                // Lưới 2 cột các sản phẩm thuộc danh mục
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp, top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products, key = { it.id }) { product ->
                        CategoryProductGridCard(
                            product = product,
                            onClick = { onProductClick(product) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Thẻ hiển thị sản phẩm tinh gọn trong lưới 2 cột bên phải
 */
@Composable
private fun CategoryProductGridCard(
    product: ProductItem,
    onClick: () -> Unit
) {
    val discount = product.discountPercent
    val originalPrice = product.formattedOriginalPrice

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceCard)
            .border(0.8.dp, BorderDivider.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        // Ảnh sản phẩm tỷ lệ 1:1
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color(0xFFF7F8FA))
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Badge giảm giá ở góc trên phải nếu có
            if (discount != null && discount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(
                            color = CoralPrimary,
                            shape = RoundedCornerShape(bottomStart = 6.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "-$discount%",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Badge Yêu thích ở góc trên trái
            if (product.rating >= 4.8f) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .background(
                            color = Color(0xFFD32F2F),
                            shape = RoundedCornerShape(3.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "Yêu thích",
                        color = Color.White,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Thông tin tên, giá, đánh giá
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            Text(
                text = product.name,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Giá hiện tại
            Text(
                text = product.formattedPrice,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = CoralPrimary,
                maxLines = 1
            )

            // Giá gốc gạch ngang nếu có
            if (originalPrice != null) {
                Text(
                    text = originalPrice,
                    fontSize = 9.5.sp,
                    color = TextSecondary,
                    textDecoration = TextDecoration.LineThrough,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Rating & Đã bán
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = StarRatingGold,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = String.format(Locale.US, "%.1f", product.rating),
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = "|",
                    fontSize = 8.sp,
                    color = BorderDivider,
                    modifier = Modifier.padding(horizontal = 1.dp)
                )
                Text(
                    text = product.formattedSoldCount,
                    fontSize = 9.5.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Ánh xạ Icon phù hợp với tên/slug của danh mục
 */
private fun resolveCategoryIcon(category: CategoryDto): ImageVector {
    val slug = category.slug.lowercase(Locale.ROOT)
    val name = category.name.lowercase(Locale.ROOT)
    val iconUrl = category.iconUrl?.lowercase(Locale.ROOT) ?: ""

    return when {
        slug.contains("thoi-trang") || name.contains("thời trang") || slug.contains("ao") || slug.contains("quan") || iconUrl.contains("person") ->
            Icons.Filled.Person

        slug.contains("dien-tu") || slug.contains("cong-nghe") || name.contains("điện tử") || iconUrl.contains("laptop") || iconUrl.contains("phone") || iconUrl.contains("headphones") ->
            Icons.Outlined.PhoneAndroid

        slug.contains("gia-dung") || name.contains("gia dụng") || name.contains("nhà cửa") || iconUrl.contains("house") || iconUrl.contains("cup") ->
            Icons.Outlined.Weekend

        slug.contains("lam-dep") || slug.contains("my-pham") || name.contains("làm đẹp") || name.contains("mỹ phẩm") || iconUrl.contains("stars") ->
            Icons.Outlined.Brush

        slug.contains("giay") || name.contains("giày") || iconUrl.contains("bicycle") || iconUrl.contains("run") ->
            Icons.Outlined.DirectionsRun

        slug.contains("dong-ho") || name.contains("đồng hồ") || iconUrl.contains("watch") ->
            Icons.Outlined.Watch

        slug.contains("me-va-be") || name.contains("mẹ") || name.contains("bé") || iconUrl.contains("balloon") || iconUrl.contains("child") ->
            Icons.Outlined.ChildCare

        slug.contains("bach-hoa") || name.contains("bách hóa") || name.contains("đồ ăn") ->
            Icons.Outlined.ShoppingBag

        slug.contains("the-thao") || name.contains("thể thao") ->
            Icons.Outlined.FitnessCenter

        else -> Icons.Outlined.ShoppingBag
    }
}
