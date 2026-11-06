package com.example.b2capp.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ripple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.B2CAppTheme
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Bộ chọn số lượng (QuantityPicker) cho Buyer App:
 * - Nút [-] bo góc viền xám, tự động disable khi <= minQuantity (mặc định = 1)
 * - Hiển thị số lượng ở giữa (14sp Bold)
 * - Nút [+] bo góc viền xám, tự động disable khi >= maxStock
 * - Hỗ trợ chạm phản hồi xúc giác ripple mượt mà
 */
@Composable
fun QuantityPicker(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minQuantity: Int = 1,
    maxStock: Int = 99,
    testTag: String = "quantity_picker"
) {
    val isDecreaseEnabled = quantity > minQuantity
    val isIncreaseEnabled = quantity < maxStock

    Row(
        modifier = modifier
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nút Giảm [-]
        PickerButton(
            icon = Icons.Filled.Remove,
            contentDescription = "Giảm số lượng",
            enabled = isDecreaseEnabled,
            onClick = {
                if (isDecreaseEnabled) {
                    onQuantityChange(quantity - 1)
                }
            },
            testTag = "${testTag}_decrease"
        )

        // Ô hiển thị số lượng ở giữa (14sp Bold)
        Box(
            modifier = Modifier
                .width(44.dp)
                .height(32.dp)
                .border(width = 1.dp, color = BorderDivider)
                .background(SurfaceCard),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = quantity.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("${testTag}_value")
            )
        }

        // Nút Tăng [+]
        PickerButton(
            icon = Icons.Filled.Add,
            contentDescription = "Tăng số lượng",
            enabled = isIncreaseEnabled,
            onClick = {
                if (isIncreaseEnabled) {
                    onQuantityChange(quantity + 1)
                }
            },
            testTag = "${testTag}_increase"
        )
    }
}

@Composable
private fun PickerButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(32.dp)
            .border(
                width = 1.dp,
                color = if (enabled) BorderDivider else BorderDivider.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            )
            .background(
                color = if (enabled) SurfaceCard else Color(0xFFF9FAFB),
                shape = RoundedCornerShape(4.dp)
            )
            .clip(RoundedCornerShape(4.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = CoralPrimary),
                enabled = enabled,
                onClick = onClick
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) TextPrimary else TextSecondary.copy(alpha = 0.4f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuantityPickerPreview() {
    var count by remember { mutableIntStateOf(1) }

    B2CAppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            QuantityPicker(
                quantity = count,
                onQuantityChange = { count = it },
                minQuantity = 1,
                maxStock = 5
            )
        }
    }
}
