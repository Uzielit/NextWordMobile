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

    fun updateProfile(context: Context, updateDto: UpdateProfileRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _updateMessage.value = null

            try {
                val token = SessionManager(context).fetchAuthToken() ?: ""

                val result = repository.updateProfile(token, updateDto)

                result.onSuccess { mensaje ->
                    // 🌟 MAGIA: Si el servidor dice "OK", guardamos los datos nuevos en la memoria del celular
                    val prefs = context.getSharedPreferences("NextWordPrefs", Context.MODE_PRIVATE)
                    prefs.edit()
                        .putString("USER_NAME", updateDto.fullName ?: "")
                        .putString("USER_PHONE", updateDto.phoneNumber ?: "")
                        .apply() // Aplicamos los cambios

                    _updateMessage.value = mensaje
                    onSuccess()
                }
                result.onFailure { excepcion ->
                    _updateMessage.value = "Error al guardar: ${excepcion.message}"
                }
            } catch (e: Exception) {
                _updateMessage.value = "Excepción crítica: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessage() {
        _updateMessage.value = null
    }
}