package com.example.b2capp.data.remote.interceptor

import com.example.b2capp.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor tự động đính kèm Header Authorization: Bearer <token>
 * vào mọi request cần xác thực.
 */
class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Nếu request đã có sẵn header Authorization (ví dụ request refresh token hoặc endpoint tự cấu hình)
        if (originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }

        val accessToken = tokenManager.getAccessToken()
        return if (!accessToken.isNullOrBlank()) {
            val authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
            chain.proceed(authenticatedRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}

