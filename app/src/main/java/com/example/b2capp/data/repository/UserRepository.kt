package com.example.b2capp.data.repository

import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.api.UserApiService
import com.example.b2capp.data.remote.dto.AvatarUploadResponseDto
import com.example.b2capp.data.remote.dto.ChangePasswordRequestDto
import com.example.b2capp.data.remote.dto.UpdateProfileRequestDto
import com.example.b2capp.data.remote.dto.UserProfileDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Repository xử lý các nghiệp vụ liên quan đến hồ sơ người dùng (User Profile):
 * - Đọc thông tin người dùng hiện tại (GET /api/v1/users/me)
 * - Cập nhật thông tin hồ sơ (PUT /api/v1/users/me)
 * - Tải lên ảnh đại diện (POST /api/v1/users/me/avatar)
 * - Đổi mật khẩu (POST /api/v1/users/change-password)
 */
class UserRepository(
    private val apiServiceHolder: () -> UserApiService = {
        ApiClient.createAuthenticatedService(UserApiService::class.java)
    }
) {

    /**
     * Lấy thông tin hồ sơ của người dùng hiện tại
     * GET /api/v1/users/me
     */
    suspend fun getMyProfile(): Result<UserProfileDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().getMyProfile()
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối đến máy chủ: ${e.localizedMessage ?: "Vui lòng kiểm tra lại mạng"}"))
        }
    }

    /**
     * Cập nhật thông tin hồ sơ cá nhân
     * PUT /api/v1/users/me
     */
    suspend fun updateProfile(request: UpdateProfileRequestDto): Result<UserProfileDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().updateMyProfile(request)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi khi cập nhật hồ sơ: ${e.localizedMessage ?: "Vui lòng thử lại sau"}"))
        }
    }

    /**
     * Tải lên ảnh đại diện mới
     * POST /api/v1/users/me/avatar
     */
    suspend fun uploadAvatar(
        bytes: ByteArray,
        mimeType: String = "image/jpeg",
        fileName: String = "avatar.jpg"
    ): Result<AvatarUploadResponseDto> = withContext(Dispatchers.IO) {
        try {
            val mediaType = mimeType.toMediaTypeOrNull()
            val requestBody = bytes.toRequestBody(mediaType)
            val filePart = MultipartBody.Part.createFormData("file", fileName, requestBody)

            val response = apiServiceHolder().uploadAvatar(filePart)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi khi tải lên ảnh đại diện: ${e.localizedMessage ?: "Vui lòng thử lại sau"}"))
        }
    }

    /**
     * Đổi mật khẩu tài khoản
     * POST /api/v1/users/change-password
     */
    suspend fun changePassword(request: ChangePasswordRequestDto): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().changePassword(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi khi đổi mật khẩu: ${e.localizedMessage ?: "Vui lòng thử lại sau"}"))
        }
    }
}

