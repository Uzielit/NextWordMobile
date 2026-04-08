package com.utez.nextwordmobile.data.remote.dto.studentDto.messagingDto

data class MessageDto(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val body: String,
    val read: String,
    val sentAt: String
)

data class SendMessageDto(
    val senderId: String,
    val receiverId: String,
    val body: String
)