package com.utez.nextwordmobile.data.repository.teacher

import com.utez.nextwordmobile.data.remote.api.teacherApi.TeacherApiService
import com.utez.nextwordmobile.data.remote.dto.teacherDto.SlotCreateRequestDto
import com.utez.nextwordmobile.data.remote.dto.teacherDto.TeacherProfileUpdateDto

class TeacherProfileRepository(private val api: TeacherApiService) {
    suspend fun getMyProfile() = api.getMyProfile()


    suspend fun getSlotsByDate(date: String, teacherId: String) =
        api.getSlotsByDate(date, date, teacherId)

    suspend fun createSlot(request: SlotCreateRequestDto) =
        api.createSlot(request)

    suspend fun updateTeacherProfile(dto: TeacherProfileUpdateDto) =
        api.updateTeacherProfile(dto)

    suspend fun getTeacherAgenda() = api.getTeacherAgenda()
}