package com.utez.nextwordmobile.data.remote.api.studentApi

import com.utez.nextwordmobile.data.remote.dto.teacherDto.TeacherDto
import retrofit2.Response
import retrofit2.http.GET

interface TeacherApiService {
    @GET("api/teachers")
    suspend fun getAllTeachers(): Response<List<TeacherDto>>
}