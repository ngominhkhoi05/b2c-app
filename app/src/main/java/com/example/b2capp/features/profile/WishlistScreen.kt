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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.b2capp.core.ui.components.ProductCard
import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.designsystem.theme.StarRatingGold
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary

/**
 * Màn hình Sản Phẩm Yêu Thích (WishlistScreen)
 * Thiết kế chính xác 100% theo WishlistScreen_1ebf41a0.png:
 * 1. Top Bar: Back icon, Tiêu đề "Wishlist", Share icon, Avatar tài khoản đỏ.
 * 2. Thẻ Cập Nhật Giá Mới (Price Drop Banner):
 *    - Icon tia sét đỏ, "CẬP NHẬT GIÁ MỚI. 3 sản phẩm của bạn vừa giảm giá sâu hôm nay!", link "Xem ngay".
 * 3. Thanh Bộ lọc Yêu thích (cuộn ngang):
 *    - "Tất cả (8)" (Nền đỏ chữ trắng khi chọn)
 *    - "Đang giảm giá (5)"
 *    - "Còn hàng (7)"
 *    - "Sắp hết hàng"
 * 4. Thanh Điều khiển Sắp xếp & Toggle Demo Trống:
 *    - Dropdown "Mới lưu gần đây ▼".
 *    - Link "👁 Xem demo trống" (Bấm vào để chuyển đổi giữa Danh sách sản phẩm & Giao diện trống!).
 *    - Link "Chọn (Sửa)" (Cho phép chọn nhiều để xóa hoặc thêm vào giỏ).
 * 5. Lưới 2 cột các Sản Phẩm Yêu Thích:
 *    - Bàn phím cơ không dây Custom (-35%, Freeship Xtra, 450.000đ, 4.9★, Nút Thêm vào giỏ).
 *    - Ly giữ nhiệt Inox 316 cao cấp (-25%, Chỉ còn 2 chiếc, 289.000đ, 4.8★, Nút Thêm vào giỏ).
 *    - Tai nghe Bluetooth 5.3 ANC (Flash Sale, Freeship Xtra, 590.000đ, 5.0★, Nút Thêm vào giỏ).
 *    - Túi Tote Canvas Unisex (-40%, 125.000đ, 4.7★, Nút Thêm vào giỏ).
 * 6. Mục "🔥 Gợi ý dành riêng cho bạn":
 *    - Hàng cuộn ngang các sản phẩm gợi ý kèm nút "♡ Lưu lại".
 */

data class WishlistItemData(
    val id: String,
    val name: String,
    val price: Long,
    val originalPrice: Long,
    val discountPercent: String,
    val badgeText: String?,
    val isRedBadge: Boolean = false,
    val rating: Float,
    val soldCount: String,
    val imageRes: Int
)

@Composable
fun WishlistScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    var isDemoEmpty by remember { mutableStateOf(false) }
    var isSelectionMode by remember { mutableStateOf(false) }

    val wishlistItems = remember {
        mutableStateListOf(
            WishlistItemData(
                id = "wish_1",
                name = "Bàn phím cơ không dây Custom Hot–swap Led...",
                price = 450000L,
                originalPrice = 690000L,
                discountPercent = "-35%",
                badgeText = "Freeship Xtra",
                rating = 4.9f,
                soldCount = "2.1k",
                imageRes = R.drawable.product_white_tshirt_1793955938236
            ),
            WishlistItemData(
                id = "wish_2",
                name = "Ly giữ nhiệt Inox 316 cao cấp 500ml nắp chống...",
                price = 289000L,
                originalPrice = 385000L,
                discountPercent = "-25%",
                badgeText = "Chỉ còn 2 chiếc",
                isRedBadge = true,
                rating = 4.8f,
                soldCount = "890",
                imageRes = R.drawable.headphones_anker_space_one_1793957334650
            ),
            WishlistItemData(
                id = "wish_3",
                name = "Tai nghe Bluetooth 5.3 chống ồn chủ động ANC",
                price = 590000L,
                originalPrice = 890000L,
                discountPercent = "Flash Sale",
                badgeText = "Freeship Xtra",
                rating = 5.0f,
                soldCount = "3.4k",
                imageRes = R.drawable.anker_gan_charger_1793957714558
            ),
            WishlistItemData(
                id = "wish_4",
                name = "Túi Tote Canvas Unisex phong cách Hàn Quốc...",
                price = 125000L,
                originalPrice = 210000L,
                discountPercent = "-40%",
                badgeText = null,
                rating = 4.7f,
                soldCount = "5.6k",
                imageRes = R.drawable.polo_navy_folded_1793957349647
            )
        )
    }

    val filterOptions = listOf(
        "Tất cả (8)",
        "Đang giảm giá (5)",
        "Còn hàng (7)",
        "Sắp hết hàng"
    )

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

                        Text(
                            text = "Wishlist",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.showToast("Chia sẻ danh sách yêu thích!") }) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = "Chia sẻ",
                                tint = TextPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

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
            // 2. Thẻ Cập Nhật Giá Mới (Price Drop Notification Banner)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFF3F0))
                        .border(0.8.dp, Color(0xFFFDE8E4), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFBA2211), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FlashOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CẬP NHẬT GIÁ MỚI",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBA2211)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "3 sản phẩm của bạn vừa giảm giá sâu hôm nay!",
                                fontSize = 12.sp,
                                color = Color(0xFF374151),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Xem ngay",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBA2211),
                            modifier = Modifier.clickable {
                                selectedFilter = "Đang giảm giá"
                            }
                        )
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.showToast("Sắp xếp theo mới lưu gần đây") }
                    ) {
                        Text(
                            text = "Mới lưu gần đây",
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
                            modifier = Modifier.clickable { isSelectionMode = !isSelectionMode }
                        )
                    }
                }
            }

            // 5. NỘI DUNG DANH SÁCH SẢN PHẨM HOẶC GIAO DIỆN TRỐNG DEMO
            if (isDemoEmpty || wishlistItems.isEmpty()) {
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
                            text = "Danh sách yêu thích đang trống",
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Hãy thả tim các sản phẩm bạn yêu thích để dễ dàng theo dõi biến động giá và mua sau nhé!",
                            fontSize = 12.5.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { isDemoEmpty = false },
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
                        for (i in wishlistItems.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                WishlistProductCard(
                                    item = wishlistItems[i],
                                    onRemove = {
                                        wishlistItems.removeAt(i)
                                        viewModel.showToast("Đã xóa khỏi danh sách yêu thích")
                                    },
                                    onAddToCart = {
                                        viewModel.addProductToCart(
                                            ProductItem(
                                                id = wishlistItems[i].id,
                                                name = wishlistItems[i].name,
                                                imageUrl = "",
                                                price = wishlistItems[i].price,
                                                rating = wishlistItems[i].rating,
                                                soldCount = 1000
                                            )
                                        )
                                    },
                                    onCardClick = {
                                        viewModel.openProductDetailsScreen(
                                            ProductItem(
                                                id = wishlistItems[i].id,
                                                name = wishlistItems[i].name,
                                                imageUrl = "",
                                                price = wishlistItems[i].price,
                                                rating = wishlistItems[i].rating,
                                                soldCount = 1000
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )

                                if (i + 1 < wishlistItems.size) {
                                    WishlistProductCard(
                                        item = wishlistItems[i + 1],
                                        onRemove = {
                                            wishlistItems.removeAt(i + 1)
                                            viewModel.showToast("Đã xóa khỏi danh sách yêu thích")
                                        },
                                        onAddToCart = {
                                            viewModel.addProductToCart(
                                                ProductItem(
                                                    id = wishlistItems[i + 1].id,
                                                    name = wishlistItems[i + 1].name,
                                                    imageUrl = "",
                                                    price = wishlistItems[i + 1].price,
                                                    rating = wishlistItems[i + 1].rating,
                                                    soldCount = 1000
                                                )
                                            )
                                        },
                                        onCardClick = {
                                            viewModel.openProductDetailsScreen(
                                                ProductItem(
                                                    id = wishlistItems[i + 1].id,
                                                    name = wishlistItems[i + 1].name,
                                                    imageUrl = "",
                                                    price = wishlistItems[i + 1].price,
                                                    rating = wishlistItems[i + 1].rating,
                                                    soldCount = 1000
                                                )
                                            )
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

            // 6. Mục "🔥 Gợi ý dành riêng cho bạn"
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.ThumbUp,
                                contentDescription = null,
                                tint = Color(0xFFBA2211),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Gợi ý dành riêng cho bạn",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = "Xem thêm",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBA2211),
                            modifier = Modifier.clickable {
                                viewModel.selectTab(0)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Hàng cuộn ngang gợi ý
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        item {
                            RecommendedWishlistCard(
                                name = "Máy xông tinh dầu gốm vân gỗ cao cấp",
                                price = "199.000đ",
                                discount = "-20%",
                                imageRes = R.drawable.socks_sport_pack_1793957699009,
                                onSave = { viewModel.showToast("Đã lưu vào Wishlist!") }
                            )
                        }
                        item {
                            RecommendedWishlistCard(
                                name = "Giá đỡ laptop nhôm tản nhiệt 6 góc gập",
                                price = "165.000đ",
                                discount = "-15%",
                                imageRes = R.drawable.anker_gan_charger_1793957714558,
                                onSave = { viewModel.showToast("Đã lưu vào Wishlist!") }
                            )
                        }
                        item {
                            RecommendedWishlistCard(
                                name = "Đế sạc không dây nam châm MagSafe 15W",
                                price = "320.000đ",
                                discount = "-30%",
                                imageRes = R.drawable.headphones_anker_space_one_1793957334650,
                                onSave = { viewModel.showToast("Đã lưu vào Wishlist!") }
                            )
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
    modifier: Modifier = Modifier
) {
    val productItem = remember(item.id) {
        ProductItem(
            id = item.id,
            name = item.name,
            imageUrl = "",
            price = item.price,
            originalPrice = item.originalPrice,
            rating = item.rating,
            soldCount = item.soldCount.filter { it.isDigit() }.toIntOrNull() ?: 1000
        )
    }

    ProductCard(
        product = productItem,
        imageRes = item.imageRes,
        badgeText = item.badgeText,
        badgeColor = if (item.isRedBadge) Color(0xFFBA2211) else Color(0xFF1D4ED8),
        customDiscountTag = item.discountPercent,
        showWishlistAction = true,
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
        modifier = modifier
    )
}

@Composable
fun RecommendedWishlistCard(
    name: String,
    price: String,
    discount: String,
    imageRes: Int,
    onSave: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clip(RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(105.dp)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(Color(0xFFBA2211), RoundedCornerShape(bottomEnd = 4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = discount,
                        color = Color.White,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = name,
                    fontSize = 11.5.sp,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = price,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBA2211)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF3F4F6))
                        .clickable { onSave() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = Color(0xFF374151),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Lưu lại",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF374151)
                        )
                    }
                }
            }
        }
    }
}
