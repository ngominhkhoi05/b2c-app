package com.example.b2capp.core.model

/**
 * Thông tin chi tiết của một Shop / Gian hàng trong ứng dụng B2C.
 */
data class ShopInfo(
    val shopName: String,
    val sellerId: Long? = null,
    val shopSlug: String? = null,
    val shopLogoUrl: String? = null,
    val bannerUrl: String? = null,
    val description: String? = null,
    val shopLocation: String = "Việt Nam",
    val rating: Float = 4.9f,
    val ratingCount: Int = 0,
    val followerCount: String = "0",
    val productCount: Int = 0,
    val isOnline: Boolean = true,
    val verified: Boolean = true,
    val responseRate: String = "99%",
    val responseTime: String = "vài phút"
)
