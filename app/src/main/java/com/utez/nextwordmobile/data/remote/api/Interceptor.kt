package com.utez.nextwordmobile.data.remote.api

import android.content.Context
import com.utez.nextwordmobile.data.remote.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Sacamos el token guardado
        val token = sessionManager.fetchAuthToken()

        // Si hay token, lo mandamos en la cabecera
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}