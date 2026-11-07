package com.example.b2capp.features.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.StarRatingGold
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary

/**
 * Màn hình Viết Đánh Giá Sản Phẩm (WriteProductReviewScreen)
 * Thiết kế chính xác 100% theo WriteProductReviewScreen_9db117fe.png:
 * 1. Top Bar: Back icon, Title "Product Variant Selection", Share icon, Avatar tài khoản đỏ.
 * 2. Thẻ Đơn Hàng Đánh Giá:
 *    - Đánh giá đơn hàng + Tag "+200 Xu" + Link "Hướng dẫn".
 *    - Thumbnail Áo thun đen Coolmate, Mall Coolmate Official Store, Đen / Size L, ₫199.000.
 * 3. Thẻ ĐÁNH GIÁ CHUNG:
 *    - "Chất lượng sản phẩm thế nào?"
 *    - 5 Sao vàng lớn tương tác + Nhãn "✔ Tuyệt vời" (nền xanh ngọc).
 *    - 3 Tiêu chí: Đúng với mô tả (5 sao), Chất lượng vải (5 sao), Dịch vụ giao hàng (5 sao).
 *    - Bảng chip gợi ý nhanh (+ Chất vải dày dặn, + Form dáng chuẩn, + Giao hàng nhanh, + Đóng gói cẩn thận).
 * 4. Thẻ NỘI DUNG CHI TIẾT:
 *    - Đếm ký tự 191/500.
 *    - Khung nhập nhận xét nhiều dòng.
 *    - Banner thưởng: "Viết đánh giá từ 50 ký tự kèm 1 hình ảnh hoặc video để nhận ngay +200 Xu thưởng".
 * 5. Thẻ HÌNH ẢNH & VIDEO THỰC TẾ:
 *    - Đếm 2/5 tệp.
 *    - Thẻ "+ Thêm ảnh", "+ Thêm video", Ảnh 1 (áo đen), Ảnh 2 (chất vải).
 * 6. Thẻ ĐÁNH GIÁ ẨN DANH:
 *    - Icon mắt ẩn, "Tên hiển thị dạng n***a...", Switch bật đỏ.
 * 7. Footer Tiêu chuẩn cộng đồng mua sắm.
 * 8. Sticky Bottom Bar:
 *    - Thưởng: +200 Xu B2C | ✔ Đủ điều kiện nhận xu (xanh lá).
 *    - Nút đỏ "GỬI ĐÁNH GIÁ" với icon máy bay gửi.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WriteProductReviewScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var generalRating by remember { mutableIntStateOf(5) }
    var descriptionRating by remember { mutableIntStateOf(5) }
    var fabricRating by remember { mutableIntStateOf(5) }
    var shippingRating by remember { mutableIntStateOf(5) }

    var reviewText by remember {
        mutableStateOf(
            "Chất vải cotton dày dặn, mặc rất thoáng mát và thấm hút mồ hôi tốt. Form áo regular fit chuẩn size L tôn dáng, đường may tỉ mỉ không chỉ thừa. Giao hàng nhanh đóng gói hộp 2 lớp rất chỉn"
        )
    }

    var isAnonymous by remember { mutableStateOf(true) }

    // Danh sách ảnh đã đính kèm
    val attachedImages = remember {
        mutableStateListOf(
            R.drawable.folded_black_tshirt_1793956682242,
            R.drawable.tshirt_black_streetwear_1793958196938
        )
    }

    val quickChips = listOf(
        "+ Chất vải dày dặn",
        "+ Form dáng chuẩn",
        "+ Giao hàng nhanh",
        "+ Đóng gói cẩn thận"
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("write_product_review_screen"),
        topBar = {
            Surface(
                color = SurfaceCard,
                shadowElevation = 0.5.dp,
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
                        IconButton(onClick = { viewModel.showToast("Chia sẻ biểu mẫu đánh giá") }) {
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
        },
        bottomBar = {
            // 8. Sticky Bottom Bar
            Surface(
                color = SurfaceCard,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🪙 Thưởng: ",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "+200 Xu B2C",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBA2211)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Đủ điều kiện nhận xu",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.setInteractiveRating(generalRating)
                            viewModel.showToast("Cảm ơn bạn đã gửi đánh giá thành công! +200 Xu B2C đã được cộng.")
                            onBackClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA2211))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = "Gửi",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GỬI ĐÁNH GIÁ",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA)),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 2. Thẻ Đơn Hàng Đánh Giá
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Đánh giá đơn hàng",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFFFF3F0), RoundedCornerShape(4.dp))
                                        .border(0.6.dp, Color(0xFFFDE8E4), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "+200 Xu",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFBA2211)
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    viewModel.showToast("Quy định tính xu thưởng khi viết đánh giá")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.HelpOutline,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Hướng dẫn",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Khung Sản phẩm
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF8F9FA))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.folded_black_tshirt_1793956682242),
                                contentDescription = "Áo Thun Nam",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp))
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFB82414), RoundedCornerShape(3.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "Mall",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Coolmate Official Store",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavySecondary,
                                        maxLines = 1
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "Áo Thun Nam Cotton Compact Siêu...",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    maxLines = 1
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "Phân loại: Đen / Size L",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "₫199.000",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            // 3. Thẻ ĐÁNH GIÁ CHUNG
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ĐÁNH GIÁ CHUNG",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Chất lượng sản phẩm thế nào?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 5 Sao Vàng Tương Tác Lớn
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (starIndex in 1..5) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "$starIndex sao",
                                    tint = if (starIndex <= generalRating) StarRatingGold else Color(0xFFE5E7EB),
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clickable { generalRating = starIndex }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Nhãn cảm nhận
                        val feelingText = when (generalRating) {
                            5 -> "Tuyệt vời"
                            4 -> "Hài lòng"
                            3 -> "Bình thường"
                            2 -> "Không hài lòng"
                            else -> "Rất tệ"
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFE6F7F5))
                                .padding(horizontal = 14.dp, vertical = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF1E8275),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = feelingText,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E8275)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 0.8.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // 3 Tiêu chí chi tiết
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CriteriaRatingRow(
                                title = "Đúng với mô tả",
                                rating = descriptionRating,
                                onRatingChange = { descriptionRating = it }
                            )

                            CriteriaRatingRow(
                                title = "Chất lượng vải",
                                rating = fabricRating,
                                onRatingChange = { fabricRating = it }
                            )

                            CriteriaRatingRow(
                                title = "Dịch vụ giao hàng",
                                rating = shippingRating,
                                onRatingChange = { shippingRating = it }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Wrap row các Chip gợi ý nhanh
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            quickChips.forEach { chipText ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFF3F4F6))
                                        .clickable {
                                            val chipClean = chipText.replace("+ ", "")
                                            if (!reviewText.contains(chipClean)) {
                                                reviewText = if (reviewText.isBlank()) chipClean else "$reviewText $chipClean."
                                            }
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = chipText,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF374151)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Thẻ NỘI DUNG CHI TIẾT (Nhận xét)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Nội dung chi tiết",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Text(
                                text = "${reviewText.length}/500 ký tự",
                                fontSize = 11.5.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Box nhận xét
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF3F4F6))
                                .padding(12.dp)
                        ) {
                            BasicTextField(
                                value = reviewText,
                                onValueChange = { if (it.length <= 500) reviewText = it },
                                modifier = Modifier.fillMaxSize(),
                                textStyle = TextStyle(
                                    fontSize = 13.5.sp,
                                    color = TextPrimary,
                                    lineHeight = 19.sp
                                ),
                                cursorBrush = SolidColor(Color(0xFFBA2211)),
                                decorationBox = { innerTextField ->
                                    if (reviewText.isEmpty()) {
                                        Text(
                                            text = "Hãy chia sẻ nhận xét chi tiết về sản phẩm này để giúp những người mua khác nhé...",
                                            fontSize = 13.sp,
                                            color = Color(0xFF9CA3AF)
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Banner Thưởng +200 Xu
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFF3F0))
                                .border(0.8.dp, Color(0xFFFDE8E4), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.CardGiftcard,
                                    contentDescription = null,
                                    tint = Color(0xFFBA2211),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Viết đánh giá từ 50 ký tự kèm 1 hình ảnh hoặc video để nhận ngay +200 Xu thưởng.",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFFBA2211),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // 5. Thẻ HÌNH ẢNH & VIDEO THỰC TẾ
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Hình ảnh & Video thực tế",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Text(
                                text = "${attachedImages.size}/5 tệp",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Hàng các Tile đính kèm media
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Tile 1: Thêm ảnh
                            Box(
                                modifier = Modifier
                                    .size(width = 82.dp, height = 82.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFF3F0))
                                    .border(1.dp, Color(0xFFFDE8E4), RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.showToast("Mở thư viện chọn ảnh thực tế...")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Filled.CameraAlt,
                                        contentDescription = null,
                                        tint = Color(0xFFBA2211),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Thêm ảnh",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFBA2211)
                                    )
                                    Text(
                                        text = "Tối đa 5",
                                        fontSize = 9.5.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            // Tile 2: Thêm video
                            Box(
                                modifier = Modifier
                                    .size(width = 82.dp, height = 82.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF3F4F6))
                                    .clickable {
                                        viewModel.showToast("Mở camera quay video sản phẩm (+100 Xu)...")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Filled.Videocam,
                                        contentDescription = null,
                                        tint = Color(0xFF1D4ED8),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Thêm video",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D4ED8)
                                    )
                                    Text(
                                        text = "+100 Xu",
                                        fontSize = 9.5.sp,
                                        color = Color(0xFF15803D),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Loop các ảnh đã tải lên
                            attachedImages.forEachIndexed { index, imgRes ->
                                Box(
                                    modifier = Modifier
                                        .size(width = 82.dp, height = 82.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                                ) {
                                    Image(
                                        painter = painterResource(id = imgRes),
                                        contentDescription = "Ảnh đính kèm",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    // Tag góc dưới: Ảnh 1 / Ảnh 2
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .fillMaxWidth()
                                            .background(Color.Black.copy(alpha = 0.5f))
                                            .padding(vertical = 2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Ảnh ${index + 1}",
                                            fontSize = 10.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Nút x xóa ảnh ở góc trên phải
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(18.dp)
                                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                            .clickable { attachedImages.removeAt(index) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Clear,
                                            contentDescription = "Xóa",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 6. Thẻ ĐÁNH GIÁ ẨN DANH
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0EDFF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color(0xFF1D4ED8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Đánh giá ẩn danh",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Tên hiển thị dạng n***a để bảo vệ danh tính riêng tư của bạn.",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Switch(
                            checked = isAnonymous,
                            onCheckedChange = { isAnonymous = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFFBA2211)
                            )
                        )
                    }
                }
            }

            // 7. Subtext Tiêu chuẩn cộng đồng
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Đánh giá tuân thủ Tiêu chuẩn cộng đồng mua sắm",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun CriteriaRatingRow(
    title: String,
    rating: Int,
    onRatingChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (starIndex in 1..5) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (starIndex <= rating) StarRatingGold else Color(0xFFE5E7EB),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onRatingChange(starIndex) }
                )
            }
        }
    }
}
