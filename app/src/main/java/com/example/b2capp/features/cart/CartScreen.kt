package com.example.b2capp.features.cart

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import com.example.b2capp.core.ui.components.EmptyStateView
import com.example.b2capp.data.remote.dto.shopDisplayName
import com.example.b2capp.features.cart.components.CartBottomSummaryBar
import com.example.b2capp.features.cart.components.CartProductItemRow
import com.example.b2capp.features.cart.components.SwipeableCartProductItem
import androidx.compose.runtime.key
import com.example.b2capp.features.cart.components.RoundCheckbox
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import java.text.NumberFormat
import java.util.Locale

/**
 * Thông tin xác nhận khi người dùng muốn đổi vùng chọn sang shop khác
 */
data class SwitchShopConfirmation(
    val currentShopName: String,
    val targetShopName: String,
    val onConfirm: () -> Unit
)

/**
 * Màn hình Giỏ hàng (Shopping Cart Screen) kết nối trực tiếp API Backend:
 * - Đọc danh sách giỏ hàng của người dùng hiện tại (GET /api/v1/cart)
 * - Tự động nhóm sản phẩm theo từng Shop / Người bán
 * - Chọn / Bỏ chọn từng sản phẩm hoặc toàn bộ sản phẩm trong shop / toàn giỏ hàng
 * - Thay đổi số lượng sản phẩm (PATCH /api/v1/cart/items/{id})
 * - Xóa sản phẩm đơn lẻ bằng cách quẹt thẻ sản phẩm (DELETE /api/v1/cart/items/{id})
 * - Hiển thị trạng thái Giỏ hàng trống khi không có sản phẩm
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pendingSwitchDialog by remember { mutableStateOf<SwitchShopConfirmation?>(null) }
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")) }

    // Tự động tải lại giỏ hàng từ API mỗi khi vào màn hình
    LaunchedEffect(Unit) {
        viewModel.loadCartItems()
    }

    val cartItems = uiState.serverCartItems
    val isLoading = uiState.isCartLoading

    // Nhóm các sản phẩm trong giỏ hàng theo Shop Name hoặc Seller ID
    val shopGroups = remember(cartItems) {
        cartItems.groupBy { item -> item.shopDisplayName }
    }

    // Tính toán tổng tiền và số lượng của các sản phẩm ĐƯỢC CHỌN
    val selectedItems = remember(cartItems) {
        cartItems.filter { it.isSelected }
    }
    val currentSelectedShopName = remember(selectedItems) {
        selectedItems.firstOrNull()?.shopDisplayName
    }
    val selectedTotalPrice = remember(selectedItems) {
        selectedItems.sumOf { item ->
            (item.lineTotal ?: ((item.priceSnapshot ?: 0.0) * item.quantity)).toLong()
        }
    }
    val selectedTotalCount = remember(selectedItems) {
        selectedItems.sumOf { it.quantity }
    }
    val currentShopLabel = if (currentSelectedShopName != null) {
        "$currentSelectedShopName ($selectedTotalCount)"
    } else {
        "Chưa chọn"
    }

    val formattedTotalPrice = "₫${currencyFormatter.format(selectedTotalPrice)}"
    val formattedSavings = "₫0"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F8))
            .testTag("cart_screen")
    ) {
        // 1. Top Bar: Nút Back, "Giỏ hàng", nút "Sửa / Xong", icon Chat
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

        // 2. Nội dung chính: Trạng thái Loading / Empty / Danh sách theo Shop
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = CoralPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Đang tải giỏ hàng...",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            cartItems.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateView(
                        icon = Icons.Outlined.RemoveShoppingCart,
                        title = "Giỏ hàng của bạn đang trống",
                        description = "Hãy chọn thêm các sản phẩm ưng ý để đặt hàng bạn nhé!",
                        actionText = "Tiếp tục mua sắm",
                        onActionClick = onNavigateToHome
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    shopGroups.forEach { (shopName, shopItems) ->
                        item(key = "shop_$shopName") {
                            val isShopAllSelected = shopItems.all { it.isSelected }
                            val sellerId = shopItems.firstOrNull()?.sellerId

                            val handleShopHeaderClick = {
                                val willSelectAll = !isShopAllSelected
                                if (!willSelectAll) {
                                    viewModel.toggleShopItemsSelection(
                                        sellerId = sellerId,
                                        shopName = shopName,
                                        selectAll = false
                                    )
                                } else {
                                    val selectedFromOtherShops = cartItems.filter { other ->
                                        other.isSelected && other.shopDisplayName != shopName
                                    }
                                    if (selectedFromOtherShops.isNotEmpty()) {
                                        val currentShop = selectedFromOtherShops.first().shopDisplayName
                                        pendingSwitchDialog = SwitchShopConfirmation(
                                            currentShopName = currentShop,
                                            targetShopName = shopName,
                                            onConfirm = {
                                                viewModel.switchSelectedShop(
                                                    sellerId = sellerId,
                                                    shopName = shopName
                                                )
                                            }
                                        )
                                    } else {
                                        viewModel.toggleShopItemsSelection(
                                            sellerId = sellerId,
                                            shopName = shopName,
                                            selectAll = true
                                        )
                                    }
                                }
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Header của Shop
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { handleShopHeaderClick() }
                                            .padding(horizontal = 12.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RoundCheckbox(
                                            isChecked = isShopAllSelected,
                                            onClick = handleShopHeaderClick
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
                                            text = "$shopName >",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavySecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )

                                        Spacer(modifier = Modifier.weight(1f))

                                        Text(
                                            text = "${shopItems.size} sản phẩm",
                                            fontSize = 11.5.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 0.8.dp)

                                    // Danh sách các sản phẩm của shop
                                    shopItems.forEachIndexed { index, item ->
                                        val price = (item.priceSnapshot ?: 0.0).toLong()
                                        val lineTotal = (item.lineTotal ?: (price.toDouble() * item.quantity)).toLong()
                                        val stock = item.stockQuantity ?: 99
                                        val alert = if (item.isAvailable == false) {
                                            "Hết hàng"
                                        } else if (stock in 1..5) {
                                            "Chỉ còn $stock"
                                        } else null

                                        key(item.id) {
                                            SwipeableCartProductItem(
                                                onDelete = {
                                                    viewModel.removeCartItem(item.id)
                                                }
                                            ) {
                                                CartProductItemRow(
                                                    isChecked = item.isSelected,
                                                    onCheckChange = {
                                                        val willSelect = !item.isSelected
                                                        if (!willSelect) {
                                                            viewModel.toggleCartItemSelection(item.id, false)
                                                        } else {
                                                            val targetShop = item.shopDisplayName
                                                            val selectedFromOtherShops = cartItems.filter { other ->
                                                                other.isSelected && other.shopDisplayName != targetShop
                                                            }
                                                            if (selectedFromOtherShops.isNotEmpty()) {
                                                                val currentShop = selectedFromOtherShops.first().shopDisplayName
                                                                pendingSwitchDialog = SwitchShopConfirmation(
                                                                    currentShopName = currentShop,
                                                                    targetShopName = targetShop,
                                                                    onConfirm = {
                                                                        viewModel.switchSelectedCartItem(item.id)
                                                                    }
                                                                )
                                                            } else {
                                                                viewModel.toggleCartItemSelection(item.id, true)
                                                            }
                                                        }
                                                    },
                                                    imageUrl = item.thumbnailUrl,
                                                    name = item.productName ?: "Sản phẩm",
                                                    variantText = item.variantName?.let { "Phân loại: $it" } ?: "Mặc định",
                                                    price = price,
                                                    quantity = item.quantity,
                                                    subtotalText = "Tạm tính:\n₫${currencyFormatter.format(lineTotal)}",
                                                    stockAlert = alert,
                                                    isPriceActive = item.isSelected,
                                                    onQuantityChange = { newQty ->
                                                        viewModel.updateCartItemQuantity(item.id, newQty)
                                                    }
                                                )
                                            }
                                        }

                                        if (index < shopItems.size - 1) {
                                            HorizontalDivider(color = Color(0xFFF9FAFB), thickness = 0.8.dp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // 3. Thanh tóm tắt và nút Mua Hàng / Xóa ở đáy màn hình
        if (cartItems.isNotEmpty()) {
            CartBottomSummaryBar(
                currentShopLabel = currentShopLabel,
                formattedTotalPrice = formattedTotalPrice,
                formattedSavings = formattedSavings,
                totalItemsCount = selectedTotalCount,
                onCheckoutClick = {
                    if (selectedTotalCount == 0) {
                        viewModel.showErrorToast("Vui lòng chọn ít nhất 1 sản phẩm để thanh toán")
                    } else {
                        viewModel.openCheckoutScreen()
                    }
                }
            )
        }
    }

    // 4. Hộp thoại xác nhận khi người dùng chọn sản phẩm sang shop khác
    pendingSwitchDialog?.let { dialogData ->
        AlertDialog(
            onDismissRequest = { pendingSwitchDialog = null },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Chuyển sang shop khác?",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Bạn chỉ có thể chọn các sản phẩm trong cùng 1 shop.\n\nBạn có muốn chuyển sang mua sản phẩm của shop \"${dialogData.targetShopName}\" không? (Các sản phẩm đã chọn của shop \"${dialogData.currentShopName}\" sẽ bị bỏ tích)",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        dialogData.onConfirm()
                        pendingSwitchDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Xác nhận",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { pendingSwitchDialog = null },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Hủy",
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        )
    }
}
