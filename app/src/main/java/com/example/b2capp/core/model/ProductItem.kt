package com.example.b2capp.core.model

import java.text.NumberFormat
import java.util.Locale

/**
 * Data model for B2C Products.
 */
data class ProductItem(
    val id: String,
    val name: String,
    val imageUrl: String,
    val price: Long,
    val originalPrice: Long? = null,
    val rating: Float = 5.0f,
    val soldCount: Int = 0,
    val isFreeShipping: Boolean = true,
    val stock: Int = 99,
    val category: String = "Thời trang",
    val description: String = "",
    val shopName: String = "Coolmate Official Store",
    val shopLocation: String = "Hà Nội"
) {
    val formattedPrice: String
        get() {
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            return "₫${formatter.format(price)}"
        }

    val formattedOriginalPrice: String?
        get() = originalPrice?.let {
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            return@let "₫${formatter.format(it)}"
        }

    val discountPercent: Int?
        get() {
            if (originalPrice != null && originalPrice > price) {
                return (((originalPrice - price).toDouble() / originalPrice) * 100).toInt()
            }
            return null
        }

    val formattedSoldCount: String
        get() {
            return if (soldCount >= 1000) {
                val k = soldCount / 1000.0
                String.format(Locale.US, "Đã bán %.1fk", k)
            } else {
                "Đã bán $soldCount"
            }
        }
}

