package com.utez.nextwordmobile.viewModel.studentViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.utez.nextwordmobile.data.remote.dto.studentDto.messagingDto.MessageDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.messagingDto.SendMessageDto
import com.utez.nextwordmobile.data.repository.MessagingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.subscribeText

class ChatDetailViewModel(

    private val repository: MessagingRepository,
    private val myId: String,
    private val contactId: String
) : ViewModel() {
    private val _messages = MutableStateFlow<List<MessageDto>>(emptyList())
    val messages: StateFlow<List<MessageDto>> = _messages

    init {
        // Traer historial del pasado
        fetchHistory()
        // Conectar Tiempo real
        connectAndSubscribe()
    }

    private fun fetchHistory() {
        viewModelScope.launch {
            try {
                val response = repository.getChatHistory(myId, contactId)
                if (response.isSuccessful) {
                    _messages.value = response.body() ?: emptyList()

                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun connectAndSubscribe() {
        viewModelScope.launch {
            try {
                val session = repository.connectToWebSocket("ws://192.168.100.8:8080/ws")

                println("WebSocket Conectado")

                session.subscribeText("/topic/messages").collect { jsonMensaje ->
                    println("Mensaje recibido del servidor: $jsonMensaje")

                    // Convertimos el JSON que mandó Java a nuestro DTO de Kotlin
                    val nuevoMensaje = Gson().fromJson(jsonMensaje, MessageDto::class.java)

                    // Verificamos que el mensaje sea para este chat
                    val isForThisChat = (nuevoMensaje.senderId == myId && nuevoMensaje.receiverId == contactId) ||
                            (nuevoMensaje.senderId == contactId && nuevoMensaje.receiverId == myId)

                    if (isForThisChat) {
                        // Lo agregamos a la lista
                        _messages.value = _messages.value + nuevoMensaje
                    }
                }
            } catch (e: Exception) {
                println("Error en WebSocket: ${e.message}")
            }
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            val newMsg = SendMessageDto(senderId = myId, receiverId = contactId, body = text)
            // Mandamos el paquete por el túnel a Spring Boot
            repository.sendMessageRealTime(newMsg)
        }
    }

}