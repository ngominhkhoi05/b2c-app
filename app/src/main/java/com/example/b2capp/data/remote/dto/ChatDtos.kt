package com.example.b2capp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO tóm tắt cuộc hội thoại trả về từ Backend (GET /api/v1/conversations hoặc /api/v1/buyer/conversations)
 */
@JsonClass(generateAdapter = true)
data class ConversationDto(
    @Json(name = "id") val id: Long,
    @Json(name = "otherPartyId") val otherPartyId: Long? = null,
    @Json(name = "otherPartyName") val otherPartyName: String? = null,
    @Json(name = "otherPartyAvatar") val otherPartyAvatar: String? = null,
    @Json(name = "lastMessage") val lastMessage: String? = null,
    @Json(name = "lastMessageAt") val lastMessageAt: String? = null,
    @Json(name = "unreadCount") val unreadCount: Int? = 0,
    @Json(name = "createdAt") val createdAt: String? = null
)

/**
 * DTO tin nhắn chi tiết trong cuộc trò chuyện (GET /api/v1/conversations/{id}/messages)
 */
@JsonClass(generateAdapter = true)
data class ChatMessageDto(
    @Json(name = "id") val id: Long,
    @Json(name = "conversationId") val conversationId: Long? = null,
    @Json(name = "senderId") val senderId: Long? = null,
    @Json(name = "senderType") val senderType: String? = null,
    @Json(name = "senderName") val senderName: String? = null,
    @Json(name = "content") val content: String,
    @Json(name = "messageType") val messageType: String = "TEXT",
    @Json(name = "attachmentUrl") val attachmentUrl: String? = null,
    @Json(name = "refProductId") val refProductId: Long? = null,
    @Json(name = "refOrderId") val refOrderId: Long? = null,
    @Json(name = "isRead") val isRead: Boolean? = null,
    @Json(name = "readAt") val readAt: String? = null,
    @Json(name = "createdAt") val createdAt: String? = null
)

/**
 * Payload gửi tin nhắn tới Shop (POST /api/v1/conversations/{id}/messages)
 */
@JsonClass(generateAdapter = true)
data class SendMessageRequestDto(
    @Json(name = "conversationId") val conversationId: Long? = null,
    @Json(name = "sellerId") val sellerId: Long? = null,
    @Json(name = "content") val content: String,
    @Json(name = "messageType") val messageType: String = "TEXT",
    @Json(name = "attachmentUrl") val attachmentUrl: String? = null,
    @Json(name = "refProductId") val refProductId: Long? = null,
    @Json(name = "refOrderId") val refOrderId: Long? = null
)

