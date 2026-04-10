package com.utez.nextwordmobile.viewModel.studentViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utez.nextwordmobile.data.remote.dto.studentDto.StudentUpdateDto
import com.utez.nextwordmobile.data.repository.StudentProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentUpdateProfileViewModel(private val repository: StudentProfileRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _updateMessage = MutableStateFlow<String?>(null)
    val updateMessage: StateFlow<String?> = _updateMessage

    fun updateProfile(dto: StudentUpdateDto, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.updateProfile(dto)
                if (response.isSuccessful) {
                    _updateMessage.value = "Perfil actualizado con éxito"
                    onSuccess()
                } else {
                    _updateMessage.value = "Error al actualizar: ${response.code()}"
                }
            } catch (e: Exception) {
                _updateMessage.value = "Error de conexión"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessage() { _updateMessage.value = null }
}
