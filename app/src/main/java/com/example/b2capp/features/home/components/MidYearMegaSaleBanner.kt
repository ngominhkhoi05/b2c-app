package com.example.b2capp.features.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalShipping
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.b2capp.data.repository.HomeBannerItem

/**
 * Banner Carousel Mid-Year Mega Sale hỗ trợ nạp dữ liệu động từ API Promotions
 */
@Composable
fun MidYearMegaSaleBanner(
    banners: List<HomeBannerItem> = emptyList(),
    onBannerClick: (HomeBannerItem) -> Unit = {}
) {
    var activeIndex by remember { mutableIntStateOf(0) }
    val currentBanner = if (banners.isNotEmpty()) banners[activeIndex.coerceIn(0, banners.lastIndex)] else null

    val tagText = currentBanner?.discountTag ?: "FREESHIP TOÀN QUỐC"
    val highlight = currentBanner?.highlightText ?: "MEGA SALE 50% OFF"
    val subtitle = currentBanner?.subtitle ?: "Siêu tiệc công nghệ & Thời trang hè bùng nổ hôm nay"
    val totalDots = if (banners.size > 1) minOf(banners.size, 5) else 3

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .aspectRatio(16f / 8.5f)
                .clickable {
                    currentBanner?.let { onBannerClick(it) }
                }
        ) {
            if (!currentBanner?.bannerImageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = currentBanner!!.bannerImageUrl,
                    contentDescription = currentBanner.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.mid_year_mega_sale_1793955341457),
                    contentDescription = "Mid-Year Mega Sale",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Dải mờ gradient phía dưới banner chứa thông tin Freeship & Mega Sale
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x660B233D),
                                Color(0xCC091E36)
                            )
                        )
                    )
                    .padding(14.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Column {
                    // Tag Freeship Toàn Quốc / Ưu đãi
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF26AA99), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocalShipping,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = tagText,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = highlight,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )

                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dấu chấm chỉ thị trang (Page Indicator Dots)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until totalDots) {
                if (i == activeIndex) {
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFBA2211))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD0D5DD))
                            .clickable {
                                if (i < banners.size) activeIndex = i
                            }
                    )
                }
                if (i < totalDots - 1) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }
}

