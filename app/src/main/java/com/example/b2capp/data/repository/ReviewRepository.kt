package com.example.b2capp.data.repository

import com.example.b2capp.core.model.ReviewItem
import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.api.ReviewApiService
import com.example.b2capp.data.remote.dto.CreateReviewRequestDto
import com.example.b2capp.data.remote.dto.toReviewItem

/**
 * Repository xử lý các tương tác nghiệp vụ Đánh giá (Review)
 */
class ReviewRepository(
    private val reviewApiService: ReviewApiService = ApiClient.reviewApiService,
    private val authenticatedReviewApiService: ReviewApiService = ApiClient.authenticatedReviewApiService
) {
    /**
     * Lấy danh sách đánh giá của sản phẩm từ API
     * Trả về Pair<List<ReviewItem>, Int> gồm (danh sách đánh giá, tổng số đánh giá)
     */
    suspend fun getProductReviews(
        productId: Long,
        rating: Int? = null,
        page: Int = 0,
        size: Int = 20
    ): Result<Pair<List<ReviewItem>, Int>> {
        return try {
            val response = reviewApiService.getProductReviews(
                productId = productId,
                rating = rating,
                page = page,
                size = size
            )
            if (response.isSuccessful && response.body()?.data != null) {
                val pageData = response.body()!!.data!!
                val items = pageData.content.map { it.toReviewItem() }
                Result.success(Pair(items, pageData.totalElements.toInt()))
            } else {
                Result.success(Pair(emptyList(), 0))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gửi đánh giá cho sản phẩm
     */
    suspend fun submitReview(
        orderItemId: Long,
        rating: Int,
        comment: String?,
        imageUrls: String? = null,
        isAnonymous: Boolean = false
    ): Result<ReviewItem> {
        return try {
            val req = CreateReviewRequestDto(
                orderItemId = orderItemId,
                rating = rating,
                comment = comment,
                imageUrls = imageUrls,
                isAnonymous = isAnonymous
            )
            val response = authenticatedReviewApiService.submitReview(req)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.toReviewItem())
            } else {
                val errMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
