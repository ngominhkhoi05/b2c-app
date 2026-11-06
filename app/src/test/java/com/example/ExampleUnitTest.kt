package com.example

import com.example.b2capp.model.CartItem
import com.example.b2capp.model.ProductItem
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.viewmodel.PageState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testProductItemCalculations() {
    val product = ProductItem(
      id = "p1",
      name = "Áo Polo Nam",
      imageUrl = "https://example.com/polo.jpg",
      price = 150000L,
      originalPrice = 200000L,
      rating = 4.8f,
      soldCount = 2500,
      stock = 50
    )

    assertEquals(25, product.discountPercent)
    assertTrue(product.formattedPrice.contains("150.000"))
    assertEquals("Đã bán 2.5k", product.formattedSoldCount)
  }

  @Test
  fun testCartItemTotal() {
    val product = ProductItem(
      id = "p1",
      name = "Sản phẩm test",
      imageUrl = "",
      price = 120000L
    )
    val cartItem = CartItem(product = product, quantity = 3)
    assertEquals(360000L, cartItem.totalPrice)
    assertTrue(cartItem.formattedTotalPrice.contains("360.000"))
  }

  @Test
  fun testViewModelCartOperations() {
    val viewModel = B2CViewModel()
    val initialCartCount = viewModel.uiState.value.cartItems.size

    val testProduct = ProductItem(
      id = "test_item_99",
      name = "Test Item",
      imageUrl = "",
      price = 50000L
    )

    viewModel.openProductDetail(testProduct)
    viewModel.setDetailQuantity(2)
    viewModel.addToCartFromDetail()

    val updatedCount = viewModel.uiState.value.cartItems.size
    assertEquals(initialCartCount + 1, updatedCount)
    assertNotNull(viewModel.uiState.value.toastMessage)

    viewModel.setPageState(PageState.ERROR)
    assertEquals(PageState.ERROR, viewModel.uiState.value.pageState)
  }
}
