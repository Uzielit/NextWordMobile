package com.utez.nextwordmobile.viewModel.teacherViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utez.nextwordmobile.data.remote.dto.teacherDto.TeacherProfileUpdateDto
import com.utez.nextwordmobile.data.repository.teacher.TeacherProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeacherUpdateProfileViewModel(
    private val repository: TeacherProfileRepository
) : ViewModel(){
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _updateMessage = MutableStateFlow<String?>(null)
    val updateMessage: StateFlow<String?> = _updateMessage


    fun updateProfile(updateDto: TeacherProfileUpdateDto, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _updateMessage.value = null

            try {
                val response = repository.updateTeacherProfile(updateDto)

                if (response.isSuccessful) {
                    val successMessage = response.body()?.get("message") ?: "Perfil actualizado correctamente"
                    _updateMessage.value = successMessage

                    onSuccess()
                } else {
                    _updateMessage.value = "Error al actualizar perfil: ${response.code()}"
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