package com.example.b2capp.data.remote.dto

import androidx.compose.ui.graphics.Color
import com.example.b2capp.core.model.ReviewItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@JsonClass(generateAdapter = true)
data class ReviewDto(
    @Json(name = "id") val id: Long,
    @Json(name = "orderItemId") val orderItemId: Long? = null,
    @Json(name = "productId") val productId: Long? = null,
    @Json(name = "productName") val productName: String? = null,
    @Json(name = "productSlug") val productSlug: String? = null,
    @Json(name = "productThumbnail") val productThumbnail: String? = null,
    @Json(name = "variantId") val variantId: Long? = null,
    @Json(name = "variantName") val variantName: String? = null,
    @Json(name = "reviewerName") val reviewerName: String? = null,
    @Json(name = "reviewerAvatar") val reviewerAvatar: String? = null,
    @Json(name = "sellerId") val sellerId: Long? = null,
    @Json(name = "shopName") val shopName: String? = null,
    @Json(name = "rating") val rating: Int? = 5,
    @Json(name = "comment") val comment: String? = null,
    @Json(name = "imageUrls") val imageUrls: String? = null,
    @Json(name = "videoUrl") val videoUrl: String? = null,
    @Json(name = "isAnonymous") val isAnonymous: Boolean? = null,
    @Json(name = "sellerReply") val sellerReply: String? = null,
    @Json(name = "repliedAt") val repliedAt: String? = null,
    @Json(name = "likeCount") val likeCount: Int? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "createdAt") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class CreateReviewRequestDto(
    @Json(name = "orderItemId") val orderItemId: Long,
    @Json(name = "rating") val rating: Int,
    @Json(name = "comment") val comment: String? = null,
    @Json(name = "imageUrls") val imageUrls: String? = null,
    @Json(name = "videoUrl") val videoUrl: String? = null,
    @Json(name = "isAnonymous") val isAnonymous: Boolean? = false
)

private val AVATAR_BG_COLORS = listOf(
    Color(0xFFFFD4C1),
    Color(0xFFD6E4FF),
    Color(0xFFA7F3D0),
    Color(0xFFFDE68A),
    Color(0xFFE9D5FF),
    Color(0xFFFED7AA)
)

/**
 * Phân tích an toàn chuỗi imageUrls trả về từ backend (JSON array hoặc comma-separated hoặc URL đơn lẻ)
 */
fun parseReviewImageUrls(raw: String?): List<String> {
    if (raw.isNullOrBlank() || raw == "null" || raw == "[]") return emptyList()
    val trimmed = raw.trim()
    return try {
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            val inner = trimmed.substring(1, trimmed.length - 1).trim()
            if (inner.isBlank()) return emptyList()
            inner.split(",")
                .map { it.trim().trim('"', '\'', '\\', ' ') }
                .filter { it.isNotBlank() && it.startsWith("http") }
        } else if (trimmed.startsWith("http")) {
            listOf(trimmed)
        } else {
            emptyList()
        }
    } catch (_: Exception) {
        emptyList()
    }
}

/**
 * Định dạng ngày giờ từ chuỗi ISO trả về từ backend sang ngày hiển thị thân thiện tiếng Việt
 */
fun formatReviewDate(isoString: String?): String {
    if (isoString.isNullOrBlank()) return "Gần đây"
    return try {
        val formats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.US),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US),
            SimpleDateFormat("yyyy-MM-dd", Locale.US)
        )
        var parsedDate: Date? = null
        for (f in formats) {
            try {
                f.timeZone = TimeZone.getTimeZone("UTC")
                parsedDate = f.parse(isoString)
                if (parsedDate != null) break
            } catch (_: Exception) {}
        }

        if (parsedDate != null) {
            val diffMs = System.currentTimeMillis() - parsedDate.time
            val diffDays = (diffMs / (1000 * 60 * 60 * 24)).toInt()
            when {
                diffDays <= 0 -> "Hôm nay"
                diffDays == 1 -> "1 ngày trước"
                diffDays in 2..30 -> "$diffDays ngày trước"
                else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(parsedDate)
            }
        } else {
            isoString.take(10)
        }
    } catch (_: Exception) {
        isoString.take(10)
    }
}

/**
 * Chuyển đổi ReviewDto thành domain model ReviewItem
 */
fun ReviewDto.toReviewItem(): ReviewItem {
    val name = if (isAnonymous == true) "Khách hàng ẩn danh" else reviewerName?.ifBlank { "Khách hàng" } ?: "Khách hàng"
    val initialChar = name.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
    val colorIndex = kotlin.math.abs(name.hashCode()) % AVATAR_BG_COLORS.size
    val bgColor = AVATAR_BG_COLORS[colorIndex]

    return ReviewItem(
        id = id.toString(),
        username = name,
        userAvatar = reviewerAvatar,
        initial = initialChar,
        avatarBgColor = bgColor,
        rating = rating ?: 5,
        date = formatReviewDate(createdAt),
        variantInfo = if (!variantName.isNullOrBlank()) "Phân loại: $variantName" else "",
        comment = comment?.trim() ?: "Đánh giá không có bình luận",
        imageUrls = parseReviewImageUrls(imageUrls),
        helpfulCount = likeCount ?: 0,
        sellerResponse = sellerReply?.ifBlank { null },
        isPurchased = true
    )
}
