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


/**
 * DTO Ảnh chi tiết sản phẩm
 */
@JsonClass(generateAdapter = true)
data class ProductImageDto(
    @Json(name = "id") val id: Long,
    @Json(name = "imageUrl") val imageUrl: String,
    @Json(name = "altText") val altText: String? = null,
    @Json(name = "sortOrder") val sortOrder: Int? = null,
    @Json(name = "isThumbnail") val isThumbnail: Boolean? = null
)

/**
 * DTO Giá trị của một phân loại (ví dụ: Màu sắc -> "Đen", "Trắng")
 */
@JsonClass(generateAdapter = true)
data class ProductOptionValueDto(
    @Json(name = "id") val id: Long,
    @Json(name = "value") val value: String,
    @Json(name = "imageUrl") val imageUrl: String? = null,
    @Json(name = "sortOrder") val sortOrder: Int? = null
)

/**
 * DTO Nhóm phân loại (ví dụ: "Màu sắc", "Kích cỡ")
 */
@JsonClass(generateAdapter = true)
data class ProductOptionDto(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "sortOrder") val sortOrder: Int? = null,
    @Json(name = "values") val values: List<ProductOptionValueDto> = emptyList()
)

/**
 * DTO Biến thể sản phẩm (SKU variant)
 */
@JsonClass(generateAdapter = true)
data class ProductVariantDto(
    @Json(name = "id") val id: Long,
    @Json(name = "sku") val sku: String? = null,
    @Json(name = "variantName") val variantName: String? = null,
    @Json(name = "price") val price: Double? = null,
    @Json(name = "salePrice") val salePrice: Double? = null,
    @Json(name = "stockQuantity") val stockQuantity: Int? = null,
    @Json(name = "soldCount") val soldCount: Int? = null,
    @Json(name = "imageUrl") val imageUrl: String? = null,
    @Json(name = "weightGram") val weightGram: Int? = null,
    @Json(name = "barcode") val barcode: String? = null,
    @Json(name = "isActive") val isActive: Boolean? = null,
    @Json(name = "optionValueIds") val optionValueIds: List<Long>? = null,
    @Json(name = "attributes") val attributes: Map<String, String>? = null
)

/**
 * DTO Chi tiết sản phẩm từ GET /api/v1/products/id/{id} hoặc /{slug}
 */
@JsonClass(generateAdapter = true)
data class ProductDetailDto(
    @Json(name = "id") val id: Long,
    @Json(name = "slug") val slug: String? = null,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "brand") val brand: String? = null,
    @Json(name = "sellerId") val sellerId: Long? = null,
    @Json(name = "shopName") val shopName: String? = null,
    @Json(name = "shopSlug") val shopSlug: String? = null,
    @Json(name = "shopLogoUrl") val shopLogoUrl: String? = null,
    @Json(name = "sellerRatingAvg") val sellerRatingAvg: Double? = null,
    @Json(name = "shopRatingCount") val shopRatingCount: Int? = null,
    @Json(name = "shopProductCount") val shopProductCount: Int? = null,
    @Json(name = "shopIsOnline") val shopIsOnline: Boolean? = null,
    @Json(name = "categoryId") val categoryId: Long? = null,
    @Json(name = "categoryName") val categoryName: String? = null,
    @Json(name = "minPrice") val minPrice: Double? = null,
    @Json(name = "maxPrice") val maxPrice: Double? = null,
    @Json(name = "thumbnailUrl") val thumbnailUrl: String? = null,
    @Json(name = "images") val images: List<ProductImageDto> = emptyList(),
    @Json(name = "variants") val variants: List<ProductVariantDto> = emptyList(),
    @Json(name = "options") val options: List<ProductOptionDto> = emptyList(),
    @Json(name = "ratingAvg") val ratingAvg: Double? = null,
    @Json(name = "ratingCount") val ratingCount: Int? = null,
    @Json(name = "soldCount") val soldCount: Int? = null,
    @Json(name = "viewCount") val viewCount: Long? = null,
    @Json(name = "weightGram") val weightGram: Int? = null,
    @Json(name = "lengthCm") val lengthCm: Double? = null,
    @Json(name = "widthCm") val widthCm: Double? = null,
    @Json(name = "heightCm") val heightCm: Double? = null,
    @Json(name = "status") val status: String? = null
)
