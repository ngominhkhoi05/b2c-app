package com.example

import com.example.b2capp.data.local.TokenManager
import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.api.CartApiService
import com.example.b2capp.data.remote.dto.ApiResponse
import com.example.b2capp.data.remote.dto.BulkDeleteCartItemRequestDto
import com.example.b2capp.data.remote.dto.CartItemDto
import com.example.b2capp.data.remote.dto.shopDisplayName
import com.example.b2capp.data.remote.dto.UpdateCartItemRequestDto
import com.example.b2capp.data.remote.dto.UserProfileDto
import com.example.b2capp.viewmodel.B2CUiState
import com.squareup.moshi.Types
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
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

    @Test
    fun testCartItemShopDisplayName() {
        val itemWithShopName = CartItemDto(id = 1L, shopName = "Coolmate Official", sellerId = 10L)
        assertEquals("Coolmate Official", itemWithShopName.shopDisplayName)

        val itemWithOnlySellerId = CartItemDto(id = 2L, shopName = null, sellerId = 5L)
        assertEquals("Cửa hàng #5", itemWithOnlySellerId.shopDisplayName)

        val itemFallback = CartItemDto(id = 3L, shopName = "", sellerId = null)
        assertEquals("Cửa hàng chính hãng", itemFallback.shopDisplayName)
    }

    @Test
    fun testSingleShopCartSelectionInvariant() {
        val itemShopA1 = CartItemDto(id = 1L, shopName = "Shop A", isSelected = true)
        val itemShopA2 = CartItemDto(id = 2L, shopName = "Shop A", isSelected = true)
        val itemShopB1 = CartItemDto(id = 3L, shopName = "Shop B", isSelected = false)
        val itemShopB2 = CartItemDto(id = 4L, shopName = "Shop B", isSelected = false)

        val initialCart = listOf(itemShopA1, itemShopA2, itemShopB1, itemShopB2)

        // Kiểm tra ban đầu: chỉ có Shop A được chọn
        val selectedShopsInitial = initialCart.filter { it.isSelected }.map { it.shopDisplayName }.distinct()
        assertEquals(listOf("Shop A"), selectedShopsInitial)

        // Khi người dùng chọn sang sản phẩm Shop B (itemShopB1):
        // Bỏ chọn tất cả sản phẩm của Shop A, chọn itemShopB1
        val switchedCart = initialCart.map { item ->
            when {
                item.id == itemShopB1.id -> item.copy(isSelected = true)
                item.isSelected -> item.copy(isSelected = false)
                else -> item
            }
        }

        val selectedShopsAfterSwitch = switchedCart.filter { it.isSelected }.map { it.shopDisplayName }.distinct()
        assertEquals(listOf("Shop B"), selectedShopsAfterSwitch)
        assertFalse(switchedCart.first { it.id == itemShopA1.id }.isSelected)
        assertFalse(switchedCart.first { it.id == itemShopA2.id }.isSelected)
        assertTrue(switchedCart.first { it.id == itemShopB1.id }.isSelected)
        assertFalse(switchedCart.first { it.id == itemShopB2.id }.isSelected)

        // Khi người dùng chọn toàn bộ Shop B:
        val selectAllShopBCart = switchedCart.map { item ->
            if (item.shopDisplayName == "Shop B") item.copy(isSelected = true)
            else if (item.isSelected) item.copy(isSelected = false)
            else item
        }
        val selectedShopsAfterSelectAllShopB = selectAllShopBCart.filter { it.isSelected }.map { it.shopDisplayName }.distinct()
        assertEquals(listOf("Shop B"), selectedShopsAfterSelectAllShopB)
        assertTrue(selectAllShopBCart.first { it.id == itemShopB1.id }.isSelected)
        assertTrue(selectAllShopBCart.first { it.id == itemShopB2.id }.isSelected)
        assertFalse(selectAllShopBCart.first { it.id == itemShopA1.id }.isSelected)
    }

    @Test
    fun testApiResponseUnitConverterAndDeserialization() {
        val moshi = ApiClient.moshi
        val type = Types.newParameterizedType(ApiResponse::class.java, Unit::class.java)
        val adapter = moshi.adapter<ApiResponse<Unit>>(type)
        assertNotNull(adapter)

        val json = """
            {
                "code": 200,
                "message": "Item removed",
                "data": null,
                "timestamp": "2026-11-12T10:00:00"
            }
        """.trimIndent()

        val parsed = adapter.fromJson(json)
        assertNotNull(parsed)
        assertEquals(200, parsed?.code)
        assertEquals("Item removed", parsed?.message)
    }

    @Test
    fun testApiResponseVoidConverterAndDeserialization() {
        val moshi = ApiClient.moshi
        val type = Types.newParameterizedType(ApiResponse::class.java, Void::class.java)
        val adapter = moshi.adapter<ApiResponse<Void>>(type)
        assertNotNull(adapter)

        val json = """
            {
                "code": 200,
                "message": "Item removed",
                "data": null,
                "timestamp": "2026-11-12T10:00:00"
            }
        """.trimIndent()

        val parsed = adapter.fromJson(json)
        assertNotNull(parsed)
        assertEquals(200, parsed?.code)
        assertEquals("Item removed", parsed?.message)
    }
}
