package com.example

import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.dto.OrderDetailDto
import com.example.b2capp.data.remote.dto.OrderSummaryDto
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.OrderStatusCounts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OrderApiTest {

    private val moshi = ApiClient.moshi

    @Test
    fun testOrderSummaryDto_fromJson() {
        val json = """
            {
                "id": 101,
                "orderCode": "ORD-2026-101",
                "status": "PENDING",
                "paymentMethod": "COD",
                "paymentStatus": "UNPAID",
                "totalAmount": 450000.0,
                "shopName": "Official Apple Store",
                "firstProductName": "Tai nghe AirPods Pro 2",
                "firstProductThumbnail": "https://example.com/airpods.jpg",
                "firstVariantName": "Trắng",
                "totalItemsCount": 2,
                "createdAt": "2026-11-09T10:00:00"
            }
        """.trimIndent()

        val adapter = moshi.adapter(OrderSummaryDto::class.java)
        val dto = adapter.fromJson(json)

        assertNotNull(dto)
        assertEquals(101L, dto?.id)
        assertEquals("ORD-2026-101", dto?.orderCode)
        assertEquals("PENDING", dto?.status)
        assertEquals(450000.0, dto?.totalAmount ?: 0.0, 0.001)
        assertEquals("Official Apple Store", dto?.shopName)
        assertEquals(2, dto?.totalItemsCount)
    }

    @Test
    fun testOrderDetailDto_fromJson() {
        val json = """
            {
                "id": 101,
                "orderCode": "ORD-2026-101",
                "receiverName": "Nguyen Van A",
                "receiverPhone": "0987654321",
                "shippingAddress": "123 Le Loi, Q1, TP.HCM",
                "note": "Giao gio hanh chinh",
                "status": "SHIPPING",
                "paymentMethod": "VNPAY",
                "paymentStatus": "PAID",
                "subtotalAmount": 400000.0,
                "shippingFee": 30000.0,
                "discountAmount": 10000.0,
                "totalAmount": 420000.0,
                "createdAt": "2026-11-09T10:00:00",
                "confirmedAt": "2026-11-09T10:30:00",
                "shippedAt": "2026-11-09T11:00:00",
                "items": [
                    {
                        "id": 1,
                        "productName": "Chuột Gaming Không Dây",
                        "variantName": "Đen",
                        "sku": "MOUSE-BLK-01",
                        "thumbnailUrl": "https://example.com/mouse.jpg",
                        "unitPrice": 400000.0,
                        "quantity": 1,
                        "totalPrice": 400000.0
                    }
                ]
            }
        """.trimIndent()

        val adapter = moshi.adapter(OrderDetailDto::class.java)
        val dto = adapter.fromJson(json)

        assertNotNull(dto)
        assertEquals(101L, dto?.id)
        assertEquals("Nguyen Van A", dto?.receiverName)
        assertEquals("SHIPPING", dto?.status)
        assertEquals("VNPAY", dto?.paymentMethod)
        assertEquals(420000.0, dto?.totalAmount ?: 0.0, 0.001)
        assertEquals(1, dto?.items?.size)
        assertEquals("Chuột Gaming Không Dây", dto?.items?.first()?.productName)
    }

    @Test
    fun testOrderStatusCounts_andUiStateBadges() {
        val sampleOrders = listOf(
            OrderSummaryDto(id = 1, status = "PENDING"),
            OrderSummaryDto(id = 2, status = "CONFIRMED"),
            OrderSummaryDto(id = 3, status = "PACKED"),
            OrderSummaryDto(id = 4, status = "SHIPPING"),
            OrderSummaryDto(id = 5, status = "SHIPPING"),
            OrderSummaryDto(id = 6, status = "DELIVERED"),
            OrderSummaryDto(id = 7, status = "COMPLETED"),
            OrderSummaryDto(id = 8, status = "CANCELLED")
        )

        val pending = sampleOrders.count { it.status.equals("PENDING", ignoreCase = true) }
        val processing = sampleOrders.count { it.status.equals("CONFIRMED", ignoreCase = true) || it.status.equals("PACKED", ignoreCase = true) }
        val shipping = sampleOrders.count { it.status.equals("SHIPPING", ignoreCase = true) }
        val toReview = sampleOrders.count { it.status.equals("DELIVERED", ignoreCase = true) || it.status.equals("COMPLETED", ignoreCase = true) }

        val counts = OrderStatusCounts(
            pendingCount = pending,
            processingCount = processing,
            shippingCount = shipping,
            toReviewCount = toReview
        )

        assertEquals(1, counts.pendingCount)
        assertEquals(2, counts.processingCount)
        assertEquals(2, counts.shippingCount)
        assertEquals(2, counts.toReviewCount)

        val state = B2CUiState(orderStatusCounts = counts)
        assertEquals(1, state.pendingOrdersCount)
        assertEquals(2, state.processingOrdersCount)
        assertEquals(2, state.shippingOrdersCount)
        assertEquals(2, state.toReviewOrdersCount)
    }
}
