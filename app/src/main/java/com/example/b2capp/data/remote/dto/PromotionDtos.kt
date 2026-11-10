package com.example.b2capp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Yêu cầu kiểm tra & tính toán mã giảm giá trước khi đặt hàng (POST /api/v1/buyer/promotions/validate)
 */
@JsonClass(generateAdapter = true)
data class ApplyPromotionRequestDto(
    @Json(name = "code") val code: String,
    @Json(name = "subtotal") val subtotal: Double
)

/**
 * Kết quả kiểm tra mã giảm giá
 */
@JsonClass(generateAdapter = true)
data class ApplyPromotionResponseDto(
    @Json(name = "code") val code: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "discountType") val discountType: String? = null,
    @Json(name = "discountAmount") val discountAmount: Double? = null,
    @Json(name = "remainingBalance") val remainingBalance: Double? = null,
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "message") val message: String? = null
)
