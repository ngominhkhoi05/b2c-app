package com.example.b2capp.core.model

/**
 * Data model for User Profile Information.
 */
data class UserProfile(
    val fullName: String = "Nguyễn Văn A",
    val phoneNumber: String = "0987 654 321",
    val email: String = "nguyenvana@gmail.com",
    val gender: String = "Nam",
    val birthDate: String = "15/08/1995"
)

