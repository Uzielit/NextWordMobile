package com.utez.nextwordmobile.data.remote.api.adminApi

import com.utez.nextwordmobile.data.remote.dto.adminDto.AdminDashboardResponse
import com.utez.nextwordmobile.data.remote.dto.adminDto.ClassHistoryResponse
import com.utez.nextwordmobile.data.remote.dto.adminDto.CreateTeacherRequest
import com.utez.nextwordmobile.data.remote.dto.adminDto.FinancialReportResponse
import com.utez.nextwordmobile.data.remote.dto.adminDto.UpdateProfileRequest
import com.utez.nextwordmobile.data.remote.dto.adminDto.UserDirectoryResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminApiService {
    @GET("api/admin/dashboard/stats")
    suspend fun getDashboardStats(
        @Header("Authorization") token: String
    ): Response<AdminDashboardResponse>

    @GET("api/admin/users")
    suspend fun getUsers(@Header("Authorization") token: String): Response<List<UserDirectoryResponse>>


    @POST("api/admin/teachers")
    suspend fun createTeacher(
        @Header("Authorization") token: String,
        @Body request: CreateTeacherRequest
    ): Response<ResponseBody>

    @PUT("api/admin/teachers/{id}/status")
    suspend fun toggleTeacherStatus(
        @Header("Authorization") token: String,
        @Path("id") teacherId: String,
        @Query("status") status: String
    ): Response<ResponseBody>

    @GET("api/admin/reports/financial")
    suspend fun getFinancialReports(@Header("Authorization") token: String): Response<FinancialReportResponse>

    @GET("api/admin/classes/history")
    suspend fun getClassHistory(
        @Header("Authorization") token: String
    ): Response<List<ClassHistoryResponse>>

    @PUT("api/admin/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<ResponseBody>
}