package com.example.b2capp.core.network

/**
 * Cấu hình mạng dùng chung (Network Configuration) cho toàn bộ ứng dụng B2C.
 *
 * Mặc định hỗ trợ:
 * - Android Emulator: "http://10.0.2.2:8080/" (10.0.2.2 trỏ về localhost của máy tính host)
 * - Thiết bị thật (Physical Device qua Wi-Fi nội bộ): "http://<IP_MÁY_TÍNH>:8080/" (Ví dụ: "http://192.168.1.15:8080/")
 * - Server Production/Staging: "https://api.yourdomain.com/"
 *
 * Dễ dàng thay đổi Base URL tại runtime hoặc qua cấu hình.
 */
object NetworkConfig {

    /**
     * Base URL mặc định cho môi trường Android Emulator kết nối Spring Boot Backend.
     * Lưu ý: Retrofit bắt buộc Base URL phải kết thúc bằng dấu gạch chéo '/'.
     */

    // const val EMULATOR_BASE_URL = "http://10.0.2.2:8080/"
    const val EMULATOR_BASE_URL = "https://panther-flatness-unzip.ngrok-free.dev/"

    /**
     * Base URL đang hoạt động hiện tại.
     */
    @Volatile
    private var activeBaseUrl: String = EMULATOR_BASE_URL

    private val listeners = mutableListOf<(String) -> Unit>()

    /**
     * Lấy Base URL hiện tại (đã đảm bảo luôn kết thúc bằng '/').
     */
    fun getBaseUrl(): String = activeBaseUrl

    /**
     * Cập nhật Base URL tại runtime.
     * Sẽ thông báo cho ApiClient để tái tạo Retrofit instance nếu URL thay đổi.
     */
    @Synchronized
    fun setBaseUrl(newUrl: String) {
        val trimmed = newUrl.trim()
        val formatted = if (trimmed.endsWith("/")) trimmed else "$trimmed/"
        if (activeBaseUrl != formatted) {
            activeBaseUrl = formatted
            listeners.forEach { it.invoke(formatted) }
        }
    }

    /**
     * Đăng ký lắng nghe khi Base URL thay đổi.
     */
    @Synchronized
    fun addOnBaseUrlChangeListener(listener: (String) -> Unit) {
        listeners.add(listener)
    }

    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
}

