package com.utez.nextwordmobile.data.remote.dto

data class ForgotPasswordRequestDto(
    val email: String
)

data class ResetPasswordRequestDto(
    val email: String,
    val token: String,
    val newPassword: String
)