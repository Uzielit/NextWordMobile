package com.utez.nextwordmobile.viewModel.studentViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utez.nextwordmobile.data.remote.dto.studentDto.ReservationResponseDto
import com.utez.nextwordmobile.data.repository.ReservationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentClassesViewModel(
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    private val _classesList = MutableStateFlow<List<ReservationResponseDto>>(emptyList())
    val classesList: StateFlow<List<ReservationResponseDto>> = _classesList

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Guardamos el filtro actual. Empezamos con "Pendiente" por defecto
    private val _currentFilter = MutableStateFlow("Pendiente")
    val currentFilter: StateFlow<String> = _currentFilter

    init {
        fetchClasses("Pendiente")
    }

    fun setFilter(filter: String) {
        _currentFilter.value = filter
        fetchClasses(filter)
    }

    private fun fetchClasses(filterUI: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {

                val statusParam = when (filterUI) {
                    "Todas" -> null
                    "Pendiente" -> "Pendiente"
                    "Completada" -> "Completado"
                    "Cancelada" -> "Cancelado"
                    else -> null
                }

                val response = reservationRepository.getMyClasses(statusParam)
                if (response.isSuccessful) {
                    _classesList.value = response.body() ?: emptyList()
                } else {
                    _classesList.value = emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    }