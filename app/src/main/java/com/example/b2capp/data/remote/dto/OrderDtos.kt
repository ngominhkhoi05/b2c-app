package com.example.b2capp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Tóm tắt đơn hàng hiển thị trên danh sách đơn mua của Buyer (GET /api/v1/buyer/orders)
 */
@JsonClass(generateAdapter = true)
data class OrderSummaryDto(
    @Json(name = "id") val id: Long,
    @Json(name = "orderCode") val orderCode: String? = null,
    @Json(name = "buyerId") val buyerId: Long? = null,
    @Json(name = "sellerId") val sellerId: Long? = null,
    @Json(name = "receiverName") val receiverName: String? = null,
    @Json(name = "paymentMethod") val paymentMethod: String? = null,
    @Json(name = "paymentStatus") val paymentStatus: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "totalAmount") val totalAmount: Double? = null,
    @Json(name = "createdAt") val createdAt: String? = null,
    @Json(name = "shopName") val shopName: String? = null,
    @Json(name = "firstProductName") val firstProductName: String? = null,
    @Json(name = "firstProductThumbnail") val firstProductThumbnail: String? = null,
    @Json(name = "firstVariantName") val firstVariantName: String? = null,
    @Json(name = "totalItemsCount") val totalItemsCount: Int? = null
)

/**
 * Sản phẩm con trong đơn hàng
 */
@JsonClass(generateAdapter = true)
data class OrderItemDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "productId") val productId: Long? = null,
    @Json(name = "variantId") val variantId: Long? = null,
    @Json(name = "productName") val productName: String? = null,
    @Json(name = "variantName") val variantName: String? = null,
    @Json(name = "imageUrl") val imageUrl: String? = null,
    @Json(name = "unitPrice") val unitPrice: Double? = null,
    @Json(name = "quantity") val quantity: Int = 1,
    @Json(name = "discount") val discount: Double? = null,
    @Json(name = "totalPrice") val totalPrice: Double? = null,
    @Json(name = "isReviewed") val isReviewed: Boolean? = null
)

/**
 * Chi tiết toàn bộ thông tin đơn hàng và lộ trình (GET /api/v1/buyer/orders/{id})
 */
@JsonClass(generateAdapter = true)
data class OrderDetailDto(
    @Json(name = "id") val id: Long,
    @Json(name = "orderCode") val orderCode: String? = null,
    @Json(name = "buyerId") val buyerId: Long? = null,
    @Json(name = "sellerId") val sellerId: Long? = null,
    @Json(name = "receiverName") val receiverName: String? = null,
    @Json(name = "receiverPhone") val receiverPhone: String? = null,
    @Json(name = "shippingAddress") val shippingAddress: String? = null,
    @Json(name = "subtotal") val subtotal: Double? = null,
    @Json(name = "shippingFee") val shippingFee: Double? = null,
    @Json(name = "discountAmount") val discountAmount: Double? = null,
    @Json(name = "platformDiscount") val platformDiscount: Double? = null,
    @Json(name = "totalAmount") val totalAmount: Double? = null,
    @Json(name = "paymentMethod") val paymentMethod: String? = null,
    @Json(name = "paymentStatus") val paymentStatus: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "note") val note: String? = null,
    @Json(name = "cancelReason") val cancelReason: String? = null,
    @Json(name = "confirmedAt") val confirmedAt: String? = null,
    @Json(name = "shippedAt") val shippedAt: String? = null,
    @Json(name = "deliveredAt") val deliveredAt: String? = null,
    @Json(name = "completedAt") val completedAt: String? = null,
    @Json(name = "cancelledAt") val cancelledAt: String? = null,
    @Json(name = "createdAt") val createdAt: String? = null,
    @Json(name = "updatedAt") val updatedAt: String? = null,
    @Json(name = "items") val items: List<OrderItemDto>? = null,
    @Json(name = "paymentUrl") val paymentUrl: String? = null,
    @Json(name = "returnReason") val returnReason: String? = null,
    @Json(name = "returnDescription") val returnDescription: String? = null,
    @Json(name = "returnRequestedAt") val returnRequestedAt: String? = null,
    @Json(name = "returnApprovedAt") val returnApprovedAt: String? = null,
    @Json(name = "returnRejectedAt") val returnRejectedAt: String? = null,
    @Json(name = "returnAdminNote") val returnAdminNote: String? = null
)

/**
 * Request DTO tạo đơn hàng khi Checkout (POST /api/v1/buyer/orders)
 */
@JsonClass(generateAdapter = true)
data class CreateOrderRequestDto(
    @Json(name = "cartItemIds") val cartItemIds: List<Long>,
    @Json(name = "addressId") val addressId: Long,
    @Json(name = "paymentMethod") val paymentMethod: String,
    @Json(name = "voucherCode") val voucherCode: String? = null,
    @Json(name = "note") val note: String? = null,
    @Json(name = "clientIp") val clientIp: String? = null
)

/**
 * Request DTO xem trước tổng tiền đơn hàng (POST /api/v1/buyer/orders/preview)
 */
@JsonClass(generateAdapter = true)
data class PreviewOrderRequestDto(
    @Json(name = "cartItemIds") val cartItemIds: List<Long>,
    @Json(name = "addressId") val addressId: Long? = null,
    @Json(name = "voucherCode") val voucherCode: String? = null
)

/**
 * Phản hồi tính toán chi phí đơn hàng xem trước
 */
@JsonClass(generateAdapter = true)
data class OrderPreviewResponseDto(
    @Json(name = "subtotal") val subtotal: Double? = null,
    @Json(name = "shippingFee") val shippingFee: Double? = null,
    @Json(name = "discountAmount") val discountAmount: Double? = null,
    @Json(name = "totalAmount") val totalAmount: Double? = null
)

/**
 * Request DTO yêu cầu trả hàng / hoàn tiền (POST /api/v1/buyer/orders/{id}/return-request)
 */
@JsonClass(generateAdapter = true)
data class ReturnOrderRequestDto(
    @Json(name = "reason") val reason: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "orderItemIds") val orderItemIds: List<Long>? = null
)

