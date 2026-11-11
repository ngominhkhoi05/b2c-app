package com.example.b2capp.features.home

import com.example.b2capp.features.home.components.BestSellingSection
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.delay
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
    val soldCount: Int = 0,
    val ratingCount: Int = 0,
    val stock: Int = 50,
    val description: String = "",
    val shopName: String = "Coolmate Official Store",
    val shopLocation: String = "Hà Nội"
) {
    val formattedPrice: String
        get() {
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            return "${formatter.format(price)}₫"
        }
}

/**
 * Dữ liệu sản phẩm bán chạy (Best Seller)
 */
data class HomeBestSellerItem(
    val id: String,
    val name: String,
    val imageUrl: String,
    val price: Long,
    val originalPrice: Long = 0L,
    val discountPercent: Int = 0,
    val rating: Float = 0f,
    val soldCount: Int = 0,
    val ratingCount: Int = 0,
    val shopName: String = "Coolmate Official Store",
    val shopLocation: String = "Hà Nội",
    val description: String = ""
) {
    val formattedPrice: String
        get() {
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            return "₫${formatter.format(price)}"
        }

    val formattedOriginalPrice: String
        get() {
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            return "₫${formatter.format(originalPrice)}"
        }

    val soldCountText: String
        get() = if (soldCount >= 1000) {
            String.format(Locale.US, "Đã bán %.1fk", soldCount / 1000.0)
        } else {
            "Đã bán $soldCount"
        }
}

typealias HomeFlashSaleItem = HomeBestSellerItem

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
    val listState = rememberLazyListState()
    var searchInput by remember { mutableStateOf("") }

    // Nạp dữ liệu Trang chủ từ API khi màn hình xuất hiện
    LaunchedEffect(Unit) {
        if (uiState.homeSuggestedProducts.isEmpty()) {
            viewModel.loadHomeData()
        }
        viewModel.refreshCartBadgeCount()
    }

    // Danh mục nhanh từ ViewModel/API (có fallback mặc định)
    val quickCategories = uiState.homeCategories

    // Dữ liệu Sản phẩm bán chạy từ ViewModel/API (có fallback mặc định)
    val bestSellerProducts = uiState.homeBestSellers

    // Danh sách Banners khuyến mãi từ ViewModel/API
    val banners = uiState.homeBanners

    // Danh sách Gợi ý hôm nay: lấy hoàn toàn từ ViewModel/API (không dùng dữ liệu mock)
    val suggestedProductsList = uiState.homeSuggestedProducts

    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisible >= totalItems - 2
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !uiState.isLoadingMoreSuggested && !uiState.isLastSuggestedPage) {
            viewModel.loadMoreSuggestedProducts()
        }
    }

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
                chatBadgeCount = uiState.shopConversations.sumOf { it.unreadCount },
                onCartClick = onNavigateToCart,
                onChatClick = { viewModel.openShopMessages() }
            )

            // Toàn bộ nội dung trang chủ cuộn dọc
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // 2. Carousel Hero Banner (Mid-Year Mega Sale)
                item {
                    MidYearMegaSaleBanner(
                        banners = banners,
                        onBannerClick = { banner ->
                            banner.code?.let { code ->
                                viewModel.showSuccessToast("Mã voucher: $code đã sẵn sàng áp dụng khi thanh toán!")
                            }
                        }
                    )
                }

                // 3. Lưới 8 Danh mục nhanh
                item {
                    QuickCategoriesGrid(
                        categories = quickCategories,
                        onCategoryClick = { cat ->
                            if (cat.id == "qc_all") {
                                viewModel.selectCategory("Tất cả")
                            } else {
                                viewModel.selectCategory(cat.title)
                            }
                            viewModel.selectTab(1) // Chuyển sang tab Danh mục
                        }
                    )
                }

                // 4. Khối SẢN PHẨM BÁN CHẠY (chỉ hiển thị khi API trả về dữ liệu)
                if (bestSellerProducts.isNotEmpty()) item {
                    BestSellingSection(
                        items = bestSellerProducts,
                        onItemClick = { item ->
                            viewModel.openProductDetailsScreen(
                                ProductItem(
                                    id = item.id,
                                    name = item.name,
                                    imageUrl = item.imageUrl,
                                    price = item.price,
                                    originalPrice = item.originalPrice,
                                    rating = item.rating,
                                    soldCount = item.soldCount,
                                    ratingCount = item.ratingCount,
                                    shopName = item.shopName,
                                    shopLocation = item.shopLocation,
                                    description = item.description
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

                // 6. Lưới 2 cột các sản phẩm gợi ý (Cuộn vô hạn)
                val productRows = suggestedProductsList.chunked(2)
                items(productRows, key = { row -> row.first().id }) { rowItems ->
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
                                    soldCount = product.soldCount,
                                    ratingCount = product.ratingCount,
                                    isFreeShipping = false,
                                    description = product.description,
                                    shopName = product.shopName,
                                    shopLocation = product.shopLocation
                                )
                            }
                            ProductCard(
                                product = productItem,
                                onClick = {
                                    viewModel.openProductDetailsScreen(productItem)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // 7. Loader chỉ thị cuộn vô hạn
                if (!uiState.isLastSuggestedPage) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = CoralPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (uiState.isLoadingMoreSuggested) "Đang tải thêm sản phẩm..." else "Cuộn xuống để xem thêm",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
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

/**
 * Hàm sinh sản phẩm động cho tính năng Cuộn Vô Hạn (Infinite Scrolling) ở Home
 */
private fun generateMoreSuggestedProducts(batchIndex: Int): List<HomeSuggestedProduct> {
    val templates = listOf(
        HomeSuggestedProduct(
            id = "sg_more_1",
            name = "Áo khoác dù bomber 2 lớp cản gió chống nước form rộng Unisex",
            imageUrl = "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=400&auto=format&fit=crop&q=75",
            price = 289000L,
            badgeType = HomeBadgeType.FREESHIP_XTRA,
            rating = 4.9f,
            soldText = "Đã bán 2.1k",
            shopName = "Thời Trang Unisex Studio",
            shopLocation = "Hà Nội",
            description = "Áo bomber 2 lớp dày dặn, lót lụa mềm mịn cản gió và chống nước đi mưa nhỏ."
        ),
        HomeSuggestedProduct(
            id = "sg_more_2",
            name = "Bình giữ nhiệt inox 304 quai xách thể thao dung tích lớn 1000ml",
            imageUrl = "https://images.unsplash.com/photo-1544816155-12df9643f363?w=400&auto=format&fit=crop&q=75",
            price = 199000L,
            badgeType = HomeBadgeType.YEU_THICH,
            rating = 4.8f,
            soldText = "Đã bán 4.5k",
            shopName = "Lock&Lock Flagship",
            shopLocation = "TP. Hồ Chí Minh",
            description = "Inox 304 giữ nhiệt nóng lạnh 24h, kèm lưới lọc trà và quai xách tiện lợi."
        ),
        HomeSuggestedProduct(
            id = "sg_more_3",
            name = "Chuột không dây Bluetooth sạc Type-C nút bấm Silent chống ồn",
            imageUrl = "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=400&auto=format&fit=crop&q=75",
            price = 185000L,
            badgeType = HomeBadgeType.FREESHIP_XTRA,
            rating = 4.9f,
            soldText = "Đã bán 6.8k",
            shopName = "Logitech Flagship Store",
            shopLocation = "Hà Nội",
            description = "Kết nối Bluetooth 5.2 và USB 2.4G, pin sạc dùng 3 tháng, nút bấm êm ái."
        ),
        HomeSuggestedProduct(
            id = "sg_more_4",
            name = "Quạt mini cầm tay tích điện 4000mAh 3 cấp độ gió siêu mát",
            imageUrl = "https://images.unsplash.com/photo-1585338107529-13afc5f02586?w=400&auto=format&fit=crop&q=75",
            price = 129000L,
            badgeType = HomeBadgeType.YEU_THICH,
            rating = 4.7f,
            soldText = "Đã bán 8.2k",
            shopName = "Gia Dụng Thông Minh",
            shopLocation = "TP. Hồ Chí Minh",
            description = "Động cơ không chổi than gió mạnh êm ái, pin dung lượng lớn dùng liên tục 12h."
        ),
        HomeSuggestedProduct(
            id = "sg_more_5",
            name = "Kính râm phân cực chống lóa chống tia UV400 thời trang nam nữ",
            imageUrl = "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=400&auto=format&fit=crop&q=75",
            price = 159000L,
            badgeType = HomeBadgeType.FREESHIP_XTRA,
            rating = 4.8f,
            soldText = "Đã bán 3.9k",
            shopName = "Mắt Kính Sài Gòn",
            shopLocation = "Đà Nẵng",
            description = "Tròng kính phân cực TAC bảo vệ mắt khi đi nắng, gọng hợp kim titan siêu nhẹ."
        ),
        HomeSuggestedProduct(
            id = "sg_more_6",
            name = "Bình xịt bọt vệ sinh giày Sneaker nano tạo bọt làm sạch tức thì",
            imageUrl = "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=400&auto=format&fit=crop&q=75",
            price = 89000L,
            badgeType = HomeBadgeType.FREESHIP_XTRA,
            rating = 4.9f,
            soldText = "Đã bán 15.3k",
            shopName = "Crep Clean Official",
            shopLocation = "Hà Nội",
            description = "Công nghệ tạo bọt nano làm sạch vết bẩn nhanh chóng mà không cần giặt nước."
        ),
        HomeSuggestedProduct(
            id = "sg_more_7",
            name = "Sạc dự phòng mini 10.000mAh vỏ trong suốt hiển thị LED sạc nhanh 22.5W",
            imageUrl = "https://images.unsplash.com/photo-1609592426806-25f385c2c9d1?w=400&auto=format&fit=crop&q=75",
            price = 289000L,
            badgeType = HomeBadgeType.FREESHIP_XTRA,
            rating = 4.9f,
            soldText = "Đã bán 7.1k",
            shopName = "Anker Audio Flagship",
            shopLocation = "Hà Nội",
            description = "Thiết kế cyberpunk trong suốt độc đáo, hỗ trợ sạc nhanh PD 20W và QC 22.5W."
        ),
        HomeSuggestedProduct(
            id = "sg_more_8",
            name = "Giày thể thao nam nữ Sneaker đế cao su non êm ái thoáng khí",
            imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&auto=format&fit=crop&q=75",
            price = 369000L,
            badgeType = HomeBadgeType.YEU_THICH,
            rating = 4.8f,
            soldText = "Đã bán 11.4k",
            shopName = "Biti's Hunter Flagship",
            shopLocation = "TP. Hồ Chí Minh",
            description = "Lót giày than hoạt tính kháng khuẩn, đế đúc chống trơn trượt tối ưu."
        ),
        HomeSuggestedProduct(
            id = "sg_more_9",
            name = "Tai nghe Gaming chụp tai 7.1 có micro khử ồn đèn LED RGB",
            imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&auto=format&fit=crop&q=75",
            price = 459000L,
            badgeType = HomeBadgeType.FREESHIP_XTRA,
            rating = 4.9f,
            soldText = "Đã bán 2.9k",
            shopName = "Sony Official Store",
            shopLocation = "Hà Nội",
            description = "Âm thanh vòm giả lập 7.1 định vị tiếng chân chính xác, đệm tai mút hoạt tính êm ái."
        ),
        HomeSuggestedProduct(
            id = "sg_more_10",
            name = "Bàn chải điện sóng âm thông minh chống nước IPX7 5 chế độ rung",
            imageUrl = "https://images.unsplash.com/photo-1559591937-e1032397e559?w=400&auto=format&fit=crop&q=75",
            price = 249000L,
            badgeType = HomeBadgeType.YEU_THICH,
            rating = 4.9f,
            soldText = "Đã bán 8.7k",
            shopName = "Xiaomi Official Store",
            shopLocation = "TP. Hồ Chí Minh",
            description = "Tần số rung 40.000 lần/phút làm sạch mảng bám hiệu quả, pin sạc dùng 60 ngày."
        ),
        HomeSuggestedProduct(
            id = "sg_more_11",
            name = "Áo sơ mi nam Oxford form Regular tay dài vải sợi tre chống nhăn",
            imageUrl = "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=400&auto=format&fit=crop&q=75",
            price = 279000L,
            badgeType = HomeBadgeType.FREESHIP_XTRA,
            rating = 4.8f,
            soldText = "Đã bán 5.4k",
            shopName = "Coolmate Official Store",
            shopLocation = "Hà Nội",
            description = "Chất vải bamboo tự nhiên thoáng mát, chống nhăn xù tối ưu, tôn dáng thanh lịch."
        ),
        HomeSuggestedProduct(
            id = "sg_more_12",
            name = "Đèn LED để bàn thông minh chống cận thị tích hợp sạc không dây",
            imageUrl = "https://images.unsplash.com/photo-1534349762230-e0cadf78f5da?w=400&auto=format&fit=crop&q=75",
            price = 329000L,
            badgeType = HomeBadgeType.FREESHIP_XTRA,
            rating = 4.8f,
            soldText = "Đã bán 3.6k",
            shopName = "Nội Thất Minimalist",
            shopLocation = "Đà Nẵng",
            description = "Ánh sáng tiêu chuẩn bảo vệ mắt, 3 dải màu điều chỉnh cảm ứng, tích hợp sạc nhanh."
        )
    )

    val startIndex = ((batchIndex - 1) * 6) % templates.size
    return (0 until 6).map { i ->
        val tmpl = templates[(startIndex + i) % templates.size]
        val uniqueId = "${tmpl.id}_b${batchIndex}_$i"
        tmpl.copy(
            id = uniqueId,
            name = if (batchIndex > 2) "${tmpl.name} (Gợi ý #$batchIndex)" else tmpl.name
        )
    }
}
