package com.example.b2capp.data.remote.api

import com.example.b2capp.data.remote.dto.ApiResponse
import com.example.b2capp.data.remote.dto.PageResponse
import com.example.b2capp.data.remote.dto.WishlistItemDto
import com.example.b2capp.data.remote.dto.WishlistToggleResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Các API quản lý danh sách yêu thích (Wishlist) cho Buyer:
 * - GET /api/v1/wishlist: Lấy danh sách sản phẩm yêu thích (phân trang)
 * - POST /api/v1/wishlist/products/{productId}: Toggle thêm/xóa sản phẩm khỏi yêu thích
 * - DELETE /api/v1/wishlist/products/{productId}: Xóa sản phẩm khỏi danh sách yêu thích
 * - GET /api/v1/wishlist/products/{productId}/check: Kiểm tra sản phẩm đã yêu thích chưa
 * - GET /api/v1/wishlist/count: Đếm tổng số sản phẩm trong danh sách yêu thích
 */
interface WishlistApiService {

    @GET("api/v1/wishlist")
    suspend fun getWishlist(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<WishlistItemDto>>>

    @POST("api/v1/wishlist/products/{productId}")
    suspend fun toggleWishlist(
        @Path("productId") productId: Long
    ): Response<ApiResponse<WishlistToggleResponse>>

    @DELETE("api/v1/wishlist/products/{productId}")
    suspend fun removeFromWishlist(
        @Path("productId") productId: Long
    ): Response<Unit>

    @GET("api/v1/wishlist/products/{productId}/check")
    suspend fun checkWishlist(
        @Path("productId") productId: Long
    ): Response<ApiResponse<WishlistToggleResponse>>

    @GET("api/v1/wishlist/count")
    suspend fun getWishlistCount(): Response<ApiResponse<Map<String, Long>>>
}
