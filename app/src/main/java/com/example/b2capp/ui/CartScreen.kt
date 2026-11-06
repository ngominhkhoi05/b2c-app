package com.example.b2capp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.b2capp.core.ui.components.EmptyStateView
import com.example.b2capp.model.ProductItem
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.ui.theme.BackgroundApp
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.StarRatingGold
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

/**
 * Dữ liệu sản phẩm mẫu trong giỏ hàng theo từng shop
 */
data class ShopCartItem(
    val id: String,
    val name: String,
    val imageRes: Int,
    val variantText: String,
    val price: Long,
    var quantity: Int,
    val stockAlert: String? = null
)

data class ShopGroup(
    val id: String,
    val shopName: String,
    val isMall: Boolean = false,
    val mallBadgeColor: Color = Color(0xFFB82414),
    val voucherBadge: String? = null,
    val freeshipNotice: String? = null,
    val items: List<ShopCartItem>
)

/**
 * Màn hình Giỏ hàng (Shopping Cart Screen)
 * Thiết kế chính xác 100% theo ShoppingCartScreen_040b475d.png:
 * 1. Top Bar: Nút Back, "Shopping Cart", nút đỏ "Sửa", avatar tròn đỏ.
 * 2. Info Banner: "Hệ thống hỗ trợ tạo đơn hàng cho 1 Shop trong mỗi lần thanh toán để đảm bảo giao hàng nhanh nhất."
 * 3. Thẻ Shop 1 (Coolmate Official - Đang chọn):
 *    - Checkbox đỏ tick trắng, tag MALL đỏ, icon Shop + Coolmate Offici >, tag "Giảm 15k >".
 *    - Item 1: Áo Thun Nam Cotton Compact Siêu Thoáng Khí Cao Cấp, Phân loại: Đen, L, ₫199.000, Tạm tính: ₫398.000, [-] 2 [+].
 *    - Item 2: Áo Thun Regular Fit Basic Thoáng Khí Hàng Ngày, Phân loại: Trắng, XL, ₫219.000, [-] 1 [+].
 *    - Dải xanh ngọc: "Đã đạt Freeship ₫25.000 từ Coolmate Official".
 * 4. Thẻ Shop 2 (Anker Audio Flagship - Chưa chọn):
 *    - Checkbox xám tròn, tag MALL xanh navy, "Voucher Shop >".
 *    - Item: Tai Nghe Chống Ồn Anker Space One Không Dây Hi–Res, Màu: Đen Titan, ₫890.000, [-] 1 [+].
 *    - Chuyển shop: "Chỉ thanh toán 1 shop mỗi lần. Chuyển sang mua Shop này".
 * 5. Thẻ Shop 3 (Uniqlo Vietnam - Chưa chọn):
 *    - Checkbox xám tròn, Uniqlo Vietnam >, "2 sản phẩm".
 *    - Item: Áo Polo Dry Pique Ngắn Tay Kháng Khuẩn, Xanh Navy, M, tag "Chỉ còn 2", ₫399.000, [-] 1 [+].
 * 6. Khung Voucher & Shopee Xu:
 *    - "Voucher giảm giá & Freeship" | "Chọn hoặc nhập mã >".
 *    - "Shopee Xu (Dùng 200 Xu để giảm ₫200)" kèm Switch bật/tắt màu đỏ cam.
 * 7. Sticky Bottom Checkout Bar:
 *    - Checkbox đỏ "Tất cả", "Coolmate (2)".
 *    - "Tổng: ₫617.000", "Tiết kiệm ₫145.000".
 *    - Nút lớn "MUA HÀNG (3)".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Trạng thái shop đang được chọn để thanh toán (mặc định là shop 1: Coolmate)
    var selectedShopId by remember { mutableStateOf("shop_coolmate") }
    var isEditMode by remember { mutableStateOf(false) }
    var isCoinApplied by remember { mutableStateOf(true) }

    // Số lượng của từng sản phẩm trong các shop
    var qtyCoolmate1 by remember { mutableIntStateOf(2) }
    var qtyCoolmate2 by remember { mutableIntStateOf(1) }
    var qtyAnker by remember { mutableIntStateOf(1) }
    var qtyUniqlo by remember { mutableIntStateOf(1) }

    // Mở popup chọn phân loại sản phẩm
    var productForVariantSheet by remember { mutableStateOf<ProductItem?>(null) }
    var variantSheetColor by remember { mutableStateOf("Đen") }
    var variantSheetSize by remember { mutableStateOf("L") }

    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale("vi", "VN")) }

    // Tính toán tổng tiền theo shop đang chọn
    val coinDiscount = if (isCoinApplied) 200L else 0L

    val (currentTotalPrice, totalItemsCount, savingsAmount, currentShopLabel) = when (selectedShopId) {
        "shop_coolmate" -> {
            val subtotal = (199000L * qtyCoolmate1) + (219000L * qtyCoolmate2)
            val finalTotal = (subtotal - coinDiscount).coerceAtLeast(0L)
            val count = qtyCoolmate1 + qtyCoolmate2
            Quadruple(finalTotal, count, 145000L, "Coolmate (2)")
        }
        "shop_anker" -> {
            val subtotal = 890000L * qtyAnker
            val finalTotal = (subtotal - coinDiscount).coerceAtLeast(0L)
            Quadruple(finalTotal, qtyAnker, 50000L, "Anker Audio (1)")
        }
        else -> {
            val subtotal = 399000L * qtyUniqlo
            val finalTotal = (subtotal - coinDiscount).coerceAtLeast(0L)
            Quadruple(finalTotal, qtyUniqlo, 30000L, "Uniqlo (1)")
        }
    }

    val formattedTotalPrice = "₫${currencyFormatter.format(currentTotalPrice)}"
    val formattedSavings = "₫${currencyFormatter.format(savingsAmount)}"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F8))
            .testTag("cart_screen")
    ) {
        // 1. Top Bar
        Surface(
            color = SurfaceCard,
            modifier = Modifier.fillMaxWidth()
        ) {
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
                        onClick = onNavigateToHome,
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
                        text = if (isEditMode) "Xong" else "Sửa",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoralPrimary,
                        modifier = Modifier
                            .clickable { isEditMode = !isEditMode }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
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
        }

        // Danh sách cuộn chính
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 2. Banner thông báo hỗ trợ 1 Shop
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFDCEAFE))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = Color(0xFF1E5B94),
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Hệ thống hỗ trợ tạo đơn hàng cho 1 Shop trong mỗi lần thanh toán để đảm bảo giao hàng nhanh nhất.",
                            fontSize = 12.5.sp,
                            color = Color(0xFF1E3A8A),
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // 3. THẺ SHOP 1: Coolmate Official (Đang chọn)
            item {
                val isCoolmateSelected = selectedShopId == "shop_coolmate"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Shop Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedShopId = "shop_coolmate" }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RoundCheckbox(
                                isChecked = isCoolmateSelected,
                                onClick = { selectedShopId = "shop_coolmate" }
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFB82414), RoundedCornerShape(3.dp))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "MALL",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Icon(
                                imageVector = Icons.Filled.Storefront,
                                contentDescription = null,
                                tint = NavySecondary,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "Coolmate Offici >",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavySecondary
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            // Tag Giảm 15k
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFFEBE7), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Giảm 15k >",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFD32F2F)
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 0.8.dp)

                        // Item 1: Áo thun đen Cotton Compact
                        CartProductItemRow(
                            isChecked = isCoolmateSelected,
                            onCheckChange = { selectedShopId = "shop_coolmate" },
                            imageRes = R.drawable.folded_black_tshirt_1793956682242,
                            name = "Áo Thun Nam Cotton Compact Siêu Thoáng Khí Cao Cấp",
                            variantText = "Phân loại: Đen, L",
                            price = 199000L,
                            subtotalText = "Tạm tính:\n₫${currencyFormatter.format(199000L * qtyCoolmate1)}",
                            quantity = qtyCoolmate1,
                            onQuantityChange = { qtyCoolmate1 = it },
                            onVariantClick = {
                                productForVariantSheet = ProductItem(
                                    id = "coolmate_1",
                                    name = "Áo Thun Nam Cotton Compact Siêu Thoáng Khí Cao Cấp",
                                    imageUrl = "",
                                    price = 199000L,
                                    stock = 15
                                )
                                variantSheetColor = "Đen"
                                variantSheetSize = "L"
                            }
                        )

                        HorizontalDivider(color = Color(0xFFF9FAFB), thickness = 0.8.dp)

                        // Item 2: Áo thun trắng Regular Fit
                        CartProductItemRow(
                            isChecked = isCoolmateSelected,
                            onCheckChange = { selectedShopId = "shop_coolmate" },
                            imageRes = R.drawable.product_white_tshirt_1793955938236,
                            name = "Áo Thun Regular Fit Basic Thoáng Khí Hàng Ngày",
                            variantText = "Phân loại: Trắng, XL",
                            price = 219000L,
                            quantity = qtyCoolmate2,
                            onQuantityChange = { qtyCoolmate2 = it },
                            onVariantClick = {
                                productForVariantSheet = ProductItem(
                                    id = "coolmate_2",
                                    name = "Áo Thun Regular Fit Basic Thoáng Khí Hàng Ngày",
                                    imageUrl = "",
                                    price = 219000L,
                                    stock = 15
                                )
                                variantSheetColor = "Trắng"
                                variantSheetSize = "XL"
                            }
                        )

                        // Dải Freeship xanh ngọc
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE6F7F5))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.LocalShipping,
                                    contentDescription = null,
                                    tint = Color(0xFF1E8275),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Đã đạt Freeship ₫25.000 từ Coolmate Official",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1E8275)
                                )
                            }
                        }
                    }
                }
            }

            // 4. THẺ SHOP 2: Anker Audio Flagship
            item {
                val isAnkerSelected = selectedShopId == "shop_anker"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Shop Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedShopId = "shop_anker" }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RoundCheckbox(
                                isChecked = isAnkerSelected,
                                onClick = { selectedShopId = "shop_anker" }
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF28537D), RoundedCornerShape(3.dp))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "MALL",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Icon(
                                imageVector = Icons.Filled.Storefront,
                                contentDescription = null,
                                tint = NavySecondary,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "Anker Audio Fl... >",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavySecondary
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = "Voucher Shop >",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = NavySecondary
                            )
                        }

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 0.8.dp)

                        // Item Tai nghe Anker Space One
                        CartProductItemRow(
                            isChecked = isAnkerSelected,
                            onCheckChange = { selectedShopId = "shop_anker" },
                            imageRes = R.drawable.headphones_anker_space_one_1793957334650,
                            name = "Tai Nghe Chống Ồn Anker Space One Không Dây Hi–Res",
                            variantText = "Màu: Đen Titan",
                            price = 890000L,
                            quantity = qtyAnker,
                            isPriceActive = isAnkerSelected,
                            onQuantityChange = { qtyAnker = it }
                        )

                        // Khung Chuyển sang mua Shop này
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF8F9FA))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.SwapHoriz,
                                    contentDescription = null,
                                    tint = Color(0xFF1E5B94),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Chỉ thanh toán 1 shop mỗi lần.",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "Chuyển sang mua Shop này",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD32F2F),
                                        textDecoration = TextDecoration.Underline,
                                        modifier = Modifier.clickable {
                                            selectedShopId = "shop_anker"
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. THẺ SHOP 3: Uniqlo Vietnam
            item {
                val isUniqloSelected = selectedShopId == "shop_uniqlo"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Shop Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedShopId = "shop_uniqlo" }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RoundCheckbox(
                                isChecked = isUniqloSelected,
                                onClick = { selectedShopId = "shop_uniqlo" }
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Icon(
                                imageVector = Icons.Filled.Storefront,
                                contentDescription = null,
                                tint = NavySecondary,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "Uniqlo Vietnam >",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavySecondary
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = "2 sản phẩm",
                                fontSize = 11.5.sp,
                                color = TextSecondary
                            )
                        }

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 0.8.dp)

                        // Item Áo Polo Uniqlo
                        CartProductItemRow(
                            isChecked = isUniqloSelected,
                            onCheckChange = { selectedShopId = "shop_uniqlo" },
                            imageRes = R.drawable.polo_navy_folded_1793957349647,
                            name = "Áo Polo Dry Pique Ngắn Tay Kháng Khuẩn",
                            variantText = "Xanh Navy, M",
                            stockAlert = "Chỉ còn 2",
                            price = 399000L,
                            quantity = qtyUniqlo,
                            isPriceActive = isUniqloSelected,
                            onQuantityChange = { qtyUniqlo = it }
                        )
                    }
                }
            }

            // 6. THẺ VOUCHER & SHOPEE XU
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Hàng Voucher
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* Mở danh sách voucher */ }
                                .padding(horizontal = 14.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ConfirmationNumber,
                                contentDescription = null,
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Voucher giảm giá & Freeship",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Chọn hoặc nhập mã",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFD32F2F)
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 0.8.dp)

                        // Hàng Shopee Xu
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MonetizationOn,
                                contentDescription = null,
                                tint = StarRatingGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Shopee Xu",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "(Dùng 200 Xu để giảm ₫200)",
                                        fontSize = 11.5.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Switch(
                                checked = isCoinApplied,
                                onCheckedChange = { isCoinApplied = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFBA2211),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFD1D5DB)
                                ),
                                modifier = Modifier.size(width = 44.dp, height = 24.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // 7. STICKY BOTTOM CHECKOUT BAR
        Surface(
            color = SurfaceCard,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 10.dp)
                .border(0.8.dp, BorderDivider)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox Tất cả
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        // Toggle chọn
                    }
                ) {
                    RoundCheckbox(
                        isChecked = true,
                        onClick = {}
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Tất cả",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = currentShopLabel,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Cụm Tổng tiền & Tiết kiệm
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "Tổng: ",
                            fontSize = 11.5.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        Text(
                            text = formattedTotalPrice,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBA2211)
                        )
                    }
                    Text(
                        text = "Tiết kiệm $formattedSavings",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E8275)
                    )
                }

                // Nút MUA HÀNG (X)
                Box(
                    modifier = Modifier
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFBA2211))
                        .clickable {
                            if (isEditMode) {
                                viewModel.showToast("Đã xóa sản phẩm khỏi giỏ hàng")
                            } else {
                                viewModel.openCheckoutScreen()
                            }
                        }
                        .padding(horizontal = 16.dp)
                        .testTag("btn_cart_checkout"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isEditMode) "XÓA ($totalItemsCount)" else "MUA HÀNG ($totalItemsCount)",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }

    // Modal chọn phân loại khi chạm vào chip phân loại trong giỏ hàng
    productForVariantSheet?.let { product ->
        ProductVariantBottomSheet(
            product = product,
            initialColor = variantSheetColor,
            initialSize = variantSheetSize,
            initialQuantity = 1,
            mode = VariantSheetMode.SELECT_ONLY,
            onDismissRequest = { productForVariantSheet = null },
            onConfirm = { chosenColor, chosenSize, _, _ ->
                productForVariantSheet = null
            }
        )
    }
}

/**
 * Hàng hiển thị từng sản phẩm trong Shop Card
 */
@Composable
private fun CartProductItemRow(
    isChecked: Boolean,
    onCheckChange: () -> Unit,
    imageRes: Int,
    name: String,
    variantText: String,
    price: Long,
    quantity: Int,
    subtotalText: String? = null,
    stockAlert: String? = null,
    isPriceActive: Boolean = true,
    onQuantityChange: (Int) -> Unit,
    onVariantClick: (() -> Unit)? = null
) {
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale("vi", "VN")) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox chọn
        RoundCheckbox(
            isChecked = isChecked,
            onClick = onCheckChange
        )

        Spacer(modifier = Modifier.width(10.dp))

        // Thumbnail sản phẩm
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF3F4F6))
                .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Nội dung & bộ chọn số lượng
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Chip phân loại
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF3F4F6))
                        .clickable(enabled = onVariantClick != null) { onVariantClick?.invoke() }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = variantText,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                if (stockAlert != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFEECE8), RoundedCornerShape(3.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stockAlert,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Hàng giá & Bộ chọn số lượng
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "₫${currencyFormatter.format(price)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPriceActive) Color(0xFFD32F2F) else TextPrimary
                    )

                    if (subtotalText != null) {
                        Text(
                            text = subtotalText,
                            fontSize = 10.sp,
                            color = TextSecondary,
                            lineHeight = 12.sp
                        )
                    }
                }

                // Bộ chọn số lượng [-] [qty] [+]
                Row(
                    modifier = Modifier
                        .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(5.dp))
                        .background(Color(0xFFF9FAFB), RoundedCornerShape(5.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clickable(enabled = quantity > 1) {
                                if (quantity > 1) onQuantityChange(quantity - 1)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "−",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (quantity > 1) TextPrimary else Color(0xFFD1D5DB)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(34.dp)
                            .height(28.dp)
                            .background(Color.White)
                            .border(BorderStroke(0.8.dp, Color(0xFFE5E7EB))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = quantity.toString(),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onQuantityChange(quantity + 1) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Checkbox hình tròn đỏ có tick trắng (Active) hoặc vòng tròn xám (Inactive)
 */
@Composable
private fun RoundCheckbox(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(if (isChecked) Color(0xFFBA2211) else Color.Transparent)
            .border(
                width = if (isChecked) 0.dp else 1.5.dp,
                color = if (isChecked) Color.Transparent else Color(0xFFD1D5DB),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isChecked) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Đã chọn",
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

/**
 * Helper class for 4 values
 */
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
