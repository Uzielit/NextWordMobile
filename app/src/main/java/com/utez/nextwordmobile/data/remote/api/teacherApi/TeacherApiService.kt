package com.utez.nextwordmobile.data.remote.api.teacherApi

import com.utez.nextwordmobile.data.remote.dto.teacherDto.TeacherDto
import retrofit2.Response
import retrofit2.http.GET

interface TeacherApiService {
    @GET("api/teachers/me")
    suspend fun getMyProfile(): Response<TeacherDto>


}