package com.example.b2capp.core.model

import java.text.NumberFormat
import java.util.Locale

/**
 * Giá trị của một tùy chọn phân loại (ví dụ: "Đen Nhám (Matte Black)")
 */
data class ProductOptionValueItem(
    val id: Long,
    val value: String,
    val imageUrl: String? = null,
    val sortOrder: Int = 0
)

/**
 * Nhóm tùy chọn phân loại (ví dụ: "Màu sắc", "Kích cỡ")
 */
data class ProductOptionItem(
    val id: Long,
    val name: String,
    val sortOrder: Int = 0,
    val values: List<ProductOptionValueItem> = emptyList()
)

/**
 * Biến thể cụ thể của sản phẩm (SKU variant) với giá bán và kho riêng
 */
data class ProductVariantItem(
    val id: Long,
    val sku: String? = null,
    val variantName: String? = null,
    val price: Long,
    val salePrice: Long,
    val stockQuantity: Int = 0,
    val soldCount: Int = 0,
    val imageUrl: String? = null,
    val optionValueIds: List<Long> = emptyList(),
    val attributes: Map<String, String> = emptyMap()
) {
    val formattedPrice: String
        get() {
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            return "₫${formatter.format(salePrice)}"
        }

    val formattedOriginalPrice: String?
        get() = if (price > salePrice) {
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            "₫${formatter.format(price)}"
        } else null

    val discountPercent: Int?
        get() = if (price > salePrice) {
            (((price - salePrice).toDouble() / price) * 100).toInt()
        } else null
}

/**
 * Data model for B2C Products.
 */
data class ProductItem(
    val id: String,
    val name: String,
    val imageUrl: String,
    val price: Long,
    val originalPrice: Long? = null,
    val rating: Float = 0f,
    val soldCount: Int = 0,
    val isFreeShipping: Boolean = true,
    val stock: Int = 99,
    val category: String = "Thời trang",
    val description: String = "",
    val shopName: String = "Coolmate Official Store",
    val shopLocation: String = "Hà Nội",
    val images: List<String> = emptyList(),
    val options: List<ProductOptionItem> = emptyList(),
    val variants: List<ProductVariantItem> = emptyList(),
    val sellerId: Long? = null,
    val shopSlug: String? = null,
    val shopLogoUrl: String? = null,
    val ratingCount: Int = 0
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

    /**
     * Tìm variant khớp với tập các optionValueId được người dùng chọn
     */
    fun findVariantByOptionValues(selectedOptionValueIds: Collection<Long>): ProductVariantItem? {
        if (variants.isEmpty()) return null
        if (selectedOptionValueIds.isEmpty()) return variants.firstOrNull()

        return variants.firstOrNull { variant ->
            // Biến thể khớp nếu tất cả optionValueIds của biến thể đều nằm trong danh sách đã chọn
            // hoặc danh sách đã chọn chứa tất cả optionValueIds của biến thể
            variant.optionValueIds.isNotEmpty() &&
                selectedOptionValueIds.containsAll(variant.optionValueIds)
        } ?: variants.firstOrNull()
    }
}

