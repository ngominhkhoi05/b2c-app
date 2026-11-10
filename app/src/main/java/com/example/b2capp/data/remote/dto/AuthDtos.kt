package com.example.b2capp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO phản hồi xác thực JWT từ Backend (sau khi login, register hoặc refresh token).
 */
@JsonClass(generateAdapter = true)
data class AuthResponseDto(
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String,
    @Json(name = "tokenType") val tokenType: String? = "Bearer",
    @Json(name = "userId") val userId: Long? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "fullName") val fullName: String? = null,
    @Json(name = "role") val role: String? = null,
    @Json(name = "roles") val roles: List<String>? = emptyList()
)

/**
 * Yêu cầu Đăng nhập (POST /api/v1/auth/login)
 */
@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

/**
 * Yêu cầu Đăng ký tài khoản người mua (POST /api/v1/auth/register)
 */
@JsonClass(generateAdapter = true)
data class RegisterRequestDto(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "fullName") val fullName: String,
    @Json(name = "phone") val phone: String? = null
)

/**
 * Yêu cầu Làm mới Access Token (POST /api/v1/auth/refresh-token)
 */
@JsonClass(generateAdapter = true)
data class RefreshTokenRequestDto(
    @Json(name = "refreshToken") val refreshToken: String
)

/**
 * Cấu trúc lỗi trả về từ GlobalExceptionHandler của Backend
 */
@JsonClass(generateAdapter = true)
data class ApiErrorResponse(
    @Json(name = "code") val code: Int? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "errorCode") val errorCode: String? = null,
    @Json(name = "errors") val errors: List<ApiFieldError>? = null,
    @Json(name = "timestamp") val timestamp: String? = null,
    @Json(name = "path") val path: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiFieldError(
    @Json(name = "field") val field: String? = null,
    @Json(name = "message") val message: String? = null
)

