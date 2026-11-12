package com.example.b2capp.features.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.core.model.ProductVariantItem
import java.text.NumberFormat
import java.util.Locale

enum class VariantSheetMode {
    ADD_TO_CART,
    BUY_NOW,
    SELECT_ONLY
}

/**
 * Popup Chọn phân loại sản phẩm (Product Variant Selection Screen / Bottom Sheet)
 * Hỗ trợ linh hoạt:
 * - Dữ liệu động theo API Backend (options, values, variants, stock, price, images)
 * - Tự động cập nhật hình ảnh, giá bán, tồn kho khi chuyển đổi các phân loại
 * - Fallback tương thích ngược hoàn toàn cho các sản phẩm mẫu tĩnh
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductVariantBottomSheet(
    product: ProductItem,
    initialSelectedOptionValues: Map<Long, Long> = emptyMap(),
    initialColor: String = "",
    initialSize: String = "",
    initialQuantity: Int = 1,
    mode: VariantSheetMode = VariantSheetMode.ADD_TO_CART,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismissRequest: () -> Unit,
    onConfirm: (color: String, size: String, quantity: Int, mode: VariantSheetMode) -> Unit = { _, _, _, _ -> },
    onConfirmVariant: ((variant: ProductVariantItem?, selectedSummary: String, quantity: Int, mode: VariantSheetMode) -> Unit)? = null
) {
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale("vi", "VN")) }

    val isRealBackendProduct = product.id.toLongOrNull() != null
    val hasDynamicOptions = product.options.isNotEmpty()

    // Map lưu lựa chọn optionId -> optionValueId
    val selectedOptionValues = remember(product.id, product.options, product.variants, initialSelectedOptionValues) {
        val map = mutableStateMapOf<Long, Long>()
        if (hasDynamicOptions) {
            val initialVariant = if (initialSelectedOptionValues.isNotEmpty()) {
                product.findVariantByOptionValues(initialSelectedOptionValues.values)
            } else null

            val targetVariant = if (initialVariant != null && initialVariant.stockQuantity > 0) {
                initialVariant
            } else {
                product.variants.firstOrNull { it.stockQuantity > 0 && it.optionValueIds.isNotEmpty() }
                    ?: product.variants.firstOrNull()
            }

            product.options.forEach { opt ->
                val preselected = initialSelectedOptionValues[opt.id]
                val matchingFromTarget = targetVariant?.optionValueIds?.let { ids ->
                    opt.values.find { ids.contains(it.id) }?.id
                }
                val fallback = opt.values.firstOrNull()?.id

                if (preselected != null && opt.values.any { it.id == preselected }) {
                    map[opt.id] = preselected
                } else if (matchingFromTarget != null) {
                    map[opt.id] = matchingFromTarget
                } else if (fallback != null) {
                    map[opt.id] = fallback
                }
            }
        }
        map
    }

    // Biến thể khớp với lựa chọn hiện tại
    val currentVariant = remember(product.variants, selectedOptionValues.toMap()) {
        if (hasDynamicOptions) {
            product.findVariantByOptionValues(selectedOptionValues.values)
        } else {
            product.variants.firstOrNull()
        }
    }

    val isOutOfStock = if (hasDynamicOptions) {
        currentVariant == null || currentVariant.stockQuantity <= 0
    } else {
        product.stock <= 0
    }

    val currentStock: Int = if (hasDynamicOptions) {
        currentVariant?.stockQuantity ?: 0
    } else {
        product.stock
    }
    val maxStock: Int = if (isOutOfStock) 0 else currentStock

    var quantity by remember {
        mutableIntStateOf(if (isOutOfStock) 0 else initialQuantity.coerceIn(1, maxStock.coerceAtLeast(1)))
    }

    LaunchedEffect(isOutOfStock, maxStock) {
        if (isOutOfStock) {
            quantity = 0
        } else if (quantity < 1 && maxStock > 0) {
            quantity = 1
        } else if (quantity > maxStock && maxStock > 0) {
            quantity = maxStock
        }
    }

    // Tính toán giá và kho
    val unitPrice: Long = currentVariant?.salePrice ?: product.price
    val originalPrice: Long? = currentVariant?.price?.let { if (it > unitPrice) it else null } ?: product.originalPrice
    val discountPercent: Int = currentVariant?.discountPercent ?: product.discountPercent ?: 0

    val totalPrice = if (isOutOfStock) 0L else (unitPrice * quantity)
    val formattedTotalPrice = if (isOutOfStock) "0₫" else "₫${currencyFormatter.format(totalPrice)}"
    val formattedUnitPrice = "₫${currencyFormatter.format(unitPrice)}"
    val formattedOriginalPrice = originalPrice?.let { "₫${currencyFormatter.format(it)}" }

    // Kiểm tra khả năng chọn của từng OptionValue
    fun isOptionValueSelectable(optionId: Long, valueId: Long): Boolean {
        // 1. Phải có ít nhất 1 biến thể chứa valueId này còn hàng (> 0)
        val hasAnyStock = product.variants.any { v ->
            v.stockQuantity > 0 && v.optionValueIds.contains(valueId)
        }
        if (!hasAnyStock) return false

        // 2. Nếu chỉ có 1 nhóm option duy nhất
        if (product.options.size <= 1) return true

        // 3. Nếu không phải option đầu tiên (ví dụ Size phụ thuộc vào Màu sắc đã chọn)
        val firstOption = product.options.firstOrNull()
        if (firstOption != null && firstOption.id != optionId) {
            val selectedFirstValId = selectedOptionValues[firstOption.id]
            if (selectedFirstValId != null) {
                val hasCombinationStock = product.variants.any { v ->
                    v.stockQuantity > 0 &&
                        v.optionValueIds.contains(selectedFirstValId) &&
                        v.optionValueIds.contains(valueId)
                }
                if (!hasCombinationStock) return false
            }
        }

        return true
    }

    // Xử lý khi người dùng click chọn OptionValue
    fun onSelectOptionValue(optionId: Long, valueId: Long) {
        selectedOptionValues[optionId] = valueId

        // Sau khi chọn, nếu các option khác không còn tạo thành tổ hợp còn hàng
        // thì tự động chuyển option khác sang giá trị còn hàng
        product.options.forEach { otherOpt ->
            if (otherOpt.id != optionId) {
                val currentOtherValId = selectedOptionValues[otherOpt.id]
                val combinationHasStock = product.variants.any { v ->
                    v.stockQuantity > 0 &&
                        v.optionValueIds.contains(valueId) &&
                        v.optionValueIds.contains(currentOtherValId)
                }
                if (!combinationHasStock) {
                    val fallbackVal = otherOpt.values.firstOrNull { valItem ->
                        product.variants.any { v ->
                            v.stockQuantity > 0 &&
                                v.optionValueIds.contains(valueId) &&
                                v.optionValueIds.contains(valItem.id)
                        }
                    }
                    if (fallbackVal != null) {
                        selectedOptionValues[otherOpt.id] = fallbackVal.id
                    }
                }
            }
        }
    }

    // Chuỗi mô tả phân loại đã chọn
    val selectedSummaryText = if (hasDynamicOptions) {
        currentVariant?.variantName ?: product.options.mapNotNull { opt ->
            val valId = selectedOptionValues[opt.id]
            opt.values.find { it.id == valId }?.value
        }.joinToString(", ")
    } else {
        "Tiêu chuẩn"
    }

    val displayThumbnail = currentVariant?.imageUrl?.ifBlank { null } ?: product.imageUrl

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = SurfaceCard,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .background(Color(0xFFD1D5DB), RoundedCornerShape(2.dp))
            )
        },
        modifier = Modifier.testTag("product_variant_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Header: Thumbnail, Giá, Kho, Đã chọn
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Ảnh thumbnail phân loại
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF3F4F6))
                        .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                ) {
                    if (displayThumbnail.isNotBlank()) {
                        AsyncImage(
                            model = displayThumbnail,
                            contentDescription = "Ảnh ${product.name}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.product_white_tshirt_1793955938236),
                            contentDescription = "Ảnh phân loại",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Cụm thông tin giá & trạng thái
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Nhãn phân biệt chế độ Popup
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (mode == VariantSheetMode.BUY_NOW) Color(0xFFFEE2E2) else Color(0xFFFFEDD5)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = when (mode) {
                                VariantSheetMode.BUY_NOW -> "⚡ Mua ngay"
                                VariantSheetMode.ADD_TO_CART -> "🛒 Thêm vào giỏ hàng"
                                VariantSheetMode.SELECT_ONLY -> "Chọn phân loại"
                            },
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (mode == VariantSheetMode.BUY_NOW) Color(0xFFB91C1C) else Color(0xFFC2410C)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Hàng giá & tag giảm giá
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = formattedUnitPrice,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )

                        if (formattedOriginalPrice != null) {
                            Text(
                                text = formattedOriginalPrice,
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }

                        if (discountPercent > 0) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFFEBE7), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "-$discountPercent%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFD32F2F)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Chấm xanh tồn kho
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(if (!isOutOfStock) Color(0xFF10B981) else Color(0xFFEF4444), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (!isOutOfStock) "Kho: Còn $maxStock sản phẩm" else "Hết hàng",
                            fontSize = 12.5.sp,
                            fontWeight = if (isOutOfStock) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isOutOfStock) Color(0xFFEF4444) else Color(0xFF555555)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Trạng thái đã chọn
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Đã chọn: ",
                            fontSize = 12.5.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = selectedSummaryText.ifBlank { "Mặc định" },
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // 2. Nhóm phân loại: Động từ API hoặc Fallback
            if (hasDynamicOptions) {
                product.options.forEach { option ->
                    val selectedValId = selectedOptionValues[option.id]
                    val selectedValue = option.values.find { it.id == selectedValId }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${option.name} (${option.values.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        if (selectedValue != null) {
                            Text(
                                text = selectedValue.value,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFD32F2F)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        option.values.forEach { optionValue ->
                            val isSelected = optionValue.id == selectedValId
                            val isSelectable = isOptionValueSelectable(option.id, optionValue.id)

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            isSelected -> Color(0xFFFFF1EE)
                                            !isSelectable -> Color(0xFFF3F4F6).copy(alpha = 0.5f)
                                            else -> Color(0xFFF3F4F6)
                                        }
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = when {
                                            isSelected -> Color(0xFFEE4D2D)
                                            !isSelectable -> Color(0xFFE5E7EB).copy(alpha = 0.6f)
                                            else -> Color.Transparent
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable(enabled = isSelectable) {
                                        onSelectOptionValue(option.id, optionValue.id)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                                    .testTag("variant_option_${option.id}_${optionValue.id}")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (optionValue.imageUrl != null) {
                                        AsyncImage(
                                            model = optionValue.imageUrl,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    } else if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Color(0xFFEE4D2D),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }

                                    Text(
                                        text = optionValue.value,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = when {
                                            !isSelectable -> Color(0xFF9CA3AF)
                                            isSelected -> Color(0xFFD32F2F)
                                            else -> TextPrimary
                                        },
                                        textDecoration = if (!isSelectable) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }
            } else {
                Text(
                    text = "Phân loại: Tiêu chuẩn",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 14.dp)
                )
            }

            // 3. Chọn Số lượng
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Số lượng",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tối đa $maxStock sản phẩm",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )
                }

                // Bộ chọn số lượng [-] [qty] [+]
                Row(
                    modifier = Modifier
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp))
                        .background(Color(0xFFF9FAFB), RoundedCornerShape(6.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clickable(enabled = !isOutOfStock && quantity > 1) {
                                if (quantity > 1) quantity--
                            }
                            .testTag("btn_decrease_quantity"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "−",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (!isOutOfStock && quantity > 1) TextPrimary else Color(0xFFD1D5DB)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height(34.dp)
                            .border(BorderStroke(1.dp, Color(0xFFE5E7EB)))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = quantity.toString(),
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOutOfStock) Color(0xFF9CA3AF) else TextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clickable(enabled = !isOutOfStock && quantity < maxStock) {
                                if (quantity < maxStock) quantity++
                            }
                            .testTag("btn_increase_quantity"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (!isOutOfStock && quantity < maxStock) TextPrimary else Color(0xFFD1D5DB)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

            // 4. Chân trang Sticky: Tạm tính & Nút XÁC NHẬN
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tạm tính:",
                        fontSize = 13.5.sp,
                        color = TextSecondary
                    )

                    Text(
                        text = formattedTotalPrice,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val confirmButtonText = if (isOutOfStock) {
                    "HẾT HÀNG"
                } else when (mode) {
                    VariantSheetMode.BUY_NOW -> "MUA NGAY"
                    VariantSheetMode.ADD_TO_CART -> "THÊM VÀO GIỎ HÀNG"
                    VariantSheetMode.SELECT_ONLY -> "XÁC NHẬN"
                }

                val confirmButtonIcon = when {
                    isOutOfStock -> null
                    mode == VariantSheetMode.ADD_TO_CART -> Icons.Outlined.ShoppingCart
                    else -> Icons.Outlined.ShoppingBag
                }

                val confirmButtonBg = if (isOutOfStock) Color(0xFFE5E7EB) else Color(0xFFBA2211)
                val confirmTextColor = if (isOutOfStock) Color(0xFF9CA3AF) else Color.White

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(confirmButtonBg)
                        .clickable(enabled = !isOutOfStock) {
                            if (onConfirmVariant != null) {
                                onConfirmVariant(currentVariant, selectedSummaryText, quantity, mode)
                            } else {
                                onConfirm("Tiêu chuẩn", "", quantity, mode)
                            }
                        }
                        .testTag("btn_confirm_variant_selection"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (confirmButtonIcon != null) {
                            Icon(
                                imageVector = confirmButtonIcon,
                                contentDescription = null,
                                tint = confirmTextColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = confirmButtonText,
                            color = confirmTextColor,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}
