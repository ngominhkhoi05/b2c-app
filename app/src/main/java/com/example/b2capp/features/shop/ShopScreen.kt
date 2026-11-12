package com.example.b2capp.features.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.b2capp.core.designsystem.theme.*
import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.core.model.ShopInfo
import com.example.b2capp.core.ui.components.ProductCard
import com.example.b2capp.viewmodel.B2CUiState

/**
 * Các tiêu chí sắp xếp sản phẩm trên trang Shop
 */
private enum class ShopSortOption(val title: String) {
    NEWEST("Mới nhất"),
    BEST_SELLER("Bán chạy"),
    PRICE_ASC("Giá tăng dần"),
    PRICE_DESC("Giá giảm dần"),
    TOP_RATED("Đánh giá cao")
}

/**
 * Màn hình trang chủ của Shop / Gian hàng (Đã kết nối hoàn toàn API Backend thực tế):
 * - Hiển thị thông tin tổng quan của Shop (Logo, Tên, Mall, Vị trí, Đánh giá, Nút Chat ngang hàng).
 * - Bộ lọc & sắp xếp sản phẩm linh hoạt.
 * - Danh sách sản phẩm của Shop (dạng lưới 2 cột sử dụng ProductCard từ API backend).
 * - Click vào sản phẩm mở trang chi tiết sản phẩm.
 */
@Composable
fun ShopScreen(
    shopInfo: ShopInfo,
    uiState: B2CUiState,
    onProductClick: (ProductItem) -> Unit,
    onChatClick: () -> Unit,
    onBackClick: () -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    var selectedSort by remember { mutableStateOf(ShopSortOption.NEWEST) }

    // Sắp xếp danh sách sản phẩm thực tế theo tiêu chí người dùng chọn
    val sortedProducts = remember(uiState.shopProducts, selectedSort) {
        when (selectedSort) {
            ShopSortOption.NEWEST -> uiState.shopProducts
            ShopSortOption.BEST_SELLER -> uiState.shopProducts.sortedByDescending { it.soldCount }
            ShopSortOption.PRICE_ASC -> uiState.shopProducts.sortedBy { it.price }
            ShopSortOption.PRICE_DESC -> uiState.shopProducts.sortedByDescending { it.price }
            ShopSortOption.TOP_RATED -> uiState.shopProducts.sortedByDescending { it.rating }
        }
    }

    Scaffold(
        containerColor = BackgroundApp
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Shop Header Card / Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF0E3B64), Color(0xFF1E5B94))
                        )
                    )
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Logo + Info Column
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val shopInitials = shopInfo.shopName.split(" ")
                                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                                .take(2)
                                .joinToString("")
                                .ifBlank { "SH" }

                            if (!shopInfo.shopLogoUrl.isNullOrBlank()) {
                                SubcomposeAsyncImage(
                                    model = shopInfo.shopLogoUrl,
                                    contentDescription = shopInfo.shopName,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color.White, CircleShape)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceCard)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = shopInitials,
                                        color = NavySecondary,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = shopInfo.shopName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (shopInfo.verified) {
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFB82414), RoundedCornerShape(3.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "Mall",
                                                color = Color.White,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color(0xFFA7F3D0),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = shopInfo.shopLocation,
                                        fontSize = 11.5.sp,
                                        color = Color(0xFFE5E7EB),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFC107),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${shopInfo.rating}",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Right: Chat Shop Button
                        Button(
                            onClick = onChatClick,
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Chat",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Stat Metrics: Sản phẩm, Người theo dõi, Phản hồi, Tốc độ
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ShopStatHeaderItem(
                            label = "Sản phẩm",
                            value = "${if (uiState.shopProducts.isNotEmpty()) uiState.shopProducts.size else shopInfo.productCount}"
                        )
                        ShopStatDivider()
                        ShopStatHeaderItem(
                            label = "Người theo dõi",
                            value = shopInfo.followerCount
                        )
                        ShopStatDivider()
                        ShopStatHeaderItem(
                            label = "Phản hồi Chat",
                            value = shopInfo.responseRate
                        )
                        ShopStatDivider()
                        ShopStatHeaderItem(
                            label = "Thời gian",
                            value = shopInfo.responseTime
                        )
                    }
                }
            }

            // 2. Section Title & Sắp xếp
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceCard,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TẤT CẢ SẢN PHẨM (${uiState.shopProducts.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavySecondary
                        )

                        Text(
                            text = selectedSort.title,
                            fontSize = 12.sp,
                            color = CoralPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sort Chips Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ShopSortOption.values().take(4).forEach { option ->
                            val isSelected = option == selectedSort
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedSort = option },
                                label = {
                                    Text(
                                        text = option.title,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CoralPrimary.copy(alpha = 0.12f),
                                    selectedLabelColor = CoralPrimary,
                                    containerColor = BackgroundApp,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isSelected) CoralPrimary else Color(0xFFE5E7EB),
                                    selectedBorderColor = CoralPrimary,
                                    enabled = true,
                                    selected = isSelected
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Product Grid hoặc Trạng thái tải / trống
            if (uiState.isShopProductsLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = CoralPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Đang tải sản phẩm từ gian hàng...",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else if (sortedProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Storefront,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Shop hiện chưa có sản phẩm nào",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Vui lòng quay lại sau hoặc liên hệ shop qua tin nhắn.",
                            fontSize = 12.5.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = onRefresh,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Làm mới")
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp)
                ) {
                    items(sortedProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShopStatHeaderItem(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun ShopStatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(22.dp)
            .background(Color.White.copy(alpha = 0.25f))
    )
}
