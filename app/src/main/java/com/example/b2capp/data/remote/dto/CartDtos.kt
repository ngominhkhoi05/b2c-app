package com.example.b2capp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO đại diện cho sản phẩm trong giỏ hàng khớp với Backend CartItemDto
 */
@JsonClass(generateAdapter = true)
data class CartItemDto(
    @Json(name = "id") val id: Long,
    @Json(name = "variantId") val variantId: Long? = null,
    @Json(name = "variantName") val variantName: String? = null,
    @Json(name = "productName") val productName: String? = null,
    @Json(name = "thumbnailUrl") val thumbnailUrl: String? = null,
    @Json(name = "quantity") val quantity: Int = 1,
    @Json(name = "priceSnapshot") val priceSnapshot: Double? = null,
    @Json(name = "lineTotal") val lineTotal: Double? = null,
    @Json(name = "isSelected") val isSelected: Boolean = true,
    @Json(name = "addedAt") val addedAt: String? = null,
    @Json(name = "productId") val productId: Long? = null,
    @Json(name = "productSlug") val productSlug: String? = null,
    @Json(name = "sellerId") val sellerId: Long? = null,
    @Json(name = "shopName") val shopName: String? = null,
    @Json(name = "stockQuantity") val stockQuantity: Int? = null,
    @Json(name = "isAvailable") val isAvailable: Boolean? = null
)

val CartItemDto.shopDisplayName: String
    get() = shopName?.ifBlank { null }
        ?: sellerId?.let { "Cửa hàng #$it" }
        ?: "Cửa hàng chính hãng"

/**
 * DTO yêu cầu thêm sản phẩm vào giỏ hàng
 */
@JsonClass(generateAdapter = true)
data class AddCartItemRequestDto(
    @Json(name = "variantId") val variantId: Long,
    @Json(name = "quantity") val quantity: Int = 1
)

/**
 * DTO yêu cầu cập nhật số lượng hoặc trạng thái chọn của sản phẩm giỏ hàng
 */
@JsonClass(generateAdapter = true)
data class UpdateCartItemRequestDto(
    @Json(name = "quantity") val quantity: Int? = null,
    @Json(name = "isSelected") val isSelected: Boolean? = null
)

/**
 * DTO yêu cầu xóa nhiều sản phẩm giỏ hàng
 */
@JsonClass(generateAdapter = true)
data class BulkDeleteCartItemRequestDto(
    @Json(name = "cartItemIds") val cartItemIds: List<Long>
)
