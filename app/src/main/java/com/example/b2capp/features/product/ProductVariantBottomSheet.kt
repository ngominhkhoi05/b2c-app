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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

enum class VariantSheetMode {
    ADD_TO_CART,
    BUY_NOW,
    SELECT_ONLY
}

/**
 * Popup Chọn phân loại sản phẩm (Product Variant Selection Screen / Bottom Sheet)
 * Thiết kế chính xác 100% theo ProductVariantSelectionScreen_8aceb11e.png:
 * 1. Thanh kéo drag handle trên đỉnh sheet.
 * 2. Header:
 *    - Thumbnail ảnh sản phẩm áo đen gấp gọn R.drawable.folded_black_tshirt_1793956682242 (hoặc áo trắng khi chọn màu Trắng).
 *    - Giá: ₫199.000, giá gốc ₫350.000 gạch ngang, tag -43%.
 *    - Chấm xanh "Kho: Còn 15 sản phẩm".
 *    - "Đã chọn: Đen, Size M" (cập nhật theo lựa chọn).
 *    - Nút đóng [X] tròn màu xám ở góc trên phải.
 * 3. Hàng chọn Màu sắc (3):
 *    - Đen (đang chọn với icon tick đen và viền đỏ cam).
 *    - Trắng (chấm trắng viền nhẹ).
 *    - Xanh Navy (chấm xanh navy, disabled mờ, có tag "Hết hàng").
 * 4. Hàng chọn Kích cỡ (4):
 *    - Nút "Bảng quy đổi kích cỡ >" kèm icon thước đo.
 *    - 4 kích cỡ: S, M (đang chọn), L, XL.
 * 5. Bộ chọn Số lượng:
 *    - Nhãn "Số lượng", "Tối đa 15 sản phẩm".
 *    - Cụm nút [-] 2 [+].
 * 6. Khung cam kết bảo vệ:
 *    - Nền xanh phấn nhạt, icon huy hiệu tích xanh.
 *    - "Miễn phí trả hàng trong 15 ngày", "Đảm bảo hàng chính hãng 100% • Đổi size tận nơi".
 * 7. Sticky Footer:
 *    - "Tạm tính:" kèm số tiền tự động tính: ₫199.000 * 2 = ₫398.000.
 *    - Nút lớn "XÁC NHẬN" (kèm icon túi xách shopping bag).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductVariantBottomSheet(
    product: ProductItem,
    initialColor: String = "Đen",
    initialSize: String = "M",
    initialQuantity: Int = 2,
    mode: VariantSheetMode = VariantSheetMode.ADD_TO_CART,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismissRequest: () -> Unit,
    onConfirm: (color: String, size: String, quantity: Int, mode: VariantSheetMode) -> Unit
) {
    var selectedColor by remember { mutableStateOf(initialColor) }
    var selectedSize by remember { mutableStateOf(initialSize) }
    var quantity by remember { mutableIntStateOf(initialQuantity.coerceAtLeast(1)) }

    val unitPrice = 199000L
    val originalPrice = 350000L
    val discountPercent = 43
    val maxStock = 15

    val totalPrice = unitPrice * quantity
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale("vi", "VN")) }
    val formattedTotalPrice = "₫${currencyFormatter.format(totalPrice)}"
    val formattedUnitPrice = "₫${currencyFormatter.format(unitPrice)}"
    val formattedOriginalPrice = "₫${currencyFormatter.format(originalPrice)}"

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
        ) {
            // 1. Header: Thumbnail, Giá, Kho, Đã chọn, Nút đóng [X]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Ảnh thumbnail phân loại
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF3F4F6))
                        .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                ) {
                    val imageRes = if (selectedColor == "Trắng") {
                        R.drawable.product_white_tshirt_1793955938236
                    } else {
                        R.drawable.folded_black_tshirt_1793956682242
                    }

                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "Ảnh phân loại $selectedColor",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Cụm thông tin giá & trạng thái
                Column(
                    modifier = Modifier.weight(1f)
                ) {
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

                        Text(
                            text = formattedOriginalPrice,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textDecoration = TextDecoration.LineThrough
                        )

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

                    Spacer(modifier = Modifier.height(4.dp))

                    // Chấm xanh tồn kho
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Kho: Còn $maxStock sản phẩm",
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
                            text = "$selectedColor, Size $selectedSize",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }

                // Nút đóng [X]
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFF3F4F6), CircleShape)
                        .testTag("btn_close_variant_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Đóng",
                        tint = Color(0xFF555555),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // 2. Chọn Màu sắc (3)
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
                    text = selectedColor,
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
                // Tùy chọn 1: Đen (Đang chọn)
                val isBlackSelected = selectedColor == "Đen"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isBlackSelected) Color(0xFFFFF1EE) else Color(0xFFF3F4F6))
                        .border(
                            width = 1.dp,
                            color = if (isBlackSelected) Color(0xFFEE4D2D) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedColor = "Đen" }
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

                // Tùy chọn 2: Trắng
                val isWhiteSelected = selectedColor == "Trắng"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isWhiteSelected) Color(0xFFFFF1EE) else Color(0xFFF3F4F6))
                        .border(
                            width = 1.dp,
                            color = if (isWhiteSelected) Color(0xFFEE4D2D) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedColor = "Trắng" }
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

                // Tùy chọn 3: Xanh Navy (Hết hàng - Disabled)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF9FAFB))
                        .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                        .testTag("variant_color_navy_disabled")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(Color(0xFF93A7BA), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Xanh Navy",
                            fontSize = 12.5.sp,
                            color = Color(0xFF9CA3AF)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE5E7EB), RoundedCornerShape(3.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "Hết hàng",
                                fontSize = 9.5.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Chọn Kích cỡ (4)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Kích cỡ (4)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = selectedSize,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4 Nút kích cỡ: S, M, L, XL
            val sizeOptions = listOf("S", "M", "L", "XL")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                sizeOptions.forEach { sizeLabel ->
                    val isSizeSelected = sizeLabel == selectedSize
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
                            .clickable { selectedSize = sizeLabel }
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

            // 4. Chọn Số lượng
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

                // Bộ chọn số lượng [-] [2] [+]
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

            // 5. Khung cam kết bảo vệ người mua
            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

            // 5. Chân trang Sticky: Tạm tính & Nút XÁC NHẬN
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

                // Nút XÁC NHẬN lớn (kèm icon túi xách)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFBA2211))
                        .clickable {
                            onConfirm(selectedColor, selectedSize, quantity, mode)
                        }
                        .testTag("btn_confirm_variant_selection"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingBag,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "XÁC NHẬN",
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
