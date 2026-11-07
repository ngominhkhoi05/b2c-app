package com.example.b2capp.core.model

import java.text.NumberFormat
import java.util.Locale

data class CartItem(
    val product: ProductItem,
    val quantity: Int
) {
    val totalPrice: Long
        get() = product.price * quantity

    val formattedTotalPrice: String
        get() {
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            return "₫${formatter.format(totalPrice)}"
        }
}

