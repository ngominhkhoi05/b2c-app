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
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.CoralPrimaryContainer
import com.example.b2capp.core.designsystem.theme.ErrorRed
import com.example.b2capp.core.designsystem.theme.FreeShippingAccent
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary

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
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = CoralPrimary
        )
    }
}

/**
 * Tag Giảm giá dạng text tuỳ biến (ví dụ "-35%")
 */
@Composable
fun DiscountTextBadge(
    discountText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color = CoralPrimary, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
        Text(
            text = discountText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

/**
 * Huy hiệu gian hàng chính hãng MALL (Nền đỏ #D32F2F, chữ trắng, bo 4.dp)
 */
@Composable
fun OfficialMallBadge(
    modifier: Modifier = Modifier,
    text: String = "MALL"
) {
    Box(
        modifier = modifier
            .background(color = ErrorRed, shape = RoundedCornerShape(bottomEnd = 4.dp, topStart = 4.dp))
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
 * Huy hiệu Shop Yêu Thích (Nền Cam #EE4D2D hoặc Đỏ #D32F2F)
 */
@Composable
fun FavoriteBadge(
    modifier: Modifier = Modifier,
    text: String = "Yêu thích"
) {
    Box(
        modifier = modifier
            .background(color = CoralPrimary, shape = RoundedCornerShape(bottomEnd = 6.dp, topStart = 4.dp))
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
