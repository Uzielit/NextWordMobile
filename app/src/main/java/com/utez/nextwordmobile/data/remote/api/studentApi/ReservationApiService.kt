package com.utez.nextwordmobile.data.remote.api.studentApi

import com.utez.nextwordmobile.data.remote.dto.ReviewRequestDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.CancelClassRequestDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.ReservationDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.ReservationResponseDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.SlotResponseDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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
    ): Response<ResponseBody>

    // ENDPOINT DE PRÓXIMAS CLASES
    @GET("api/reservations/myAgenda")
    suspend fun getStudentAgenda(): Response<List<ReservationResponseDto>>

    //  ENDPOINT DE HISTORIAL DE CLASES
    @GET("api/reservations/myClass")
    suspend fun getStudentHistory(
        @Query("status") status: String? = null
    ): Response<List<ReservationResponseDto>>


    @POST("api/reviews")
    suspend fun leaveReview(@Body request: ReviewRequestDto): Response<String>

    @POST("api/reservations/cancel")
    suspend fun cancelClass(
        @Body request: CancelClassRequestDto
    ): Response<ResponseBody>

}