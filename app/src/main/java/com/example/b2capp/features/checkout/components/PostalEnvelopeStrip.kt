package com.example.b2capp.features.checkout.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary

/**
 * Viền phong bì bưu điện trang trí với các vạch chéo đỏ và xanh Navy xen kẽ
 */
@Composable
fun PostalEnvelopeStrip() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(3.5.dp)
    ) {
        repeat(16) { index ->
            val color = if (index % 2 == 0) CoralPrimary else NavySecondary
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(color)
            )
            if (index < 15) {
                Spacer(modifier = Modifier.width(2.5.dp))
            }
        }
    }
}

