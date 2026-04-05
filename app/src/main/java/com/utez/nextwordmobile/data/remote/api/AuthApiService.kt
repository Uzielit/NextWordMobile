package com.utez.nextwordmobile.data.remote.api

import com.utez.nextwordmobile.data.remote.dto.AuthResponseDto
import com.utez.nextwordmobile.data.remote.dto.ForgotPasswordRequestDto
import com.utez.nextwordmobile.data.remote.dto.LoginRequestDto
import com.utez.nextwordmobile.data.remote.dto.ResetPasswordRequestDto
import com.utez.nextwordmobile.data.remote.dto.StudentRegistrationRequest
import com.utez.nextwordmobile.data.remote.dto.VerificationMailRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    //Rutas login y resgitro
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>

    @POST("api/auth/register/student")
    suspend fun registerStudent(@Body request: StudentRegistrationRequest): Response<Unit>

    //Recuperar contraseña
    @POST("api/auth/forgotPassword")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDto): Response<Unit>

    @POST("api/auth/resetPassword")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto): Response<Unit>

    //Verificacion de mail
    @POST("api/auth/verify-email")
    suspend fun verifyEmail(@Body request: VerificationMailRequestDto): Response<Unit>

}