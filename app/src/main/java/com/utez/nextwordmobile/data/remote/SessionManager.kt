package com.utez.nextwordmobile.data.remote

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("NextWordPrefs", Context.MODE_PRIVATE)

    // Función para guardar la pulsera
    fun saveAuthToken(token: String) {
        prefs.edit().putString("JWT_TOKEN", token).apply()
    }

    // Función para sacar la pulsera
    fun fetchAuthToken(): String? {
        return prefs.getString("JWT_TOKEN", null)
    }

    // Función por si el usuario cierra sesión
    fun clearSession() {
        prefs.edit().remove("JWT_TOKEN").apply()
    }
}