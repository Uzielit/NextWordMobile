package com.utez.nextwordmobile.data.remote.api

import com.utez.nextwordmobile.data.remote.dto.studentDto.ReservationDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.ReservationResponseDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.SlotResponseDto
import retrofit2.Response
import retrofit2.http.*

interface ReservationApiService {

    @GET("api/reservations/slots/filter")
    suspend fun getAvailableSlots(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("teacherId") teacherId: String? = null
    ): Response<List<SlotResponseDto>>

    @POST("api/reservations/book")
    suspend fun bookSlot(
        @Body request: ReservationDto
    ): Response<String>

    // ENDPOINT DE PRÓXIMAS CLASES
    @GET("api/reservations/myAgenda")
    suspend fun getStudentAgenda(): Response<List<ReservationResponseDto>>

    //  ENDPOINT DE HISTORIAL DE CLASES
    @GET("api/reservations/myClass")
    suspend fun getStudentHistory(
        @Query("status") status: String? = null
    ): Response<List<ReservationResponseDto>>



}