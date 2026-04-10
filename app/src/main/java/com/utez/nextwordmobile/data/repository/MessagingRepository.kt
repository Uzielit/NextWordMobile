package com.utez.nextwordmobile.data.repository

import com.google.gson.Gson
import com.utez.nextwordmobile.data.remote.api.studentApi.MessagingApiService
import com.utez.nextwordmobile.data.remote.dto.studentDto.messagingDto.InboxDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.messagingDto.MessageDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.messagingDto.SendMessageDto
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import retrofit2.Response

class MessagingRepository(private val apiService: MessagingApiService) {
    suspend fun getInbox(): Response<List<InboxDto>> {
        return apiService.getInbox()
    }


    private val stompClient = StompClient(OkHttpWebSocketClient())
    var stompSession: StompSession? = null

    suspend fun getChatHistory(user1: String, user2: String): Response<List<MessageDto>> {
        return apiService.getChatHistory(user1, user2)
    }

    suspend fun markMessageAsRead(idMensaje: String): Response<MessageDto> {
        return apiService.markMessageAsRead(idMensaje)
    }

    suspend fun connectToWebSocket(url: String): StompSession {
        val session = stompClient.connect(url)
        stompSession = session
        return session
    }

    // Enviar un mensaje por el ws
    suspend fun sendMessageRealTime(message: SendMessageDto) {
        val json = Gson().toJson(message)
        stompSession?.sendText("/app/chat", json)
    }
}