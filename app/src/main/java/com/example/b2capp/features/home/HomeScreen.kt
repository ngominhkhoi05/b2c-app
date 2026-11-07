package com.example.b2capp.features.home

import com.example.b2capp.features.home.components.FlashSaleSection
import com.example.b2capp.features.home.components.HomeTopBar
import com.example.b2capp.features.home.components.MidYearMegaSaleBanner
import com.example.b2capp.features.home.components.QuickCategoriesGrid

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material.icons.outlined.Weekend
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.b2capp.core.ui.components.AppButton
import com.example.b2capp.core.ui.components.ButtonVariant
import com.example.b2capp.core.ui.components.ProductCard
import com.example.b2capp.core.ui.components.QuantityPicker
import com.example.b2capp.core.ui.components.StarRatingBar
import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.designsystem.theme.BackgroundApp
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.FreeShippingAccent
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.StarRatingGold
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/**
 * Loại huy hiệu sản phẩm: Freeship Xtra hoặc Yêu thích
 */
enum class HomeBadgeType {
    FREESHIP_XTRA,
    YEU_THICH
}

/**
 * Dữ liệu sản phẩm đề xuất (Gợi ý hôm nay)
 */
data class HomeSuggestedProduct(
    val id: String,
    val name: String,
    val imageUrl: String,
    val price: Long,
    val badgeType: HomeBadgeType,
    val rating: Float,
    val soldText: String,
    val stock: Int = 50,
    val description: String = ""
) {
    val formattedPrice: String
        get() {
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            return "${formatter.format(price)}₫"
        }
}

/**
 * Dữ liệu sản phẩm Flash Sale
 */
data class HomeFlashSaleItem(
    val id: String,
    val name: String,
    val imageUrl: String,
    val price: Long,
    val originalPrice: Long,
    val discountPercent: Int,
    val progressText: String,
    val progressRatio: Float // 0.0f đến 1.0f
) {
    val formattedPrice: String
        get() {
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            return "${formatter.format(price)}₫"
        }

    val formattedOriginalPrice: String
        get() {
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            return "${formatter.format(originalPrice)}₫"
        }
}

/**
 * Dữ liệu biểu tượng danh mục nhanh
 */
data class QuickCategoryItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val containerBg: Color,
    val iconTint: Color
)

/**
 * Màn hình Trang Chủ chuẩn 100% theo HomeScreen_60160265.png:
 * 1. Top Bar: Thanh tìm kiếm "Tìm sản phẩm, thương hiệu..." bo góc 8.dp kèm icon camera, icon chat và giỏ hàng có badge đỏ số 3.
 * 2. Hero Carousel Banner: "MID-YEAR MEGA SALE 50% OFF" kèm tag FREESHIP TOÀN QUỐC và 3 dấu chấm chỉ thị trang.
 * 3. Lưới 8 Danh mục nhanh (Thời trang, Điện tử, Gia dụng, Làm đẹp, Giày dép, Đồng hồ, Mẹ & Bé, Xem tất cả).
 * 4. FLASH SALE: Lửa đỏ, Đồng hồ đếm ngược 02:45:10, danh sách thẻ cuộn ngang với thanh tiến độ ĐÃ BÁN / ĐANG BÁN CHẠY.
 * 5. GỢI Ý HÔM NAY: Vạch đỏ đậm chỉ thị, Lưới 2 cột các sản phẩm có tag Freeship Xtra & Yêu thích.
 * 6. Nút "Xem thêm sản phẩm gợi ý ˇ" ở cuối trang.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var searchInput by remember { mutableStateOf("") }

    // 8 Danh mục nhanh
    val quickCategories = listOf(
        QuickCategoryItem("qc_fashion", "Thời trang", Icons.Outlined.Checkroom, Color(0xFFFEECE8), Color(0xFFEE4D2D)),
        QuickCategoryItem("qc_tech", "Điện tử", Icons.Outlined.Devices, Color(0xFFEDF4FB), Color(0xFF1E75C4)),
        QuickCategoryItem("qc_home", "Gia dụng", Icons.Outlined.Weekend, Color(0xFFF3EFF8), Color(0xFF555555)),
        QuickCategoryItem("qc_beauty", "Làm đẹp", Icons.Outlined.Spa, Color(0xFFFDECEE), Color(0xFFD32F2F)),
        QuickCategoryItem("qc_shoes", "Giày dép", Icons.Outlined.DirectionsRun, Color(0xFFEDF2FC), Color(0xFF3B82F6)),
        QuickCategoryItem("qc_watches", "Đồng hồ", Icons.Outlined.Watch, Color(0xFFE0F7F3), Color(0xFF14B8A6)),
        QuickCategoryItem("qc_baby", "Mẹ & Bé", Icons.Outlined.ChildCare, Color(0xFFFEEFE8), Color(0xFFF97316)),
        QuickCategoryItem("qc_all", "Xem tất cả", Icons.Outlined.Apps, Color(0xFFF0F1F3), Color(0xFF666666))
    )

    // Dữ liệu Flash Sale
    val flashSaleProducts = listOf(
        HomeFlashSaleItem(
            id = "fs_1",
            name = "Tai nghe không dây chống ồn cao cấp",
            imageUrl = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=400&auto=format&fit=crop&q=75",
            price = 299000L,
            originalPrice = 460000L,
            discountPercent = 35,
            progressText = "ĐÃ BÁN 80%",
            progressRatio = 0.80f
        ),
        HomeFlashSaleItem(
            id = "fs_2",
            name = "Bình giữ nhiệt thông minh hiển thị nhiệt độ",
            imageUrl = "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400&auto=format&fit=crop&q=75",
            price = 149000L,
            originalPrice = 298000L,
            discountPercent = 50,
            progressText = "ĐANG BÁN CHẠY",
            progressRatio = 0.95f
        ),
        HomeFlashSaleItem(
            id = "fs_3",
            name = "Bàn phím cơ Bluetooth kèm kê tay gỗ",
            imageUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400&auto=format&fit=crop&q=75",
            price = 590000L,
            originalPrice = 739000L,
            discountPercent = 20,
            progressText = "ĐÃ BÁN 65%",
            progressRatio = 0.65f
        )
    )

    // Dữ liệu Gợi ý hôm nay (6 sản phẩm chuẩn theo ảnh mẫu)
    val suggestedProducts = listOf(
        HomeSuggestedProduct(
            id = "sg_1",
            name = "Balo canvas unisex thời trang cao cấp phong cách Hàn Quốc",
            imageUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&auto=format&fit=crop&q=75",
            price = 245000L,
            badgeType = HomeBadgeType.FREESHIP_XTRA,
            rating = 4.9f,
            soldText = "Đã bán 1.8k",
            description = "Balo unisex chất vải canvas cao cấp, chống trầy xước, ngăn chứa laptop tiện lợi cho sinh viên và dân văn phòng."
        ),
        HomeSuggestedProduct(
            id = "sg_2",
            name = "Bộ phễu pha cà phê gốm sứ thủ công kèm bình thủy tinh chịu nhiệt",
            imageUrl = "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=400&auto=format&fit=crop&q=75",
            price = 320000L,
            badgeType = HomeBadgeType.YEU_THICH,
            rating = 4.8f,
            soldText = "Đã bán 950",
            description = "Thiết kế gốm sứ tráng men mộc mạc phong cách vintage, tặng kèm giấy lọc cà phê chuyên dụng."
        ),
        HomeSuggestedProduct(
            id = "sg_3",
            name = "Đế sạc nhanh không dây 3 trong 1 chuẩn Qi đa năng cho điện thoại, đồng hồ",
            imageUrl = "https://images.unsplash.com/photo-1586953208448-b95a79798f07?w=400&auto=format&fit=crop&q=75",
            price = 489000L,
            badgeType = HomeBadgeType.FREESHIP_XTRA,
            rating = 4.7f,
            soldText = "Đã bán 3.4k",
            description = "Hỗ trợ sạc không dây đồng thời iPhone, Apple Watch và AirPods với công suất lên đến 15W tản nhiệt an toàn."
        ),
        HomeSuggestedProduct(
            id = "sg_4",
            name = "Tinh chất dưỡng ẩm sáng da Serum Hyaluronic Acid chiết xuất nha đam tự nhiên",
            imageUrl = "https://images.unsplash.com/photo-1608248597359-242e88a0e9c9?w=400&auto=format&fit=crop&q=75",
            price = 185000L,
            badgeType = HomeBadgeType.YEU_THICH,
            rating = 5.0f,
            soldText = "Đã bán 5.1k",
            description = "Cấp ẩm sâu đa tầng, giảm nếp nhăn và phục hồi hàng rào bảo vệ da căng bóng rạng ngời."
        ),
        HomeSuggestedProduct(
            id = "sg_5",
            name = "Đệm ngồi công thái học cao su non giảm áp lực cột sống thoáng khí",
            imageUrl = "https://images.unsplash.com/photo-1580481077111-2b0b144fa54a?w=400&auto=format&fit=crop&q=75",
            price = 210000L,
            badgeType = HomeBadgeType.FREESHIP_XTRA,
            rating = 4.8f,
            soldText = "Đã bán 720",
            description = "Chất liệu cao su non nguyên khối đàn hồi cao, thiết kế rãnh chữ U giảm đau xương cụt khi ngồi làm việc lâu."
        ),
        HomeSuggestedProduct(
            id = "sg_6",
            name = "Loa bluetooth không dây phong cách Vintage bọc da quai xách cổ điển",
            imageUrl = "https://images.unsplash.com/photo-1545454675-3531b543be5d?w=400&auto=format&fit=crop&q=75",
            price = 429000L,
            badgeType = HomeBadgeType.FREESHIP_XTRA,
            rating = 4.9f,
            soldText = "Đã bán 1.2k",
            description = "Âm bass trầm ấm đầy nội lực, pin nghe nhạc liên tục 12 tiếng, thiết kế hoài cổ sang trọng trang trí phòng khách."
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .testTag("home_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Bar
            HomeTopBar(
                searchQuery = searchInput,
                onSearchChange = { searchInput = it },
                onSearchSubmit = { query ->
                    viewModel.openSearchResults(query.ifBlank { "áo thun nam" })
                },
                cartBadgeCount = uiState.totalCartCount,
                onCartClick = onNavigateToCart,
                onChatClick = { viewModel.openShopMessages() }
            )

            // Toàn bộ nội dung trang chủ cuộn dọc
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // 2. Carousel Hero Banner (Mid-Year Mega Sale)
                item {
                    MidYearMegaSaleBanner()
                }

                // 3. Lưới 8 Danh mục nhanh
                item {
                    QuickCategoriesGrid(
                        categories = quickCategories,
                        onCategoryClick = { cat ->
                            viewModel.selectCategory(cat.title)
                            viewModel.selectTab(1) // Chuyển sang tab Danh mục
                        }
                    )
                }

                // 4. Khối FLASH SALE với đồng hồ đếm ngược
                item {
                    FlashSaleSection(
                        items = flashSaleProducts,
                        onItemClick = { item ->
                            viewModel.openProductDetailsScreen(
                                ProductItem(
                                    id = item.id,
                                    name = item.name,
                                    imageUrl = item.imageUrl,
                                    price = item.price,
                                    originalPrice = item.originalPrice,
                                    rating = 4.9f,
                                    soldCount = 1200
                                )
                            )
                        },
                        onViewAllClick = {
                            viewModel.selectCategory("Tất cả")
                            viewModel.selectTab(1)
                        }
                    )
                }

                // 5. Tiêu đề "GỢI Ý HÔM NAY" có vạch đỏ đậm
                item {
                    GoiYHomNayHeader()
                }

                // 6. Lưới 2 cột các sản phẩm gợi ý
                val productRows = suggestedProducts.chunked(2)
                items(productRows) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { product ->
                            val productItem = remember(product.id) {
                                ProductItem(
                                    id = product.id,
                                    name = product.name,
                                    imageUrl = product.imageUrl,
                                    price = product.price,
                                    rating = product.rating,
                                    soldCount = 1800,
                                    isFreeShipping = product.badgeType == HomeBadgeType.FREESHIP_XTRA,
                                    description = product.description
                                )
                            }
                            val (bColor, bText) = when (product.badgeType) {
                                HomeBadgeType.FREESHIP_XTRA -> Pair(FreeShippingAccent, "Freeship Xtra")
                                HomeBadgeType.YEU_THICH -> Pair(CoralPrimary, "Yêu thích")
                            }
                            ProductCard(
                                product = productItem,
                                onClick = {
                                    viewModel.openProductDetailsScreen(productItem)
                                },
                                badgeText = bText,
                                badgeColor = bColor,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // 7. Nút "Xem thêm sản phẩm gợi ý ˇ"
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, BorderDivider, RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.selectCategory("Tất cả")
                                    viewModel.selectTab(1)
                                },
                            color = SurfaceCard
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Xem thêm sản phẩm gợi ý",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NavySecondary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Outlined.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = NavySecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // BottomSheet chi tiết sản phẩm khi chạm vào bất kỳ thẻ nào
        uiState.selectedProductForSheet?.let { product ->
            ModalBottomSheet(
                onDismissRequest = { viewModel.closeProductDetail() },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                containerColor = SurfaceCard,
                modifier = Modifier.testTag("home_product_detail_sheet")
            ) {
                ProductDetailModalContent(
                    product = product,
                    selectedQuantity = uiState.detailSelectedQuantity,
                    onQuantityChange = { viewModel.setDetailQuantity(it) },
                    onAddToCart = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            viewModel.addToCartFromDetail()
                        }
                    },
                    onClose = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            viewModel.closeProductDetail()
                        }
                    }
                )
            }
        }
    }
}



/**
 * Tiêu đề GỢI Ý HÔM NAY có vạch đỏ đậm
 */
@Composable
private fun GoiYHomNayHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, top = 20.dp, bottom = 8.dp)
    ) {
        Text(
            text = "GỢI Ý HÔM NAY",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD32F2F)
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Vạch đỏ chỉ thị phía dưới
        Box(
            modifier = Modifier
                .width(68.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(1.5.dp))
                .background(Color(0xFFD32F2F))
        )
    }
}


/**
 * Nội dung chi tiết Bottom Sheet khi nhấn vào bất kỳ sản phẩm nào
 */
@Composable
private fun ProductDetailModalContent(
    product: ProductItem,
    selectedQuantity: Int,
    onQuantityChange: (Int) -> Unit,
    onAddToCart: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF7F8FA))
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.formattedPrice,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F)
                )
                Text(
                    text = "Kho: ${product.stock} sản phẩm có sẵn",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                StarRatingBar(
                    rating = product.rating,
                    starSize = 14.dp,
                    showRatingText = true
                )
            }

            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Đóng",
                    tint = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = product.name,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (product.description.isNotBlank()) product.description else "Sản phẩm chính hãng chất lượng cao, bảo hành 12 tháng, đổi trả miễn phí trong vòng 7 ngày.",
            fontSize = 13.sp,
            color = TextSecondary,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Số lượng mua",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )

            QuantityPicker(
                quantity = selectedQuantity,
                onQuantityChange = onQuantityChange,
                minQuantity = 1,
                maxStock = product.stock
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppButton(
                text = "Thêm vào giỏ",
                onClick = onAddToCart,
                variant = ButtonVariant.OUTLINED,
                modifier = Modifier.weight(1f)
            )

            AppButton(
                text = "Mua ngay",
                onClick = onAddToCart,
                variant = ButtonVariant.PRIMARY,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
