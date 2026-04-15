package com.utez.nextwordmobile.viewModel.adminViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utez.nextwordmobile.data.remote.SessionManager
import com.utez.nextwordmobile.data.remote.dto.adminDto.CreateTeacherRequest
import com.utez.nextwordmobile.data.remote.dto.adminDto.UserDirectoryResponse
import com.utez.nextwordmobile.data.repository.admin.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUsersUiState(
    val isLoading: Boolean = true,
    val users: List<UserDirectoryResponse> = emptyList(),
    val error: String? = null,
    val isCreating: Boolean = false,
    val createMessage: String? = null
)

class AdminUsersViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUsersUiState())
    val uiState: StateFlow<AdminUsersUiState> = _uiState.asStateFlow()

    fun fetchUsers(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val token = SessionManager(context).fetchAuthToken() ?: ""

            val result = repository.getUsers(token)
            result.onSuccess { data ->
                _uiState.update { it.copy(isLoading = false, users = data) }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message) }
            }
        }
    }

    fun createTeacher(context: Context, request: CreateTeacherRequest) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true, createMessage = null) }
            val token = SessionManager(context).fetchAuthToken() ?: ""

            val result = repository.createTeacher(token, request)
            result.onSuccess { msg ->
                _uiState.update { it.copy(isCreating = false, createMessage = msg) }
                // Recargamos la lista automáticamente para ver al nuevo profe
                fetchUsers(context)
            }.onFailure { exception ->
                _uiState.update { it.copy(isCreating = false, createMessage = exception.message) }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(createMessage = null) }
    }

    fun toggleTeacherStatus(context: Context, teacherId: String, currentStatus: String) {
        viewModelScope.launch {
            val token = SessionManager(context).fetchAuthToken() ?: ""
            // Si está activo lo desactivamos, si está desactivado lo activamos
            val newStatus = if (currentStatus == "Activo") "Desactivado" else "Activo"

            val result = repository.toggleTeacherStatus(token, teacherId, newStatus)
            result.onSuccess {
                fetchUsers(context) // Recargamos la lista para ver el cambio de color
            }
        }
    }
}