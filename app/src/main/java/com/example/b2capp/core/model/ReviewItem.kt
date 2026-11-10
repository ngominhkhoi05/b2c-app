package com.example.b2capp.core.model

import androidx.compose.ui.graphics.Color

/**
 * Domain model đại diện cho một đánh giá sản phẩm trong ứng dụng
 */
data class ReviewItem(
    val id: String,
    val username: String,
    val userAvatar: String? = null,
    val initial: String = "U",
    val avatarBgColor: Color = Color(0xFFE0E7FF),
    val rating: Int = 5,
    val date: String = "",
    val variantInfo: String = "",
    val comment: String = "",
    val imageUrls: List<String> = emptyList(),
    val helpfulCount: Int = 0,
    val sellerResponse: String? = null,
    val isPurchased: Boolean = true
)
