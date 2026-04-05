package com.utez.nextwordmobile.data.repository

import com.utez.nextwordmobile.data.remote.RetrofitClient
import com.utez.nextwordmobile.data.remote.dto.ForgotPasswordRequestDto
import com.utez.nextwordmobile.data.remote.dto.LoginRequestDto
import com.utez.nextwordmobile.data.remote.dto.ResetPasswordRequestDto
import com.utez.nextwordmobile.data.remote.dto.StudentRegistrationRequest
import com.utez.nextwordmobile.data.remote.dto.VerificationMailRequestDto

class AuthRepository {

    private val apiService = RetrofitClient.authService

    suspend fun registerStudent(request: StudentRegistrationRequest) =
        apiService.registerStudent(request)

    suspend fun login(request: LoginRequestDto) =
        apiService.login(request)

    suspend fun verificationMail(request: VerificationMailRequestDto) =
        apiService.verifyEmail(request)

    suspend fun forgotPassword(request: ForgotPasswordRequestDto) =
        apiService.forgotPassword(request)

    suspend fun resetPassword(request: ResetPasswordRequestDto) =
        apiService.resetPassword(request)



}