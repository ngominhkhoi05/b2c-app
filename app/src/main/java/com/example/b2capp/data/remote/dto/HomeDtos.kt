package com.example.b2capp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Phản hồi phân trang chuẩn từ Spring Boot PageResponse<T>
 */
@JsonClass(generateAdapter = true)
data class PageResponse<T>(
    @Json(name = "content") val content: List<T> = emptyList(),
    @Json(name = "page") val page: Int = 0,
    @Json(name = "size") val size: Int = 20,
    @Json(name = "totalElements") val totalElements: Long = 0,
    @Json(name = "totalPages") val totalPages: Int = 0,
    @Json(name = "first") val first: Boolean = true,
    @Json(name = "last") val last: Boolean = true
)

/**
 * DTO Danh mục sản phẩm từ GET /api/v1/categories hoặc /roots
 */
@JsonClass(generateAdapter = true)
data class CategoryDto(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "slug") val slug: String,
    @Json(name = "iconUrl") val iconUrl: String? = null,
    @Json(name = "level") val level: Int? = null,
    @Json(name = "path") val path: String? = null,
    @Json(name = "parentId") val parentId: Long? = null,
    @Json(name = "children") val children: List<CategoryDto>? = null
)

/**
 * DTO Tóm tắt sản phẩm trong danh sách và tìm kiếm từ GET /api/v1/products và /featured
 */
@JsonClass(generateAdapter = true)
data class ProductSummaryDto(
    @Json(name = "id") val id: Long,
    @Json(name = "slug") val slug: String? = null,
    @Json(name = "name") val name: String,
    @Json(name = "minPrice") val minPrice: Double? = null,
    @Json(name = "maxPrice") val maxPrice: Double? = null,
    @Json(name = "thumbnailUrl") val thumbnailUrl: String? = null,
    @Json(name = "brand") val brand: String? = null,
    @Json(name = "ratingAvg") val ratingAvg: Double? = null,
    @Json(name = "ratingCount") val ratingCount: Int? = null,
    @Json(name = "soldCount") val soldCount: Int? = null,
    @Json(name = "sellerId") val sellerId: Long? = null,
    @Json(name = "shopName") val shopName: String? = null,
    @Json(name = "categoryId") val categoryId: Long? = null,
    @Json(name = "categoryName") val categoryName: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "createdAt") val createdAt: String? = null
)

/**
 * DTO Khuyến mãi từ GET /api/v1/promotions
 */
@JsonClass(generateAdapter = true)
data class PromotionDto(
    @Json(name = "id") val id: Long,
    @Json(name = "sellerId") val sellerId: Long? = null,
    @Json(name = "code") val code: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "discountType") val discountType: String? = null, // PERCENT, FIXED_AMOUNT
    @Json(name = "discountValue") val discountValue: Double? = null,
    @Json(name = "maxDiscountAmount") val maxDiscountAmount: Double? = null,
    @Json(name = "minOrderValue") val minOrderValue: Double? = null,
    @Json(name = "quantity") val quantity: Int? = null,
    @Json(name = "usedCount") val usedCount: Int? = null,
    @Json(name = "remainingQuantity") val remainingQuantity: Int? = null,
    @Json(name = "limitPerUser") val limitPerUser: Int? = null,
    @Json(name = "applyScope") val applyScope: String? = null,
    @Json(name = "startAt") val startAt: String? = null,
    @Json(name = "endAt") val endAt: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "createdAt") val createdAt: String? = null
)

