package com.example

import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.dto.ApiResponse
import com.example.b2capp.data.remote.dto.CategoryDto
import com.example.b2capp.data.remote.dto.PageResponse
import com.example.b2capp.data.remote.dto.ProductSummaryDto
import com.example.b2capp.data.remote.dto.PromotionDto
import com.example.b2capp.data.repository.HomeRepository
import com.squareup.moshi.Types
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeApiTest {

    @Test
    fun testCategoryDtoSerialization() {
        val json = """
            {
                "id": 1,
                "name": "Thời Trang Nam",
                "slug": "thoi-trang-nam",
                "iconUrl": "https://example.com/icon.png",
                "level": 0,
                "path": "/1",
                "parentId": null,
                "children": []
            }
        """.trimIndent()

        val adapter = ApiClient.moshi.adapter(CategoryDto::class.java)
        val category = adapter.fromJson(json)

        assertNotNull(category)
        assertEquals(1L, category?.id)
        assertEquals("Thời Trang Nam", category?.name)
        assertEquals("thoi-trang-nam", category?.slug)
    }

    @Test
    fun testProductSummaryDtoSerialization() {
        val json = """
            {
                "id": 101,
                "slug": "ao-thun-nam-compact",
                "name": "Áo Thun Nam Cotton Compact",
                "minPrice": 199000.0,
                "maxPrice": 299000.0,
                "thumbnailUrl": "https://images.unsplash.com/photo-1521572267360",
                "brand": "Coolmate",
                "ratingAvg": 4.9,
                "ratingCount": 120,
                "soldCount": 5800,
                "sellerId": 5,
                "shopName": "Coolmate Official Store",
                "categoryId": 1,
                "categoryName": "Thời Trang Nam",
                "status": "ACTIVE",
                "createdAt": "2026-11-08T10:00:00"
            }
        """.trimIndent()

        val adapter = ApiClient.moshi.adapter(ProductSummaryDto::class.java)
        val product = adapter.fromJson(json)

        assertNotNull(product)
        assertEquals(101L, product?.id)
        assertEquals("Áo Thun Nam Cotton Compact", product?.name)
        assertEquals(199000.0, product?.minPrice)
        assertEquals(4.9, product?.ratingAvg)
        assertEquals(5800, product?.soldCount)
        assertEquals("Coolmate Official Store", product?.shopName)
    }

    @Test
    fun testPageResponseProductSummaryDtoSerialization() {
        val json = """
            {
                "content": [
                    {
                        "id": 101,
                        "slug": "ao-thun",
                        "name": "Áo Thun Nam",
                        "minPrice": 199000.0,
                        "maxPrice": 299000.0,
                        "soldCount": 1200,
                        "ratingAvg": 4.8
                    }
                ],
                "page": 0,
                "size": 10,
                "totalElements": 1,
                "totalPages": 1,
                "first": true,
                "last": true
            }
        """.trimIndent()

        val type = Types.newParameterizedType(PageResponse::class.java, ProductSummaryDto::class.java)
        val adapter = ApiClient.moshi.adapter<PageResponse<ProductSummaryDto>>(type)
        val pageResponse = adapter.fromJson(json)

        assertNotNull(pageResponse)
        assertEquals(1, pageResponse?.content?.size)
        assertEquals("Áo Thun Nam", pageResponse?.content?.first()?.name)
        assertTrue(pageResponse?.last == true)
    }

    @Test
    fun testPromotionDtoSerialization() {
        val json = """
            {
                "id": 12,
                "sellerId": null,
                "code": "MEGASALE50",
                "name": "Mid-Year Mega Sale",
                "discountType": "PERCENT",
                "discountValue": 50.0,
                "maxDiscountAmount": 100000.0,
                "minOrderValue": 200000.0,
                "quantity": 500,
                "usedCount": 150,
                "remainingQuantity": 350,
                "limitPerUser": 1,
                "applyScope": "ALL",
                "startAt": "2026-11-01T00:00:00",
                "endAt": "2026-11-30T23:59:59",
                "status": "ACTIVE",
                "createdAt": "2026-11-01T00:00:00"
            }
        """.trimIndent()

        val adapter = ApiClient.moshi.adapter(PromotionDto::class.java)
        val promo = adapter.fromJson(json)

        assertNotNull(promo)
        assertEquals("MEGASALE50", promo?.code)
        assertEquals(50.0, promo?.discountValue)
        assertEquals("PERCENT", promo?.discountType)
    }

    @Test
    fun testDefaultFallbackData() {
        val categories = HomeRepository.getDefaultCategories()
        assertEquals(8, categories.size)
        assertEquals("qc_all", categories.last().id)
        assertEquals("Xem tất cả", categories.last().title)

        val bestSellers = HomeRepository.getDefaultBestSellers()
        assertTrue(bestSellers.isNotEmpty())
        assertTrue(bestSellers.first().price > 0)

        val banners = HomeRepository.getDefaultBanners()
        assertTrue(banners.isNotEmpty())
        assertEquals("MEGASALE50", banners.first().code)
    }
}

