package com.utez.nextwordmobile.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utez.nextwordmobile.data.remote.dto.LoginRequestDto
import com.utez.nextwordmobile.data.remote.dto.StudentRegistrationRequest
import com.utez.nextwordmobile.data.remote.dto.VerificationMailRequestDto
import com.utez.nextwordmobile.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private fun extractErrorMessage(errorBodyString: String?, defaultMessage: String): String {
        if (errorBodyString == null) return defaultMessage
        return try {
            val jsonObject = JSONObject(errorBodyString)

            jsonObject.optString("error", jsonObject.optString("message", defaultMessage))
        } catch (e: Exception) {
            errorBodyString
        }
    }

    private fun getFriendlyNetworkMessage(e: Exception): String {
        return if (e is IOException) {
            "No hay conexión con el servidor. Revisa tu internet."
        } else {
            "Ocurrió un error inesperado. Inténtalo de nuevo."
        }
    }

    fun login(email: String, password: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val request = LoginRequestDto(email, password)

        viewModelScope.launch {
            try {
                val response = repository.login(request)
                if (response.isSuccessful) {
                    val token = response.body()?.token ?: ""
                    withContext(Dispatchers.Main) { onSuccess(token) }
                } else {
                    val errorLimpio = extractErrorMessage(response.errorBody()?.string(), "Credenciales incorrectas")
                    withContext(Dispatchers.Main) { onError(errorLimpio) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { onError(getFriendlyNetworkMessage(e)) }
            }
        }
    }

    fun registerStudent(
        email: String, password: String, fullname: String, phone: String,
        dob: String, tutorName: String, tutorEmail: String, tutorPhone: String,
        onSuccess: () -> Unit, onError: (String) -> Unit
    ) {
        val request = StudentRegistrationRequest(
            email = email, password = password, fullname = fullname, phoneNumber = phone,
            dateOfBirth = dob, tutorName = tutorName.ifBlank { null },
            tutorEmail = tutorEmail.ifBlank { null }, tutorPhone = tutorPhone.ifBlank { null }
        )

        viewModelScope.launch {
            try {
                val response = repository.registerStudent(request)

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) { onSuccess() }
                } else {
                    val errorLimpio = extractErrorMessage(response.errorBody()?.string(), "Error al registrar")
                    withContext(Dispatchers.Main) { onError(errorLimpio) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { onError(getFriendlyNetworkMessage(e)) }
            }
        }
    }

    fun verifyAccount(email: String, code: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val request = VerificationMailRequestDto(email, code)

        viewModelScope.launch {
            try {
                val response = repository.verificationMail(request)

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) { onSuccess() }
                } else {
                    val errorLimpio = extractErrorMessage(response.errorBody()?.string(), "Código incorrecto.")
                    withContext(Dispatchers.Main) { onError(errorLimpio) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { onError(getFriendlyNetworkMessage(e)) }
            }
        }
    }
}