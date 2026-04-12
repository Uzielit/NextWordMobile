package com.utez.nextwordmobile.data.repository

import com.utez.nextwordmobile.data.remote.api.studentApi.ReservationApiService
import com.utez.nextwordmobile.data.remote.dto.ReviewRequestDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.ReservationDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.ReservationResponseDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.SlotResponseDto
import retrofit2.Response

class ReservationRepository( private val apiService : ReservationApiService) {

    suspend fun getAvailableSlots(startDate: String, endDate: String, teacherId: String?): Response<List<SlotResponseDto>> {
        return apiService.getAvailableSlots(startDate, endDate, teacherId)
    }

    suspend fun bookSlot(request: ReservationDto): Response<String> {
        return apiService.bookSlot(request)
    }

    suspend fun getMyClasses(status: String?): Response<List<ReservationResponseDto>> {
        return apiService.getStudentHistory(status)
    }


    suspend fun leaveReview(request: ReviewRequestDto) = apiService.leaveReview(request)

}