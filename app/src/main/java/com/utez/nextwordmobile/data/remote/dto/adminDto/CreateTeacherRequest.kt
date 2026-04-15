package com.utez.nextwordmobile.data.remote.dto.adminDto

data class CreateTeacherRequest(
    val fullName: String,
    val email: String,
    val password: String
)