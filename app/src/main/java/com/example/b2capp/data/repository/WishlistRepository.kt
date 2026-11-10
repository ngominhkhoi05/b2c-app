package com.example.b2capp.data.repository

import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.api.WishlistApiService
import com.example.b2capp.data.remote.dto.PageResponse
import com.example.b2capp.data.remote.dto.WishlistItemDto
import com.example.b2capp.data.remote.dto.WishlistToggleResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository xử lý các thao tác với Danh sách yêu thích (Wishlist) kết nối trực tiếp Backend:
 * - Đọc danh sách sản phẩm yêu thích (phân trang)
 * - Bật/tắt yêu thích (Toggle)
 * - Xóa sản phẩm khỏi yêu thích
 * - Kiểm tra trạng thái yêu thích
 * - Đếm số lượng sản phẩm yêu thích
 */
class WishlistRepository(
    private val apiServiceHolder: () -> WishlistApiService = {
        ApiClient.wishlistApiService
    }
) {

    /**
     * Lấy danh sách sản phẩm yêu thích của người dùng hiện tại
     * GET /api/v1/wishlist
     */
    suspend fun getWishlist(page: Int = 0, size: Int = 20): Result<PageResponse<WishlistItemDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().getWishlist(page = page, size = size)
            if (response.isSuccessful) {
                val data = response.body()?.data ?: PageResponse()
                Result.success(data)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Bật/tắt sản phẩm trong danh sách yêu thích
     * POST /api/v1/wishlist/products/{productId}
     */
    suspend fun toggleWishlist(productId: Long): Result<WishlistToggleResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().toggleWishlist(productId)
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
     * Xóa sản phẩm khỏi danh sách yêu thích
     * DELETE /api/v1/wishlist/products/{productId}
     */
    suspend fun removeFromWishlist(productId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().removeFromWishlist(productId)
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
     * Kiểm tra xem một sản phẩm đã có trong danh sách yêu thích chưa
     * GET /api/v1/wishlist/products/{productId}/check
     */
    suspend fun checkWishlist(productId: Long): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().checkWishlist(productId)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.wishlisted)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lấy tổng số lượng sản phẩm trong danh sách yêu thích
     * GET /api/v1/wishlist/count
     */
    suspend fun getWishlistCount(): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().getWishlistCount()
            if (response.isSuccessful) {
                val count = response.body()?.data?.get("count") ?: 0L
                Result.success(count)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
