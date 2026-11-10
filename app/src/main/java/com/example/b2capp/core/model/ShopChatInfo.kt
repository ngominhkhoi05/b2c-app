package com.example.b2capp.core.model

import androidx.compose.ui.graphics.Color

/**
 * Thông tin phiên trò chuyện trực tiếp với Shop.
 */
data class ShopChatInfo(
    val conversationId: Long? = null,
    val sellerId: Long? = null,
    val shopName: String = "Coolmate Official Store",
    val avatarLetter: String? = null,
    val avatarBg: Color? = null,
    val avatarUrl: String? = null,
    val isMall: Boolean = true,
    val isOnline: Boolean = true,
    val product: ProductItem? = null
)
