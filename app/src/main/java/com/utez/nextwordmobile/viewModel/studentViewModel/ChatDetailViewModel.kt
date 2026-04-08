package com.utez.nextwordmobile.viewModel.studentViewModel

import androidx.lifecycle.ViewModel
import com.utez.nextwordmobile.data.repository.MessagingRepository

class ChatDetailViewModel(

    private val repository: MessagingRepository,
    private val myId: String,
    private val contactId: String
) : ViewModel() {

}