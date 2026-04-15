package com.utez.nextwordmobile.viewModel.adminViewModel

import com.utez.nextwordmobile.data.remote.dto.adminDto.AdminDashboardResponse

data class AdminDashboardUiState(
    val isLoading: Boolean = true,
    val stats: AdminDashboardResponse = AdminDashboardResponse(0, 0, 0.0, 0, 0, 0, 0.0),
    val error: String? = null
)