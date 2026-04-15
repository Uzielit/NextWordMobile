package com.utez.nextwordmobile.viewModel.studentViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utez.nextwordmobile.data.remote.RetrofitClient
import com.utez.nextwordmobile.data.remote.api.studentApi.ReservationApiService
import com.utez.nextwordmobile.data.remote.api.studentApi.StudentApiService
import com.utez.nextwordmobile.data.remote.api.studentApi.StudentTeacherApiService
import com.utez.nextwordmobile.data.remote.dto.ReviewRequestDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.ReservationResponseDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.StudentProfileDto
import com.utez.nextwordmobile.data.remote.dto.teacherDto.TeacherDto
import com.utez.nextwordmobile.data.repository.ReservationRepository
import com.utez.nextwordmobile.data.repository.StudentProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.jvm.java

class StudentProfileViewModel (
    private val profileRepository: StudentProfileRepository,
    private val reservationRepository: ReservationRepository
): ViewModel() {
    private val _studentProfile = MutableStateFlow<StudentProfileDto?>(null)
    val studentProfile: StateFlow<StudentProfileDto?> = _studentProfile


    private val _isReviewLoading = MutableStateFlow(false)
    val isReviewLoading: StateFlow<Boolean> = _isReviewLoading

    // Función para enviar la reseña
    fun submitReview(reservationId: String, rating: Int, comment: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isReviewLoading.value = true
            try {
                val request = ReviewRequestDto(reservationId, comment, rating)
                val response = reservationRepository.leaveReview(request)

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    println("Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isReviewLoading.value = false
            }
        }
    }
    fun fetchMyProfile(context: Context) {
        viewModelScope.launch {
            try {
                val api = RetrofitClient.getAuthenticatedClient(context)
                    .create(StudentApiService::class.java)
                val response = api.getMyProfile()

                if (response.isSuccessful) {
                    _studentProfile.value = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val _teachersList = MutableStateFlow<List<TeacherDto>>(emptyList())
    val teachersList: StateFlow<List<TeacherDto>> = _teachersList

    fun fetchTeachers(context: Context) {
        viewModelScope.launch {
            try {
                val api = RetrofitClient.getAuthenticatedClient(context)
                    .create(StudentTeacherApiService::class.java)
                val response = api.getAllTeachers()
                if (response.isSuccessful) {
                    _teachersList.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val _agendaList = MutableStateFlow<List<ReservationResponseDto>>(emptyList())
    val agendaList: StateFlow<List<ReservationResponseDto>> = _agendaList

    private val _historialList = MutableStateFlow<List<ReservationResponseDto>>(emptyList())
    val historialList: StateFlow<List<ReservationResponseDto>> = _historialList

    fun fetchMyAgenda(context: Context) {
        viewModelScope.launch {
            try {
                val api = RetrofitClient.getAuthenticatedClient(context).create(ReservationApiService::class.java)
                val response = api.getStudentAgenda()
                if (response.isSuccessful) {
                    _agendaList.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // FUNCIÓN PARA TRAER HISTORIAL
    fun fetchMyHistory(context: Context) {
        viewModelScope.launch {
            try {
                val api = RetrofitClient.getAuthenticatedClient(context).create(ReservationApiService::class.java)
                val response = api.getStudentHistory()
                if (response.isSuccessful) {
                    _historialList.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun claimPayment(paymentIdStr: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val paymentId = paymentIdStr.toLongOrNull()
        if (paymentId == null) {
            onError("Por favor ingresa un número de transacción válido.")
            return
        }
        viewModelScope.launch {
            try {

                val response = profileRepository.claimPayment(paymentId)

                if (response.isSuccessful) {
                    val msg = response.body()?.get("message") ?: "¡Saldo recargado!"
                    onSuccess(msg)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = try {
                        val json = JSONObject(errorBody ?: "")
                        json.optString("error", "Verifica tu número de transacción.")
                    } catch (e: Exception) {
                        "Verifica tu número de transacción."
                    }
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                onError("Error de red. Verifica tu internet.")
            }
        }
    }
}