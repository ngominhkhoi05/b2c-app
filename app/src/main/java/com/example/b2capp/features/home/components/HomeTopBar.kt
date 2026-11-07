package com.example.b2capp.features.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary

/**
 * Top Bar màu trắng với ô tìm kiếm bo tròn nhẹ và icon Camera, Chat, Giỏ hàng
 */
@Composable
fun HomeTopBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit = {},
    cartBadgeCount: Int,
    onCartClick: () -> Unit,
    onChatClick: () -> Unit
) {
    Surface(
        color = SurfaceCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thanh tìm kiếm
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .background(color = Color(0xFFF0F2F5), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Tìm kiếm",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(19.dp)
                        .clickable { onSearchSubmit(searchQuery) }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Tìm sản phẩm, thương hiệu...",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 13.sp,
                            color = TextPrimary
                        ),
                        cursorBrush = SolidColor(CoralPrimary),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = { onSearchSubmit(searchQuery) }
                        )
                    )
                }

                // Icon máy ảnh chụp tìm kiếm
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = "Tìm kiếm ảnh",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { /* Mở camera */ }
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Icon Chat tin nhắn
            IconButton(
                onClick = onChatClick,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Tin nhắn",
                    tint = TextPrimary,
                    modifier = Modifier.size(23.dp)
                )
            }

            // Icon Giỏ hàng kèm Badge đỏ
            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(
                    onClick = onCartClick,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Giỏ hàng",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (cartBadgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .offset(x = 2.dp, y = (-2).dp)
                            .size(17.dp)
                            .background(Color(0xFFD32F2F), CircleShape)
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (cartBadgeCount > 99) "99+" else cartBadgeCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 10.sp
                        )
                    }
                }
            }
        }
    }
}

