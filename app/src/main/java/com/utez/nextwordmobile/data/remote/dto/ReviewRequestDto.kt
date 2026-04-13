package com.utez.nextwordmobile.data.remote.dto

data class ReviewRequestDto(
    val reservationId: String,
    val comentary: String,
    val rating: Int
)