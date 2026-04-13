package com.utez.nextwordmobile.viewModel.studentViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utez.nextwordmobile.data.remote.RetrofitClient
import com.utez.nextwordmobile.data.remote.api.studentApi.ReservationApiService
import com.utez.nextwordmobile.data.remote.dto.studentDto.ReservationDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.ReservationResponseDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.SlotResponseDto
import com.utez.nextwordmobile.data.repository.ReservationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudenReservationViewModel(
    private val repository: ReservationRepository
): ViewModel(){

    private val _availableSlots = MutableStateFlow<List<SlotResponseDto>>(emptyList())
    val availableSlots: StateFlow<List<SlotResponseDto>> = _availableSlots

    private val _isBookingLoading = MutableStateFlow(false)
    val isBookingLoading: StateFlow<Boolean> = _isBookingLoading




    fun fetchSlots(teacherId: String, date: String) {
        viewModelScope.launch {
            try {
                val response = repository.getAvailableSlots(date, date, teacherId)

                if (response.isSuccessful) {
                    _availableSlots.value = response.body() ?: emptyList()
                } else {
                    _availableSlots.value = emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _availableSlots.value = emptyList()
            }
        }
    }

    fun confirmBooking(
        studentId: String,
        slotId: String,
        topic: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isBookingLoading.value = true
            try {
                val request = ReservationDto(studentId, slotId, topic)
                val response = repository.bookSlot(request)

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("No se pudo completar la reserva. Verifica tu saldo.")
                }
            } catch (e: Exception) {
                onError("Error de conexión con el servidor.")
            } finally {
                _isBookingLoading.value = false
            }
        }
    }


}