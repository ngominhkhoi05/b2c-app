package com.example.b2capp.features.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import com.example.b2capp.data.remote.dto.PromotionDto
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/**
 * BottomSheet Chọn Mã Giảm Giá / Voucher (Voucher Selection BottomSheet)
 * Gắn kết 100% dữ liệu API Khuyến mãi từ Backend:
 * - Hiển thị danh sách Voucher thực tế từ server
 * - Nhập mã voucher bất kỳ và xác thực qua API
 * - Tự động nhận diện và làm nổi bật voucher "Giảm sâu nhất"
 * - Tính toán số tiền giảm thực tế dựa theo giá trị đơn hàng
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherSelectionBottomSheet(
    promotions: List<PromotionDto>,
    selectedVoucher: PromotionDto?,
    orderSubtotal: Double,
    isLoading: Boolean = false,
    onDismissRequest: () -> Unit,
    onSelectVoucher: (PromotionDto?) -> Unit,
    onApplyCustomCode: (String) -> Unit,
    calculateDiscount: (PromotionDto, Double) -> Double,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    var inputCode by remember { mutableStateOf("") }
    var currentSelected by remember { mutableStateOf(selectedVoucher) }

    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale("vi", "VN")) }

    // Tìm voucher có mức giảm sâu nhất
    val bestVoucher = remember(promotions, orderSubtotal) {
        promotions
            .filter { promo ->
                val isActive = promo.status == null || promo.status.equals("ACTIVE", ignoreCase = true)
                val minOrder = promo.minOrderValue ?: 0.0
                isActive && orderSubtotal >= minOrder
            }
            .map { promo -> Pair(promo, calculateDiscount(promo, orderSubtotal)) }
            .filter { it.second > 0.0 }
            .maxByOrNull { it.second }
            ?.first
    }

    val currentSavings = currentSelected?.let { calculateDiscount(it, orderSubtotal) } ?: 0.0

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = SurfaceCard,
        modifier = modifier.testTag("voucher_selection_bottom_sheet")
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
                    .padding(top = 10.dp, bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFE2E8F0))
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Chọn Mã Giảm Giá",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Áp dụng voucher để tiết kiệm chi phí tối đa",
                            fontSize = 12.5.sp,
                            color = Color(0xFF6B7280)
                        )
                    }

                    IconButton(
                        onClick = {
                            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                onDismissRequest()
                            }
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // Danh sách nội dung cuộn
            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 2. Ô nhập mã voucher & Nút Áp dụng
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ConfirmationNumber,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = inputCode,
                                onValueChange = { inputCode = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                ),
                                decorationBox = { innerTextField ->
                                    if (inputCode.isEmpty()) {
                                        Text(
                                            text = "Nhập mã voucher...",
                                            fontSize = 13.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (inputCode.isNotBlank()) {
                                    onApplyCustomCode(inputCode)
                                }
                            },
                            enabled = inputCode.isNotBlank(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NavySecondary,
                                disabledContainerColor = Color(0xFFE2E8F0)
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Text(
                                text = "Áp dụng",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (inputCode.isNotBlank()) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                // Trạng thái đang tải
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = CoralPrimary,
                                modifier = Modifier.size(32.dp),
                                strokeWidth = 2.5.dp
                            )
                        }
                    }
                }

                // Khi danh sách voucher trống
                if (!isLoading && promotions.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ConfirmationNumber,
                                    contentDescription = null,
                                    tint = Color(0xFF9CA3AF),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Hiện chưa có mã giảm giá nào",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF4B5563)
                                )
                                Text(
                                    text = "Bạn có thể nhập trực tiếp mã ưu đãi ở ô phía trên",
                                    fontSize = 12.sp,
                                    color = Color(0xFF9CA3AF)
                                )
                            }
                        }
                    }
                }

                // Hiển thị danh sách các mã giảm giá từ API
                items(promotions) { promo ->
                    val isBest = bestVoucher?.id == promo.id
                    val isSelected = currentSelected?.id == promo.id
                    val minOrder = promo.minOrderValue ?: 0.0
                    val isEligible = orderSubtotal >= minOrder
                    val discountAmount = calculateDiscount(promo, orderSubtotal)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = isEligible) {
                                currentSelected = if (isSelected) null else promo
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (!isEligible) Color(0xFFFAFAFA) else Color.White
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 0.8.dp,
                            color = when {
                                isSelected -> CoralPrimary
                                isBest -> Color(0xFFE53935).copy(alpha = 0.5f)
                                else -> Color(0xFFE5E7EB)
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Cột icon / cuống vé
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            promo.discountType.equals("FREESHIP", ignoreCase = true) -> Color(0xFFE6F7F5)
                                            isBest -> Color(0xFFFFEBE7)
                                            else -> Color(0xFFF3F4F6)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (promo.discountType.equals("FREESHIP", ignoreCase = true)) {
                                        Icons.Default.LocalShipping
                                    } else {
                                        Icons.Default.ConfirmationNumber
                                    },
                                    contentDescription = null,
                                    tint = when {
                                        promo.discountType.equals("FREESHIP", ignoreCase = true) -> Color(0xFF00897B)
                                        isBest -> CoralPrimary
                                        else -> NavySecondary
                                    },
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Thông tin voucher
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = promo.code.orEmpty(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isEligible) Color(0xFF111827) else Color(0xFF9CA3AF)
                                    )

                                    if (isBest) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFFEF2F2))
                                                .border(0.5.dp, Color(0xFFFCA5A5), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "🔥 Giảm sâu nhất",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFB91C1C)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = promo.name.orEmpty(),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isEligible) Color(0xFF374151) else Color(0xFF9CA3AF),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                if (isEligible && discountAmount > 0) {
                                    Text(
                                        text = "Tiết kiệm: ₫${currencyFormatter.format(discountAmount.toLong())}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CoralPrimary
                                    )
                                } else {
                                    Text(
                                        text = "Đơn tối thiểu: ₫${currencyFormatter.format(minOrder.toLong())}",
                                        fontSize = 11.5.sp,
                                        color = if (isEligible) Color(0xFF6B7280) else Color(0xFFDC2626)
                                    )
                                }

                                if (!isEligible) {
                                    val needMore = (minOrder - orderSubtotal).toLong()
                                    Text(
                                        text = "Mua thêm ₫${currencyFormatter.format(needMore)} để áp dụng mã này",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFDC2626)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    if (isEligible) {
                                        currentSelected = if (isSelected) null else promo
                                    }
                                },
                                enabled = isEligible,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = CoralPrimary,
                                    unselectedColor = Color(0xFFD1D5DB)
                                )
                            )
                        }
                    }
                }
            }

            // Thanh xác nhận cố định đáy
            Surface(
                color = SurfaceCard,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        if (currentSelected != null && currentSavings > 0) {
                            Text(
                                text = "Mã: ${currentSelected?.code}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavySecondary
                            )
                            Text(
                                text = "Tiết kiệm ₫${currencyFormatter.format(currentSavings.toLong())}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = CoralPrimary
                            )
                        } else {
                            Text(
                                text = "Chưa chọn mã nào",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (currentSelected != null) {
                            Button(
                                onClick = {
                                    currentSelected = null
                                    onSelectVoucher(null)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = "Bỏ chọn",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF4B5563)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                onSelectVoucher(currentSelected)
                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                    onDismissRequest()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary),
                            contentPadding = PaddingValues(horizontal = 18.dp)
                        ) {
                            Text(
                                text = "XÁC NHẬN",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
