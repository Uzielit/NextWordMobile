package com.utez.nextwordmobile.viewModel.adminViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utez.nextwordmobile.data.remote.SessionManager
import com.utez.nextwordmobile.data.remote.dto.adminDto.UpdateProfileRequest
import com.utez.nextwordmobile.data.repository.admin.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminUpdateProfileViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _updateMessage = MutableStateFlow<String?>(null)
    val updateMessage: StateFlow<String?> = _updateMessage

    fun updateProfile(context: Context, adminId: String, updateDto: UpdateProfileRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _updateMessage.value = null

            try {
                // Recuperamos el token
                val token = SessionManager(context).fetchAuthToken() ?: ""

                val result = repository.updateProfile(token, adminId, updateDto)

                result.onSuccess { mensaje ->
                    _updateMessage.value = mensaje
                    onSuccess() // 🌟 Si todo sale bien, cerramos el diálogo
                }
                result.onFailure { excepcion ->
                    _updateMessage.value = "Error al actualizar perfil: ${excepcion.message}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _updateMessage.value = "Error de conexión: Verifica tu internet"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessage() {
        _updateMessage.value = null
    }
}