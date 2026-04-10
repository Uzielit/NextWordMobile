package com.utez.nextwordmobile.data.remote.api.studentApi

import com.utez.nextwordmobile.data.remote.dto.studentDto.StudentProfileDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.StudentUpdateDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface StudentApiService {
    @GET("api/students/me")
    suspend fun getMyProfile(): Response<StudentProfileDto>


    // Endpoint para actualizar el perfil del usuario

    @PUT("api/students/profile")
    suspend fun updateStudentProfile(
        @Body updateDto: StudentUpdateDto
    ): Response<String>

}