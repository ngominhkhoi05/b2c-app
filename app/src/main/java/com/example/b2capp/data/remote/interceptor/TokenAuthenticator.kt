package com.example.b2capp.data.remote.interceptor

import android.util.Log
import com.example.b2capp.data.local.TokenManager
import com.example.b2capp.data.remote.api.AuthApiService
import com.example.b2capp.data.remote.dto.RefreshTokenRequestDto
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * OkHttp Authenticator thực hiện cơ chế Refresh Token tự động khi gặp HTTP 401 Unauthorized:
 * 1. Chặn lỗi 401 từ bất kỳ API bảo vệ nào.
 * 2. Khóa đồng bộ (synchronized) để chống race condition khi nhiều request đồng thời bị 401.
 * 3. Nếu thread khác đã refresh token thành công rồi, request hiện tại chỉ cần lấy token mới và retry ngay.
 * 4. Gọi POST /api/v1/auth/refresh-token lấy cặp accessToken & refreshToken mới (Token Rotation).
 * 5. Lưu token mới vào TokenManager và retry request ban đầu với header mới.
 * 6. Nếu Refresh Token hết hạn hoặc không hợp lệ, xóa session và đăng xuất.
 */
class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val authServiceHolder: () -> AuthApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val requestAuthHeader = response.request.header("Authorization")

        // 1. Nếu request ban đầu không gửi Authorization header hoặc đã retry quá 2 lần -> dừng
        if (requestAuthHeader == null || responseCount(response) >= 3) {
            return null
        }

        val failedToken = requestAuthHeader.removePrefix("Bearer ").trim()

        synchronized(this) {
            val currentToken = tokenManager.getAccessToken()

            // 2. Kiểm tra nếu một request khác vừa hoàn thành refresh token trong lúc request này đang đợi lock:
            if (!currentToken.isNullOrBlank() && currentToken != failedToken) {
                Log.d("TokenAuthenticator", "Token đã được refresh bởi thread khác, retry với token mới")
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            // 3. Lấy Refresh Token hiện tại
            val refreshToken = tokenManager.getRefreshToken()
            if (refreshToken.isNullOrBlank()) {
                Log.w("TokenAuthenticator", "Không tìm thấy Refresh Token, xóa session")
                tokenManager.clearAuth()
                return null
            }

            Log.d("TokenAuthenticator", "Đang thực hiện Refresh Token từ backend...")
            try {
                // Gọi API refresh token đồng bộ
                val refreshCall = authServiceHolder().refreshTokenSync(
                    RefreshTokenRequestDto(refreshToken)
                )
                val refreshResponse = refreshCall.execute()

                if (refreshResponse.isSuccessful) {
                    val authData = refreshResponse.body()?.data
                    if (authData != null && authData.accessToken.isNotBlank()) {
                        Log.i("TokenAuthenticator", "Refresh Token thành công! Cập nhật token mới.")
                        tokenManager.updateTokens(
                            accessToken = authData.accessToken,
                            refreshToken = authData.refreshToken
                        )
                        // Retry request ban đầu với accessToken mới
                        return response.request.newBuilder()
                            .header("Authorization", "Bearer ${authData.accessToken}")
                            .build()
                    }
                }

                // Nếu backend trả về lỗi (401 Refresh Token expired/revoked)
                Log.w("TokenAuthenticator", "Refresh token thất bại (mã ${refreshResponse.code()}), phiên đăng nhập hết hạn")
                tokenManager.clearAuth()
                return null
            } catch (e: Exception) {
                Log.e("TokenAuthenticator", "Lỗi mạng khi refresh token", e)
                return null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}

