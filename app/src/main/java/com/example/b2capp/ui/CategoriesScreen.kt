package com.example.b2capp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material.icons.outlined.Weekend
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.ui.theme.BackgroundApp
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Dữ liệu danh mục chính bên thanh Sidebar trái
 */
data class MainCategoryItem(
    val id: String,
    val name: String,
    val icon: ImageVector
)

/**
 * Dữ liệu nhóm danh mục con (Áo nam, Quần nam, Phụ kiện nam...)
 */
data class SubCategoryItem(
    val name: String,
    val imageUrl: String
)

data class SubCategorySection(
    val title: String,
    val accentColor: Color,
    val items: List<SubCategoryItem>
)

/**
 * Màn hình Duyệt danh mục chuẩn xác theo CategoryBrowserScreen_9bda734a.png:
 * 1. Top bar: Nút quay lại, thanh tìm kiếm bo góc kèm icon camera, giỏ hàng kèm badge đỏ số 3.
 * 2. Cột trái: Danh mục chính dạng dọc (Thời trang nam đang chọn có vạch đỏ và icon nổi bật).
 * 3. Cột phải:
 *    - Banner HOT DEAL màu hồng đào: "Ưu đãi thời trang nam - Giảm đến 40% hôm nay" kèm ảnh người mẫu.
 *    - Nhóm 1: "Áo nam" (Áo thun, Áo sơ mi, Áo polo, Áo khoác, Áo hoodie, Áo len).
 *    - Nhóm 2: "Quần nam" (Quần jeans, Quần tây, Quần short, Quần jogger, Quần kaki, Quần lót nam).
 *    - Nhóm 3: "Phụ kiện nam" (Thắt lưng, Ví da, Cà vạt & Nơ).
 */
@Composable
fun CategoriesScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onNavigateToCart: () -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategoryId by remember { mutableStateOf("cat_men_fashion") }
    var searchInput by remember { mutableStateOf("") }

    // Danh sách danh mục chính bên cột trái
    val mainCategories = listOf(
        MainCategoryItem("cat_men_fashion", "Thời trang nam", Icons.Filled.Person),
        MainCategoryItem("cat_tech", "Điện thoại &\nTablet", Icons.Outlined.PhoneAndroid),
        MainCategoryItem("cat_beauty", "Mỹ phẩm &\nLàm đẹp", Icons.Outlined.Brush),
        MainCategoryItem("cat_home", "Nhà cửa &\nĐời sống", Icons.Outlined.Weekend),
        MainCategoryItem("cat_sport", "Thể thao &\nDã ngoại", Icons.Outlined.FitnessCenter),
        MainCategoryItem("cat_baby", "Mẹ & Bé", Icons.Outlined.ChildCare),
        MainCategoryItem("cat_shoes", "Giày dép nam", Icons.Outlined.DirectionsRun),
        MainCategoryItem("cat_watches", "Đồng hồ &...", Icons.Outlined.Watch)
    )

    // Dữ liệu các nhóm danh mục con cho Thời trang nam (như trong ảnh mẫu)
    val subSections = listOf(
        SubCategorySection(
            title = "Áo nam",
            accentColor = Color(0xFFD32F2F),
            items = listOf(
                SubCategoryItem("Áo thun", "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=250&auto=format&fit=crop&q=70"),
                SubCategoryItem("Áo sơ mi", "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=250&auto=format&fit=crop&q=70"),
                SubCategoryItem("Áo polo", "https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=250&auto=format&fit=crop&q=70"),
                SubCategoryItem("Áo khoác", "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=250&auto=format&fit=crop&q=70"),
                SubCategoryItem("Áo hoodie", "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=250&auto=format&fit=crop&q=70"),
                SubCategoryItem("Áo len", "https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=250&auto=format&fit=crop&q=70")
            )
        ),
        SubCategorySection(
            title = "Quần nam",
            accentColor = Color(0xFF1E5B94),
            items = listOf(
                SubCategoryItem("Quần jeans", "https://images.unsplash.com/photo-1542272604-780c96856592?w=250&auto=format&fit=crop&q=70"),
                SubCategoryItem("Quần tây", "https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=250&auto=format&fit=crop&q=70"),
                SubCategoryItem("Quần short", "https://images.unsplash.com/photo-1591195853828-11db59a44f6b?w=250&auto=format&fit=crop&q=70"),
                SubCategoryItem("Quần jogger", "https://images.unsplash.com/photo-1552902865-b72c031ac5ea?w=250&auto=format&fit=crop&q=70"),
                SubCategoryItem("Quần kaki", "https://images.unsplash.com/photo-1473966968600-fa801b869a1a?w=250&auto=format&fit=crop&q=70"),
                SubCategoryItem("Quần lót nam", "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=250&auto=format&fit=crop&q=70")
            )
        ),
        SubCategorySection(
            title = "Phụ kiện nam",
            accentColor = Color(0xFF26AA99),
            items = listOf(
                SubCategoryItem("Thắt lưng", "https://images.unsplash.com/photo-1624222247344-550fb60583dc?w=250&auto=format&fit=crop&q=70"),
                SubCategoryItem("Ví da", "https://images.unsplash.com/photo-1627123424574-724758594e93?w=250&auto=format&fit=crop&q=70"),
                SubCategoryItem("Cà vạt & Nơ", "https://images.unsplash.com/photo-1589756033938-7fed161c9e89?w=250&auto=format&fit=crop&q=70")
            )
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCard)
            .testTag("category_browser_screen")
    ) {
        // 1. Top Bar: Nút Back, Ô tìm kiếm với icon camera, và Giỏ hàng có badge
        CategoryTopBar(
            searchQuery = searchInput,
            onSearchChange = { searchInput = it },
            onSearchSubmit = { query ->
                viewModel.openSearchResults(query.ifBlank { "áo thun nam" })
            },
            cartBadgeCount = uiState.totalCartCount,
            onBackClick = onBackClick,
            onCartClick = onNavigateToCart
        )

        HorizontalDivider(color = BorderDivider.copy(alpha = 0.6f), thickness = 0.8.dp)

        // 2. Nội dung dạng Split View 2 cột: Cột trái (Sidebar) & Cột phải (Sub-categories)
        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar danh mục bên trái (~92.dp)
            LazyColumn(
                modifier = Modifier
                    .width(92.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFF5F6F8))
            ) {
                items(mainCategories, key = { it.id }) { cat ->
                    val isSelected = cat.id == selectedCategoryId

                    SidebarCategoryItem(
                        item = cat,
                        isSelected = isSelected,
                        onClick = { selectedCategoryId = cat.id }
                    )
                }
            }

            // Nội dung bên phải cuộn dọc (Banner + Các nhóm Áo nam, Quần nam, Phụ kiện)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(SurfaceCard),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Thẻ Banner HOT DEAL
                item {
                    HotDealBanner(
                        title = "Ưu đãi thời trang nam",
                        subtitle = "Giảm đến 40% hôm nay",
                        onClick = {
                            val sample = uiState.products.firstOrNull()
                            if (sample != null) {
                                viewModel.openProductDetailsScreen(sample)
                            }
                        }
                    )
                }

                // Các nhóm danh mục con (Áo nam, Quần nam, Phụ kiện nam)
                items(subSections) { section ->
                    SubCategorySectionBlock(
                        section = section,
                        onItemClick = { item ->
                            val sample = uiState.products.firstOrNull()
                            if (sample != null) {
                                viewModel.openProductDetailsScreen(sample.copy(name = "${item.name} cao cấp phong cách Hàn Quốc"))
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

/**
 * Top Bar chuẩn theo ảnh CategoryBrowserScreen_9bda734a.png
 */
@Composable
private fun CategoryTopBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit = {},
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
            // Nút Back mũi tên trái
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

            // Ô tìm kiếm bo tròn nhẹ kèm icon kính lúp và máy ảnh
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .background(color = Color(0xFFF0F2F5), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Tìm kiếm",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(19.dp)
                        .clickable { onSearchSubmit(searchQuery) }
                )

                Spacer(modifier = Modifier.width(8.dp))

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
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Search
                        ),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                            onSearch = { onSearchSubmit(searchQuery) }
                        )
                    )
                }

                // Icon Camera chụp ảnh tìm kiếm
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = "Tìm kiếm bằng hình ảnh",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { /* Mở camera */ }
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Giỏ hàng bên phải kèm Badge đỏ "3"
            IconButton(
                onClick = onCartClick,
                modifier = Modifier.size(40.dp)
            ) {
                BadgedBox(
                    badge = {
                        if (cartBadgeCount > 0) {
                            Box(
                                modifier = Modifier
                                    .offset(x = 6.dp, y = (-4).dp)
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
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Giỏ hàng",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * Mục danh mục chính ở thanh Sidebar bên trái
 */
@Composable
private fun SidebarCategoryItem(
    item: MainCategoryItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) SurfaceCard else Color.Transparent
    val textColor = if (isSelected) Color(0xFFD32F2F) else Color(0xFF555555)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Vạch đỏ chỉ thị bên mép trái
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(3.5.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFD32F2F))
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
                    imageVector = item.icon,
                    contentDescription = item.name,
                    tint = if (isSelected) Color(0xFFD32F2F) else Color(0xFF555555),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = item.name,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                maxLines = 2
            )
        }
    }
}

/**
 * Banner HOT DEAL màu hồng đào phía trên cột phải
 */
@Composable
private fun HotDealBanner(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFFECE7),
                        Color(0xFFFFDDD6)
                    )
                )
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Tag HOT DEAL đỏ
                Box(
                    modifier = Modifier
                        .background(Color(0xFFB82414), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "HOT DEAL",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFB82414)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Ảnh người mẫu thời trang nam
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fashion_hot_deal_model_1793954934603),
                    contentDescription = "Hot Deal Model",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Khối nhóm danh mục con (Áo nam, Quần nam, Phụ kiện nam...)
 */
@Composable
private fun SubCategorySectionBlock(
    section: SubCategorySection,
    onItemClick: (SubCategoryItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Tiêu đề nhóm kèm vạch màu chỉ thị và "Xem tất cả >"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Vạch màu chỉ thị dọc
                Box(
                    modifier = Modifier
                        .width(3.5.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(section.accentColor)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = section.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavySecondary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { /* Xem tất cả */ }
            ) {
                Text(
                    text = "Xem tất cả",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Lưới 3 cột các danh mục con
        val rows = section.items.chunked(3)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { item ->
                        SubCategoryGridCell(
                            item = item,
                            onClick = { onItemClick(item) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Điền khoảng trống nếu hàng không đủ 3 mục
                    val emptySlots = 3 - rowItems.size
                    repeat(emptySlots) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * Ô danh mục con trong lưới (ảnh tỷ lệ vuông bo tròn góc + tên danh mục bên dưới)
 */
@Composable
private fun SubCategoryGridCell(
    item: SubCategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Khung ảnh vuông bo góc 12.dp
        Box(
            modifier = Modifier
                .size(66.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF7F8FA))
                .border(0.8.dp, Color(0xFFEAEAEA), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Tên danh mục con
        Text(
            text = item.name,
            fontSize = 12.sp,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
