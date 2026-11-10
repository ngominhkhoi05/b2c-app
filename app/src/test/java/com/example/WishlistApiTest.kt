package com.example

import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.api.WishlistApiService
import com.example.b2capp.data.remote.dto.ApiResponse
import com.example.b2capp.data.remote.dto.PageResponse
import com.example.b2capp.data.remote.dto.WishlistItemDto
import com.example.b2capp.data.remote.dto.WishlistToggleResponse
import com.example.b2capp.data.repository.WishlistRepository
import com.squareup.moshi.Types
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class WishlistApiTest {

    @Test
    fun testWishlistItemDtoSerialization() {
        val json = """
            {
                "id": 1,
                "productId": 105,
                "productSlug": "ban-phim-co-khong-day",
                "productName": "Bàn phím cơ không dây Custom Hot-swap",
                "productThumbnail": "https://example.com/keyboard.png",
                "minPrice": 450000.0,
                "maxPrice": 690000.0,
                "sellerName": "Keychron Official",
                "sellerId": 2,
                "status": "ACTIVE",
                "addedAt": "2026-11-09T22:00:00"
            }
        """.trimIndent()

        val adapter = ApiClient.moshi.adapter(WishlistItemDto::class.java)
        val item = adapter.fromJson(json)

        assertNotNull(item)
        assertEquals(1L, item?.id)
        assertEquals(105L, item?.productId)
        assertEquals("Bàn phím cơ không dây Custom Hot-swap", item?.productName)
        assertEquals(450000.0, item?.minPrice)
        assertEquals(690000.0, item?.maxPrice)
        assertEquals("Keychron Official", item?.sellerName)
        assertEquals("ACTIVE", item?.status)
    }

    @Test
    fun testWishlistToggleResponseSerialization() {
        val json = """
            {
                "wishlisted": true,
                "productId": 105
            }
        """.trimIndent()

        val adapter = ApiClient.moshi.adapter(WishlistToggleResponse::class.java)
        val toggle = adapter.fromJson(json)

        assertNotNull(toggle)
        assertTrue(toggle?.wishlisted == true)
        assertEquals(105L, toggle?.productId)
    }

    @Test
    fun testPageResponseWishlistItemDtoSerialization() {
        val json = """
            {
                "code": 200,
                "message": "Success",
                "data": {
                    "content": [
                        {
                            "id": 1,
                            "productId": 105,
                            "productName": "Bàn phím cơ không dây Custom Hot-swap",
                            "minPrice": 450000.0,
                            "maxPrice": 690000.0,
                            "status": "ACTIVE"
                        }
                    ],
                    "page": 0,
                    "size": 20,
                    "totalElements": 1,
                    "totalPages": 1,
                    "first": true,
                    "last": true
                }
            }
        """.trimIndent()

        val type = Types.newParameterizedType(
            ApiResponse::class.java,
            Types.newParameterizedType(PageResponse::class.java, WishlistItemDto::class.java)
        )
        val adapter = ApiClient.moshi.adapter<ApiResponse<PageResponse<WishlistItemDto>>>(type)
        val resp = adapter.fromJson(json)

        assertNotNull(resp)
        assertEquals(200, resp?.code)
        assertEquals(1, resp?.data?.content?.size)
        assertEquals(105L, resp?.data?.content?.first()?.productId)
        assertEquals(1L, resp?.data?.totalElements)
    }

    @Test
    fun testWishlistRepositoryGetWishlistSuccess() = runBlocking {
        val mockItems = listOf(
            WishlistItemDto(
                id = 1L,
                productId = 100L,
                productName = "Áo Thun Nam",
                minPrice = 199000.0,
                maxPrice = 299000.0,
                status = "ACTIVE"
            )
        )
        val pageResponse = PageResponse(
            content = mockItems,
            totalElements = 1L,
            totalPages = 1
        )
        val apiService = object : MockWishlistApiService() {
            override suspend fun getWishlist(page: Int, size: Int): Response<ApiResponse<PageResponse<WishlistItemDto>>> {
                return Response.success(ApiResponse(code = 200, message = "Success", data = pageResponse))
            }
        }

        val repository = WishlistRepository(apiServiceHolder = { apiService })
        val result = repository.getWishlist()

        assertTrue(result.isSuccess)
        val data = result.getOrNull()
        assertNotNull(data)
        assertEquals(1, data?.content?.size)
        assertEquals("Áo Thun Nam", data?.content?.first()?.productName)
        assertEquals(1L, data?.totalElements)
    }

    @Test
    fun testWishlistRepositoryToggleSuccess() = runBlocking {
        val apiService = object : MockWishlistApiService() {
            override suspend fun toggleWishlist(productId: Long): Response<ApiResponse<WishlistToggleResponse>> {
                return Response.success(ApiResponse(code = 200, data = WishlistToggleResponse(wishlisted = true, productId = productId)))
            }
        }

        val repository = WishlistRepository(apiServiceHolder = { apiService })
        val result = repository.toggleWishlist(100L)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.wishlisted == true)
        assertEquals(100L, result.getOrNull()?.productId)
    }

    @Test
    fun testWishlistRepositoryRemoveSuccess() = runBlocking {
        val apiService = object : MockWishlistApiService() {
            override suspend fun removeFromWishlist(productId: Long): Response<Unit> {
                return Response.success(Unit)
            }
        }

        val repository = WishlistRepository(apiServiceHolder = { apiService })
        val result = repository.removeFromWishlist(100L)

        assertTrue(result.isSuccess)
    }

    @Test
    fun testWishlistRepositoryCountSuccess() = runBlocking {
        val apiService = object : MockWishlistApiService() {
            override suspend fun getWishlistCount(): Response<ApiResponse<Map<String, Long>>> {
                return Response.success(ApiResponse(code = 200, data = mapOf("count" to 5L)))
            }
        }

        val repository = WishlistRepository(apiServiceHolder = { apiService })
        val result = repository.getWishlistCount()

        assertTrue(result.isSuccess)
        assertEquals(5L, result.getOrNull())
    }

    private open class MockWishlistApiService : WishlistApiService {
        override suspend fun getWishlist(page: Int, size: Int): Response<ApiResponse<PageResponse<WishlistItemDto>>> =
            throw NotImplementedError()

        override suspend fun toggleWishlist(productId: Long): Response<ApiResponse<WishlistToggleResponse>> =
            throw NotImplementedError()

        override suspend fun removeFromWishlist(productId: Long): Response<Unit> =
            throw NotImplementedError()

        override suspend fun checkWishlist(productId: Long): Response<ApiResponse<WishlistToggleResponse>> =
            throw NotImplementedError()

        override suspend fun getWishlistCount(): Response<ApiResponse<Map<String, Long>>> =
            throw NotImplementedError()
    }
}
