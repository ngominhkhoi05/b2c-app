package com.example.b2capp.core.model

/**
 * Thông tin phiên trò chuyện trực tiếp với Shop.
 */
data class ShopChatInfo(
    val shopName: String = "Coolmate Official Store",
    val isMall: Boolean = true,
    val isOnline: Boolean = true,
    val product: ProductItem? = null
)

