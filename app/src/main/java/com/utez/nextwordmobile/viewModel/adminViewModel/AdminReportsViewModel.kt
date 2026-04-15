package com.utez.nextwordmobile.viewModel.adminViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utez.nextwordmobile.data.remote.SessionManager
import com.utez.nextwordmobile.data.remote.dto.adminDto.FinancialReportResponse
import com.utez.nextwordmobile.data.repository.admin.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminReportsUiState(
    val isLoading: Boolean = true,
    val reportData: FinancialReportResponse? = null,
    val error: String? = null
)

class AdminReportsViewModel(private val repository: AdminRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminReportsUiState())
    val uiState: StateFlow<AdminReportsUiState> = _uiState.asStateFlow()

    fun fetchReports(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val token = SessionManager(context).fetchAuthToken() ?: ""

            val result = repository.getFinancialReports(token)
            result.onSuccess { data ->
                _uiState.update { it.copy(isLoading = false, reportData = data) }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message) }
            }
        }
    }
}