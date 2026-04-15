package com.utez.nextwordmobile.data.repository

import com.utez.nextwordmobile.data.remote.api.studentApi.StudentApiService
import com.utez.nextwordmobile.data.remote.dto.studentDto.StudentUpdateDto
import retrofit2.Response


class StudentProfileRepository(private val apiService: StudentApiService) {
    suspend fun updateProfile(updateDto: StudentUpdateDto): Response<String> {
        return apiService.updateStudentProfile(updateDto)
    }

    suspend fun claimPayment(paymentId: Long) = apiService.claimPayment(paymentId)
}