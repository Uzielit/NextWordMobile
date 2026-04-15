package com.utez.nextwordmobile.viewModel.teacherViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utez.nextwordmobile.data.remote.dto.studentDto.ReservationResponseDto
import com.utez.nextwordmobile.data.repository.teacher.TeacherProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeacherClassesViewModel(private val repository: TeacherProfileRepository) : ViewModel() {

    private val _classesList = MutableStateFlow<List<ReservationResponseDto>>(emptyList())
    val classesList: StateFlow<List<ReservationResponseDto>> = _classesList

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentFilter = MutableStateFlow("Todas")
    val currentFilter: StateFlow<String> = _currentFilter

    private var allClasses = emptyList<ReservationResponseDto>()

    init {
        fetchClasses()
    }

    fun fetchClasses() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getTeacherAgenda()
                if (response.isSuccessful) {
                    allClasses = response.body() ?: emptyList()
                    applyFilter(_currentFilter.value)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setFilter(filter: String) {
        _currentFilter.value = filter
        applyFilter(filter)
    }

    private fun applyFilter(filter: String) {
        _classesList.value = if (filter == "Todas") {
            allClasses
        } else {
            // Filtrar inteligentemente ignorando la "s" final (Pendientes -> Pendiente)
            allClasses.filter { it.status.startsWith(filter.dropLast(1), ignoreCase = true) }
        }
    }
}