package com.example.b2capp.core.model

/**
 * Thông tin đơn hàng sau khi hoàn tất thanh toán thành công.
 */
data class OrderSuccessInfo(
    val orderId: String = "ORD-20261105-0088",
    val paymentMethodName: String = "Cổng VNPAY",
    val isOnlinePaid: Boolean = true,
    val orderTime: String = "05/11/2026 14:20",
    val expectedDelivery: String = "14 Th04 – 16 Th04",
    val totalAmount: Long = 388200L,
    val recipientName: String = "Nguyễn Văn A",
    val recipientPhoneMasked: String = "090****123",
    val deliveryAddress: String = "Số 45 Lê Duẩn, Phường Bến Nghé, Q.1, TP. HCM"
)

