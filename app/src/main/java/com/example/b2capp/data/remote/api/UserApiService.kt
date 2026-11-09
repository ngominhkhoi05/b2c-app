package com.example.b2capp.data.remote.api

import com.example.b2capp.data.remote.dto.ApiResponse
import com.example.b2capp.data.remote.dto.AvatarUploadResponseDto
import com.example.b2capp.data.remote.dto.ChangePasswordRequestDto
import com.example.b2capp.data.remote.dto.UpdateProfileRequestDto
import com.example.b2capp.data.remote.dto.UserProfileDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

/**
 * Interface Retrofit cho User API
 */
interface UserApiService {

    /**
     * Lấy thông tin hồ sơ của người dùng hiện tại (cần xác thực Token)
     * GET /api/v1/users/me
     */
    @GET("api/v1/users/me")
    suspend fun getMyProfile(): Response<ApiResponse<UserProfileDto>>

    /**
     * Cập nhật thông tin hồ sơ của người dùng hiện tại
     * PUT /api/v1/users/me
     */
    @PUT("api/v1/users/me")
    suspend fun updateMyProfile(
        @Body request: UpdateProfileRequestDto
    ): Response<ApiResponse<UserProfileDto>>

    /**
     * Tải lên ảnh đại diện mới
     * POST /api/v1/users/me/avatar
     */
    @Multipart
    @POST("api/v1/users/me/avatar")
    suspend fun uploadAvatar(
        @Part file: MultipartBody.Part
    ): Response<ApiResponse<AvatarUploadResponseDto>>

    /**
     * Đổi mật khẩu tài khoản
     * POST /api/v1/users/change-password
     */
    @POST("api/v1/users/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequestDto
    ): Response<ApiResponse<Void>>
}
