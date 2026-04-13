package com.utez.nextwordmobile.viewModel.teacherViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utez.nextwordmobile.data.remote.dto.studentDto.SlotResponseDto
import com.utez.nextwordmobile.data.remote.dto.teacherDto.SlotCreateRequestDto
import com.utez.nextwordmobile.data.repository.teacher.TeacherProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TeacherSlotsViewModel(private val repository: TeacherProfileRepository) : ViewModel() {

    private val _slots = MutableStateFlow<List<SlotResponseDto>>(emptyList())
    val slots: StateFlow<List<SlotResponseDto>> = _slots

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    fun fetchSlots(date: String, teacherId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getSlotsByDate(date, teacherId)
                if (response.isSuccessful) {
                    _slots.value = response.body() ?: emptyList()
                } else {
                    _slots.value = emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createSlot(
        teacherId: String, date: String, startTime: String, endTime: String, classType: String,
        onSuccess: () -> Unit, onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = SlotCreateRequestDto(teacherId, date, startTime, endTime, classType)
                val response = repository.createSlot(request)

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) { onSuccess() }

                    fetchSlots(date, teacherId)
                } else {
                    withContext(Dispatchers.Main) { onError("Error al crear el horario. Verifica que no se cruce con otro.") }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError("Error de conexión.Intentalo más tarde.") }
            }
        }
    }
}