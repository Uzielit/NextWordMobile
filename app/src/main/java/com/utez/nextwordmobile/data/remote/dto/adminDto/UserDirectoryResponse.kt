package com.utez.nextwordmobile.data.remote.dto.adminDto

data class UserDirectoryResponse(
    val userId: String,
    val fullName: String,
    val email: String,
    val roleId: Int,
    val registrationDate: String,
    val status: String
)