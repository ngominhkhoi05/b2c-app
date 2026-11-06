package com.example.b2capp.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.CoralPrimaryContainer
import com.example.ui.theme.FreeShippingAccent
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Tag Freeship Xtra chuẩn màu #26AA99, bo 4.dp
 */
@Composable
fun FreeShippingBadge(
    modifier: Modifier = Modifier,
    text: String = "Freeship Xtra"
) {
    Box(
        modifier = modifier
            .background(color = FreeShippingAccent, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

/**
 * Tag Giảm giá Sale % chuẩn nền #FFEBE7, chữ & viền #EE4D2D, bo 4.dp
 */
@Composable
fun DiscountBadge(
    percent: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color = CoralPrimaryContainer, shape = RoundedCornerShape(4.dp))
            .border(width = 0.5.dp, color = CoralPrimary.copy(alpha = 0.4f), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "-$percent%",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = CoralPrimary
        )
    }
}

/**
 * Category Chip bo góc 16.dp chuẩn Design Tokens
 */
@Composable
fun FilterChipM3(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) CoralPrimary else SurfaceCard
    val contentColor = if (isSelected) Color.White else TextPrimary
    val borderColor = if (isSelected) CoralPrimary else BorderDivider

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = contentColor
        )
    }
}
