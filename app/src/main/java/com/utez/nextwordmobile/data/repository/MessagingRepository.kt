package com.utez.nextwordmobile.data.repository

import com.utez.nextwordmobile.data.remote.api.studentApi.MessagingApiService
import com.utez.nextwordmobile.data.remote.dto.studentDto.messagingDto.InboxDto
import retrofit2.Response

class MessagingRepository(private val apiService: MessagingApiService) {
    suspend fun getInbox(): Response<List<InboxDto>> {
        return apiService.getInbox()
    }
}