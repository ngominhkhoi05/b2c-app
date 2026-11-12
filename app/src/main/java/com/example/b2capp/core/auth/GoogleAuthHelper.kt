package com.example.b2capp.core.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

/**
 * Tiện ích hỗ trợ Đăng nhập Google sử dụng Android Credential Manager hiện đại.
 * Lấy Google ID Token trên thiết bị để gửi lên Backend xác thực.
 */
object GoogleAuthHelper {

    /**
     * Web Client ID từ Google Cloud Console (khớp với GOOGLE_CLIENT_ID của Backend).
     */
    const val GOOGLE_SERVER_CLIENT_ID = "605436530214-a1cusc4gpankj8p7nccbfhha0jglq79k.apps.googleusercontent.com"

    suspend fun signInWithGoogle(
        context: Context,
        serverClientId: String = GOOGLE_SERVER_CLIENT_ID
    ): Result<String> {
        return try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            when {
                credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        Result.success(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Result.failure(Exception("Không thể phân tích Google ID Token: ${e.message}"))
                    }
                }
                else -> {
                    Result.failure(Exception("Kiểu xác thực không hỗ trợ: ${credential.type}"))
                }
            }
        } catch (e: GetCredentialCancellationException) {
            // Người dùng đóng popup chọn tài khoản Google
            Result.failure(Exception("Đã huỷ đăng nhập Google"))
        } catch (e: GetCredentialException) {
            Result.failure(Exception("Lỗi Google Sign-In: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Không thể đăng nhập bằng Google: ${e.localizedMessage ?: "Vui lòng thử lại"}"))
        }
    }
}

