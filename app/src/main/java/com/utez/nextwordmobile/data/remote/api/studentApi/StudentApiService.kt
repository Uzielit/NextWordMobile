package com.utez.nextwordmobile.data.remote.api.studentApi

import com.utez.nextwordmobile.data.remote.dto.studentDto.StudentProfileDto
import retrofit2.Response
import retrofit2.http.GET

interface StudentApiService {
    @GET("api/students/me")
    suspend fun getMyProfile(): Response<StudentProfileDto>
}