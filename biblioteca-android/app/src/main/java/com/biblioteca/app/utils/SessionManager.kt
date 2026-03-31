package com.biblioteca.app.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("biblioteca_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_TOKEN = "token"
        const val KEY_USERNAME = "username"
        const val KEY_ROL = "rol"
    }

    fun guardarSesion(token: String, username: String, rol: String) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_USERNAME, username)
            putString(KEY_ROL, rol)
            apply()
        }
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getBearerToken(): String = "Bearer ${getToken()}"

    fun getUsername(): String = prefs.getString(KEY_USERNAME, "") ?: ""

    fun getRol(): String = prefs.getString(KEY_ROL, "") ?: ""

    fun isLoggedIn(): Boolean = getToken() != null

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}
