package com.example.b2capp.data.remote.api

import com.example.b2capp.data.remote.dto.ApiResponse
import com.example.b2capp.data.remote.dto.ApplyPromotionRequestDto
import com.example.b2capp.data.remote.dto.ApplyPromotionResponseDto
import com.example.b2capp.data.remote.dto.PromotionDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Service Retrofit cho Promotion / Voucher API:
 * - GET /api/v1/buyer/promotions: Danh sách mã giảm giá khả dụng cho Buyer đã đăng nhập
 * - GET /api/v1/promotions: Danh sách mã giảm giá công khai cho khách vãng lai
 * - POST /api/v1/buyer/promotions/validate: Kiểm tra tính hợp lệ và số tiền giảm giá của mã
 */
interface PromotionApiService {

    @GET("api/v1/buyer/promotions")
    suspend fun getBuyerPromotions(): Response<ApiResponse<List<PromotionDto>>>

    @GET("api/v1/promotions")
    suspend fun getPublicPromotions(): Response<ApiResponse<List<PromotionDto>>>

    @POST("api/v1/buyer/promotions/validate")
    suspend fun validatePromotion(
        @Body request: ApplyPromotionRequestDto
    ): Response<ApiResponse<ApplyPromotionResponseDto>>
}
