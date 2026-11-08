package com.example

import com.example.b2capp.core.model.ProductItem
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun testProductAttributes() {
    val product = ProductItem(
      id = "item_1",
      name = "B2C Shop Product",
      imageUrl = "",
      price = 100000L,
      originalPrice = 200000L,
      rating = 5.0f,
      soldCount = 1000
    )
    assertEquals(50, product.discountPercent)
    assertEquals("Đã bán 1.0k", product.formattedSoldCount)
  }
}
