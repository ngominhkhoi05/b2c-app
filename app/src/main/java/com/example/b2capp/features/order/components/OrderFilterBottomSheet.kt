package com.example.b2capp.features.order.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import kotlinx.coroutines.launch

/**
 * Tiêu chí bộ lọc nâng cao cho danh sách đơn hàng
 */
data class OrderFilterCriteria(
    val dateRange: String = "Tất cả", // "Tất cả", "30 ngày qua", "3 tháng qua", "6 tháng qua", "Năm nay"
    val orderStatus: String = "Tất cả", // "Tất cả", "Chờ thanh toán", "Đang chuẩn bị", "Đang giao", "Đánh giá", "Đã hủy"
    val pricePreset: String = "Tất cả", // "Tất cả", "Dưới 200.000₫", "200k – 500k", "500k – 1.000k", "Trên 1.000.000₫", "Tùy chỉnh"
    val customMinPrice: Double? = null,
    val customMaxPrice: Double? = null,
    val paymentMethod: String = "Tất cả", // "Tất cả", "COD", "VNPay"
    val sortBy: String = "Mới nhất" // "Mới nhất", "Cũ nhất", "Giá: Cao → Thấp", "Giá: Thấp → Cao"
) {
    val isActive: Boolean
        get() = dateRange != "Tất cả" ||
                orderStatus != "Tất cả" ||
                pricePreset != "Tất cả" ||
                customMinPrice != null ||
                customMaxPrice != null ||
                paymentMethod != "Tất cả" ||
                sortBy != "Mới nhất"

    val effectiveMinPrice: Double?
        get() = when (pricePreset) {
            "200k – 500k" -> 200000.0
            "500k – 1.000k" -> 500000.0
            "Trên 1.000.000₫" -> 1000000.0
            "Tùy chỉnh" -> customMinPrice
            else -> null
        }

    val effectiveMaxPrice: Double?
        get() = when (pricePreset) {
            "Dưới 200.000₫" -> 200000.0
            "200k – 500k" -> 500000.0
            "500k – 1.000k" -> 1000000.0
            "Tùy chỉnh" -> customMaxPrice
            else -> null
        }
}

/**
 * Modal BottomSheet Bộ lọc đơn hàng chuẩn Material 3:
 * - Hỗ trợ lọc theo Thời gian đặt (30 ngày, 3 tháng, 6 tháng, năm nay)
 * - Hỗ trợ lọc theo Trạng thái đơn hàng
 * - Hỗ trợ lọc theo Khoảng giá trị đơn hàng (Presets hoặc nhập tùy chỉnh)
 * - Hỗ trợ lọc theo Phương thức thanh toán (COD, VNPay)
 * - Hỗ trợ Sắp xếp theo ngày tạo hoặc giá trị
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OrderFilterBottomSheet(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    initialCriteria: OrderFilterCriteria = OrderFilterCriteria(),
    onApply: (OrderFilterCriteria) -> Unit
) {
    if (!isOpen) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    var tempCriteria by remember(initialCriteria) { mutableStateOf(initialCriteria) }
    var minPriceInput by remember(initialCriteria.customMinPrice) {
        mutableStateOf(initialCriteria.customMinPrice?.toLong()?.toString() ?: "")
    }
    var maxPriceInput by remember(initialCriteria.customMaxPrice) {
        mutableStateOf(initialCriteria.customMaxPrice?.toLong()?.toString() ?: "")
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .testTag("order_filter_bottom_sheet")
        ) {
            // 1. Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
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
                        text = "Bộ lọc đơn hàng",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                IconButton(
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        onDismiss()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Đóng",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            HorizontalDivider(color = BorderDivider, thickness = 1.dp)

            // 2. Nội dung cuộn
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Section 1: Thời gian đặt hàng
                FilterSectionTitle(title = "Thời gian đặt hàng")
                val dateOptions = listOf("Tất cả", "30 ngày qua", "3 tháng qua", "6 tháng qua", "Năm nay")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dateOptions.forEach { option ->
                        FilterChip(
                            label = option,
                            isSelected = tempCriteria.dateRange == option,
                            onClick = { tempCriteria = tempCriteria.copy(dateRange = option) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: Trạng thái đơn hàng
                FilterSectionTitle(title = "Trạng thái đơn hàng")
                val statusOptions = listOf("Tất cả", "Chờ thanh toán", "Đang chuẩn bị", "Đang giao", "Đánh giá", "Đã hủy")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    statusOptions.forEach { option ->
                        FilterChip(
                            label = option,
                            isSelected = tempCriteria.orderStatus == option,
                            onClick = { tempCriteria = tempCriteria.copy(orderStatus = option) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 3: Khoảng giá trị đơn hàng
                FilterSectionTitle(title = "Khoảng giá trị đơn hàng")
                val priceOptions = listOf("Tất cả", "Dưới 200.000₫", "200k – 500k", "500k – 1.000k", "Trên 1.000.000₫", "Tùy chỉnh")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    priceOptions.forEach { option ->
                        FilterChip(
                            label = option,
                            isSelected = tempCriteria.pricePreset == option,
                            onClick = {
                                tempCriteria = tempCriteria.copy(pricePreset = option)
                            }
                        )
                    }
                }

                // Nếu chọn "Tùy chỉnh", hiển thị 2 ô nhập giá từ - giá đến
                if (tempCriteria.pricePreset == "Tùy chỉnh") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PriceInputField(
                            value = minPriceInput,
                            onValueChange = { input ->
                                minPriceInput = input.filter { it.isDigit() }
                                tempCriteria = tempCriteria.copy(customMinPrice = minPriceInput.toDoubleOrNull())
                            },
                            placeholder = "Tối thiểu",
                            modifier = Modifier.weight(1f)
                        )
                        Text(text = "—", color = TextSecondary, fontSize = 16.sp)
                        PriceInputField(
                            value = maxPriceInput,
                            onValueChange = { input ->
                                maxPriceInput = input.filter { it.isDigit() }
                                tempCriteria = tempCriteria.copy(customMaxPrice = maxPriceInput.toDoubleOrNull())
                            },
                            placeholder = "Tối đa",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 4: Phương thức thanh toán
                FilterSectionTitle(title = "Phương thức thanh toán")
                val paymentOptions = listOf("Tất cả", "COD (Khi nhận hàng)", "VNPay")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    paymentOptions.forEach { option ->
                        val code = when (option) {
                            "COD (Khi nhận hàng)" -> "COD"
                            "VNPay" -> "VNPay"
                            else -> "Tất cả"
                        }
                        FilterChip(
                            label = option,
                            isSelected = tempCriteria.paymentMethod == code,
                            onClick = { tempCriteria = tempCriteria.copy(paymentMethod = code) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 5: Sắp xếp
                FilterSectionTitle(title = "Sắp xếp theo")
                val sortOptions = listOf("Mới nhất", "Cũ nhất", "Giá: Cao → Thấp", "Giá: Thấp → Cao")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sortOptions.forEach { option ->
                        FilterChip(
                            label = option,
                            isSelected = tempCriteria.sortBy == option,
                            onClick = { tempCriteria = tempCriteria.copy(sortBy = option) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            HorizontalDivider(color = BorderDivider, thickness = 1.dp)

            // 3. Bottom Bar: Nút Thiết lập lại & Áp dụng
            Surface(
                color = SurfaceCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            tempCriteria = OrderFilterCriteria()
                            minPriceInput = ""
                            maxPriceInput = ""
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDivider),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextSecondary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.RotateLeft,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Thiết lập lại",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = {
                            val finalCriteria = if (tempCriteria.pricePreset == "Tùy chỉnh") {
                                tempCriteria.copy(
                                    customMinPrice = minPriceInput.toDoubleOrNull(),
                                    customMaxPrice = maxPriceInput.toDoubleOrNull()
                                )
                            } else {
                                tempCriteria
                            }
                            coroutineScope.launch { sheetState.hide() }
                            onApply(finalCriteria)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(46.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CoralPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (tempCriteria.isActive) "Áp dụng bộ lọc" else "Áp dụng",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFFFF0ED) else Color(0xFFF3F4F6))
            .border(
                width = 1.dp,
                color = if (isSelected) CoralPrimary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) CoralPrimary else Color(0xFF374151)
        )
    }
}

@Composable
private fun PriceInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFF9FAFB))
            .border(1.dp, BorderDivider, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "₫", color = TextSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(4.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(
                    fontSize = 13.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                ),
                decorationBox = { innerTextField ->
                    if (value.isBlank()) {
                        Text(text = placeholder, color = TextSecondary, fontSize = 12.5.sp)
                    }
                    innerTextField()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
