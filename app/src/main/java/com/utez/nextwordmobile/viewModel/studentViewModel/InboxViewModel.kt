package com.utez.nextwordmobile.viewModel.studentViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utez.nextwordmobile.data.remote.dto.studentDto.messagingDto.InboxDto
import com.utez.nextwordmobile.data.repository.MessagingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InboxViewModel(
    private val repository: MessagingRepository
): ViewModel() {
    private val _inboxList = MutableStateFlow<List<InboxDto>>(emptyList())
    val inboxList: StateFlow<List<InboxDto>> = _inboxList

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchInbox() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getInbox()
                if (response.isSuccessful) {
                    _inboxList.value = response.body() ?: emptyList()
                } else {
                    _inboxList.value = emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _inboxList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}