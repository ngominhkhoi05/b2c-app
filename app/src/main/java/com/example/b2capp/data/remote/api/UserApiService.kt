package com.example.b2capp.data.remote.api

import com.example.b2capp.data.remote.dto.AddressDto
import com.example.b2capp.data.remote.dto.AddressRequestDto
import com.example.b2capp.data.remote.dto.ApiResponse
import com.example.b2capp.data.remote.dto.AvatarUploadResponseDto
import com.example.b2capp.data.remote.dto.ChangePasswordRequestDto
import com.example.b2capp.data.remote.dto.UpdateProfileRequestDto
import com.example.b2capp.data.remote.dto.UploadResponseDto
import com.example.b2capp.data.remote.dto.UserProfileDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Part
import retrofit2.http.Query

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
     * Tải lên hình ảnh lên Cloudinary qua API generic POST /api/v1/upload
     */
    @Multipart
    @POST("api/v1/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Query("purpose") purpose: String? = null
    ): Response<ApiResponse<UploadResponseDto>>

    /**
     * Đổi mật khẩu tài khoản
     * POST /api/v1/users/change-password
     */
    @POST("api/v1/users/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequestDto
    ): Response<ApiResponse<Unit>>

    /**
     * Lấy danh sách địa chỉ nhận hàng của người dùng hiện tại
     * GET /api/v1/users/addresses
     */
    @GET("api/v1/users/addresses")
    suspend fun getMyAddresses(): Response<ApiResponse<List<AddressDto>>>

    /**
     * Tạo mới địa chỉ nhận hàng
     * POST /api/v1/users/addresses
     */
    @POST("api/v1/users/addresses")
    suspend fun createAddress(
        @Body request: AddressRequestDto
    ): Response<ApiResponse<AddressDto>>

    /**
     * Lấy thông tin chi tiết một địa chỉ theo ID
     * GET /api/v1/users/addresses/{id}
     */
    @GET("api/v1/users/addresses/{id}")
    suspend fun getAddress(
        @Path("id") addressId: Long
    ): Response<ApiResponse<AddressDto>>

    /**
     * Cập nhật địa chỉ nhận hàng
     * PUT /api/v1/users/addresses/{id}
     */
    @PUT("api/v1/users/addresses/{id}")
    suspend fun updateAddress(
        @Path("id") addressId: Long,
        @Body request: AddressRequestDto
    ): Response<ApiResponse<AddressDto>>

    /**
     * Xóa địa chỉ nhận hàng
     * DELETE /api/v1/users/addresses/{id}
     */
    @DELETE("api/v1/users/addresses/{id}")
    suspend fun deleteAddress(
        @Path("id") addressId: Long
    ): Response<ApiResponse<Unit>>

    /**
     * Đặt địa chỉ làm mặc định
     * PUT /api/v1/users/addresses/{id}/default
     */
    @PUT("api/v1/users/addresses/{id}/default")
    suspend fun setDefaultAddress(
        @Path("id") addressId: Long
    ): Response<ApiResponse<AddressDto>>
}
