package com.utez.nextwordmobile.data.remote.dto.adminDto

data class AdminDashboardResponse(
    val activeProfessors: Int,
    val classesToday: Int,
    val monthlyIncome: Double,
    val newStudentsThisWeek: Int,
    val completedClassesThisWeek: Int,
    val cancelledClassesThisWeek: Int,
    val weeklyIncome: Double
)

data class UpdateProfileRequest(
    val fullName: String?,
    val phoneNumber: String?,
    val profilePicture: String?,
    val newPassword: String?,
)