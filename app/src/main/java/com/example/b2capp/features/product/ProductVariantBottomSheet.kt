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
    initialColor: String = "Đen",
    initialSize: String = "M",
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
    val selectedOptionValues = remember(product.id, product.options) {
        val map = mutableStateMapOf<Long, Long>()
        if (hasDynamicOptions) {
            product.options.forEach { opt ->
                val preselected = initialSelectedOptionValues[opt.id]
                val fallback = opt.values.firstOrNull()?.id
                if (preselected != null && opt.values.any { it.id == preselected }) {
                    map[opt.id] = preselected
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

    // Dành cho fallback sản phẩm mock khi không có options từ API
    var fallbackColor by remember { mutableStateOf(initialColor) }
    var fallbackSize by remember { mutableStateOf(initialSize) }

    var quantity by remember { mutableIntStateOf(initialQuantity.coerceAtLeast(1)) }

    // Tính toán giá và kho
    val unitPrice: Long = currentVariant?.salePrice ?: product.price
    val originalPrice: Long? = currentVariant?.price?.let { if (it > unitPrice) it else null } ?: product.originalPrice
    val discountPercent: Int = currentVariant?.discountPercent ?: product.discountPercent ?: 0
    val maxStock: Int = (currentVariant?.stockQuantity ?: product.stock).coerceAtLeast(1)

    if (quantity > maxStock && maxStock > 0) {
        quantity = maxStock
    }

    val totalPrice = unitPrice * quantity
    val formattedTotalPrice = "₫${currencyFormatter.format(totalPrice)}"
    val formattedUnitPrice = "₫${currencyFormatter.format(unitPrice)}"
    val formattedOriginalPrice = originalPrice?.let { "₫${currencyFormatter.format(it)}" }

    // Chuỗi mô tả phân loại đã chọn
    val selectedSummaryText = when {
        hasDynamicOptions -> {
            currentVariant?.variantName ?: product.options.mapNotNull { opt ->
                val valId = selectedOptionValues[opt.id]
                opt.values.find { it.id == valId }?.value
            }.joinToString(", ")
        }
        isRealBackendProduct -> "Tiêu chuẩn"
        else -> "$fallbackColor, Size $fallbackSize"
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
                        val imageRes = if (fallbackColor == "Trắng") {
                            R.drawable.product_white_tshirt_1793955938236
                        } else {
                            R.drawable.folded_black_tshirt_1793956682242
                        }

                        Image(
                            painter = painterResource(id = imageRes),
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
                                .background(if (maxStock > 0) Color(0xFF10B981) else Color(0xFFEF4444), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (maxStock > 0) "Kho: Còn $maxStock sản phẩm" else "Hết hàng",
                            fontSize = 12.5.sp,
                            color = Color(0xFF555555)
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

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFFFFF1EE) else Color(0xFFF3F4F6))
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) Color(0xFFEE4D2D) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        selectedOptionValues[option.id] = optionValue.id
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
                                        color = if (isSelected) Color(0xFFD32F2F) else TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }
            } else if (!isRealBackendProduct) {
                // Fallback hiển thị màu sắc và kích cỡ cho dữ liệu mẫu áo thun
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Màu sắc (3)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = fallbackColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFD32F2F)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isBlackSelected = fallbackColor == "Đen"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isBlackSelected) Color(0xFFFFF1EE) else Color(0xFFF3F4F6))
                            .border(
                                width = 1.dp,
                                color = if (isBlackSelected) Color(0xFFEE4D2D) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { fallbackColor = "Đen" }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("variant_color_black")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(Color(0xFF1E1E1E), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isBlackSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Đen",
                                fontSize = 13.sp,
                                fontWeight = if (isBlackSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isBlackSelected) Color(0xFFD32F2F) else TextPrimary
                            )
                        }
                    }

                    val isWhiteSelected = fallbackColor == "Trắng"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isWhiteSelected) Color(0xFFFFF1EE) else Color(0xFFF3F4F6))
                            .border(
                                width = 1.dp,
                                color = if (isWhiteSelected) Color(0xFFEE4D2D) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { fallbackColor = "Trắng" }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("variant_color_white")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(Color.White, CircleShape)
                                    .border(1.dp, Color(0xFFD1D5DB), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isWhiteSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Color(0xFFEE4D2D),
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Trắng",
                                fontSize = 13.sp,
                                fontWeight = if (isWhiteSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isWhiteSelected) Color(0xFFD32F2F) else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Kích cỡ (4)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = fallbackSize,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val sizeOptions = listOf("S", "M", "L", "XL")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    sizeOptions.forEach { sizeLabel ->
                        val isSizeSelected = sizeLabel == fallbackSize
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSizeSelected) Color(0xFFFFF1EE) else Color(0xFFF3F4F6))
                                .border(
                                    width = 1.dp,
                                    color = if (isSizeSelected) Color(0xFFEE4D2D) else Color.Transparent,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clickable { fallbackSize = sizeLabel }
                                .testTag("variant_size_$sizeLabel"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sizeLabel,
                                fontSize = 13.sp,
                                fontWeight = if (isSizeSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSizeSelected) Color(0xFFD32F2F) else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
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
                            .clickable(enabled = quantity > 1) {
                                if (quantity > 1) quantity--
                            }
                            .testTag("btn_decrease_quantity"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "−",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (quantity > 1) TextPrimary else Color(0xFFD1D5DB)
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
                            color = TextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clickable(enabled = quantity < maxStock) {
                                if (quantity < maxStock) quantity++
                            }
                            .testTag("btn_increase_quantity"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (quantity < maxStock) TextPrimary else Color(0xFFD1D5DB)
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

                val confirmButtonText = when (mode) {
                    VariantSheetMode.BUY_NOW -> "MUA NGAY"
                    VariantSheetMode.ADD_TO_CART -> "THÊM VÀO GIỎ HÀNG"
                    VariantSheetMode.SELECT_ONLY -> "XÁC NHẬN"
                }

                val confirmButtonIcon = when (mode) {
                    VariantSheetMode.ADD_TO_CART -> Icons.Outlined.ShoppingCart
                    else -> Icons.Outlined.ShoppingBag
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFBA2211))
                        .clickable {
                            if (onConfirmVariant != null) {
                                onConfirmVariant(currentVariant, selectedSummaryText, quantity, mode)
                            }
                            onConfirm(fallbackColor, fallbackSize, quantity, mode)
                        }
                        .testTag("btn_confirm_variant_selection"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = confirmButtonIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = confirmButtonText,
                            color = Color.White,
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
