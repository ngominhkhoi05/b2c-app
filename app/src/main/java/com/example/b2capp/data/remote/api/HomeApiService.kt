package com.example.b2capp.data.remote.api

import com.example.b2capp.data.remote.dto.ApiResponse
import com.example.b2capp.data.remote.dto.CategoryDto
import com.example.b2capp.data.remote.dto.PageResponse
import com.example.b2capp.data.remote.dto.ProductSummaryDto
import com.example.b2capp.data.remote.dto.PromotionDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Các API công khai dùng cho màn hình Trang chủ:
 * - Danh mục sản phẩm (Roots / All)
 * - Sản phẩm bán chạy (Featured)
 * - Sản phẩm gợi ý (Search/Listing với phân trang)
 * - Khuyến mãi công khai (Promotions / Banners)
 */
interface HomeApiService {

    @GET("api/v1/categories/roots")
    suspend fun getRootCategories(): Response<ApiResponse<List<CategoryDto>>>

    @GET("api/v1/categories")
    suspend fun getAllCategories(): Response<ApiResponse<List<CategoryDto>>>

    @GET("api/v1/products/featured")
    suspend fun getFeaturedProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<ApiResponse<PageResponse<ProductSummaryDto>>>

    @GET("api/v1/products")
    suspend fun getProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "created",
        @Query("sortDir") sortDir: String = "desc",
        @Query("categoryId") categoryId: Long? = null,
        @Query("q") query: String? = null
    ): Response<ApiResponse<PageResponse<ProductSummaryDto>>>

    @GET("api/v1/promotions")
    suspend fun getPublicPromotions(): Response<ApiResponse<List<PromotionDto>>>
}

