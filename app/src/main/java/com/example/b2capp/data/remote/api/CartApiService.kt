package com.example.b2capp.data.remote.api

import com.example.b2capp.data.remote.dto.ApiResponse
import retrofit2.Response
import retrofit2.http.GET

/**
 * Các API giỏ hàng phục vụ cho Buyer:
 * - GET /api/v1/cart/count: Lấy số lượng giỏ hàng để hiển thị badge
 */
interface CartApiService {

    @GET("api/v1/cart/count")
    suspend fun getCartCount(): Response<ApiResponse<Map<String, Long>>>
}

