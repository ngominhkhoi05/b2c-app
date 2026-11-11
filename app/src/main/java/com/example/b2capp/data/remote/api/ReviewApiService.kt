package com.example.b2capp.data.remote.api

import com.example.b2capp.data.remote.dto.ApiResponse
import com.example.b2capp.data.remote.dto.CreateReviewRequestDto
import com.example.b2capp.data.remote.dto.PageResponse
import com.example.b2capp.data.remote.dto.ReviewDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API Service cho các nghiệp vụ Đánh giá (Review & Rating)
 */
interface ReviewApiService {

    /**
     * Lấy danh sách đánh giá của sản phẩm (công khai, hỗ trợ lọc theo số sao 1-5 và phân trang)
     */
    @GET("api/v1/products/{productId}/reviews")
    suspend fun getProductReviews(
        @Path("productId") productId: Long,
        @Query("rating") rating: Int? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<ReviewDto>>>

    /**
     * Người mua gửi đánh giá cho sản phẩm đã mua
     */
    @POST("api/v1/buyer/reviews")
    suspend fun submitReview(
        @Body request: CreateReviewRequestDto
    ): Response<ApiResponse<ReviewDto>>
}
