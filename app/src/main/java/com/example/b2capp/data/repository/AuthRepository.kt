package com.example.b2capp.data.repository

import com.example.b2capp.core.model.UserProfile
import com.example.b2capp.data.local.TokenManager
import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.dto.AuthResponseDto
import com.example.b2capp.data.remote.dto.LoginRequestDto
import com.example.b2capp.data.remote.dto.RegisterRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository xử lý các nghiệp vụ Xác thực (Login, Register, Logout)
 * và đồng bộ trạng thái người dùng với TokenManager.
 */
class AuthRepository(
    private val tokenManager: TokenManager = TokenManager.getInstance()
) {

    /**
     * Đăng nhập với Email và Mật khẩu qua API Backend
     */
    suspend fun login(
        email: String,
        password: String
    ): Result<AuthResponseDto> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.unauthenticatedAuthApiService.login(
                LoginRequestDto(email = email, password = password)
            )

            if (response.isSuccessful) {
                val authData = response.body()?.data
                if (authData != null) {
                    tokenManager.saveAuthResponse(authData)
                    Result.success(authData)
                } else {
                    Result.failure(Exception("Không nhận được dữ liệu xác thực từ máy chủ"))
                }
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối đến máy chủ: ${e.localizedMessage ?: "Vui lòng kiểm tra kết nối mạng và Base URL"}"))
        }
    }

    /**
     * Đăng ký tài khoản người mua (BUYER) qua API Backend
     */
    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phone: String? = null
    ): Result<AuthResponseDto> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.unauthenticatedAuthApiService.register(
                RegisterRequestDto(
                    email = email,
                    password = password,
                    fullName = fullName,
                    phone = phone?.ifBlank { null }
                )
            )

            if (response.isSuccessful) {
                val authData = response.body()?.data
                if (authData != null) {
                    tokenManager.saveAuthResponse(authData)
                    Result.success(authData)
                } else {
                    Result.failure(Exception("Không nhận được dữ liệu xác thực từ máy chủ"))
                }
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối đến máy chủ: ${e.localizedMessage ?: "Vui lòng kiểm tra kết nối mạng và Base URL"}"))
        }
    }

    /**
     * Đăng xuất khỏi hệ thống: gọi API backend và xóa session local
     */
    suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ApiClient.authenticatedAuthApiService.logout()
        } catch (_: Exception) {
            // Ngay cả khi request logout mạng lỗi, vẫn xóa session local
        }
        tokenManager.clearAuth()
        Result.success(Unit)
    }

    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    fun getCurrentUserProfile(): UserProfile = tokenManager.getUserProfile()
}

