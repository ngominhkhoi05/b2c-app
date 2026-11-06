package com.example.b2capp.ui

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch

/**
 * Model biểu diễn voucher trong màn hình chọn
 */
data class SelectedVoucherState(
    val hasOrderVoucher: Boolean = true,
    val orderVoucherDiscount: Long = 39800L,
    val orderVoucherCode: String = "B2C COOL",
    val hasFreeshipVoucher: Boolean = true,
    val freeshipDiscount: Long = 30000L
) {
    val totalVouchersCount: Int
        get() = (if (hasOrderVoucher) 1 else 0) + (if (hasFreeshipVoucher) 1 else 0)

    val totalSavingsAmount: Long
        get() = (if (hasOrderVoucher) orderVoucherDiscount else 0L) + (if (hasFreeshipVoucher) freeshipDiscount else 0L)
}

/**
 * BottomSheet Chọn Mã Giảm Giá / Voucher (Voucher Selection Screen)
 * Thiết kế chính xác 100% theo VoucherSelectionScreen_b82f3992.png:
 * 1. Header:
 *    - Thanh gạch xám kéo thả
 *    - Tiêu đề "Chọn Mã Giảm Giá", phụ đề "Chọn tối đa 1 Voucher Giảm giá & 1 Voucher Vận chuyển"
 *    - Nút đóng (X) bên phải
 * 2. Ô nhập mã voucher:
 *    - Trường nhập với icon vé "Nhập mã voucher..."
 *    - Nút "Áp dụng" màu xanh Navy
 * 3. Mục 1: Mã Giảm Giá Đơn Hàng (Chọn 1) - 1 mã phù hợp:
 *    - Thẻ vé viền cam đỏ
 *    - Cuống vé đỏ mận "% GIẢM 10%", nhãn "B2C COOL"
 *    - Nội dung: "GIẢM 10% TỐI ĐA 50K", "Đơn tối thiểu ₫200.000", tag "Đã áp dụng", HSD: 15/11/2026
 *    - RadioButton cam đỏ
 * 4. Mục 2: Mã Miễn Phí Vận Chuyển (Freeship) - Tự động chọn:
 *    - Thẻ vé viền xanh ngọc
 *    - Cuống vé xanh rêu đậm, icon xe tải "FREESHIP", nhãn "ĐƠN ĐẦU"
 *    - Nội dung: "Miễn phí vận chuyển ₫30.000", "Đơn tối thiểu ₫150.000", tag "Freeship Xtra", HSD: 20/11/2026
 *    - RadioButton xanh ngọc
 * 5. Thanh xác nhận cố định đáy:
 *    - "Đã chọn: 2 voucher"
 *    - Badge "Tiết kiệm ₫69.800"
 *    - Nút lớn "XÁC NHẬN ÁP DỤNG" kèm icon tích tròn (✓)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherSelectionBottomSheet(
    initialState: SelectedVoucherState = SelectedVoucherState(),
    onDismissRequest: () -> Unit,
    onConfirmSelection: (SelectedVoucherState) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    var inputCode by remember { mutableStateOf("") }
    var hasOrderVoucher by remember { mutableStateOf(initialState.hasOrderVoucher) }
    var hasFreeshipVoucher by remember { mutableStateOf(initialState.hasFreeshipVoucher) }

    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale("vi", "VN")) }

    // Tính toán số lượng voucher đã chọn và số tiền tiết kiệm
    val selectedCount = (if (hasOrderVoucher) 1 else 0) + (if (hasFreeshipVoucher) 1 else 0)
    val totalSavings = (if (hasOrderVoucher) 39800L else 0L) + (if (hasFreeshipVoucher) 30000L else 0L)

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
                // Thanh gạch xám kéo thả
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFE2E8F0))
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Hàng Tiêu đề & Nút đóng
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
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Chọn tối đa 1 Voucher Giảm giá & 1 Voucher Vận chuyển",
                            fontSize = 12.5.sp,
                            color = Color(0xFF6B7280),
                            lineHeight = 16.5.sp
                        )
                    }

                    // Nút X đóng
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 2. Ô nhập mã voucher & Nút Áp dụng
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Khung nhập liệu
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(6.dp))
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

                        Spacer(modifier = Modifier.width(10.dp))

                        // Nút Áp dụng màu xanh Navy
                        Box(
                            modifier = Modifier
                                .height(42.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF335C85))
                                .clickable {
                                    if (inputCode.isNotBlank()) {
                                        hasOrderVoucher = true
                                        inputCode = ""
                                    }
                                }
                                .padding(horizontal = 18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Áp dụng",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // 3. Mục 1: Mã Giảm Giá Đơn Hàng
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Mã Giảm Giá Đơn Hàng",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(Chọn 1)",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }

                            Text(
                                text = "1 mã phù hợp",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB82414)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Thẻ vé Voucher Đơn Hàng (GIẢM 10% TỐI ĐA 50K)
                        VoucherCouponCard(
                            stubColor = Color(0xFFB82414),
                            stubTitle = "GIẢM 10%",
                            stubSubtitle = "B2C COOL",
                            stubIsPercentage = true,
                            title = "GIẢM 10% TỐI ĐA 50K",
                            minOrderText = "Đơn tối thiểu ₫200.000",
                            tagText = "Đã áp dụng",
                            tagBg = Color(0xFFFFEBE7),
                            tagColor = Color(0xFFB82414),
                            expiryText = "HSD: 15/11/2026",
                            isSelected = hasOrderVoucher,
                            accentColor = Color(0xFFB82414),
                            onClick = { hasOrderVoucher = !hasOrderVoucher }
                        )
                    }
                }

                // 4. Mục 2: Mã Miễn Phí Vận Chuyển
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Mã Miễn Phí Vận Chuyển",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(Freeship)",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }

                            Text(
                                text = "Tự động\nchọn",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00897B),
                                lineHeight = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Thẻ vé Voucher Vận Chuyển (Miễn phí vận chuyển ₫30.000)
                        VoucherCouponCard(
                            stubColor = Color(0xFF09584F),
                            stubTitle = "FREESHIP",
                            stubSubtitle = "ĐƠN ĐẦU",
                            stubIsTruck = true,
                            title = "Miễn phí vận chuyển ₫30.000",
                            minOrderText = "Đơn tối thiểu ₫150.000",
                            tagText = "Freeship Xtra",
                            tagBg = Color(0xFFE6F7F5),
                            tagColor = Color(0xFF00897B),
                            expiryText = "HSD: 20/11/2026",
                            isSelected = hasFreeshipVoucher,
                            accentColor = Color(0xFF00897B),
                            onClick = { hasFreeshipVoucher = !hasFreeshipVoucher }
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 0.8.dp)

            // 5. Thanh tóm tắt & Xác nhận cố định đáy
            Surface(
                color = SurfaceCard,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    // Hàng đếm voucher & Số tiền tiết kiệm
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Đã chọn: ",
                                fontSize = 13.5.sp,
                                color = Color(0xFF6B7280)
                            )
                            Text(
                                text = "$selectedCount voucher",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                        }

                        // Tag "Tiết kiệm ₫69.800"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE6F7F5))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Savings,
                                    contentDescription = null,
                                    tint = Color(0xFF00897B),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Tiết kiệm ₫${currencyFormatter.format(totalSavings)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00897B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Nút lớn "XÁC NHẬN ÁP DỤNG ✓"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFB82414))
                            .clickable {
                                val result = SelectedVoucherState(
                                    hasOrderVoucher = hasOrderVoucher,
                                    orderVoucherDiscount = if (hasOrderVoucher) 39800L else 0L,
                                    hasFreeshipVoucher = hasFreeshipVoucher,
                                    freeshipDiscount = if (hasFreeshipVoucher) 30000L else 0L
                                )
                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                    onConfirmSelection(result)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "XÁC NHẬN ÁP DỤNG",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Thẻ Voucher dạng cuống vé chuẩn B2C e-commerce
 */
@Composable
private fun VoucherCouponCard(
    stubColor: Color,
    stubTitle: String,
    stubSubtitle: String,
    stubIsPercentage: Boolean = false,
    stubIsTruck: Boolean = false,
    title: String,
    minOrderText: String,
    tagText: String,
    tagBg: Color,
    tagColor: Color,
    expiryText: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            1.dp,
            if (isSelected) accentColor else Color(0xFFE5E7EB)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cuống vé bên trái
            Box(
                modifier = Modifier
                    .width(88.dp)
                    .fillMaxHeight()
                    .background(stubColor)
                    .padding(horizontal = 6.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (stubIsPercentage) {
                        Text(
                            text = "%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    if (stubIsTruck) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    Text(
                        text = stubTitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = stubSubtitle,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Nội dung chi tiết bên phải
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = minOrderText,
                    fontSize = 11.5.sp,
                    color = Color(0xFF6B7280)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(2.dp))
                            .background(tagBg)
                            .padding(horizontal = 5.dp, vertical = 1.5.dp)
                    ) {
                        Text(
                            text = tagText,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = tagColor
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = expiryText,
                        fontSize = 10.5.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

            // Radio Button bên góc phải
            Box(
                modifier = Modifier.padding(end = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Radio button tùy biến theo màu accent
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .border(
                            1.5.dp,
                            if (isSelected) accentColor else Color(0xFFD1D5DB),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                        )
                    }
                }
            }
        }
    }
}
