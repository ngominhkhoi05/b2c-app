package com.example.b2capp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.StarRatingGold
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Màn hình Đánh Giá Sản Phẩm (Product Reviews Screen)
 * Thiết kế chính xác 100% theo ProductReviewsScreen_5efc9fd4.png:
 * 1. Top Bar: Back icon, Title "Product Variant Selection", Share icon, User Red Avatar.
 * 2. Thẻ Đánh giá Tổng Quan:
 *    - Điểm trung bình 4.9 / 5 với 5 sao vàng, "342 đánh giá", nhãn xanh "98% hài lòng".
 *    - Bảng phân bổ 5 Sao (310), 4 Sao (25), 3 Sao (5), 2 Sao (2), 1 Sao (0).
 * 3. Thanh Bộ lọc Đánh giá (cuộn ngang):
 *    - "Tất cả (342)" (nền đỏ chữ trắng khi chọn)
 *    - "★ 5 Sao (310)"
 *    - "★ 4 Sao (25)"
 *    - "📷 Có hình ảnh (128)"
 * 4. Danh sách các Đánh giá Chi tiết từ người mua đã xác thực:
 *    - N nguyen***a (Đã mua hàng, 12/04/2026, 5 Sao, Đen Size L, kèm 2 ảnh thật, Nút Hữu ích 14, Phản hồi từ Người bán).
 *    - H hoang_minh_92 (Đã mua hàng, 10/04/2026, 5 Sao, Trắng Tinh Khôi Size L, kèm 2 ảnh thật, Nút Hữu ích 8).
 *    - T tran***8 (Đã mua hàng, 05/04/2026, 4 Sao, Xanh Navy Size XL, Nút Hữu ích 3, Phản hồi từ Người bán).
 * 5. Footer: "Đã hiển thị tất cả 342 đánh giá", "Được kiểm duyệt tự động & xác thực người mua bởi hệ thống".
 */

data class ReviewData(
    val id: String,
    val initial: String,
    val avatarBgColor: Color,
    val username: String,
    val isPurchased: Boolean = true,
    val date: String,
    val rating: Int,
    val variantInfo: String,
    val comment: String,
    val images: List<Int> = emptyList(),
    var helpfulCount: Int,
    val sellerResponse: String? = null
)

@Composable
fun ProductReviewsScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("Tất cả") }

    // Dữ liệu mẫu các bài đánh giá chuẩn 100% theo ảnh
    val allReviews = remember {
        listOf(
            ReviewData(
                id = "rev_1",
                initial = "N",
                avatarBgColor = Color(0xFFFFD4C1),
                username = "nguyen***a",
                date = "12/04/2026",
                rating = 5,
                variantInfo = "Phân loại: Đen, Size L",
                comment = "Chất vải cotton dày dặn, form áo mặc rất vừa vặn và thoáng mát. Giao hàng cực nhanh, đóng gói cẩn thận 2 lớp!",
                images = listOf(
                    R.drawable.folded_black_tshirt_1793956682242,
                    R.drawable.tshirt_black_streetwear_1793958196938
                ),
                helpfulCount = 14,
                sellerResponse = "Dạ Coolmate cảm ơn bạn đã tin tưởng và đánh giá 5 sao cho sản phẩm ạ! Chúc bạn có trải nghiệm thật tuyệt vời cùng chiếc áo mới nhé!"
            ),
            ReviewData(
                id = "rev_2",
                initial = "H",
                avatarBgColor = Color(0xFFD6E4FF),
                username = "hoang_minh_92",
                date = "10/04/2026",
                rating = 5,
                variantInfo = "Phân loại: Trắng Tinh Khôi, Size L",
                comment = "Áo đẹp xuất sắc, chất vải cotton compact mềm mát cực kỳ, form áo chuẩn regular mặc lên tôn dáng lắm. Sẽ ủng hộ shop thêm màu xanh navy nữa!",
                images = listOf(
                    R.drawable.fashion_hot_deal_model_1793954934603,
                    R.drawable.product_white_tshirt_1793955938236
                ),
                helpfulCount = 8,
                sellerResponse = null
            ),
            ReviewData(
                id = "rev_3",
                initial = "T",
                avatarBgColor = Color(0xFFA7F3D0),
                username = "tran***8",
                date = "05/04/2026",
                rating = 4,
                variantInfo = "Phân loại: Xanh Navy, Size XL",
                comment = "Chất lượng áo tốt so với tầm giá, đường may chỉnh chu. Giao hàng hơi chậm hơn dự kiến 1 ngày do mưa bão nhưng shop hỗ trợ nhiệt tình.",
                images = emptyList(),
                helpfulCount = 3,
                sellerResponse = "Dạ shop cảm ơn bạn đã góp ý ạ, shop sẽ cải thiện tốc độ vận chuyển tốt hơn trong các đơn tiếp theo ạ!"
            )
        )
    }

    // Trạng thái nút Hữu ích từng đánh giá
    val helpfulStateMap = remember { mutableStateMapOf<String, Boolean>() }
    val helpfulCountsMap = remember {
        mutableStateMapOf<String, Int>().apply {
            allReviews.forEach { put(it.id, it.helpfulCount) }
        }
    }

    val filterOptions = listOf(
        "Tất cả (342)",
        "★ 5 Sao (310)",
        "★ 4 Sao (25)",
        "📷 Có hình ảnh (128)"
    )

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
                        text = "Product Variant Selection",
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
                                        text = "4.9",
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

                                // 5 Sao vàng
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    repeat(5) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = StarRatingGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "342 đánh giá",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "98% hài lòng",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Cột bên phải: Thanh tiến trình 5 sao -> 1 sao
                            Column(
                                modifier = Modifier.weight(0.58f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                RatingBarRow(starCount = 5, progress = 0.90f, countText = "310")
                                RatingBarRow(starCount = 4, progress = 0.15f, countText = "25")
                                RatingBarRow(starCount = 3, progress = 0.05f, countText = "5")
                                RatingBarRow(starCount = 2, progress = 0.02f, countText = "2")
                                RatingBarRow(starCount = 1, progress = 0.00f, countText = "0")
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

            // 4. Danh sách các Bài Đánh giá
            items(allReviews) { review ->
                ReviewItemCard(
                    review = review,
                    helpfulCount = helpfulCountsMap[review.id] ?: review.helpfulCount,
                    isHelpfulClicked = helpfulStateMap[review.id] ?: false,
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
                        text = "Đã hiển thị tất cả 342 đánh giá",
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
                fontWeight = FontWeight.SemiBold,
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

        Box(
            modifier = Modifier
                .weight(1f)
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFE5E7EB))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(7.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (progress > 0) StarRatingGold else Color.Transparent)
            )
        }

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
    review: ReviewData,
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

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = review.variantInfo,
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Nội dung comment bài đánh giá
            Text(
                text = review.comment,
                fontSize = 13.5.sp,
                color = TextPrimary,
                lineHeight = 19.sp
            )

            // Ảnh thực tế đính kèm (nếu có)
            if (review.images.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    review.images.forEach { imgRes ->
                        Image(
                            painter = painterResource(id = imgRes),
                            contentDescription = "Ảnh thực tế đánh giá",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp))
                        )
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
            if (review.sellerResponse != null) {
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
