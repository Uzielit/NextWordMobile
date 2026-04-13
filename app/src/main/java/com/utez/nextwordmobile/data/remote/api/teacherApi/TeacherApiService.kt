package com.utez.nextwordmobile.data.remote.api.teacherApi

import com.utez.nextwordmobile.data.remote.dto.studentDto.ReservationResponseDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.SlotResponseDto
import com.utez.nextwordmobile.data.remote.dto.teacherDto.SlotCreateRequestDto
import com.utez.nextwordmobile.data.remote.dto.teacherDto.TeacherDto
import com.utez.nextwordmobile.data.remote.dto.teacherDto.TeacherProfileUpdateDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface TeacherApiService {
    @GET("api/teachers/me")
    suspend fun getMyProfile(): Response<TeacherDto>


    @PUT("api/teachers/profile")
    suspend fun updateTeacherProfile(
        @Body request: TeacherProfileUpdateDto
    ): Response<Map<String, String>>

    @GET("api/reservations/teacherAgenda")
    suspend fun getTeacherAgenda(): Response<List<ReservationResponseDto>>

    @GET("api/reservations/slots/filter")
    suspend fun getSlotsByDate(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("teacherId") teacherId: String
    ): Response<List<SlotResponseDto>>

    @POST("api/reservations/slot")
    suspend fun createSlot(@Body request: SlotCreateRequestDto): Response<Map<String, String>>

}