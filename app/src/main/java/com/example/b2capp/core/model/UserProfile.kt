package com.example.b2capp.core.model

import com.example.b2capp.data.remote.dto.UpdateProfileRequestDto
import com.example.b2capp.data.remote.dto.UserProfileDto
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Model đại diện cho thông tin hồ sơ người dùng trong toàn bộ ứng dụng
 */
data class UserProfile(
    val id: Long? = null,
    val fullName: String = "Nguyễn Văn A",
    val phoneNumber: String = "0987 654 321",
    val email: String = "nguyenvana@gmail.com",
    val avatarUrl: String? = null,
    val gender: String = "Nam",
    val birthDate: String = "15/08/1995",
    val role: String = "BUYER",
    val status: String = "ACTIVE"
)

/**
 * Chuyển đổi từ UserProfileDto nhận từ API Backend sang domain model UserProfile
 */
fun UserProfileDto.toDomain(): UserProfile {
    val displayGender = when (this.gender?.uppercase()) {
        "FEMALE" -> "Nữ"
        "OTHER" -> "Khác"
        else -> "Nam"
    }

    val displayBirthDate = formatIsoToDisplayDate(this.dateOfBirth) ?: "15/08/1995"

    return UserProfile(
        id = this.id,
        fullName = this.fullName?.takeIf { it.isNotBlank() } ?: "Người dùng",
        phoneNumber = this.phone?.takeIf { it.isNotBlank() } ?: "",
        email = this.email?.takeIf { it.isNotBlank() } ?: "",
        avatarUrl = this.avatarUrl?.takeIf { it.isNotBlank() },
        gender = displayGender,
        birthDate = displayBirthDate,
        role = this.role ?: "BUYER",
        status = this.status ?: "ACTIVE"
    )
}

/**
 * Chuyển đổi từ UserProfile sang UpdateProfileRequestDto để gửi lên API Backend
 */
fun UserProfile.toUpdateRequest(): UpdateProfileRequestDto {
    val backendGender = when (this.gender.trim().lowercase()) {
        "nữ", "female" -> "FEMALE"
        "khác", "other" -> "OTHER"
        else -> "MALE"
    }

    val isoBirthDate = formatDisplayToIsoDate(this.birthDate)

    // Chuẩn hóa số điện thoại: bỏ khoảng trắng và chuyển +84 thành 0 nếu có
    val cleanPhone = this.phoneNumber.replace("\\s+".toRegex(), "").let {
        if (it.startsWith("+84")) {
            "0" + it.removePrefix("+84")
        } else {
            it
        }
    }.ifBlank { null }

    return UpdateProfileRequestDto(
        fullName = this.fullName.trim().ifBlank { null },
        phone = cleanPhone,
        avatarUrl = this.avatarUrl?.ifBlank { null },
        gender = backendGender,
        dateOfBirth = isoBirthDate
    )
}

/**
 * Chuyển đổi chuỗi ngày từ định dạng ISO yyyy-MM-dd sang dd/MM/yyyy
 */
fun formatIsoToDisplayDate(isoDate: String?): String? {
    if (isoDate.isNullOrBlank()) return null
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = parser.parse(isoDate.trim())
        if (date != null) formatter.format(date) else isoDate
    } catch (_: Exception) {
        isoDate
    }
}

/**
 * Chuyển đổi chuỗi ngày từ định dạng dd/MM/yyyy sang ISO yyyy-MM-dd
 */
fun formatDisplayToIsoDate(displayDate: String?): String? {
    if (displayDate.isNullOrBlank()) return null
    return try {
        val parser = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = parser.parse(displayDate.trim())
        if (date != null) formatter.format(date) else displayDate
    } catch (_: Exception) {
        displayDate
    }
}
