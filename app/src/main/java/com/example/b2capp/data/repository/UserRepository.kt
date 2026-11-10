package com.example.b2capp.data.repository

import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.api.UserApiService
import com.example.b2capp.data.remote.dto.AddressDto
import com.example.b2capp.data.remote.dto.AddressRequestDto
import com.example.b2capp.data.remote.dto.AvatarUploadResponseDto
import com.example.b2capp.data.remote.dto.ChangePasswordRequestDto
import com.example.b2capp.data.remote.dto.UpdateProfileRequestDto
import com.example.b2capp.data.remote.dto.UploadResponseDto
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
     * Tải lên hình ảnh bất kỳ lên Cloudinary thông qua API generic POST /api/v1/upload
     */
    suspend fun uploadImage(
        bytes: ByteArray,
        mimeType: String = "image/jpeg",
        fileName: String = "image.jpg",
        purpose: String = "uploads"
    ): Result<UploadResponseDto> = withContext(Dispatchers.IO) {
        try {
            val mediaType = mimeType.toMediaTypeOrNull()
            val requestBody = bytes.toRequestBody(mediaType)
            val filePart = MultipartBody.Part.createFormData("file", fileName, requestBody)

            val response = apiServiceHolder().uploadImage(filePart, purpose)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi khi tải ảnh lên Cloudinary: ${e.localizedMessage ?: "Vui lòng thử lại sau"}"))
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

    /**
     * Lấy danh sách địa chỉ nhận hàng của người dùng hiện tại
     * GET /api/v1/users/addresses
     */
    suspend fun getMyAddresses(): Result<List<AddressDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().getMyAddresses()
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi khi tải danh sách địa chỉ: ${e.localizedMessage ?: "Vui lòng thử lại sau"}"))
        }
    }

    /**
     * Tạo mới một địa chỉ nhận hàng
     * POST /api/v1/users/addresses
     */
    suspend fun createAddress(request: AddressRequestDto): Result<AddressDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().createAddress(request)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi khi thêm địa chỉ mới: ${e.localizedMessage ?: "Vui lòng thử lại sau"}"))
        }
    }

    /**
     * Cập nhật thông tin địa chỉ nhận hàng
     * PUT /api/v1/users/addresses/{id}
     */
    suspend fun updateAddress(addressId: Long, request: AddressRequestDto): Result<AddressDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().updateAddress(addressId, request)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi khi cập nhật địa chỉ: ${e.localizedMessage ?: "Vui lòng thử lại sau"}"))
        }
    }

    /**
     * Xóa một địa chỉ nhận hàng
     * DELETE /api/v1/users/addresses/{id}
     */
    suspend fun deleteAddress(addressId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().deleteAddress(addressId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi khi xóa địa chỉ: ${e.localizedMessage ?: "Vui lòng thử lại sau"}"))
        }
    }

    /**
     * Đặt địa chỉ làm mặc định
     * PUT /api/v1/users/addresses/{id}/default
     */
    suspend fun setDefaultAddress(addressId: Long): Result<AddressDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().setDefaultAddress(addressId)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi khi thiết lập địa chỉ mặc định: ${e.localizedMessage ?: "Vui lòng thử lại sau"}"))
        }
    }
}

