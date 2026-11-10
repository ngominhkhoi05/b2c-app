package com.example.b2capp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO đại diện cho một sản phẩm trong Danh sách yêu thích (Wishlist) từ Backend:
 * GET /api/v1/wishlist
 */
@JsonClass(generateAdapter = true)
data class WishlistItemDto(
    @Json(name = "id") val id: Long,
    @Json(name = "productId") val productId: Long,
    @Json(name = "productSlug") val productSlug: String? = null,
    @Json(name = "productName") val productName: String,
    @Json(name = "productThumbnail") val productThumbnail: String? = null,
    @Json(name = "minPrice") val minPrice: Double? = null,
    @Json(name = "maxPrice") val maxPrice: Double? = null,
    @Json(name = "sellerName") val sellerName: String? = null,
    @Json(name = "sellerId") val sellerId: Long? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "addedAt") val addedAt: String? = null
)

/**
 * Phản hồi khi thêm/xóa/kiểm tra trạng thái yêu thích sản phẩm:
 * POST /api/v1/wishlist/products/{productId}
 * GET /api/v1/wishlist/products/{productId}/check
 */
@JsonClass(generateAdapter = true)
data class WishlistToggleResponse(
    @Json(name = "wishlisted") val wishlisted: Boolean,
    @Json(name = "productId") val productId: Long
)
