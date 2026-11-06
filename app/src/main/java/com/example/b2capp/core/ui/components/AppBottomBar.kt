package com.example.b2capp.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.B2CAppTheme
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Dữ liệu cho từng tab trong Bottom Navigation Bar
 */
data class BottomNavItem(
    val id: Int,
    val title: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
)

/**
 * Thanh BottomBar chuẩn xác theo thiết kế đính kèm:
 * - Nền trắng (#FFFFFF) kèm viền mờ 1.dp và đổ bóng nhẹ phía trên
 * - 5 tabs: Trang chủ, Danh mục, Giỏ hàng (badge đỏ), Đơn hàng, Tài khoản
 * - Icon mảnh thanh lịch, nhãn 11sp
 * - Xử lý navigationBarsPadding tránh bị che bởi thanh cử chỉ của thiết bị
 */
@Composable
fun AppBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    cartBadgeCount: Int = 3,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        BottomNavItem(0, "Trang chủ", Icons.Outlined.Home),
        BottomNavItem(1, "Danh mục", Icons.Outlined.GridView),
        BottomNavItem(2, "Giỏ hàng", Icons.Outlined.ShoppingCart, badgeCount = cartBadgeCount),
        BottomNavItem(3, "Đơn mua", Icons.Outlined.ReceiptLong),
        BottomNavItem(4, "Tài khoản", Icons.Outlined.AccountCircle)
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                spotColor = Color.Black.copy(alpha = 0.08f),
                ambientColor = Color.Black.copy(alpha = 0.04f)
            )
            .border(width = 0.8.dp, color = BorderDivider.copy(alpha = 0.7f)),
        color = SurfaceCard,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(60.dp)
                .testTag("app_bottom_bar"),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = selectedTab == item.id

                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) CoralPrimary else Color(0xFF6E6E6E),
                    animationSpec = tween(durationMillis = 200),
                    label = "iconColor"
                )

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) CoralPrimary else Color(0xFF6E6E6E),
                    animationSpec = tween(durationMillis = 200),
                    label = "textColor"
                )

                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = ripple(bounded = false, radius = 28.dp),
                            onClick = { onTabSelected(item.id) }
                        )
                        .testTag("bottom_nav_tab_${item.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Icon kèm Badge (nếu có badgeCount > 0)
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = iconColor,
                                modifier = Modifier
                                    .padding(top = 2.dp, end = 2.dp)
                                    .size(24.dp)
                            )

                            if (item.badgeCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .offset(x = 10.dp, y = (-5).dp)
                                        .size(17.dp)
                                        .background(
                                            color = Color(0xFFD32F2F), // Màu đỏ chuẩn theo mẫu ảnh
                                            shape = CircleShape
                                        )
                                        .border(width = 1.dp, color = Color.White, shape = CircleShape)
                                        .testTag("cart_badge_${item.badgeCount}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (item.badgeCount > 99) "99+" else item.badgeCount.toString(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 10.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = item.title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = textColor,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppBottomBarPreview() {
    B2CAppTheme {
        AppBottomBar(
            selectedTab = 0,
            onTabSelected = {},
            cartBadgeCount = 3
        )
    }
}
