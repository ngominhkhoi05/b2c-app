package com.example.b2capp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Lớp bao bọc ApiResponse dùng chung khớp với Backend Spring Boot:
 * ApiResponse<T> { code, message, data, timestamp }
 */
@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "code") val code: Int = 200,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: T? = null,
    @Json(name = "timestamp") val timestamp: String? = null
)

