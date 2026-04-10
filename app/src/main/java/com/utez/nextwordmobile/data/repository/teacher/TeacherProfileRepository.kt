package com.utez.nextwordmobile.data.repository.teacher

import com.utez.nextwordmobile.data.remote.api.teacherApi.TeacherApiService

class TeacherProfileRepository(private val api: TeacherApiService) {
    suspend fun getMyProfile() = api.getMyProfile()
}