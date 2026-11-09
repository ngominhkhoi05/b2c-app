package com.example.b2capp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO đại diện cho thông tin hồ sơ người dùng trả về từ GET /api/v1/users/me
 */
@JsonClass(generateAdapter = true)
data class UserProfileDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "fullName") val fullName: String? = null,
    @Json(name = "avatarUrl") val avatarUrl: String? = null,
    @Json(name = "gender") val gender: String? = null,
    @Json(name = "dateOfBirth") val dateOfBirth: String? = null,
    @Json(name = "role") val role: String? = null,
    @Json(name = "status") val status: String? = null
)

/**
 * Request DTO cập nhật hồ sơ người dùng (PUT /api/v1/users/me)
 */
@JsonClass(generateAdapter = true)
data class UpdateProfileRequestDto(
    @Json(name = "fullName") val fullName: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "avatarUrl") val avatarUrl: String? = null,
    @Json(name = "gender") val gender: String? = null,       // "MALE", "FEMALE", "OTHER"
    @Json(name = "dateOfBirth") val dateOfBirth: String? = null // ISO 8601 "yyyy-MM-dd"
)

/**
 * Response DTO sau khi tải lên ảnh đại diện (POST /api/v1/users/me/avatar)
 */
@JsonClass(generateAdapter = true)
data class AvatarUploadResponseDto(
    @Json(name = "avatarUrl") val avatarUrl: String? = null,
    @Json(name = "avatarPublicId") val avatarPublicId: String? = null,
    @Json(name = "format") val format: String? = null,
    @Json(name = "bytes") val bytes: Long? = null
)

/**
 * Request DTO đổi mật khẩu (POST /api/v1/users/change-password)
 */
@JsonClass(generateAdapter = true)
data class ChangePasswordRequestDto(
    @Json(name = "currentPassword") val currentPassword: String,
    @Json(name = "newPassword") val newPassword: String,
    @Json(name = "confirmPassword") val confirmPassword: String
)
