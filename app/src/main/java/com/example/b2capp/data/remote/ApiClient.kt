package com.example.b2capp.data.remote

import com.example.b2capp.core.network.NetworkConfig
import com.example.b2capp.data.local.TokenManager
import com.example.b2capp.data.remote.api.AuthApiService
import com.example.b2capp.data.remote.dto.ApiErrorResponse
import com.example.b2capp.data.remote.interceptor.AuthInterceptor
import com.example.b2capp.data.remote.interceptor.TokenAuthenticator
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton quản lý cấu hình Network Client (OkHttp & Retrofit) dùng chung cho toàn bộ ứng dụng:
 * - Hỗ trợ Base URL tập trung và cập nhật linh hoạt tại runtime.
 * - Tự động đính kèm JWT Authorization header.
 * - Tự động Refresh Token khi gặp lỗi HTTP 401.
 * - Cung cấp hàm parse lỗi chuẩn từ Backend.
 */
object ApiClient {

    val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val tokenManager: TokenManager
        get() = TokenManager.getInstance()

    /**
     * OkHttpClient cơ bản (không đính kèm token hoặc authenticator).
     * Dùng cho đăng nhập, đăng ký và chính endpoint refresh-token để tránh vòng lặp 401.
     */
    private val baseOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(NetworkConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NetworkConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NetworkConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /**
     * OkHttpClient có bảo vệ (tự động gắn Bearer Token và kích hoạt TokenAuthenticator khi 401).
     */
    private val authenticatedOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(NetworkConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NetworkConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NetworkConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(tokenManager))
            .authenticator(TokenAuthenticator(tokenManager) { unauthenticatedAuthApiService })
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Volatile
    private var unauthenticatedRetrofit: Retrofit? = null

    @Volatile
    private var authenticatedRetrofit: Retrofit? = null

    init {
        // Tự động rebuild Retrofit nếu Base URL thay đổi
        NetworkConfig.addOnBaseUrlChangeListener {
            synchronized(this) {
                unauthenticatedRetrofit = null
                authenticatedRetrofit = null
            }
        }
    }

    private fun getUnauthenticatedRetrofit(): Retrofit {
        return unauthenticatedRetrofit ?: synchronized(this) {
            unauthenticatedRetrofit ?: buildRetrofit(baseOkHttpClient).also { unauthenticatedRetrofit = it }
        }
    }

    private fun getAuthenticatedRetrofit(): Retrofit {
        return authenticatedRetrofit ?: synchronized(this) {
            authenticatedRetrofit ?: buildRetrofit(authenticatedOkHttpClient).also { authenticatedRetrofit = it }
        }
    }

    private fun buildRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConfig.getBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * AuthApiService không yêu cầu token trước (dành cho Login, Register, Refresh Token)
     */
    val unauthenticatedAuthApiService: AuthApiService
        get() = getUnauthenticatedRetrofit().create(AuthApiService::class.java)

    /**
     * AuthApiService có kèm token (dành cho Logout hoặc endpoint cần xác thực)
     */
    val authenticatedAuthApiService: AuthApiService
        get() = getAuthenticatedRetrofit().create(AuthApiService::class.java)

    /**
     * Tạo Service công khai không yêu cầu token trước (Categories, Products, Promotions...)
     */
    fun <T> createService(serviceClass: Class<T>): T {
        return getUnauthenticatedRetrofit().create(serviceClass)
    }

    /**
     * ChatApiService có bảo vệ token (quản lý tin nhắn và hội thoại)
     */
    val chatApiService: com.example.b2capp.data.remote.api.ChatApiService
        get() = createAuthenticatedService(com.example.b2capp.data.remote.api.ChatApiService::class.java)

    /**
     * WishlistApiService có bảo vệ token (quản lý danh sách sản phẩm yêu thích)
     */
    val wishlistApiService: com.example.b2capp.data.remote.api.WishlistApiService
        get() = createAuthenticatedService(com.example.b2capp.data.remote.api.WishlistApiService::class.java)

    /**
     * Tạo Service cho bất kỳ endpoint nào cần xác thực (Cart, Order, User...)
     */
    fun <T> createAuthenticatedService(serviceClass: Class<T>): T {
        return getAuthenticatedRetrofit().create(serviceClass)
    }

    /**
     * Trích xuất thông điệp lỗi thân thiện với người dùng từ JSON phản hồi lỗi của Backend
     */
    fun parseErrorMessage(errorBody: String?): String {
        if (errorBody.isNullOrBlank()) {
            return "Đã xảy ra lỗi không xác định. Vui lòng thử lại."
        }
        return try {
            val adapter = moshi.adapter(ApiErrorResponse::class.java)
            val errorResponse = adapter.fromJson(errorBody)

            if (!errorResponse?.errors.isNullOrEmpty()) {
                errorResponse?.errors?.mapNotNull { it.message }?.joinToString("\n")
                    ?: errorResponse?.message ?: "Dữ liệu không hợp lệ"
            } else {
                errorResponse?.message ?: "Yêu cầu thất bại (mã ${errorResponse?.code ?: "error"})"
            }
        } catch (e: Exception) {
            "Lỗi kết nối máy chủ: ${e.localizedMessage ?: "Vui lòng kiểm tra lại mạng hoặc địa chỉ máy chủ"}"
        }
    }
}
