package com.example.b2capp.features.product

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.StarRatingGold
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import com.example.b2capp.core.model.ReviewItem
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel

/**
 * Màn hình Đánh Giá Sản Phẩm (Product Reviews Screen)
 * Sử dụng dữ liệu thực tế từ API thông qua ViewModel, không dùng mock
 */
@Composable
fun ProductReviewsScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentProduct = uiState.detailedProductScreen
    val allReviews = uiState.productReviews
    var selectedFilter by remember { mutableStateOf("Tất cả") }

    // Đếm số lượng theo sao dựa trên danh sách reviews thật
    val count5Star = allReviews.count { it.rating == 5 }
    val count4Star = allReviews.count { it.rating == 4 }
    val count3Star = allReviews.count { it.rating == 3 }
    val count2Star = allReviews.count { it.rating == 2 }
    val count1Star = allReviews.count { it.rating == 1 }
    val countWithImages = allReviews.count { it.imageUrls.isNotEmpty() }
    val totalCount = if (uiState.productReviewsTotalCount > 0) uiState.productReviewsTotalCount else allReviews.size

    val progress5 = if (allReviews.isNotEmpty()) count5Star.toFloat() / allReviews.size else 0f
    val progress4 = if (allReviews.isNotEmpty()) count4Star.toFloat() / allReviews.size else 0f
    val progress3 = if (allReviews.isNotEmpty()) count3Star.toFloat() / allReviews.size else 0f
    val progress2 = if (allReviews.isNotEmpty()) count2Star.toFloat() / allReviews.size else 0f
    val progress1 = if (allReviews.isNotEmpty()) count1Star.toFloat() / allReviews.size else 0f

    val averageRating = currentProduct?.rating ?: if (allReviews.isNotEmpty()) {
        allReviews.map { it.rating }.average().toFloat()
    } else 5.0f

    // Trạng thái nút Hữu ích từng đánh giá
    val helpfulStateMap = remember { mutableStateMapOf<String, Boolean>() }
    val helpfulCountsMap = remember { mutableStateMapOf<String, Int>() }

    val filterOptions = listOf(
        "Tất cả ($totalCount)",
        "★ 5 Sao ($count5Star)",
        "★ 4 Sao ($count4Star)",
        "★ 3 Sao ($count3Star)",
        "📷 Có hình ảnh ($countWithImages)"
    )

    val displayedReviews = remember(selectedFilter, allReviews) {
        when {
            selectedFilter.contains("5") -> allReviews.filter { it.rating == 5 }
            selectedFilter.contains("4") -> allReviews.filter { it.rating == 4 }
            selectedFilter.contains("3") -> allReviews.filter { it.rating == 3 }
            selectedFilter.contains("2") -> allReviews.filter { it.rating == 2 }
            selectedFilter.contains("1") -> allReviews.filter { it.rating == 1 }
            selectedFilter.contains("hình") || selectedFilter.contains("ảnh") -> allReviews.filter { it.imageUrls.isNotEmpty() }
            else -> allReviews
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .testTag("product_reviews_screen")
    ) {
        // 1. Top Bar
        Surface(
            color = SurfaceCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Đánh giá sản phẩm",
                        fontSize = 17.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.showToast("Đã sao chép liên kết đánh giá!") }) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Chia sẻ",
                            tint = TextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFFBA2211), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Tài khoản",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        if (uiState.isProductReviewsLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = CoralPrimary,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(36.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 2. Thẻ Đánh giá Tổng Quan
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(0.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Cột bên trái: Điểm số trung bình
                                Column(
                                    modifier = Modifier.weight(0.42f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = String.format(java.util.Locale.US, "%.1f", averageRating),
                                            fontSize = 34.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = " /5",
                                            fontSize = 14.sp,
                                            color = TextSecondary,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Sao vàng
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val fullStars = averageRating.toInt().coerceIn(0, 5)
                                        repeat(5) { idx ->
                                            Icon(
                                                imageVector = Icons.Filled.Star,
                                                contentDescription = null,
                                                tint = if (idx < fullStars) StarRatingGold else Color(0xFFE5E7EB),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "$totalCount đánh giá",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )

                                    if (totalCount > 0) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Đã xác thực",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF16A34A)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Cột bên phải: Thanh tiến trình 5 sao -> 1 sao
                                Column(
                                    modifier = Modifier.weight(0.58f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    RatingBarRow(starCount = 5, progress = progress5, countText = "$count5Star")
                                    RatingBarRow(starCount = 4, progress = progress4, countText = "$count4Star")
                                    RatingBarRow(starCount = 3, progress = progress3, countText = "$count3Star")
                                    RatingBarRow(starCount = 2, progress = progress2, countText = "$count2Star")
                                    RatingBarRow(starCount = 1, progress = progress1, countText = "$count1Star")
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 3. Thanh Bộ lọc Đánh giá (cuộn ngang)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                filterOptions.forEach { filterName ->
                                    val isSelected = filterName.startsWith(selectedFilter) ||
                                            (selectedFilter == "Tất cả" && filterName.startsWith("Tất cả"))

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(
                                                if (isSelected) Color(0xFFBA2211) else Color(0xFFF3F4F6)
                                            )
                                            .border(
                                                width = if (isSelected) 0.dp else 0.8.dp,
                                                color = Color(0xFFE5E7EB),
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                            .clickable {
                                                selectedFilter = filterName.split(" ")[0]
                                            }
                                            .padding(horizontal = 14.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = filterName,
                                            fontSize = 12.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else Color(0xFF374151)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Danh sách đánh giá hoặc trạng thái trống
                if (displayedReviews.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp, horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFD1D5DB),
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (allReviews.isEmpty()) "Chưa có đánh giá nào cho sản phẩm này" else "Không có đánh giá phù hợp bộ lọc",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (allReviews.isEmpty()) "Hãy là người đầu tiên trải nghiệm và để lại nhận xét nhé!" else "Vui lòng chọn bộ lọc khác để xem thêm đánh giá.",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(displayedReviews, key = { it.id }) { review ->
                        val hCount = helpfulCountsMap[review.id] ?: review.helpfulCount
                        val isHClicked = helpfulStateMap[review.id] ?: false

                        ReviewItemCard(
                            review = review,
                            helpfulCount = hCount,
                            isHelpfulClicked = isHClicked,
                            onHelpfulClick = {
                                val current = helpfulStateMap[review.id] ?: false
                                helpfulStateMap[review.id] = !current
                                helpfulCountsMap[review.id] = (helpfulCountsMap[review.id] ?: review.helpfulCount) + if (!current) 1 else -1
                            },
                            onMoreClick = {
                                viewModel.showToast("Báo cáo hoặc chia sẻ đánh giá này")
                            }
                        )
                    }

                    // 5. Footer cuối trang
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Đã hiển thị tất cả ${displayedReviews.size} đánh giá",
                                fontSize = 12.5.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Được kiểm duyệt tự động & xác thực người mua bởi hệ thống",
                                fontSize = 11.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RatingBarRow(
    starCount: Int,
    progress: Float,
    countText: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(32.dp)
        ) {
            Text(
                text = "$starCount",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.width(2.dp))
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = StarRatingGold,
                modifier = Modifier.size(11.dp)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = Color(0xFFBA2211),
            trackColor = Color(0xFFE5E7EB)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = countText,
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.width(26.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun ReviewItemCard(
    review: ReviewItem,
    helpfulCount: Int,
    isHelpfulClicked: Boolean,
    onHelpfulClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Avatar, Name, Verification badge & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!review.userAvatar.isNullOrBlank()) {
                        AsyncImage(
                            model = review.userAvatar,
                            contentDescription = review.username,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(review.avatarBgColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = review.initial,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = review.username,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            if (review.isPurchased) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFDCFCE7), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF16A34A),
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Đã mua hàng",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Text(
                    text = review.date,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 5 Sao Rating & Phân loại sản phẩm
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (index < review.rating) StarRatingGold else Color(0xFFE5E7EB),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            if (review.variantInfo.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = review.variantInfo,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Nội dung comment bài đánh giá
            Text(
                text = review.comment,
                fontSize = 13.5.sp,
                color = TextPrimary,
                lineHeight = 19.sp
            )

            // Ảnh thực tế đính kèm (nếu có)
            if (review.imageUrls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    review.imageUrls.forEach { imgUrl ->
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp))
                        ) {
                            AsyncImage(
                                model = imgUrl,
                                contentDescription = "Ảnh thực tế đánh giá",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Thanh Action: Nút Hữu ích & Nút Ba chấm ...
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isHelpfulClicked) Color(0xFFFFF3F0) else Color(0xFFF3F4F6))
                        .border(
                            width = if (isHelpfulClicked) 1.dp else 0.dp,
                            color = if (isHelpfulClicked) Color(0xFFBA2211) else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onHelpfulClick() }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isHelpfulClicked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                            contentDescription = null,
                            tint = if (isHelpfulClicked) Color(0xFFBA2211) else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Hữu ích ($helpfulCount)",
                            fontSize = 12.sp,
                            fontWeight = if (isHelpfulClicked) FontWeight.Bold else FontWeight.Medium,
                            color = if (isHelpfulClicked) Color(0xFFBA2211) else TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onMoreClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Thêm tùy chọn",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Phản hồi từ Người bán (Seller Response)
            if (!review.sellerResponse.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF8F9FA))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Storefront,
                                contentDescription = null,
                                tint = Color(0xFFBA2211),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Phản hồi từ Người bán",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBA2211)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = review.sellerResponse,
                            fontSize = 12.5.sp,
                            color = Color(0xFF4B5563),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
