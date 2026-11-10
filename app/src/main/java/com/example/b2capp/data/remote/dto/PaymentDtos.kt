package com.example.b2capp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request callback xác nhận giao dịch thanh toán (POST /api/v1/payments/callback)
 */
@JsonClass(generateAdapter = true)
data class PaymentCallbackRequestDto(
    @Json(name = "orderId") val orderId: Long,
    @Json(name = "transactionId") val transactionId: String,
    @Json(name = "status") val status: String, // "SUCCESS" | "FAILED"
    @Json(name = "provider") val provider: String? = "VNPAY",
    @Json(name = "signature") val signature: String? = null
)

/**
 * Bản ghi thanh toán trả về từ Backend (PaymentDto)
 */
@JsonClass(generateAdapter = true)
data class PaymentDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "orderId") val orderId: Long? = null,
    @Json(name = "method") val method: String? = null,
    @Json(name = "provider") val provider: String? = null,
    @Json(name = "transactionId") val transactionId: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "paidAt") val paidAt: String? = null,
    @Json(name = "refundedAt") val refundedAt: String? = null,
    @Json(name = "refundAmount") val refundAmount: Double? = null
)
