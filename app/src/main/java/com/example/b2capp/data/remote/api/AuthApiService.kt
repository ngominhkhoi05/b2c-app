package com.example.b2capp.data.remote.api

import com.example.b2capp.data.remote.dto.ApiResponse
import com.example.b2capp.data.remote.dto.AuthResponseDto
import com.example.b2capp.data.remote.dto.GoogleLoginRequestDto
import com.example.b2capp.data.remote.dto.LoginRequestDto
import com.example.b2capp.data.remote.dto.RefreshTokenRequestDto
import com.example.b2capp.data.remote.dto.RegisterRequestDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Interface Retrofit cho Auth API
interface AuthApiService {

    // Đăng nhập tài khoản bằng email và mật khẩu
    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<ApiResponse<AuthResponseDto>>

    // Đăng nhập / đăng ký bằng Google ID Token
    @POST("api/v1/auth/google")
    suspend fun loginWithGoogle(
        @Body request: GoogleLoginRequestDto
    ): Response<ApiResponse<AuthResponseDto>>

    // Đăng ký tài khoản người mua (BUYER) mới
    @POST("api/v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): Response<ApiResponse<AuthResponseDto>>

    // Làm mới token đồng bộ (dùng trong OkHttp Authenticator khi gặp HTTP 401)
    @POST("api/v1/auth/refresh-token")
    fun refreshTokenSync(
        @Body request: RefreshTokenRequestDto
    ): Call<ApiResponse<AuthResponseDto>>

    // Đăng xuất khỏi hệ thống
    @POST("api/v1/auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>
}
