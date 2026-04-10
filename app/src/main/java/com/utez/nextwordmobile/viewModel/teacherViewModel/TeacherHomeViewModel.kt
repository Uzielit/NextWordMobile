package com.utez.nextwordmobile.viewModel.teacherViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utez.nextwordmobile.data.remote.dto.teacherDto.TeacherDto
import com.utez.nextwordmobile.data.repository.teacher.TeacherProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeacherHomeViewModel(private val repository: TeacherProfileRepository) : ViewModel() {

    private val _teacherProfile = MutableStateFlow<TeacherDto?>(null)
    val teacherProfile: StateFlow<TeacherDto?> = _teacherProfile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 🌟 NOTA: Por ahora dejamos estos vacíos hasta que hagamos los endpoints en Spring Boot
    private val _agendaList = MutableStateFlow<List<Any>>(emptyList())
    val agendaList: StateFlow<List<Any>> = _agendaList

    private val _recentMessages = MutableStateFlow<List<Any>>(emptyList())
    val recentMessages: StateFlow<List<Any>> = _recentMessages

    fun fetchMyProfile( context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getMyProfile()
                if (response.isSuccessful) {
                    _teacherProfile.value = response.body()
                }
            } catch (e: Exception) {

                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}