package com.example.b2capp.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ripple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.core.designsystem.theme.B2CAppTheme
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.StarRatingGold
import com.example.b2capp.core.designsystem.theme.TextPrimary
import java.util.Locale

/**
 * Thanh đánh giá sao (StarRatingBar):
 * - Hiển thị 5 ngôi sao màu #FFB800
 * - Hỗ trợ 2 chế độ:
 *   1. Chế độ chỉ hiển thị (Read-only): onRatingChanged = null, hỗ trợ sao chẵn, sao nửa và hiển thị số điểm (ví dụ: 4.8)
 *   2. Chế độ tương tác (Interactive): người dùng chạm vào sao từ 1 đến 5 để đánh giá sản phẩm
 */
@Composable
fun StarRatingBar(
    rating: Float,
    modifier: Modifier = Modifier,
    onRatingChanged: ((Int) -> Unit)? = null,
    maxStars: Int = 5,
    starSize: Dp = 18.dp,
    showRatingText: Boolean = true,
    testTag: String = "star_rating_bar"
) {
    val isInteractive = onRatingChanged != null

    Row(
        modifier = modifier.testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        for (i in 1..maxStars) {
            val starIndex = i
            val icon = when {
                rating >= starIndex -> Icons.Filled.Star
                rating >= starIndex - 0.5f -> Icons.Filled.StarHalf
                else -> Icons.Outlined.StarOutline
            }

            val tintColor = if (rating >= starIndex - 0.5f) {
                StarRatingGold
            } else {
                BorderDivider
            }

            val itemModifier = if (isInteractive) {
                Modifier
                    .size(starSize)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false, radius = starSize),
                        onClick = { onRatingChanged?.invoke(starIndex) }
                    )
                    .testTag("${testTag}_star_$starIndex")
            } else {
                Modifier
                    .size(starSize)
                    .testTag("${testTag}_star_$starIndex")
            }

            Icon(
                imageVector = icon,
                contentDescription = "Sao thứ $starIndex",
                tint = tintColor,
                modifier = itemModifier
            )
        }

        if (showRatingText) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format(Locale.US, "%.1f", rating),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.testTag("${testTag}_score_text")
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StarRatingBarPreview() {
    var userRating by remember { mutableFloatStateOf(4.0f) }

    B2CAppTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Chế độ chỉ hiển thị (Read-only):", fontWeight = FontWeight.SemiBold)
            StarRatingBar(
                rating = 4.8f,
                onRatingChanged = null,
                starSize = 20.dp,
                showRatingText = true
            )

            Text("Chế độ tương tác chạm để chấm sao:", fontWeight = FontWeight.SemiBold)
            StarRatingBar(
                rating = userRating,
                onRatingChanged = { newRating -> userRating = newRating.toFloat() },
                starSize = 32.dp,
                showRatingText = true
            )
        }
    }
}
