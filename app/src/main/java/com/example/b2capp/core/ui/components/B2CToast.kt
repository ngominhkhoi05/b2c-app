package com.example.b2capp.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Phân loại Toast thông báo:
 * - ERROR: Lỗi (Màu đỏ)
 * - SUCCESS: Thành công (Màu xanh lá)
 * - WARNING: Cảnh báo (Màu vàng / cam)
 * - INFO: Thông báo chung (Màu xanh dương)
 */
enum class ToastType {
    ERROR,
    SUCCESS,
    WARNING,
    INFO
}

/**
 * Dữ liệu Toast thông báo hiển thị
 */
data class ToastData(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val type: ToastType = ToastType.INFO
)

/**
 * Tự động phân loại loại Toast dựa trên nội dung câu thông báo nếu không chỉ định rõ
 */
fun resolveToastType(message: String, explicitType: ToastType? = null): ToastType {
    if (explicitType != null) return explicitType
    val lower = message.lowercase()
    return when {
        lower.contains("lỗi") ||
                lower.contains("thất bại") ||
                lower.contains("không khớp") ||
                lower.contains("không hợp lệ") ||
                lower.contains("sai") ||
                lower.contains("vui lòng nhập") ||
                lower.contains("vui lòng đồng ý") ||
                lower.contains("chưa chọn") -> ToastType.ERROR

        lower.contains("cảnh báo") ||
                lower.contains("lưu ý") ||
                lower.contains("chú ý") ||
                lower.contains("quy định") ||
                lower.contains("chỉ thanh toán") ||
                lower.contains("đang lấy") ||
                lower.contains("đang xác định") ||
                lower.contains("đang mở") -> ToastType.WARNING

        lower.contains("thành công") ||
                lower.contains("đã thêm") ||
                lower.contains("đã lưu") ||
                lower.contains("đã sao chép") ||
                lower.contains("đã xóa") ||
                lower.contains("đã áp dụng") ||
                lower.contains("đã cập nhật") ||
                lower.contains("đã gửi") ||
                lower.contains("chào mừng") ||
                lower.contains("cảm ơn") ||
                lower.contains("hợp lệ") -> ToastType.SUCCESS

        else -> ToastType.SUCCESS
    }
}

fun createToastData(message: String, type: ToastType? = null): ToastData {
    return ToastData(
        id = System.currentTimeMillis(),
        message = message,
        type = resolveToastType(message, type)
    )
}

/**
 * Component Host Toast thông báo ở cuối màn hình:
 * - Hiển thị khoảng 2 giây
 * - Vuốt qua trái hoặc qua phải để xóa nhanh
 * - Màu sắc trực quan theo từng loại (Lỗi = Đỏ, Thành công = Xanh lá, Cảnh báo = Vàng)
 */
@Composable
fun B2CToastHost(
    currentToast: ToastData?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = currentToast != null,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(animationSpec = tween(150)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(150)
        ) + fadeOut(animationSpec = tween(100)),
        modifier = modifier
    ) {
        if (currentToast != null) {
            B2CSwipeableToast(
                toast = currentToast,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
fun B2CSwipeableToast(
    toast: ToastData,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val offsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    var isDismissed by remember { mutableStateOf(false) }

    // Tự động đóng sau khoảng 2 giây
    LaunchedEffect(toast.id) {
        delay(2000L)
        if (!isDismissed) {
            isDismissed = true
            onDismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .graphicsLayer {
                val dragDistance = abs(offsetX.value)
                alpha = (1f - (dragDistance / 450f)).coerceIn(0.2f, 1f)
            }
            .pointerInput(toast.id) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val distance = offsetX.value
                        // Ngưỡng vuốt sang trái hoặc sang phải để xóa nhanh
                        if (abs(distance) > 100f && !isDismissed) {
                            isDismissed = true
                            coroutineScope.launch {
                                val targetX = if (distance > 0) 1200f else -1200f
                                offsetX.animateTo(
                                    targetValue = targetX,
                                    animationSpec = tween(durationMillis = 180)
                                )
                                onDismiss()
                            }
                        } else if (!isDismissed) {
                            // Chưa đủ ngưỡng vuốt -> Bật nảy về giữa
                            coroutineScope.launch {
                                offsetX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                            }
                        }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        if (!isDismissed) {
                            coroutineScope.launch {
                                offsetX.snapTo(offsetX.value + dragAmount)
                            }
                        }
                    }
                )
            }
    ) {
        ToastCard(
            toast = toast,
            onDismiss = {
                if (!isDismissed) {
                    isDismissed = true
                    onDismiss()
                }
            }
        )
    }
}

@Composable
private fun ToastCard(
    toast: ToastData,
    onDismiss: () -> Unit
) {
    val (backgroundColor, borderColor, icon: ImageVector, labelTitle: String) = when (toast.type) {
        ToastType.ERROR -> Quadruple(
            Color(0xFFDC2626), // Đỏ
            Color(0xFFB91C1C),
            Icons.Filled.ErrorOutline,
            "Lỗi"
        )
        ToastType.SUCCESS -> Quadruple(
            Color(0xFF16A34A), // Xanh lá
            Color(0xFF15803D),
            Icons.Filled.CheckCircle,
            "Thành công"
        )
        ToastType.WARNING -> Quadruple(
            Color(0xFFD97706), // Vàng hổ phách / Vàng cam
            Color(0xFFB45309),
            Icons.Filled.WarningAmber,
            "Cảnh báo"
        )
        ToastType.INFO -> Quadruple(
            Color(0xFF2563EB), // Xanh dương
            Color(0xFF1D4ED8),
            Icons.Filled.Info,
            "Thông báo"
        )
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon hình tròn nền mờ
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(Color.White.copy(alpha = 0.22f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Nội dung thông báo
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = labelTitle,
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.4.sp
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = toast.message,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 17.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Nút đóng nhanh
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Đóng",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

