package com.example.b2capp.data.remote.api

import com.example.b2capp.data.remote.dto.AddCartItemRequestDto
import com.example.b2capp.data.remote.dto.ApiResponse
import com.example.b2capp.data.remote.dto.BulkDeleteCartItemRequestDto
import com.example.b2capp.data.remote.dto.CartItemDto
import com.example.b2capp.data.remote.dto.UpdateCartItemRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Các API giỏ hàng phục vụ cho Buyer:
 * - GET /api/v1/cart: Lấy danh sách sản phẩm trong giỏ hàng
 * - POST /api/v1/cart/items: Thêm sản phẩm vào giỏ hàng
 * - PATCH /api/v1/cart/items/{id}: Cập nhật số lượng hoặc chọn/bỏ chọn
 * - DELETE /api/v1/cart/items/{id}: Xóa một sản phẩm
 * - DELETE /api/v1/cart/items: Xóa nhiều sản phẩm theo danh sách ID
 * - GET /api/v1/cart/count: Lấy tổng số lượng sản phẩm trong giỏ hàng
 */
interface CartApiService {

    @GET("api/v1/cart")
    suspend fun getCartItems(): Response<ApiResponse<List<CartItemDto>>>

    @POST("api/v1/cart/items")
    suspend fun addCartItem(
        @Body request: AddCartItemRequestDto
    ): Response<ApiResponse<CartItemDto>>

    @PATCH("api/v1/cart/items/{id}")
    suspend fun updateCartItem(
        @Path("id") id: Long,
        @Body request: UpdateCartItemRequestDto
    ): Response<ApiResponse<CartItemDto>>

    @DELETE("api/v1/cart/items/{id}")
    suspend fun removeCartItem(
        @Path("id") id: Long
    ): Response<ApiResponse<Unit>>

    @DELETE("api/v1/cart/items")
    suspend fun bulkRemoveCartItems(
        @Body request: BulkDeleteCartItemRequestDto
    ): Response<ApiResponse<Unit>>

    @GET("api/v1/cart/count")
    suspend fun getCartCount(): Response<ApiResponse<Map<String, Long>>>
}
