package com.example.b2capp.data.remote.api

import com.example.b2capp.data.remote.dto.ApiResponse
import com.example.b2capp.data.remote.dto.ChatMessageDto
import com.example.b2capp.data.remote.dto.ConversationDto
import com.example.b2capp.data.remote.dto.PageResponse
import com.example.b2capp.data.remote.dto.SendMessageRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API Service giao tiếp các endpoints Chat & Conversation với Spring Boot Backend
 */
interface ChatApiService {

    /**
     * Lấy danh sách cuộc hội thoại của Buyer (GET /api/v1/buyer/conversations)
     */
    @GET("api/v1/buyer/conversations")
    suspend fun getBuyerConversations(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<ConversationDto>>>

    /**
     * Lấy danh sách cuộc hội thoại của User hiện tại (GET /api/v1/conversations)
     */
    @GET("api/v1/conversations")
    suspend fun getConversations(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<ConversationDto>>>

    /**
     * Lấy chi tiết một cuộc hội thoại theo ID (GET /api/v1/conversations/{id})
     */
    @GET("api/v1/conversations/{id}")
    suspend fun getConversation(
        @Path("id") id: Long
    ): Response<ApiResponse<ConversationDto>>

    /**
     * Khởi tạo hoặc lấy cuộc hội thoại với một Seller (POST /api/v1/conversations?sellerId={sellerId})
     */
    @POST("api/v1/conversations")
    suspend fun startConversation(
        @Query("sellerId") sellerId: Long
    ): Response<ApiResponse<ConversationDto>>

    /**
     * Lấy danh sách tin nhắn trong một cuộc hội thoại (GET /api/v1/conversations/{id}/messages)
     */
    @GET("api/v1/conversations/{id}/messages")
    suspend fun getMessages(
        @Path("id") id: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): Response<ApiResponse<PageResponse<ChatMessageDto>>>

    /**
     * Gửi tin nhắn mới trong một cuộc hội thoại (POST /api/v1/conversations/{id}/messages)
     */
    @POST("api/v1/conversations/{id}/messages")
    suspend fun sendMessage(
        @Path("id") id: Long,
        @Body request: SendMessageRequestDto
    ): Response<ApiResponse<ChatMessageDto>>

    /**
     * Gửi tin nhắn trực tiếp với sellerId hoặc conversationId (POST /api/v1/conversations/messages)
     */
    @POST("api/v1/conversations/messages")
    suspend fun sendMessageDirect(
        @Body request: SendMessageRequestDto
    ): Response<ApiResponse<ChatMessageDto>>
}

