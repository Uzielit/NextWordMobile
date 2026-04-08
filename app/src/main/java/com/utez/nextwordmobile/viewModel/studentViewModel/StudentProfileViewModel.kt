package com.utez.nextwordmobile.viewModel.studentViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utez.nextwordmobile.data.remote.RetrofitClient
import com.utez.nextwordmobile.data.remote.api.ReservationApiService
import com.utez.nextwordmobile.data.remote.api.studentApi.StudentApiService
import com.utez.nextwordmobile.data.remote.api.studentApi.TeacherApiService
import com.utez.nextwordmobile.data.remote.dto.studentDto.ReservationResponseDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.StudentProfileDto
import com.utez.nextwordmobile.data.remote.dto.teacherDto.TeacherDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.jvm.java

class StudentProfileViewModel : ViewModel() {
    private val _studentProfile = MutableStateFlow<StudentProfileDto?>(null)
    val studentProfile: StateFlow<StudentProfileDto?> = _studentProfile


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
                    .create(TeacherApiService::class.java)
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
}