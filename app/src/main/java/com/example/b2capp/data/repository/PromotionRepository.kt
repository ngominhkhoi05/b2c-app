package com.example.b2capp.data.repository

import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.api.PromotionApiService
import com.example.b2capp.data.remote.dto.ApplyPromotionRequestDto
import com.example.b2capp.data.remote.dto.ApplyPromotionResponseDto
import com.example.b2capp.data.remote.dto.PromotionDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository phụ trách quản lý tương tác dữ liệu Khuyến mãi / Mã giảm giá với Backend:
 * - Lấy danh sách voucher khả dụng (hỗ trợ cả người mua đăng nhập và khách công khai)
 * - Kiểm tra (validate) mã giảm giá trước khi áp dụng
 */
class PromotionRepository(
    private val authenticatedApiHolder: () -> PromotionApiService = {
        ApiClient.createAuthenticatedService(PromotionApiService::class.java)
    },
    private val publicApiHolder: () -> PromotionApiService = {
        ApiClient.createService(PromotionApiService::class.java)
    }
) {

    /**
     * Lấy danh sách voucher đang hoạt động
     * GET /api/v1/buyer/promotions hoặc /api/v1/promotions
     */
    suspend fun getAvailablePromotions(isLoggedIn: Boolean): Result<List<PromotionDto>> = withContext(Dispatchers.IO) {
        try {
            val response = if (isLoggedIn) {
                authenticatedApiHolder().getBuyerPromotions()
            } else {
                publicApiHolder().getPublicPromotions()
            }
            if (response.isSuccessful) {
                val list = response.body()?.data ?: emptyList()
                Result.success(list)
            } else {
                // Fallback thử endpoint public nếu endpoint buyer trả về lỗi xác thực
                val fallbackResponse = publicApiHolder().getPublicPromotions()
                if (fallbackResponse.isSuccessful) {
                    Result.success(fallbackResponse.body()?.data ?: emptyList())
                } else {
                    val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                    Result.failure(Exception(errorMsg))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Kiểm tra và tính toán giảm giá của một mã giảm giá cụ thể
     * POST /api/v1/buyer/promotions/validate
     */
    suspend fun validatePromotion(code: String, subtotal: Double): Result<ApplyPromotionResponseDto> = withContext(Dispatchers.IO) {
        try {
            val req = ApplyPromotionRequestDto(code = code.trim().uppercase(), subtotal = subtotal)
            val response = authenticatedApiHolder().validatePromotion(req)
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
}
