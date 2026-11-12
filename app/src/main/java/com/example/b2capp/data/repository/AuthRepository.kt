package com.example.b2capp.data.repository

import com.example.b2capp.core.model.UserProfile
import com.example.b2capp.data.local.TokenManager
import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.api.UserApiService
import com.example.b2capp.data.remote.dto.AuthResponseDto
import com.example.b2capp.data.remote.dto.GoogleLoginRequestDto
import com.example.b2capp.data.remote.dto.LoginRequestDto
import com.example.b2capp.data.remote.dto.RegisterRequestDto
import com.example.b2capp.data.remote.dto.UserProfileDto
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
     * Đăng nhập hoặc đăng ký bằng Google ID Token
     */
    suspend fun loginWithGoogle(idToken: String): Result<AuthResponseDto> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.unauthenticatedAuthApiService.loginWithGoogle(
                GoogleLoginRequestDto(idToken = idToken)
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
     * Gửi mã OTP quên mật khẩu đến Email người dùng
     */
    suspend fun sendForgotPasswordOtp(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.unauthenticatedAuthApiService.sendMobileForgotPasswordOtp(
                com.example.b2capp.data.remote.dto.MobileForgotPasswordRequestDto(email = email.trim())
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối đến máy chủ: ${e.localizedMessage ?: "Vui lòng kiểm tra kết nối mạng"}"))
        }
    }

    /**
     * Xác thực mã OTP 6 số, nhận về resetToken
     */
    suspend fun verifyForgotPasswordOtp(
        email: String,
        otp: String
    ): Result<com.example.b2capp.data.remote.dto.MobileVerifyOtpResponseDto> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.unauthenticatedAuthApiService.verifyMobileOtp(
                com.example.b2capp.data.remote.dto.MobileVerifyOtpRequestDto(
                    email = email.trim(),
                    otp = otp.trim()
                )
            )
            if (response.isSuccessful) {
                val data = response.body()?.data
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("Không nhận được mã xác nhận đặt lại mật khẩu"))
                }
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối đến máy chủ: ${e.localizedMessage ?: "Vui lòng kiểm tra kết nối mạng"}"))
        }
    }

    /**
     * Đặt lại mật khẩu mới với resetToken
     */
    suspend fun resetPassword(
        email: String,
        resetToken: String,
        newPassword: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.unauthenticatedAuthApiService.resetMobilePassword(
                com.example.b2capp.data.remote.dto.MobileResetPasswordRequestDto(
                    email = email.trim(),
                    resetToken = resetToken,
                    newPassword = newPassword
                )
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối đến máy chủ: ${e.localizedMessage ?: "Vui lòng kiểm tra kết nối mạng"}"))
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

    /**
     * Xác thực người dùng hiện tại từ API Backend (GET /api/v1/users/me).
     * Khi Access Token hết hạn, OkHttp Authenticator sẽ tự động thử Refresh Token.
     * Nếu Refresh Token hết hạn hoặc User không tồn tại (401, 404, 403), phương thức trả về lỗi.
     */
    suspend fun validateCurrentUser(): Result<UserProfileDto> = withContext(Dispatchers.IO) {
        try {
            val userApiService = ApiClient.createAuthenticatedService(UserApiService::class.java)
            val response = userApiService.getMyProfile()
            if (response.isSuccessful && response.body()?.data != null) {
                val profile = response.body()!!.data!!
                Result.success(profile)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception("HTTP ${response.code()}: $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    fun getCurrentUserProfile(): UserProfile = tokenManager.getUserProfile()
}

