package com.example.b2capp.data.remote.api

import com.example.b2capp.data.remote.dto.ApiResponse
import com.example.b2capp.data.remote.dto.PageResponse
import com.example.b2capp.data.remote.dto.ProductSummaryDto
import com.example.b2capp.data.remote.dto.PublicShopDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Service API dành cho gian hàng / Shop (công khai):
 * - Lấy hồ sơ shop theo slug hoặc sellerId: GET /api/v1/public/shops/{slugOrId}
 * - Lấy danh sách sản phẩm của shop: GET /api/v1/public/shops/{slugOrId}/products
 * - Lấy danh sách sản phẩm theo sellerId: GET /api/v1/products/seller/{sellerId}
 */
interface ShopApiService {

    @GET("api/v1/public/shops/{slugOrId}")
    suspend fun getShopProfile(
        @Path("slugOrId") slugOrId: String
    ): Response<ApiResponse<PublicShopDto>>

    @GET("api/v1/public/shops/{slugOrId}/products")
    suspend fun getShopProducts(
        @Path("slugOrId") slugOrId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50,
        @Query("sort") sort: String? = null,
        @Query("categoryId") categoryId: Long? = null
    ): Response<ApiResponse<PageResponse<ProductSummaryDto>>>

    @GET("api/v1/products/seller/{sellerId}")
    suspend fun getProductsBySellerId(
        @Path("sellerId") sellerId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): Response<ApiResponse<PageResponse<ProductSummaryDto>>>
}

