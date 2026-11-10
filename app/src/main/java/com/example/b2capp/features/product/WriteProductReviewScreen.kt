package com.example.b2capp.features.product

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.R
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.StarRatingGold
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import com.example.b2capp.data.remote.dto.OrderItemDto
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import java.text.NumberFormat
import java.util.Locale

/**
 * Màn hình Viết Đánh Giá Sản Phẩm (WriteProductReviewScreen)
 * - Tích hợp gọi API Backend: POST /api/v1/buyer/reviews
 * - Hỗ trợ đánh giá từng sản phẩm trong đơn hàng đã hoàn tất
 * - Chọn số sao tương tác 1 - 5 sao
 * - Nhập nhận xét kèm gợi ý nhanh
 * - Tải ảnh đính kèm từ thư viện / URL
 * - Tùy chọn đánh giá ẩn danh
 */
@Composable
fun WriteProductReviewScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale("vi", "VN")) }

    // Dữ liệu đơn hàng và sản phẩm mục tiêu
    val targetOrder = uiState.reviewTargetOrder
    val targetSummary = uiState.reviewTargetSummary
    val items = targetOrder?.items ?: emptyList()
    val selectedItem = uiState.selectedReviewOrderItem ?: items.firstOrNull()

    // Trạng thái form
    var generalRating by remember { mutableIntStateOf(5) }
    var reviewText by remember { mutableStateOf("") }
    var isAnonymous by remember { mutableStateOf(false) }

    // Danh sách URL ảnh thực tế đính kèm
    val attachedImageUrls = remember { mutableStateListOf<String>() }

    // Dialog dán URL ảnh / chọn ảnh mẫu
    var showAddUrlDialog by remember { mutableStateOf(false) }
    var inputImageUrl by remember { mutableStateOf("") }

    // Launcher chọn ảnh từ máy điện thoại Android
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        uris.take(5 - attachedImageUrls.size).forEach { uri ->
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                if (bytes != null) {
                    val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
                    viewModel.uploadReviewImage(
                        bytes = bytes,
                        mimeType = mime,
                        fileName = "review_${System.currentTimeMillis()}.jpg"
                    ) { uploadedUrl ->
                        if (uploadedUrl.isNotBlank() && !attachedImageUrls.contains(uploadedUrl)) {
                            attachedImageUrls.add(uploadedUrl)
                        }
                    }
                }
            } catch (e: Exception) {
                viewModel.showErrorToast("Không thể đọc ảnh: ${e.message}")
            }
        }
    }

    // Khi đổi sản phẩm đang chọn: reset lại form nếu chuyển sang item khác
    LaunchedEffect(selectedItem?.id) {
        reviewText = ""
        generalRating = 5
        attachedImageUrls.clear()
        isAnonymous = false
    }

    // Tên gian hàng đại diện
    val shopName = targetSummary?.shopName
        ?: targetOrder?.items?.firstOrNull()?.productName?.let { "Gian hàng chính hãng" }
        ?: "Gian hàng Shopee/B2C"

    val isCurrentItemReviewed = selectedItem?.isReviewed == true
    val allItemsReviewed = items.isNotEmpty() && items.all { it.isReviewed == true }

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
                        .padding(horizontal = 8.dp, vertical = 10.dp),
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

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Đánh giá sản phẩm",
                            fontSize = 17.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        val orderCode = targetOrder?.orderCode ?: targetSummary?.orderCode
                        if (!orderCode.isNullOrBlank()) {
                            Text(
                                text = "Đơn hàng: #$orderCode",
                                fontSize = 11.5.sp,
                                color = TextSecondary
                            )
                        }
                    }
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
                    val canSubmit = !uiState.isSubmittingReview &&
                            !uiState.isReviewOrderLoading &&
                            !isCurrentItemReviewed &&
                            selectedItem?.id != null

                    Button(
                        onClick = {
                            if (isCurrentItemReviewed) {
                                viewModel.showErrorToast("Sản phẩm này đã được đánh giá rồi")
                                return@Button
                            }
                            val orderItemId = selectedItem?.id
                            if (orderItemId == null) {
                                viewModel.showErrorToast("Không tìm thấy mã sản phẩm để đánh giá")
                                return@Button
                            }
                            if (reviewText.trim().isBlank()) {
                                viewModel.showErrorToast("Vui lòng nhập nội dung đánh giá sản phẩm")
                                return@Button
                            }

                            viewModel.submitProductReview(
                                orderItemId = orderItemId,
                                rating = generalRating,
                                comment = reviewText,
                                imageUrls = attachedImageUrls.toList(),
                                isAnonymous = isAnonymous,
                                onSuccess = {
                                    // Kiểm tra xem đơn hàng còn item nào chưa đánh giá không
                                    val remainingUnreviewed = targetOrder?.items?.filter {
                                        it.id != orderItemId && it.isReviewed != true
                                    } ?: emptyList()

                                    if (remainingUnreviewed.isEmpty()) {
                                        // Đã đánh giá hết các sản phẩm trong đơn -> quay về
                                        onBackClick()
                                    }
                                }
                            )
                        },
                        enabled = canSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFBA2211),
                            disabledContainerColor = if (isCurrentItemReviewed) Color(0xFF4CAF50) else Color(0xFFCBD5E1)
                        )
                    ) {
                        if (uiState.isSubmittingReview) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ĐANG GỬI...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else if (isCurrentItemReviewed) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SẢN PHẨM ĐÃ ĐƯỢC ĐÁNH GIÁ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else {
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
        }
    ) { innerPadding ->
        if (uiState.isReviewOrderLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF8F9FA)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = CoralPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Đang tải thông tin sản phẩm cần đánh giá...",
                        fontSize = 13.5.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF8F9FA)),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Banner thông báo hoàn thành tất cả đánh giá (nếu có)
                if (allItemsReviewed) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            border = BorderStroke(1.dp, Color(0xFFC8E6C9))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Bạn đã hoàn tất đánh giá cho tất cả sản phẩm trong đơn hàng này!",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                        }
                    }
                }

                // 1. Selector chọn sản phẩm nếu đơn hàng có nhiều hơn 1 sản phẩm
                if (items.size > 1) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Chọn sản phẩm trong đơn (${items.size} món):",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items.forEachIndexed { idx, itm ->
                                        val isSelected = itm.id == selectedItem?.id
                                        val isItemReviewed = itm.isReviewed == true

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    when {
                                                        isSelected -> Color(0xFFFFF0ED)
                                                        isItemReviewed -> Color(0xFFF1F8E9)
                                                        else -> Color(0xFFF3F4F6)
                                                    }
                                                )
                                                .border(
                                                    width = if (isSelected) 1.5.dp else 0.8.dp,
                                                    color = when {
                                                        isSelected -> CoralPrimary
                                                        isItemReviewed -> Color(0xFF81C784)
                                                        else -> Color(0xFFE5E7EB)
                                                    },
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable {
                                                    viewModel.selectReviewOrderItem(itm)
                                                }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "SP ${idx + 1}: ${itm.productName?.take(16) ?: "Sản phẩm"}...",
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) CoralPrimary else TextPrimary
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                if (isItemReviewed) {
                                                    Text(
                                                        text = "(Đã đánh giá)",
                                                        fontSize = 10.5.sp,
                                                        color = Color(0xFF2E7D32),
                                                        fontWeight = FontWeight.Medium
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

                // 2. Thẻ Đơn Hàng & Thông tin sản phẩm đang chọn
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Header shop
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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
                                        text = shopName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavySecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (isCurrentItemReviewed) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFE8F5E9))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "✓ Đã đánh giá",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Khung Sản phẩm
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF8F9FA))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val itemImageUrl = selectedItem?.imageUrl ?: targetSummary?.firstProductThumbnail
                                if (!itemImageUrl.isNullOrBlank()) {
                                    SubcomposeAsyncImage(
                                        model = itemImageUrl,
                                        contentDescription = selectedItem?.productName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(62.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp)),
                                        loading = {
                                            Box(
                                                modifier = Modifier
                                                    .size(62.dp)
                                                    .background(Color(0xFFF3F4F6)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(16.dp),
                                                    strokeWidth = 2.dp,
                                                    color = CoralPrimary
                                                )
                                            }
                                        },
                                        error = {
                                            Image(
                                                painter = painterResource(id = R.drawable.folded_black_tshirt_1793956682242),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.size(62.dp)
                                            )
                                        }
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.folded_black_tshirt_1793956682242),
                                        contentDescription = "Sản phẩm",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(62.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp))
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedItem?.productName
                                            ?: targetSummary?.firstProductName
                                            ?: "Sản phẩm chất lượng B2C",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    val variant = selectedItem?.variantName ?: targetSummary?.firstVariantName
                                    if (!variant.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Phân loại: $variant",
                                            fontSize = 11.5.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    val price = selectedItem?.unitPrice ?: targetSummary?.totalAmount ?: 0.0
                                    Text(
                                        text = "₫${currencyFormatter.format(price)}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }

                            if (isCurrentItemReviewed) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ghi chú: Sản phẩm này đã gửi đánh giá. Nếu đơn hàng còn sản phẩm khác, bạn hãy chọn ở trên để tiếp tục đánh giá.",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF2E7D32),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // 3. Thẻ Đánh giá chất lượng (Số sao tương tác)
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
                                fontSize = 15.5.sp,
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
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .clickable(enabled = !isCurrentItemReviewed) {
                                                generalRating = starIndex
                                            }
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
                                    .background(
                                        when (generalRating) {
                                            5, 4 -> Color(0xFFE6F7F5)
                                            3 -> Color(0xFFFFF8E1)
                                            else -> Color(0xFFFFEBEE)
                                        }
                                    )
                                    .padding(horizontal = 14.dp, vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = when (generalRating) {
                                            5, 4 -> Color(0xFF1E8275)
                                            3 -> Color(0xFFF57F17)
                                            else -> Color(0xFFD32F2F)
                                        },
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = feelingText,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (generalRating) {
                                            5, 4 -> Color(0xFF1E8275)
                                            3 -> Color(0xFFF57F17)
                                            else -> Color(0xFFD32F2F)
                                        }
                                    )
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
                                    text = "Nội dung nhận xét",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )

                                Text(
                                    text = "${reviewText.length}/500 ký tự",
                                    fontSize = 11.5.sp,
                                    color = if (reviewText.length > 450) CoralPrimary else Color(0xFF9CA3AF)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Gợi ý nhận xét nhanh
                            val suggestions = listOf(
                                "Chất lượng sản phẩm tuyệt vời",
                                "Đóng gói cẩn thận",
                                "Giao hàng rất nhanh",
                                "Đúng mô tả & hình ảnh",
                                "Shop phục vụ chu đáo"
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                suggestions.forEach { suggestion ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xFFF0F2F5))
                                            .border(0.6.dp, Color(0xFFE5E7EB), RoundedCornerShape(14.dp))
                                            .clickable(enabled = !isCurrentItemReviewed) {
                                                reviewText = if (reviewText.isBlank()) {
                                                    suggestion
                                                } else if (reviewText.contains(suggestion)) {
                                                    reviewText
                                                } else {
                                                    "$reviewText. $suggestion"
                                                }.take(500)
                                            }
                                            .padding(horizontal = 9.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "+ $suggestion",
                                            fontSize = 11.sp,
                                            color = NavySecondary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Ô nhập nhận xét
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
                                    onValueChange = {
                                        if (it.length <= 500 && !isCurrentItemReviewed) {
                                            reviewText = it
                                        }
                                    },
                                    enabled = !isCurrentItemReviewed,
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

                // 5. Thẻ HÌNH ẢNH THỰC TẾ
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
                                    text = "${attachedImageUrls.size}/5 tệp",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Nút: Chọn ảnh từ thư viện thiết bị
                                if (attachedImageUrls.size < 5 && !isCurrentItemReviewed) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 82.dp, height = 82.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFFFF3F0))
                                            .border(1.dp, Color(0xFFFDE8E4), RoundedCornerShape(8.dp))
                                            .clickable {
                                                galleryLauncher.launch("image/*")
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
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "Thêm ảnh",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFBA2211)
                                            )
                                            Text(
                                                text = "Từ máy",
                                                fontSize = 9.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    // Nút: Dán link URL ảnh hoặc ảnh mẫu
                                    Box(
                                        modifier = Modifier
                                            .size(width = 82.dp, height = 82.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF0F4F8))
                                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                            .clickable {
                                                inputImageUrl = ""
                                                showAddUrlDialog = true
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Filled.Link,
                                                contentDescription = null,
                                                tint = NavySecondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "Link URL",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NavySecondary
                                            )
                                            Text(
                                                text = "Ảnh mẫu",
                                                fontSize = 9.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }

                                // Trạng thái đang tải ảnh lên Cloudinary
                                if (uiState.isReviewImageUploading) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 82.dp, height = 82.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF3F4F6))
                                            .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                strokeWidth = 2.dp,
                                                color = CoralPrimary
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Đang tải...",
                                                fontSize = 9.5.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }

                                // Danh sách ảnh đã đính kèm
                                attachedImageUrls.forEachIndexed { index, imgUrl ->
                                    Box(
                                        modifier = Modifier
                                            .size(width = 82.dp, height = 82.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(0.8.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                                    ) {
                                        SubcomposeAsyncImage(
                                            model = imgUrl,
                                            contentDescription = "Ảnh đính kèm ${index + 1}",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize(),
                                            loading = {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(Color(0xFFF3F4F6)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(16.dp),
                                                        strokeWidth = 2.dp,
                                                        color = CoralPrimary
                                                    )
                                                }
                                            },
                                            error = {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(Color(0xFFFEE2E2)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.ErrorOutline,
                                                        contentDescription = null,
                                                        tint = Color(0xFFDC2626)
                                                    )
                                                }
                                            }
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
                                        if (!isCurrentItemReviewed) {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(3.dp)
                                                    .size(20.dp)
                                                    .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                                                    .clickable { attachedImageUrls.removeAt(index) },
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
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Đánh giá ẩn danh",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Tên tài khoản sẽ hiển thị dạng K*** trên trang công khai",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary
                                )
                            }

                            Switch(
                                checked = isAnonymous,
                                onCheckedChange = { if (!isCurrentItemReviewed) isAnonymous = it },
                                enabled = !isCurrentItemReviewed,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFBA2211)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog nhập liên kết ảnh hoặc chọn ảnh mẫu thực tế
    if (showAddUrlDialog) {
        val sampleImages = listOf(
            "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=500&auto=format&fit=crop"
        )

        AlertDialog(
            onDismissRequest = { showAddUrlDialog = false },
            title = {
                Text(
                    text = "Thêm hình ảnh thực tế",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Nhập URL hình ảnh hoặc chọn nhanh ảnh mẫu:",
                        fontSize = 12.5.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = inputImageUrl,
                        onValueChange = { inputImageUrl = it },
                        label = { Text("Đường dẫn ảnh (URL)") },
                        placeholder = { Text("https://...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 13.sp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Chọn nhanh ảnh mẫu thực tế:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sampleImages.forEachIndexed { i, url ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFF3F4F6))
                                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp))
                                    .clickable {
                                        inputImageUrl = url
                                    }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "Mẫu ${i + 1}",
                                    fontSize = 11.5.sp,
                                    color = CoralPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = inputImageUrl.trim()
                        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                            if (!attachedImageUrls.contains(trimmed)) {
                                attachedImageUrls.add(trimmed)
                            }
                            showAddUrlDialog = false
                        } else {
                            viewModel.showErrorToast("Vui lòng nhập URL ảnh hợp lệ bắt đầu bằng http:// hoặc https://")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA2211))
                ) {
                    Text("Thêm ảnh", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddUrlDialog = false }) {
                    Text("Hủy", color = TextSecondary)
                }
            }
        )
    }
}
