package com.utez.nextwordmobile.data.remote.dto.studentDto

data class StudentProfileDto(
    val id: String,
    val fullName: String,
    val email: String,
    val walletBalance: Double,
    val dateOfBirth: String?,
    val tutorName: String?,
    val tutorEmail: String?,
    val tutorPhone: String?
)

data class ReservationDto(
    val studentId: String,
    val slotId: String,
    val topic: String
)

data class SlotResponseDto(
    val slotId: String,
    val teacherName: String,
    val slotDate: String,
    val startTime: String,
    val endTime: String,
    val classType: String?
)

data class ReservationResponseDto(
    val reservationId: String,
    val participantName: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val classType: String,
    val status: String,
    val meetLink: String?
)


data class StudentUpdateDto(
    val fullName: String?,
    val phoneNumber: String?,
    val profilePicture: String?,
    val newPassword: String?,
    val tutorName: String?,
    val tutorPhone: String?,
    val tutorEmail: String?
)