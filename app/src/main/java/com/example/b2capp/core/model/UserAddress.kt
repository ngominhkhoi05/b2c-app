package com.example.b2capp.core.model

import com.example.b2capp.data.remote.dto.AddressDto
import com.example.b2capp.data.remote.dto.AddressRequestDto

/**
 * Data model for User Delivery Addresses.
 */
data class UserAddress(
    val id: String,
    val recipientName: String,
    val phoneNumber: String,
    val addressType: String, // "Nhà riêng" hoặc "Văn phòng" hoặc "Khác"
    val fullAddress: String,
    val streetDetail: String = "",
    val provinceCode: String? = null,
    val districtCode: String? = null,
    val wardCode: String? = null,
    val isDefault: Boolean = false,
    val deliveryNote: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

/**
 * Chuyển đổi từ AddressDto từ Backend API sang domain model UserAddress
 */
fun AddressDto.toDomain(): UserAddress {
    val displayType = when (this.type?.trim()?.uppercase()) {
        "OFFICE" -> "Văn phòng"
        else -> "Nhà riêng"
    }

    // Nếu streetDetail chưa có, lấy phần đầu của fullAddress
    val street = this.streetDetail?.takeIf { it.isNotBlank() }
        ?: this.fullAddress?.split(",")?.firstOrNull()?.trim()
        ?: ""

    return UserAddress(
        id = this.id?.toString() ?: "",
        recipientName = this.recipientName.orEmpty(),
        phoneNumber = this.phone.orEmpty(),
        addressType = displayType,
        fullAddress = this.fullAddress.orEmpty(),
        streetDetail = street,
        provinceCode = this.provinceCode,
        districtCode = this.districtCode,
        wardCode = this.wardCode,
        isDefault = this.isDefault ?: false,
        deliveryNote = null,
        latitude = this.latitude,
        longitude = this.longitude
    )
}

/**
 * Chuyển đổi từ UserAddress sang AddressRequestDto để gửi lên Backend API
 */
fun UserAddress.toRequestDto(): AddressRequestDto {
    val backendType = if (this.addressType.contains("Văn phòng", ignoreCase = true) || this.addressType.equals("OFFICE", ignoreCase = true)) {
        "OFFICE"
    } else {
        "HOME"
    }

    // Chuẩn hóa số điện thoại: bỏ khoảng trắng và chuyển định dạng
    val cleanPhone = this.phoneNumber.replace("\\s+".toRegex(), "").replace("-", "").let {
        if (it.startsWith("+84")) {
            "0" + it.removePrefix("+84")
        } else {
            it
        }
    }.ifBlank { "0987654321" }

    val street = if (this.streetDetail.isNotBlank()) {
        this.streetDetail.trim()
    } else {
        this.fullAddress.split(",").firstOrNull()?.trim()?.ifBlank { this.fullAddress } ?: this.fullAddress
    }

    return AddressRequestDto(
        recipientName = this.recipientName.trim().ifBlank { "Người nhận" },
        phone = cleanPhone,
        provinceCode = this.provinceCode?.trim()?.ifBlank { null },
        districtCode = this.districtCode?.trim()?.ifBlank { null },
        wardCode = this.wardCode?.trim()?.ifBlank { null },
        streetDetail = street.ifBlank { "Địa chỉ" },
        fullAddress = this.fullAddress.trim().ifBlank { street },
        type = backendType,
        isDefault = this.isDefault,
        latitude = this.latitude,
        longitude = this.longitude
    )
}
