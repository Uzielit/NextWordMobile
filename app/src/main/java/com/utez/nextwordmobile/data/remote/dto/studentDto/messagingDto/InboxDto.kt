package com.utez.nextwordmobile.data.remote.dto.studentDto.messagingDto

data class InboxDto(
    val contactId: String,
    val photoPerfil: String?,
    val name: String,
    val lastMessage: String,
    val dateLastMessage: String,
    val sentByMe: Boolean,
    val unreadCount: Int,
    val lastMessageRead: String? = "0"
)