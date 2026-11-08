package com.example.b2capp.core.model

/**
 * Data model for User Delivery Addresses.
 */
data class UserAddress(
    val id: String,
    val recipientName: String,
    val phoneNumber: String,
    val addressType: String, // "Nhà riêng" hoặc "Văn phòng"
    val fullAddress: String,
    val isDefault: Boolean = false,
    val deliveryNote: String? = null
)

