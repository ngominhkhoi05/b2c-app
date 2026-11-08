package com.example.b2capp.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.b2capp.core.model.UserProfile
import com.example.b2capp.core.network.NetworkConfig
import com.example.b2capp.data.remote.dto.AuthResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Quản lý lưu trữ phiên đăng nhập (Access Token, Refresh Token, User Info)
 * sử dụng SharedPreferences. Nếu chạy trong Unit Test (context == null),
 * tự động fallback sang bộ nhớ RAM (in-memory) để tránh Exception.
 */
class TokenManager private constructor(context: Context?) {

    private val prefs: SharedPreferences? =
        context?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val inMemoryMap = mutableMapOf<String, Any?>()

    private val _isLoggedInState = MutableStateFlow(isLoggedIn())
    val isLoggedInState: StateFlow<Boolean> = _isLoggedInState.asStateFlow()

    init {
        // Khôi phục custom Base URL nếu đã từng lưu trước đó
        val savedBaseUrl = getString(KEY_CUSTOM_BASE_URL)
        if (!savedBaseUrl.isNullOrBlank()) {
            NetworkConfig.setBaseUrl(savedBaseUrl)
        }
    }

    private fun getString(key: String): String? {
        return prefs?.getString(key, null) ?: (inMemoryMap[key] as? String)
    }

    private fun putString(key: String, value: String?) {
        if (prefs != null) {
            prefs.edit().putString(key, value).apply()
        } else {
            inMemoryMap[key] = value
        }
    }

    private fun putLong(key: String, value: Long) {
        if (prefs != null) {
            prefs.edit().putLong(key, value).apply()
        } else {
            inMemoryMap[key] = value
        }
    }

    private fun getLong(key: String, defValue: Long): Long {
        return prefs?.getLong(key, defValue) ?: (inMemoryMap[key] as? Long ?: defValue)
    }

    private fun putStringSet(key: String, values: Set<String>) {
        if (prefs != null) {
            prefs.edit().putStringSet(key, values).apply()
        } else {
            inMemoryMap[key] = values
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getStringSet(key: String): Set<String> {
        return prefs?.getStringSet(key, emptySet()) ?: (inMemoryMap[key] as? Set<String> ?: emptySet())
    }

    /**
     * Lưu toàn bộ thông tin đăng nhập từ AuthResponseDto
     */
    fun saveAuthResponse(auth: AuthResponseDto) {
        putString(KEY_ACCESS_TOKEN, auth.accessToken)
        putString(KEY_REFRESH_TOKEN, auth.refreshToken)
        putString(KEY_TOKEN_TYPE, auth.tokenType ?: "Bearer")
        auth.userId?.let { putLong(KEY_USER_ID, it) } ?: remove(KEY_USER_ID)
        putString(KEY_USER_EMAIL, auth.email ?: "")
        putString(KEY_USER_FULL_NAME, auth.fullName ?: "")
        putString(KEY_USER_ROLE, auth.role ?: "")
        putStringSet(KEY_USER_ROLES, auth.roles?.toSet() ?: emptySet())
        _isLoggedInState.value = true
    }

    /**
     * Cập nhật cặp Access Token & Refresh Token mới (sau khi Refresh Token thành công)
     */
    fun updateTokens(accessToken: String, refreshToken: String) {
        putString(KEY_ACCESS_TOKEN, accessToken)
        putString(KEY_REFRESH_TOKEN, refreshToken)
        _isLoggedInState.value = true
    }

    fun getAccessToken(): String? = getString(KEY_ACCESS_TOKEN)

    fun getRefreshToken(): String? = getString(KEY_REFRESH_TOKEN)

    fun getTokenType(): String = getString(KEY_TOKEN_TYPE) ?: "Bearer"

    fun getUserId(): Long? {
        val id = getLong(KEY_USER_ID, -1L)
        return if (id != -1L) id else null
    }

    fun getEmail(): String? = getString(KEY_USER_EMAIL)

    fun getFullName(): String? = getString(KEY_USER_FULL_NAME)

    fun getRole(): String? = getString(KEY_USER_ROLE)

    fun getRoles(): List<String> = getStringSet(KEY_USER_ROLES).toList()

    fun isLoggedIn(): Boolean = !getAccessToken().isNullOrBlank()

    /**
     * Tạo UserProfile từ thông tin đang lưu
     */
    fun getUserProfile(): UserProfile {
        return UserProfile(
            fullName = getFullName() ?: "Người dùng",
            phoneNumber = "",
            email = getEmail() ?: "",
            gender = "Nam",
            birthDate = ""
        )
    }

    private fun remove(key: String) {
        if (prefs != null) {
            prefs.edit().remove(key).apply()
        } else {
            inMemoryMap.remove(key)
        }
    }

    /**
     * Xóa sạch thông tin đăng nhập (khi Đăng xuất hoặc Refresh Token hết hạn)
     */
    fun clearAuth() {
        remove(KEY_ACCESS_TOKEN)
        remove(KEY_REFRESH_TOKEN)
        remove(KEY_TOKEN_TYPE)
        remove(KEY_USER_ID)
        remove(KEY_USER_EMAIL)
        remove(KEY_USER_FULL_NAME)
        remove(KEY_USER_ROLE)
        remove(KEY_USER_ROLES)
        _isLoggedInState.value = false
    }

    /**
     * Lưu tùy chỉnh Base URL lâu dài
     */
    fun saveCustomBaseUrl(url: String) {
        putString(KEY_CUSTOM_BASE_URL, url)
        NetworkConfig.setBaseUrl(url)
    }

    companion object {
        private const val PREF_NAME = "b2c_auth_prefs"
        private const val KEY_ACCESS_TOKEN = "key_access_token"
        private const val KEY_REFRESH_TOKEN = "key_refresh_token"
        private const val KEY_TOKEN_TYPE = "key_token_type"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_USER_EMAIL = "key_user_email"
        private const val KEY_USER_FULL_NAME = "key_user_full_name"
        private const val KEY_USER_ROLE = "key_user_role"
        private const val KEY_USER_ROLES = "key_user_roles"
        private const val KEY_CUSTOM_BASE_URL = "key_custom_base_url"

        @Volatile
        private var instance: TokenManager? = null

        fun init(context: Context): TokenManager {
            return synchronized(this) {
                val newInstance = TokenManager(context.applicationContext)
                instance = newInstance
                newInstance
            }
        }

        fun getInstance(): TokenManager {
            return instance ?: synchronized(this) {
                instance ?: TokenManager(null).also { instance = it }
            }
        }
    }
}
