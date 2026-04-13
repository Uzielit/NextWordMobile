package com.utez.nextwordmobile.data.remote.dto.teacherDto

data class SlotCreateRequestDto(
    val teacherId: String,
    val slotDate: String,
    val startTime: String,
    val endTime: String,
    val classType: String
)