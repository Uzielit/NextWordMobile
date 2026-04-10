package com.utez.nextwordmobile.data.remote.dto.teacherDto

data class TeacherDto(
    val id: String,
    val fullName: String,
    val specialization: String?,
    val averageRating: Double,
    val professionalDescription: String? = null,
    val certifications: String? = null,
    val yearsOfExperience: Int? = 0
)