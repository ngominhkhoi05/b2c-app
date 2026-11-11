package com.example.b2capp.core.model

/**
 * Thông tin đơn hàng sau khi hoàn tất thanh toán thành công (dữ liệu từ Backend).
 */
data class OrderSuccessInfo(
    val rawOrderId: Long? = null,
    val orderId: String = "",
    val paymentMethodName: String = "",
    val isOnlinePaid: Boolean = false,
    val orderTime: String = "",
    val expectedDelivery: String = "",
    val totalAmount: Long = 0L,
    val recipientName: String = "",
    val recipientPhoneMasked: String = "",
    val deliveryAddress: String = "",
    val paymentUrl: String? = null
)
