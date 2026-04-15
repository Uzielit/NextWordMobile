package com.utez.nextwordmobile.viewModel.adminViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utez.nextwordmobile.data.remote.SessionManager
import com.utez.nextwordmobile.data.remote.dto.adminDto.ClassHistoryResponse
import com.utez.nextwordmobile.data.repository.admin.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminClassesUiState(
    val isLoading: Boolean = true,
    val classes: List<ClassHistoryResponse> = emptyList(),
    val error: String? = null
)

class AdminClassesViewModel(private val repository: AdminRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminClassesUiState())
    val uiState: StateFlow<AdminClassesUiState> = _uiState.asStateFlow()

    fun fetchClassHistory(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val token = SessionManager(context).fetchAuthToken() ?: ""

            val result = repository.getClassHistory(token)
            result.onSuccess { data ->
                _uiState.update { it.copy(isLoading = false, classes = data) }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message) }
            }
        }
    }
}