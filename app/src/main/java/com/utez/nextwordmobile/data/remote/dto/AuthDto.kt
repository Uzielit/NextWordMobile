package com.utez.nextwordmobile.data.remote.dto

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class AuthResponseDto(
    val token: String
)

data class StudentRegistrationRequest(
    val email: String,
    val password: String,
    val fullname: String,
    val phoneNumber: String,
    val dateOfBirth: String,
    val tutorName: String?,
    val tutorEmail: String?,
    val tutorPhone: String?
)