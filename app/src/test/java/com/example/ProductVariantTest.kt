package com.example

import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.core.model.ProductOptionItem
import com.example.b2capp.core.model.ProductOptionValueItem
import com.example.b2capp.core.model.ProductVariantItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductVariantTest {

    private fun createSampleProduct(): ProductItem {
        val options = listOf(
            ProductOptionItem(
                id = 1,
                name = "Màu sắc",
                sortOrder = 1,
                values = listOf(
                    ProductOptionValueItem(id = 101, value = "Trắng Tinh Khôi", sortOrder = 1),
                    ProductOptionValueItem(id = 102, value = "Đen Huyền Bí", sortOrder = 2),
                    ProductOptionValueItem(id = 103, value = "Xanh Navy", sortOrder = 3)
                )
            ),
            ProductOptionItem(
                id = 2,
                name = "Kích cỡ",
                sortOrder = 2,
                values = listOf(
                    ProductOptionValueItem(id = 201, value = "M", sortOrder = 1),
                    ProductOptionValueItem(id = 202, value = "L", sortOrder = 2),
                    ProductOptionValueItem(id = 203, value = "XL", sortOrder = 3),
                    ProductOptionValueItem(id = 204, value = "2XL", sortOrder = 4)
                )
            )
        )

        val variants = listOf(
            ProductVariantItem(
                id = 1,
                sku = "CM-TSHIRT-WHT-M",
                variantName = "Trắng Tinh Khôi / M",
                price = 299000,
                salePrice = 199000,
                stockQuantity = 100,
                soldCount = 1500,
                optionValueIds = listOf(101, 201),
                attributes = mapOf("Màu sắc" to "Trắng Tinh Khôi", "Kích cỡ" to "M")
            ),
            ProductVariantItem(
                id = 4,
                sku = "CM-TSHIRT-BLK-L",
                variantName = "Đen Huyền Bí / L",
                price = 299000,
                salePrice = 199000,
                stockQuantity = 70,
                soldCount = 1000,
                optionValueIds = listOf(102, 202),
                attributes = mapOf("Màu sắc" to "Đen Huyền Bí", "Kích cỡ" to "L")
            ),
            ProductVariantItem(
                id = 2,
                sku = "CM-TSHIRT-WHT-L",
                variantName = "Trắng Tinh Khôi / L",
                price = 299000,
                salePrice = 199000,
                stockQuantity = 85,
                soldCount = 2100,
                optionValueIds = listOf(101, 202),
                attributes = mapOf("Màu sắc" to "Trắng Tinh Khôi", "Kích cỡ" to "L")
            ),
            ProductVariantItem(
                id = 3,
                sku = "CM-TSHIRT-BLK-M",
                variantName = "Đen Huyền Bí / M",
                price = 299000,
                salePrice = 199000,
                stockQuantity = 0,
                soldCount = 1200,
                optionValueIds = listOf(102, 201),
                attributes = mapOf("Màu sắc" to "Đen Huyền Bí", "Kích cỡ" to "M")
            )
        )

        return ProductItem(
            id = "1",
            name = "Áo Thun Nam Cotton Compact",
            imageUrl = "https://example.com/thumb.jpg",
            price = 199000,
            originalPrice = 299000,
            options = options,
            variants = variants
        )
    }

    @Test
    fun testFindVariantByOptionValues_exactMatch() {
        val product = createSampleProduct()

        val blackM = product.findVariantByOptionValues(listOf(102L, 201L))
        assertNotNull(blackM)
        assertEquals(3L, blackM?.id)
        assertEquals("Đen Huyền Bí / M", blackM?.variantName)
        assertEquals(0, blackM?.stockQuantity) // Hết hàng

        val whiteM = product.findVariantByOptionValues(listOf(101L, 201L))
        assertNotNull(whiteM)
        assertEquals(1L, whiteM?.id)
        assertEquals(100, whiteM?.stockQuantity)
    }

    @Test
    fun testFindVariantByOptionValues_nonExistentReturnsNull() {
        val product = createSampleProduct()

        // Xanh Navy + M không tồn tại trong variants -> phải trả về null
        val navyM = product.findVariantByOptionValues(listOf(103L, 201L))
        assertNull(navyM)

        // Đen + XL không tồn tại -> null
        val blackXL = product.findVariantByOptionValues(listOf(102L, 203L))
        assertNull(blackXL)
    }

    @Test
    fun testOptionAvailabilityLogic() {
        val product = createSampleProduct()

        fun isOptionSelectable(
            optionId: Long,
            valueId: Long,
            currentSelectedFirstOptionValId: Long?
        ): Boolean {
            val hasAnyStock = product.variants.any { v ->
                v.stockQuantity > 0 && v.optionValueIds.contains(valueId)
            }
            if (!hasAnyStock) return false

            val firstOption = product.options.firstOrNull()
            if (firstOption != null && firstOption.id != optionId) {
                if (currentSelectedFirstOptionValId != null) {
                    val hasCombinationStock = product.variants.any { v ->
                        v.stockQuantity > 0 &&
                            v.optionValueIds.contains(currentSelectedFirstOptionValId) &&
                            v.optionValueIds.contains(valueId)
                    }
                    if (!hasCombinationStock) return false
                }
            }
            return true
        }

        // 1. Kiểm tra nhóm Màu sắc (Option 1)
        assertTrue("Trắng phải chọn được vì còn hàng ở size M và L", isOptionSelectable(1, 101, null))
        assertTrue("Đen phải chọn được vì còn hàng ở size L", isOptionSelectable(1, 102, null))
        assertFalse("Xanh Navy không có biến thể nào -> không được chọn", isOptionSelectable(1, 103, null))

        // 2. Khi Màu sắc = Đen Huyền Bí (102):
        assertFalse("Size M khi chọn Đen phải bị vô hiệu hóa vì stock = 0", isOptionSelectable(2, 201, 102))
        assertTrue("Size L khi chọn Đen phải khả dụng vì stock = 70", isOptionSelectable(2, 202, 102))
        assertFalse("Size XL không có biến thể -> không được chọn", isOptionSelectable(2, 203, 102))
        assertFalse("Size 2XL không có biến thể -> không được chọn", isOptionSelectable(2, 204, 102))

        // 3. Khi Màu sắc = Trắng Tinh Khôi (101):
        assertTrue("Size M khi chọn Trắng khả dụng (stock = 100)", isOptionSelectable(2, 201, 101))
        assertTrue("Size L khi chọn Trắng khả dụng (stock = 85)", isOptionSelectable(2, 202, 101))
        assertFalse("Size XL không có biến thể -> không được chọn", isOptionSelectable(2, 203, 101))
    }
}

