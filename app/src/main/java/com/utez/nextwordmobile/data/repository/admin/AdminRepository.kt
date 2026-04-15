package com.utez.nextwordmobile.data.repository.admin


import com.utez.nextwordmobile.data.remote.api.adminApi.AdminApiService
import com.utez.nextwordmobile.data.remote.dto.adminDto.AdminDashboardResponse
import com.utez.nextwordmobile.data.remote.dto.adminDto.ClassHistoryResponse
import com.utez.nextwordmobile.data.remote.dto.adminDto.CreateTeacherRequest
import com.utez.nextwordmobile.data.remote.dto.adminDto.FinancialReportResponse
import com.utez.nextwordmobile.data.remote.dto.adminDto.UpdateProfileRequest
import com.utez.nextwordmobile.data.remote.dto.adminDto.UserDirectoryResponse
import org.json.JSONObject

class AdminRepository(private val apiService: AdminApiService) {

    suspend fun getDashboardStats(token: String): Result<AdminDashboardResponse> {
        return try {
            val response = apiService.getDashboardStats("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar estadísticas: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    //  Obtener la lista de usuarios
    suspend fun getUsers(token: String): Result<List<UserDirectoryResponse>> {
        return try {
            val response = apiService.getUsers("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar usuarios: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    // rear un nuevo profesor
    suspend fun createTeacher(token: String, request: CreateTeacherRequest): Result<String> {
        return try {
            val response = apiService.createTeacher("Bearer $token", request)
            if (response.isSuccessful) {
                Result.success("Profesor creado exitosamente")
            } else {

                val errorBodyString = response.errorBody()?.string()
                val errorMessage = try {
                    val jsonObject = JSONObject(errorBodyString ?: "")
                    jsonObject.optString("error", "Error al crear profesor")
                } catch (e: Exception) {
                    "Error de validación en el servidor"
                }

                Result.failure(Exception(errorMessage)) // Mandamos el mensaje limpio al ViewModel
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: Revisa tu conexión"))
        }
    }

    suspend fun toggleTeacherStatus(token: String, teacherId: String, status: String): Result<String> {
        return try {
            val response = apiService.toggleTeacherStatus("Bearer $token", teacherId, status)
            if (response.isSuccessful) {
                Result.success("Estatus actualizado")
            } else {
                Result.failure(Exception("Error al actualizar estatus"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    suspend fun getFinancialReports(token: String): Result<FinancialReportResponse> {
        return try {
            val response = apiService.getFinancialReports("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar reportes: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    suspend fun getClassHistory(token: String): Result<List<ClassHistoryResponse>> {
        return try {
            val response = apiService.getClassHistory("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar historial: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    suspend fun updateProfile(token: String, request: UpdateProfileRequest): Result<String> {
        return try {
            val response = apiService.updateProfile("Bearer $token", request)
            if (response.isSuccessful) {
                val message = response.body()?.string() ?: "Perfil actualizado"
                Result.success(message)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }
}