package com.example.b2capp.data.repository

import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.api.CartApiService
import com.example.b2capp.data.remote.dto.AddCartItemRequestDto
import com.example.b2capp.data.remote.dto.BulkDeleteCartItemRequestDto
import com.example.b2capp.data.remote.dto.CartItemDto
import com.example.b2capp.data.remote.dto.UpdateCartItemRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository xử lý các thao tác với Giỏ hàng (Cart) kết nối trực tiếp Backend:
 * - Đọc danh sách giỏ hàng của người dùng hiện tại
 * - Thêm sản phẩm
 * - Cập nhật số lượng và trạng thái chọn/bỏ chọn
 * - Xóa một hoặc nhiều sản phẩm
 * - Đếm số lượng sản phẩm
 */
class CartRepository(
    private val apiServiceHolder: () -> CartApiService = {
        ApiClient.createAuthenticatedService(CartApiService::class.java)
    }
) {

    /**
     * Lấy toàn bộ sản phẩm trong giỏ hàng của người dùng hiện tại
     * GET /api/v1/cart
     */
    suspend fun getCartItems(): Result<List<CartItemDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().getCartItems()
            if (response.isSuccessful) {
                val items = response.body()?.data ?: emptyList()
                Result.success(items)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cập nhật số lượng hoặc trạng thái chọn của sản phẩm trong giỏ hàng
     * PATCH /api/v1/cart/items/{id}
     */
    suspend fun updateCartItem(
        id: Long,
        quantity: Int? = null,
        isSelected: Boolean? = null
    ): Result<CartItemDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().updateCartItem(
                id = id,
                request = UpdateCartItemRequestDto(quantity = quantity, isSelected = isSelected)
            )
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Xóa một sản phẩm khỏi giỏ hàng
     * DELETE /api/v1/cart/items/{id}
     */
    suspend fun removeCartItem(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().removeCartItem(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Xóa nhiều sản phẩm khỏi giỏ hàng cùng lúc
     * DELETE /api/v1/cart/items
     */
    suspend fun bulkRemoveCartItems(cartItemIds: List<Long>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().bulkRemoveCartItems(
                BulkDeleteCartItemRequestDto(cartItemIds = cartItemIds)
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Thêm sản phẩm theo variantId vào giỏ hàng
     * POST /api/v1/cart/items
     */
    suspend fun addCartItem(variantId: Long, quantity: Int = 1): Result<CartItemDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().addCartItem(
                AddCartItemRequestDto(variantId = variantId, quantity = quantity)
            )
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lấy tổng số lượng sản phẩm trong giỏ hàng
     * GET /api/v1/cart/count
     */
    suspend fun getCartCount(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().getCartCount()
            if (response.isSuccessful) {
                val count = response.body()?.data?.get("count")?.toInt() ?: 0
                Result.success(count)
            } else {
                Result.failure(Exception("Không lấy được số lượng giỏ hàng"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
