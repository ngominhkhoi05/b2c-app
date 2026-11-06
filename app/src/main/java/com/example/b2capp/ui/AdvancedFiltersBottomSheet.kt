package com.example.b2capp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.StarRatingGold
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch

/**
 * Dữ liệu trạng thái bộ lọc nâng cao
 */
data class AdvancedFilterCriteria(
    val selectedCategories: Set<String> = setOf("Áo thun"),
    val minPrice: Long = 100000L,
    val maxPrice: Long = 500000L,
    val selectedPricePreset: String? = "100k – 300k",
    val selectedBrands: Set<String> = setOf("Coolmate"),
    val starRatingMin: Int = 4, // 5: Từ 5 sao, 4: Từ 4 sao trở lên, 3: Từ 3 sao
    val isFreeShipping: Boolean = true,
    val isDiscountSale: Boolean = true,
    val isShopeeMall: Boolean = false
)

/**
 * Màn hình / BottomSheet Bộ Lọc Tìm Kiếm Nâng Cao
 * Thiết kế chính xác 100% theo AdvancedFiltersScreen_1917256d.png:
 * 1. Header:
 *    - Nút gạch xám kéo thả
 *    - Icon bộ lọc cam đỏ + Tiêu đề "Bộ lọc tìm kiếm"
 *    - Nút đóng (X) tròn xám bên phải
 * 2. Mục Danh mục:
 *    - Nhãn "Danh mục", số lượng "Đã chọn: 1"
 *    - Chips: Áo thun (checked), Áo sơ mi, Áo polo, Áo hoodie, Áo khoác
 * 3. Mục Khoảng giá (₫):
 *    - Container xám nhẹ, nhãn hiển thị "100.000₫ – 500.000₫"
 *    - Thanh trượt 2 đầu (RangeSlider) màu cam đỏ
 *    - 2 ô hiển thị Tối thiểu (₫ 100.000) - Tối đa (₫ 500.000)
 *    - 4 nút preset nhanh: Dưới 100k, 100k – 300k (selected), 300k – 500k, Trên 500k
 * 4. Mục Thương hiệu:
 *    - Nhãn "Thương hiệu", link "Xem thêm ⌵"
 *    - Lưới thương hiệu: Coolmate (checked), Uniqlo, Nike, Adidas, Anker, Khác +
 * 5. Mục Đánh giá sao:
 *    - Từ 5 sao (3.2k)
 *    - Từ 4 sao trở lên (8.9k) (selected checked)
 *    - Từ 3 sao (12k)
 * 6. Mục Dịch vụ & Khuyến mãi:
 *    - Miễn phí vận chuyển [⚡ Freeship Xtra] (checked)
 *    - Đang giảm giá [-40% SALE] (checked)
 *    - Chính hãng Shopee Mall [MALL] (unchecked)
 * 7. Thanh nút cố định đáy:
 *    - Nút "↻ Thiết lập lại"
 *    - Nút "ÁP DỤNG (124 sản phẩm)" màu cam đỏ nổi bật
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedFiltersBottomSheet(
    initialCriteria: AdvancedFilterCriteria = AdvancedFilterCriteria(),
    onDismissRequest: () -> Unit,
    onApplyFilter: (AdvancedFilterCriteria) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    // State các tiêu chí lọc
    var selectedCategories by remember { mutableStateOf(initialCriteria.selectedCategories) }
    var minPrice by remember { mutableFloatStateOf(initialCriteria.minPrice.toFloat()) }
    var maxPrice by remember { mutableFloatStateOf(initialCriteria.maxPrice.toFloat()) }
    var selectedPricePreset by remember { mutableStateOf(initialCriteria.selectedPricePreset) }
    var selectedBrands by remember { mutableStateOf(initialCriteria.selectedBrands) }
    var selectedStarRating by remember { mutableIntStateOf(initialCriteria.starRatingMin) }
    var isFreeShipping by remember { mutableStateOf(initialCriteria.isFreeShipping) }
    var isDiscountSale by remember { mutableStateOf(initialCriteria.isDiscountSale) }
    var isShopeeMall by remember { mutableStateOf(initialCriteria.isShopeeMall) }

    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale("vi", "VN")) }

    // Tính toán số lượng sản phẩm phù hợp giả lập dựa trên tiêu chí
    val calculatedProductCount = remember(
        selectedCategories,
        minPrice,
        maxPrice,
        selectedBrands,
        selectedStarRating,
        isFreeShipping,
        isDiscountSale,
        isShopeeMall
    ) {
        var base = 124
        if (selectedCategories.contains("Áo thun")) base += 18
        if (isFreeShipping) base -= 10
        if (isShopeeMall) base -= 42
        if (selectedBrands.contains("Coolmate")) base += 8
        base.coerceAtLeast(12)
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = SurfaceCard,
        modifier = modifier.testTag("advanced_filters_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // 1. Drag Handle + Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Thanh gạch xám kéo thả
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 4.5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFE2E8F0))
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Hàng Tiêu đề & Nút đóng (X)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Tune,
                            contentDescription = null,
                            tint = CoralPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bộ lọc tìm kiếm",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    }

                    // Nút X tròn xám
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3F4F6))
                            .clickable {
                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                    onDismissRequest()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = Color(0xFF4B5563),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // Danh sách nội dung các mục lọc (Cuộn mượt mà)
            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // ==================== MỤC 1: DANH MỤC ====================
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Danh mục",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Đã chọn: ",
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF6B7280)
                                )
                                Text(
                                    text = "${selectedCategories.size}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CoralPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Hàng 1: Áo thun, Áo sơ mi, Áo polo, Áo hoodie
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Áo thun", "Áo sơ mi", "Áo polo", "Áo hoodie").forEach { category ->
                                val isSelected = selectedCategories.contains(category)
                                CategoryFilterPill(
                                    text = category,
                                    isSelected = isSelected,
                                    onClick = {
                                        selectedCategories = if (isSelected) {
                                            if (selectedCategories.size > 1) selectedCategories - category else selectedCategories
                                        } else {
                                            selectedCategories + category
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Hàng 2: Áo khoác
                        CategoryFilterPill(
                            text = "Áo khoác",
                            isSelected = selectedCategories.contains("Áo khoác"),
                            onClick = {
                                val cat = "Áo khoác"
                                selectedCategories = if (selectedCategories.contains(cat)) {
                                    if (selectedCategories.size > 1) selectedCategories - cat else selectedCategories
                                } else {
                                    selectedCategories + cat
                                }
                            }
                        )
                    }
                }

                // ==================== MỤC 2: KHOẢNG GIÁ (₫) ====================
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        border = BorderStroke(0.8.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Khoảng giá (₫)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937)
                                )
                                Text(
                                    text = "${currencyFormatter.format(minPrice.toLong())}₫ – ${currencyFormatter.format(maxPrice.toLong())}₫",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E5B94)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Thanh RangeSlider màu cam đỏ
                            RangeSlider(
                                value = minPrice..maxPrice,
                                onValueChange = { range ->
                                    minPrice = (range.start / 10000).toInt() * 10000f
                                    maxPrice = (range.endInclusive / 10000).toInt() * 10000f
                                    selectedPricePreset = null
                                },
                                valueRange = 0f..1000000f,
                                colors = SliderDefaults.colors(
                                    thumbColor = CoralPrimary,
                                    activeTrackColor = CoralPrimary,
                                    inactiveTrackColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // 2 Ô Tối thiểu & Tối đa
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Ô Tối thiểu
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White)
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "Tối thiểu",
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "₫ ${currencyFormatter.format(minPrice.toLong())}",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF111827)
                                    )
                                }

                                Text(
                                    text = "—",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9CA3AF),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                // Ô Tối đa
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White)
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "Tối đa",
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "₫ ${currencyFormatter.format(maxPrice.toLong())}",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF111827)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 4 Nút preset giá: Dưới 100k, 100k – 300k, 300k – 500k, Trên 500k
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "Dưới 100k" to (0f to 100000f),
                                    "100k – 300k" to (100000f to 300000f),
                                    "300k – 500k" to (300000f to 500000f),
                                    "Trên 500k" to (500000f to 1000000f)
                                ).forEach { (label, range) ->
                                    val isSelected = selectedPricePreset == label
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) Color(0xFFFFEBE7) else Color.White)
                                            .border(
                                                1.dp,
                                                if (isSelected) Color(0xFFFCA5A5) else Color(0xFFE5E7EB),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable {
                                                selectedPricePreset = label
                                                minPrice = range.first
                                                maxPrice = range.second
                                            }
                                            .padding(vertical = 7.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) CoralPrimary else Color(0xFF374151),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ==================== MỤC 3: THƯƠNG HIỆU ====================
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Thương hiệu",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { /* Mở rộng thêm */ }
                            ) {
                                Text(
                                    text = "Xem thêm",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1E5B94)
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color(0xFF1E5B94),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Lưới 2 hàng 3 cột thương hiệu
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BrandPillBox(
                                name = "Coolmate",
                                isSelected = selectedBrands.contains("Coolmate"),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    selectedBrands = if (selectedBrands.contains("Coolmate")) {
                                        selectedBrands - "Coolmate"
                                    } else {
                                        selectedBrands + "Coolmate"
                                    }
                                }
                            )
                            BrandPillBox(
                                name = "Uniqlo",
                                isSelected = selectedBrands.contains("Uniqlo"),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    selectedBrands = if (selectedBrands.contains("Uniqlo")) {
                                        selectedBrands - "Uniqlo"
                                    } else {
                                        selectedBrands + "Uniqlo"
                                    }
                                }
                            )
                            BrandPillBox(
                                name = "Nike",
                                isSelected = selectedBrands.contains("Nike"),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    selectedBrands = if (selectedBrands.contains("Nike")) {
                                        selectedBrands - "Nike"
                                    } else {
                                        selectedBrands + "Nike"
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BrandPillBox(
                                name = "Adidas",
                                isSelected = selectedBrands.contains("Adidas"),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    selectedBrands = if (selectedBrands.contains("Adidas")) {
                                        selectedBrands - "Adidas"
                                    } else {
                                        selectedBrands + "Adidas"
                                    }
                                }
                            )
                            BrandPillBox(
                                name = "Anker",
                                isSelected = selectedBrands.contains("Anker"),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    selectedBrands = if (selectedBrands.contains("Anker")) {
                                        selectedBrands - "Anker"
                                    } else {
                                        selectedBrands + "Anker"
                                    }
                                }
                            )
                            // Nút "Khác +"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFEBF3FC))
                                    .clickable { /* Xem thêm thương hiệu */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Khác +",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E5B94)
                                )
                            }
                        }
                    }
                }

                // ==================== MỤC 4: ĐÁNH GIÁ SAO ====================
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Đánh giá sao",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Từ 5 sao
                            StarRatingRowItem(
                                title = "Từ 5 sao",
                                filledStars = 5,
                                countText = "(3.2k)",
                                isSelected = selectedStarRating == 5,
                                onClick = { selectedStarRating = 5 }
                            )

                            // Từ 4 sao trở lên (Đang được chọn)
                            StarRatingRowItem(
                                title = "Từ 4 sao trở lên",
                                filledStars = 4,
                                countText = "(8.9k)",
                                isSelected = selectedStarRating == 4,
                                onClick = { selectedStarRating = 4 }
                            )

                            // Từ 3 sao
                            StarRatingRowItem(
                                title = "Từ 3 sao",
                                filledStars = 3,
                                countText = "(12k)",
                                isSelected = selectedStarRating == 3,
                                onClick = { selectedStarRating = 3 }
                            )
                        }
                    }
                }

                // ==================== MỤC 5: DỊCH VỤ & KHUYẾN MÃI ====================
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Dịch vụ & Khuyến mãi",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // 1. Miễn phí vận chuyển [⚡ Freeship Xtra]
                            ServicePromotionCard(
                                title = "Miễn phí vận chuyển",
                                isChecked = isFreeShipping,
                                onCheckedChange = { isFreeShipping = it },
                                badge = {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF00897B))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Bolt,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "Freeship Xtra",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            )

                            // 2. Đang giảm giá [-40% SALE]
                            ServicePromotionCard(
                                title = "Đang giảm giá",
                                isChecked = isDiscountSale,
                                onCheckedChange = { isDiscountSale = it },
                                badge = {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFB91C1C))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "-40% SALE",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                    }
                                }
                            )

                            // 3. Chính hãng Shopee Mall [MALL]
                            ServicePromotionCard(
                                title = "Chính hãng Shopee Mall",
                                isChecked = isShopeeMall,
                                onCheckedChange = { isShopeeMall = it },
                                badge = {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFDC2626))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "MALL",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 0.8.dp)

            // ==================== 6. THANH HÀNH ĐỘNG CỐ ĐỊNH ĐÁY ====================
            Surface(
                color = SurfaceCard,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nút "↻ Thiết lập lại"
                    Box(
                        modifier = Modifier
                            .height(46.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF3F4F6))
                            .clickable {
                                selectedCategories = setOf("Áo thun")
                                minPrice = 100000f
                                maxPrice = 500000f
                                selectedPricePreset = "100k – 300k"
                                selectedBrands = setOf("Coolmate")
                                selectedStarRating = 4
                                isFreeShipping = true
                                isDiscountSale = true
                                isShopeeMall = false
                            }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.RotateLeft,
                                contentDescription = null,
                                tint = Color(0xFF1F2937),
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Thiết lập lại",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1F2937)
                            )
                        }
                    }

                    // Nút "ÁP DỤNG (124 sản phẩm)"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CoralPrimary)
                            .clickable {
                                val criteria = AdvancedFilterCriteria(
                                    selectedCategories = selectedCategories,
                                    minPrice = minPrice.toLong(),
                                    maxPrice = maxPrice.toLong(),
                                    selectedPricePreset = selectedPricePreset,
                                    selectedBrands = selectedBrands,
                                    starRatingMin = selectedStarRating,
                                    isFreeShipping = isFreeShipping,
                                    isDiscountSale = isDiscountSale,
                                    isShopeeMall = isShopeeMall
                                )
                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                    onApplyFilter(criteria)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ÁP DỤNG ",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "($calculatedProductCount sản phẩm)",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Chip chọn danh mục
 */
@Composable
private fun CategoryFilterPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFFFEBE7) else Color(0xFFF3F4F6))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = CoralPrimary,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) CoralPrimary else Color(0xFF374151)
            )
        }
    }
}

/**
 * Hộp chọn thương hiệu
 */
@Composable
private fun BrandPillBox(
    name: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFFFEBE7) else Color(0xFFF3F4F6))
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(CoralPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = name,
                fontSize = 12.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) CoralPrimary else Color(0xFF374151)
            )
        }
    }
}

/**
 * Dòng đánh giá sao
 */
@Composable
private fun StarRatingRowItem(
    title: String,
    filledStars: Int,
    countText: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFFFEBE7) else Color(0xFFF9FAFB))
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(CoralPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) CoralPrimary else Color(0xFF1F2937)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 5 ngôi sao (Vàng nếu trong số sao, xám nhạt nếu sao khuyết)
            Row(horizontalArrangement = Arrangement.spacedBy(1.5.dp)) {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= filledStars) Icons.Default.Star else Icons.Outlined.StarOutline,
                        contentDescription = null,
                        tint = if (i <= filledStars) StarRatingGold else Color(0xFFCBD5E1),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Text(
            text = countText,
            fontSize = 12.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) CoralPrimary else Color(0xFF6B7280)
        )
    }
}

/**
 * Thẻ checkbox dịch vụ & khuyến mãi
 */
@Composable
private fun ServicePromotionCard(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    badge: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        border = BorderStroke(0.8.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Checkbox cam đỏ bo góc
                Box(
                    modifier = Modifier
                        .size(19.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isChecked) CoralPrimary else Color.White)
                        .border(
                            1.2.dp,
                            if (isChecked) CoralPrimary else Color(0xFFD1D5DB),
                            RoundedCornerShape(4.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isChecked) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937)
                )
            }

            badge()
        }
    }
}
