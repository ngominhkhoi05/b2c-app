package com.example.b2capp.data.repository

import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.api.PaymentApiService
import com.example.b2capp.data.remote.dto.PaymentCallbackRequestDto
import com.example.b2capp.data.remote.dto.PaymentDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository xử lý các tương tác thanh toán và cổng VNPAY
 */
class PaymentRepository(
    private val paymentApiService: PaymentApiService = ApiClient.paymentApiService
) {

    /**
     * Xác nhận hoàn tất thanh toán cho đơn hàng
     * Gọi endpoint POST /api/v1/payments/callback
     */
    suspend fun confirmPayment(
        orderId: Long,
        transactionId: String,
        status: String = "SUCCESS",
        provider: String = "VNPAY"
    ): Result<PaymentDto> = withContext(Dispatchers.IO) {
        try {
            val req = PaymentCallbackRequestDto(
                orderId = orderId,
                transactionId = transactionId,
                status = status,
                provider = provider
            )
            val response = paymentApiService.confirmPayment(req)
            if (response.isSuccessful) {
                val body = response.body()
                val data = body?.data
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.success(
                        PaymentDto(
                            orderId = orderId,
                            status = status,
                            transactionId = transactionId,
                            provider = provider
                        )
                    )
                }
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lấy danh sách giao dịch thanh toán của đơn hàng
     */
    suspend fun listPaymentsForOrder(orderId: Long): Result<List<PaymentDto>> = withContext(Dispatchers.IO) {
        try {
            val response = paymentApiService.listPaymentsForOrder(orderId)
            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
