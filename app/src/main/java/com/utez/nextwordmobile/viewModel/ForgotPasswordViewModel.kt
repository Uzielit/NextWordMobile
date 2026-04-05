package com.utez.nextwordmobile.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utez.nextwordmobile.data.remote.dto.ForgotPasswordRequestDto
import com.utez.nextwordmobile.data.remote.dto.ResetPasswordRequestDto
import com.utez.nextwordmobile.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException

class ForgotPasswordViewModel : ViewModel() {

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


    fun forgotPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val request = ForgotPasswordRequestDto(email)
                val response = repository.forgotPassword(request)

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) { onSuccess() }
                } else {
                    val errorString = response.errorBody()?.string()
                    var mensajeLimpio = "Error desconocido"

                    if (errorString != null) {
                        try {
                            val jsonObject = JSONObject(errorString)
                            mensajeLimpio = jsonObject.getString("error")
                        } catch (e: Exception) {
                            mensajeLimpio = errorString
                        }
                    }

                    withContext(Dispatchers.Main) { onError(mensajeLimpio) }
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onError("No hay conexión con el servidor. Revisa tu internet.") }
            }
        }
    }

    fun resetPassword(
        email: String,
        code: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = ResetPasswordRequestDto(email, code, newPassword)
                val response = repository.resetPassword(request)

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) { onSuccess() }
                } else {
                    val errorString = response.errorBody()?.string()
                    var mensajeLimpio = "Código incorrecto.Intenta de nuevo."

                    if (errorString != null) {
                        try {
                            val jsonObject = JSONObject(errorString)
                            mensajeLimpio = jsonObject.getString("error")
                        } catch (e: Exception) {
                            mensajeLimpio = errorString
                        }
                    }

                    withContext(Dispatchers.Main) { onError(mensajeLimpio) }
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
                e.printStackTrace()
                withContext(Dispatchers.Main) { onError("No hay conexión con el servidor. Revisa tu internet.")}
            }
        }
    }
}