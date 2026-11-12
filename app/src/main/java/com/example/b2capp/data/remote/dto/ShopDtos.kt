package com.example.b2capp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO Chi tiết Shop công khai từ GET /api/v1/public/shops/{slugOrId}
 */
@JsonClass(generateAdapter = true)
data class PublicShopDto(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String? = null,
    @Json(name = "shopName") val shopName: String? = null,
    @Json(name = "slug") val slug: String? = null,
    @Json(name = "logoUrl") val logoUrl: String? = null,
    @Json(name = "bannerUrl") val bannerUrl: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "businessType") val businessType: String? = null,
    @Json(name = "verified") val verified: Boolean = false,
    @Json(name = "rating") val rating: Double? = null,
    @Json(name = "ratingAvg") val ratingAvg: Double? = null,
    @Json(name = "ratingCount") val ratingCount: Int? = null,
    @Json(name = "productCount") val productCount: Long? = null,
    @Json(name = "responseRate") val responseRate: String? = null,
    @Json(name = "responseTime") val responseTime: String? = null,
    @Json(name = "followerCount") val followerCount: Int? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "createdAt") val createdAt: String? = null,
    @Json(name = "isOnline") val isOnline: Boolean? = null,
    @Json(name = "isFollowing") val isFollowing: Boolean? = null
)

