package com.example.b2capp.data.repository

import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.core.model.ShopInfo
import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.api.ShopApiService
import com.example.b2capp.data.remote.dto.PublicShopDto
import java.util.Locale

/**
 * Repository xử lý dữ liệu và API liên quan đến Gian hàng / Shop:
 * - Lấy thông tin hồ sơ Shop (Public Shop Profile)
 * - Lấy danh sách sản phẩm thực tế của Shop
 */
class ShopRepository(
    private val shopApiService: ShopApiService = ApiClient.shopApiService
) {

    /**
     * Tải thông tin hồ sơ chi tiết của Shop từ API Backend.
     * @param slugOrId Slug (ví dụ "coolmate-official") hoặc ID của seller (ví dụ "1")
     */
    suspend fun getShopProfile(slugOrId: String): Result<ShopInfo> {
        return try {
            val response = shopApiService.getShopProfile(slugOrId)
            if (response.isSuccessful && response.body()?.data != null) {
                val dto = response.body()!!.data!!
                Result.success(dto.toShopInfo())
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Tải danh sách tất cả sản phẩm của Shop từ API Backend.
     * Thử theo slug qua GET /api/v1/public/shops/{slug}/products,
     * nếu không có slug thì gọi theo sellerId qua GET /api/v1/products/seller/{sellerId}.
     */
    suspend fun getShopProducts(
        slug: String? = null,
        sellerId: Long? = null,
        page: Int = 0,
        size: Int = 50,
        sort: String? = null
    ): Result<List<ProductItem>> {
        return try {
            // 1. Thử gọi qua endpoint public shops bằng slug hoặc sellerId dạng chuỗi
            val identifier = slug?.ifBlank { null } ?: sellerId?.toString()
            if (!identifier.isNullOrBlank()) {
                val response = shopApiService.getShopProducts(
                    slugOrId = identifier,
                    page = page,
                    size = size,
                    sort = sort
                )
                if (response.isSuccessful && response.body()?.data != null) {
                    val pageData = response.body()!!.data!!
                    val items = pageData.content.map { it.toProductItem() }
                    return Result.success(items)
                }
            }

            // 2. Nếu có sellerId, thử gọi qua endpoint seller products
            if (sellerId != null) {
                val response = shopApiService.getProductsBySellerId(
                    sellerId = sellerId,
                    page = page,
                    size = size
                )
                if (response.isSuccessful && response.body()?.data != null) {
                    val pageData = response.body()!!.data!!
                    val items = pageData.content.map { it.toProductItem() }
                    return Result.success(items)
                }
            }

            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun PublicShopDto.toShopInfo(): ShopInfo {
        val followersFormatted = when {
            followerCount == null || followerCount == 0 -> "1.2k"
            followerCount >= 1000 -> String.format(Locale.US, "%.1fk", followerCount / 1000.0)
            else -> followerCount.toString()
        }

        val resolvedName = shopName?.ifBlank { null } ?: name?.ifBlank { null } ?: "Gian hàng chính hãng"
        val resolvedLocation = address?.ifBlank { null } ?: "Việt Nam"

        return ShopInfo(
            shopName = resolvedName,
            sellerId = id,
            shopSlug = slug,
            shopLogoUrl = logoUrl,
            bannerUrl = bannerUrl,
            description = description,
            shopLocation = resolvedLocation,
            rating = (ratingAvg ?: rating ?: 4.9).toFloat(),
            ratingCount = ratingCount ?: 0,
            followerCount = followersFormatted,
            productCount = (productCount ?: 0L).toInt(),
            isOnline = isOnline ?: false,
            verified = verified,
            responseRate = responseRate?.ifBlank { "99%" } ?: "99%",
            responseTime = responseTime?.ifBlank { "vài phút" } ?: "vài phút"
        )
    }
}

