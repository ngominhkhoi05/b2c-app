package com.example

import com.example.b2capp.data.local.TokenManager
import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.dto.BulkDeleteCartItemRequestDto
import com.example.b2capp.data.remote.dto.CartItemDto
import com.example.b2capp.data.remote.dto.UpdateCartItemRequestDto
import com.example.b2capp.data.remote.dto.UserProfileDto
import com.example.b2capp.viewmodel.B2CUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Base64

class CartAndSessionValidationTest {

    @Test
    fun testCartItemDtoDeserialization() {
        val moshi = ApiClient.moshi
        val json = """
            {
                "id": 101,
                "variantId": 12,
                "variantName": "Đen / L",
                "productName": "Áo Thun Nam Cotton Compact",
                "thumbnailUrl": "https://example.com/item.jpg",
                "quantity": 2,
                "priceSnapshot": 199000.0,
                "lineTotal": 398000.0,
                "isSelected": true,
                "addedAt": "2026-11-09T10:00:00",
                "productId": 5,
                "productSlug": "ao-thun-nam-cotton-compact",
                "sellerId": 2,
                "shopName": "Coolmate Official",
                "stockQuantity": 50,
                "isAvailable": true
            }
        """.trimIndent()

        val adapter = moshi.adapter(CartItemDto::class.java)
        val item = adapter.fromJson(json)

        assertNotNull(item)
        assertEquals(101L, item?.id)
        assertEquals("Coolmate Official", item?.shopName)
        assertEquals(2L, item?.sellerId)
        assertEquals("Áo Thun Nam Cotton Compact", item?.productName)
        assertEquals("Đen / L", item?.variantName)
        assertEquals(2, item?.quantity)
        assertEquals(199000.0, item?.priceSnapshot ?: 0.0, 0.001)
        assertEquals(398000.0, item?.lineTotal ?: 0.0, 0.001)
        assertTrue(item?.isSelected == true)
        assertEquals(50, item?.stockQuantity)
        assertTrue(item?.isAvailable == true)
    }

    @Test
    fun testUpdateCartItemRequestDtoSerialization() {
        val moshi = ApiClient.moshi
        val req = UpdateCartItemRequestDto(quantity = 3, isSelected = false)
        val adapter = moshi.adapter(UpdateCartItemRequestDto::class.java)
        val json = adapter.toJson(req)

        assertTrue(json.contains("\"quantity\":3"))
        assertTrue(json.contains("\"isSelected\":false"))
    }

    @Test
    fun testBulkDeleteCartItemRequestDtoSerialization() {
        val moshi = ApiClient.moshi
        val req = BulkDeleteCartItemRequestDto(cartItemIds = listOf(1L, 2L, 5L))
        val adapter = moshi.adapter(BulkDeleteCartItemRequestDto::class.java)
        val json = adapter.toJson(req)

        assertTrue(json.contains("1"))
        assertTrue(json.contains("2"))
        assertTrue(json.contains("5"))
    }

    @Test
    fun testUserProfileDtoDeserialization() {
        val moshi = ApiClient.moshi
        val json = """
            {
                "id": 10,
                "email": "nguyenvana@gmail.com",
                "phone": "0987654321",
                "fullName": "Nguyễn Văn A",
                "avatarUrl": "https://example.com/avatar.jpg",
                "gender": "MALE",
                "dateOfBirth": "1998-10-24",
                "role": "BUYER",
                "status": "ACTIVE"
            }
        """.trimIndent()

        val adapter = moshi.adapter(UserProfileDto::class.java)
        val profile = adapter.fromJson(json)

        assertNotNull(profile)
        assertEquals(10L, profile?.id)
        assertEquals("nguyenvana@gmail.com", profile?.email)
        assertEquals("Nguyễn Văn A", profile?.fullName)
        assertEquals("BUYER", profile?.role)
        assertEquals("ACTIVE", profile?.status)
    }

    @Test
    fun testTokenManagerIsTokenExpired() {
        val tokenManager = TokenManager.getInstance()

        // 1. Token null hoặc rỗng -> coi như đã hết hạn
        assertTrue(tokenManager.isTokenExpired(null))
        assertTrue(tokenManager.isTokenExpired(""))
        assertTrue(tokenManager.isTokenExpired("   "))

        // 2. Token không đúng định dạng JWT (không có ít nhất 2 phần phân cách bởi .)
        assertTrue(tokenManager.isTokenExpired("invalid_token_string"))

        // 3. Token đã hết hạn (exp trong quá khứ)
        val pastExpSeconds = (System.currentTimeMillis() / 1000L) - 3600L // 1 giờ trước
        val expiredPayload = """{"sub":"nguyenvana@gmail.com","exp":$pastExpSeconds}"""
        val expiredPayloadB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(expiredPayload.toByteArray())
        val expiredJwt = "eyJhbGciOiJIUzI1NiJ9.$expiredPayloadB64.signature"

        assertTrue("Token với exp trong quá khứ phải báo hết hạn", tokenManager.isTokenExpired(expiredJwt))

        // 4. Token còn hạn (exp trong tương lai)
        val futureExpSeconds = (System.currentTimeMillis() / 1000L) + 7200L // 2 giờ sau
        val validPayload = """{"sub":"nguyenvana@gmail.com","exp":$futureExpSeconds}"""
        val validPayloadB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(validPayload.toByteArray())
        val validJwt = "eyJhbGciOiJIUzI1NiJ9.$validPayloadB64.signature"

        assertFalse("Token với exp trong tương lai không được báo hết hạn", tokenManager.isTokenExpired(validJwt))
    }

    @Test
    fun testUiStateCartCalculation() {
        val item1 = CartItemDto(
            id = 1L,
            productName = "Sản phẩm 1",
            priceSnapshot = 100000.0,
            lineTotal = 200000.0,
            quantity = 2,
            isSelected = true
        )
        val item2 = CartItemDto(
            id = 2L,
            productName = "Sản phẩm 2",
            priceSnapshot = 300000.0,
            lineTotal = 300000.0,
            quantity = 1,
            isSelected = false // Không chọn
        )
        val item3 = CartItemDto(
            id = 3L,
            productName = "Sản phẩm 3",
            priceSnapshot = 50000.0,
            lineTotal = 150000.0,
            quantity = 3,
            isSelected = true
        )

        val state = B2CUiState(
            isLoggedIn = true,
            serverCartItems = listOf(item1, item2, item3)
        )

        // Tổng số lượng giỏ hàng: 2 + 1 + 3 = 6
        assertEquals(6, state.totalCartCount)

        // Tổng tiền các sản phẩm ĐƯỢC CHỌN (item1 + item3): 200.000 + 150.000 = 350.000
        assertEquals(350000L, state.totalCartPrice)
        assertTrue(state.formattedCartTotal.contains("350.000"))
    }
}
