package com.example.b2capp.features.product

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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.StarRatingGold
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel

/**
 * Màn hình Viết Đánh Giá Sản Phẩm (WriteProductReviewScreen)
 */
@Composable
fun WriteProductReviewScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var generalRating by remember { mutableIntStateOf(5) }

    var reviewText by remember {
        mutableStateOf(
            "Chất vải cotton dày dặn, mặc rất thoáng mát và thấm hút mồ hôi tốt. Form áo regular fit chuẩn size L tôn dáng, đường may tỉ mỉ không chỉ thừa. Giao hàng nhanh đóng gói hộp 2 lớp rất chỉn chu."
        )
    }

    // Danh sách ảnh đã đính kèm
    val attachedImages = remember {
        mutableStateListOf(
            R.drawable.folded_black_tshirt_1793956682242,
            R.drawable.tshirt_black_streetwear_1793958196938
        )
    }

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
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
            }
        },
        bottomBar = {
            Surface(
                color = SurfaceCard,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.setInteractiveRating(generalRating)
                            viewModel.showToast("Cảm ơn bạn đã gửi đánh giá thành công!")
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
                                imageVector = Icons.AutoMirrored.Filled.Send,
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
            // 1. Thẻ Đơn Hàng Đánh Giá
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Đánh giá đơn hàng",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

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

            // 2. Thẻ Đánh giá chất lượng
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
                    }
                }
            }

            // 3. Thẻ NỘI DUNG CHI TIẾT (Nhận xét)
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
                                .height(120.dp)
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
                    }
                }
            }

            // 4. Thẻ HÌNH ẢNH THỰC TẾ
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
                                text = "Hình ảnh thực tế",
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

                        // Hàng các Tile đính kèm ảnh
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Tile: Thêm ảnh
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
        }
    }
}
