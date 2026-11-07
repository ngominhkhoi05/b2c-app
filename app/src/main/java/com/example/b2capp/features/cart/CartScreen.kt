package com.example.b2capp.features.cart

import com.example.b2capp.features.cart.components.CartBottomSummaryBar
import com.example.b2capp.features.cart.components.CartProductItemRow
import com.example.b2capp.features.cart.components.RoundCheckbox
import com.example.b2capp.features.product.ProductVariantBottomSheet
import com.example.b2capp.features.product.VariantSheetMode

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.b2capp.core.ui.components.EmptyStateView
import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.designsystem.theme.BackgroundApp
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
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
 * Thiết kế chính xác theo ShoppingCartScreen_040b475d.png:
 * 1. Top Bar: Nút Back, "Giỏ hàng", nút đỏ "Sửa / Xong", icon chat shop.
 * 2. Thẻ Shop 1 (Coolmate Official - Đang chọn):
 *    - Checkbox đỏ tick trắng, tag MALL đỏ, icon Shop + Coolmate Offici >, tag "Giảm 15k >".
 *    - Item 1: Áo Thun Nam Cotton Compact Siêu Thoáng Khí Cao Cấp, Phân loại: Đen, L, ₫199.000, Tạm tính: ₫398.000, [-] 2 [+].
 *    - Item 2: Áo Thun Regular Fit Basic Thoáng Khí Hàng Ngày, Phân loại: Trắng, XL, ₫219.000, [-] 1 [+].
 * 3. Thẻ Shop 2 (Anker Audio Flagship - Chưa chọn):
 *    - Checkbox xám tròn, tag MALL xanh navy, "Voucher Shop >".
 *    - Item: Tai Nghe Chống Ồn Anker Space One Không Dây Hi–Res, Màu: Đen Titan, ₫890.000, [-] 1 [+].
 * 4. Thẻ Shop 3 (Uniqlo Vietnam - Chưa chọn):
 *    - Checkbox xám tròn, Uniqlo Vietnam >, "2 sản phẩm".
 *    - Item: Áo Polo Dry Pique Ngắn Tay Kháng Khuẩn, Xanh Navy, M, tag "Chỉ còn 2", ₫399.000, [-] 1 [+].
 * 5. Sticky Bottom Checkout Bar:
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
    val (currentTotalPrice, totalItemsCount, savingsAmount, currentShopLabel) = when (selectedShopId) {
        "shop_coolmate" -> {
            val subtotal = (199000L * qtyCoolmate1) + (219000L * qtyCoolmate2)
            val count = qtyCoolmate1 + qtyCoolmate2
            Quadruple(subtotal, count, 145000L, "Coolmate (2)")
        }
        "shop_anker" -> {
            val subtotal = 890000L * qtyAnker
            Quadruple(subtotal, qtyAnker, 50000L, "Anker Audio (1)")
        }
        else -> {
            val subtotal = 399000L * qtyUniqlo
            Quadruple(subtotal, qtyUniqlo, 30000L, "Uniqlo (1)")
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
                    .height(48.dp)
                    .padding(horizontal = 8.dp),
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

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Giỏ hàng",
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
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.width(2.dp))

                    IconButton(
                        onClick = { viewModel.openShopMessages() },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Tin nhắn với Shop",
                            tint = TextPrimary,
                            modifier = Modifier.size(23.dp)
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
            // 2. THẺ SHOP 1: Coolmate Official (Đang chọn)
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
                    }
                }
            }

            // 3. THẺ SHOP 2: Anker Audio Flagship
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
                    }
                }
            }

            // 4. THẺ SHOP 3: Uniqlo Vietnam
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

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // 5. STICKY BOTTOM CHECKOUT BAR
        CartBottomSummaryBar(
            currentShopLabel = currentShopLabel,
            formattedTotalPrice = formattedTotalPrice,
            formattedSavings = formattedSavings,
            totalItemsCount = totalItemsCount,
            isEditMode = isEditMode,
            onCheckoutClick = {
                if (isEditMode) {
                    viewModel.showToast("Đã xóa sản phẩm khỏi giỏ hàng")
                } else {
                    viewModel.openCheckoutScreen()
                }
            }
        )
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
 * Helper class for 4 values
 */
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
