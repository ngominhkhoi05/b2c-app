package com.example.b2capp.features.cart.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Hàng hiển thị từng sản phẩm trong Shop Card
 */
@Composable
fun CartProductItemRow(
    isChecked: Boolean,
    onCheckChange: () -> Unit,
    imageRes: Int? = null,
    imageUrl: String? = null,
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
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")) }

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
            val fallbackRes = imageRes ?: R.drawable.folded_black_tshirt_1793956682242
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = fallbackRes),
                    error = painterResource(id = fallbackRes),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = fallbackRes),
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
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
fun RoundCheckbox(
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
 * Container bọc hàng sản phẩm hỗ trợ quẹt qua trái hoặc qua phải để lộ nút "Xóa",
 * sau khi quẹt lộ nút ra, người dùng nhấn vào nút "Xóa" thì mới thực hiện xóa sản phẩm khỏi giỏ hàng.
 */
@Composable
fun SwipeableCartProductItem(
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val deleteBtnWidth = 80.dp
    val deleteBtnWidthPx = with(density) { deleteBtnWidth.toPx() }

    var targetOffset by remember { mutableFloatStateOf(0f) }
    val animatedOffset by animateFloatAsState(
        targetValue = targetOffset,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "swipe_delete_offset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(0.dp))
    ) {
        // Nền nút Xóa màu đỏ lộ ra khi quẹt (hỗ trợ cả quẹt trái và quẹt phải)
        if (animatedOffset != 0f) {
            val isSwipingLeft = animatedOffset < 0
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0xFFE53935)),
                contentAlignment = if (isSwipingLeft) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                Column(
                    modifier = Modifier
                        .width(deleteBtnWidth)
                        .fillMaxHeight()
                        .clickable {
                            targetOffset = 0f
                            onDelete()
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Xóa sản phẩm",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Xóa",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Hàng sản phẩm nằm trên, trượt theo gesture
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .background(SurfaceCard)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            targetOffset = (targetOffset + dragAmount).coerceIn(
                                -deleteBtnWidthPx * 1.25f,
                                deleteBtnWidthPx * 1.25f
                            )
                        },
                        onDragEnd = {
                            targetOffset = when {
                                targetOffset < -deleteBtnWidthPx / 2f -> -deleteBtnWidthPx
                                targetOffset > deleteBtnWidthPx / 2f -> deleteBtnWidthPx
                                else -> 0f
                            }
                        },
                        onDragCancel = {
                            targetOffset = 0f
                        }
                    )
                }
                .clickable(enabled = targetOffset != 0f) {
                    // Nhấn vào hàng sản phẩm khi đang mở nút xóa sẽ đóng lại
                    targetOffset = 0f
                }
        ) {
            content()
        }
    }
}


