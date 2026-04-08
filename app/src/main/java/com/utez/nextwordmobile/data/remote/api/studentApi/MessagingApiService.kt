package com.utez.nextwordmobile.data.remote.api.studentApi

import com.utez.nextwordmobile.data.remote.dto.studentDto.messagingDto.InboxDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.messagingDto.MessageDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface MessagingApiService {
    @GET("api/messages/inbox")
    suspend fun getInbox(): Response<List<InboxDto>>

    @GET("api/messages/history/{user1}/{user2}")
    suspend fun getChatHistory(
        @Path("user1") user1: String,
        @Path("user2") user2: String
    ): Response<List<MessageDto>>

    @PUT("api/messages/{idMensaje}/read")
    suspend fun markMessageAsRead(
        @Path("idMensaje") idMensaje: String
    ): Response<MessageDto>
}