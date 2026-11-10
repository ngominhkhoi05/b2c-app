package com.example

import com.example.b2capp.core.network.NetworkConfig
import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.dto.AuthResponseDto
import com.example.b2capp.data.remote.dto.LoginRequestDto
import com.example.b2capp.data.remote.dto.RefreshTokenRequestDto
import com.example.b2capp.data.remote.dto.RegisterRequestDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthAndNetworkTest {

    @Test
    fun testNetworkConfigBaseUrlNormalization() {
        // Kiểm tra url có tự động thêm dấu / nếu thiếu
        NetworkConfig.setBaseUrl("http://192.168.1.100:8080")
        assertEquals("http://192.168.1.100:8080/", NetworkConfig.getBaseUrl())

        NetworkConfig.setBaseUrl("https://api.b2cmarket.com/")
        assertEquals("https://api.b2cmarket.com/", NetworkConfig.getBaseUrl())

        // Đặt lại mặc định
        NetworkConfig.setBaseUrl(NetworkConfig.EMULATOR_BASE_URL)
        assertEquals("http://10.0.2.2:8080/", NetworkConfig.getBaseUrl())
    }

    @Test
    fun testNetworkConfigChangeListener() {
        var triggeredUrl = ""
        NetworkConfig.addOnBaseUrlChangeListener { url ->
            triggeredUrl = url
        }

        NetworkConfig.setBaseUrl("http://custom-host:8080")
        assertEquals("http://custom-host:8080/", triggeredUrl)

        // Reset
        NetworkConfig.setBaseUrl(NetworkConfig.EMULATOR_BASE_URL)
    }

    @Test
    fun testParseErrorMessageWithSingleMessage() {
        val errorJson = """
            {
                "code": 401,
                "message": "Invalid email or password",
                "errorCode": null,
                "errors": null,
                "timestamp": "2026-11-08T12:00:00",
                "path": "/api/v1/auth/login"
            }
        """.trimIndent()

        val parsed = ApiClient.parseErrorMessage(errorJson)
        assertEquals("Invalid email or password", parsed)
    }

    @Test
    fun testParseErrorMessageWithValidationErrors() {
        val validationJson = """
            {
                "code": 400,
                "message": "Validation failed",
                "errorCode": null,
                "errors": [
                    { "field": "password", "message": "Password must be at least 8 characters" }
                ],
                "timestamp": "2026-11-08T12:00:00",
                "path": "/api/v1/auth/register"
            }
        """.trimIndent()

        val parsed = ApiClient.parseErrorMessage(validationJson)
        assertTrue(parsed.contains("Password must be at least 8 characters"))
    }

    @Test
    fun testParseErrorMessageEmptyBody() {
        val parsed = ApiClient.parseErrorMessage(null)
        assertTrue(parsed.contains("Đã xảy ra lỗi không xác định"))
    }

    @Test
    fun testAuthDtosSerialization() {
        val moshi = ApiClient.moshi

        // LoginRequestDto
        val loginReq = LoginRequestDto("user@example.com", "pass123456")
        val loginAdapter = moshi.adapter(LoginRequestDto::class.java)
        val loginJson = loginAdapter.toJson(loginReq)
        assertTrue(loginJson.contains("user@example.com"))

        // RegisterRequestDto
        val regReq = RegisterRequestDto("buyer@example.com", "password888", "Nguyễn Văn B", "0901234567")
        val regAdapter = moshi.adapter(RegisterRequestDto::class.java)
        val regJson = regAdapter.toJson(regReq)
        assertTrue(regJson.contains("Nguyễn Văn B"))
        assertTrue(regJson.contains("buyer@example.com"))

        // RefreshTokenRequestDto
        val refReq = RefreshTokenRequestDto("refresh_sample_token")
        val refAdapter = moshi.adapter(RefreshTokenRequestDto::class.java)
        val refJson = refAdapter.toJson(refReq)
        assertTrue(refJson.contains("refresh_sample_token"))

        // AuthResponseDto
        val authJson = """
            {
                "accessToken": "acc_123",
                "refreshToken": "ref_456",
                "tokenType": "Bearer",
                "userId": 42,
                "email": "test@b2c.com",
                "fullName": "Le Van C",
                "role": "BUYER",
                "roles": ["BUYER"]
            }
        """.trimIndent()
        val authAdapter = moshi.adapter(AuthResponseDto::class.java)
        val authResponse = authAdapter.fromJson(authJson)

        assertNotNull(authResponse)
        assertEquals("acc_123", authResponse?.accessToken)
        assertEquals("ref_456", authResponse?.refreshToken)
        assertEquals("BUYER", authResponse?.role)
        assertEquals(42L, authResponse?.userId)
    }
}

