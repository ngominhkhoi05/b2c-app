package com.example.b2capp.data.remote.api

import com.example.b2capp.data.remote.dto.ApiResponse
import com.example.b2capp.data.remote.dto.PaymentCallbackRequestDto
import com.example.b2capp.data.remote.dto.PaymentDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit API Service cho các nghiệp vụ Thanh toán (Payment Gateway & Callback)
 */
interface PaymentApiService {

    /**
     * Xác nhận thanh toán qua callback cổng thanh toán (POST /api/v1/payments/callback)
     * Backend xử lý chuyển trạng thái đơn hàng sang PAID và CONFIRMED.
     */
    @POST("api/v1/payments/callback")
    suspend fun confirmPayment(
        @Body request: PaymentCallbackRequestDto,
        @Header("Idempotency-Key") idempotencyKey: String? = null
    ): Response<ApiResponse<PaymentDto>>

    /**
     * Lấy danh sách bản ghi thanh toán cho đơn hàng (GET /api/v1/payments/orders/{orderId})
     */
    @GET("api/v1/payments/orders/{orderId}")
    suspend fun listPaymentsForOrder(
        @Path("orderId") orderId: Long
    ): Response<ApiResponse<List<PaymentDto>>>
}
