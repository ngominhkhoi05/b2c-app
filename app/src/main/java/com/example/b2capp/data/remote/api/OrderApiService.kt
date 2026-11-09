package com.example.b2capp.data.remote.api

import com.example.b2capp.data.remote.dto.ApiResponse
import com.example.b2capp.data.remote.dto.CreateOrderRequestDto
import com.example.b2capp.data.remote.dto.OrderDetailDto
import com.example.b2capp.data.remote.dto.OrderSummaryDto
import com.example.b2capp.data.remote.dto.PageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Service định nghĩa các API Đơn hàng của người mua (Buyer Orders):
 * - GET /api/v1/buyer/orders : Danh sách đơn hàng (hỗ trợ lọc status, phân trang)
 * - GET /api/v1/buyer/orders/{id} : Chi tiết đơn hàng và các mặt hàng bên trong
 * - PATCH /api/v1/buyer/orders/{id}/cancel : Hủy đơn hàng khi đang PENDING / CONFIRMED
 * - POST /api/v1/buyer/orders/{id}/pay : Lấy lại link thanh toán VNPay cho đơn chưa thanh toán
 * - POST /api/v1/buyer/orders : Tạo đơn hàng mới từ giỏ hàng (Checkout)
 */
interface OrderApiService {

    @GET("api/v1/buyer/orders")
    suspend fun getOrders(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<OrderSummaryDto>>>

    @GET("api/v1/buyer/orders/{id}")
    suspend fun getOrderDetail(
        @Path("id") id: Long
    ): Response<ApiResponse<OrderDetailDto>>

    @PATCH("api/v1/buyer/orders/{id}/cancel")
    suspend fun cancelOrder(
        @Path("id") id: Long,
        @Query("reason") reason: String? = null
    ): Response<ApiResponse<OrderDetailDto>>

    @POST("api/v1/buyer/orders/{id}/pay")
    suspend fun retryPayment(
        @Path("id") id: Long
    ): Response<ApiResponse<Map<String, String>>>

    @POST("api/v1/buyer/orders")
    suspend fun createOrder(
        @Body request: CreateOrderRequestDto,
        @Header("Idempotency-Key") idempotencyKey: String? = null
    ): Response<ApiResponse<OrderDetailDto>>
}
