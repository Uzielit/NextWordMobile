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
                    markMessagesAsRead()

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
                    val nuevoMensaje = Gson().fromJson(jsonMensaje, MessageDto::class.java)

                    val isForThisChat = (nuevoMensaje.senderId == myId && nuevoMensaje.receiverId == contactId) ||
                            (nuevoMensaje.senderId == contactId && nuevoMensaje.receiverId == myId)

                    if (isForThisChat) {
                        // 🌟 1. Evitamos duplicados y actualizamos palomitas azules
                        val currentList = _messages.value.toMutableList()
                        val index = currentList.indexOfFirst { it.id == nuevoMensaje.id } // ⚠️ Asegúrate de que MessageDto tenga 'id'

                        if (index != -1) {
                            // Si ya existe, lo reemplazamos (actualiza de read "0" a "1")
                            currentList[index] = nuevoMensaje
                        } else {
                            // Si es nuevo, lo agregamos al final
                            currentList.add(nuevoMensaje)
                        }

                        _messages.value = currentList

                        // 🌟 2. Si el mensaje que acaba de llegar es del otro usuario, lo marcamos leído inmediatamente
                        if (nuevoMensaje.senderId == contactId && nuevoMensaje.read == "0") {
                            markMessagesAsRead()
                        }
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
    fun markMessagesAsRead() {

        val mensajesSinLeer = _messages.value.filter { it.senderId == contactId && it.read == "0" }
        _messages.value = _messages.value.map { msg ->
            if (msg.senderId == contactId && msg.read == "0") {
                msg.copy(read = "1")
            } else {
                msg
            }
        }
        viewModelScope.launch {
            mensajesSinLeer.forEach { msg ->
                try {
                    repository.markMessageAsRead(msg.id)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}