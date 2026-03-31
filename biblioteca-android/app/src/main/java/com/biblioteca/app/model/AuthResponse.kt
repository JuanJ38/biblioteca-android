package com.biblioteca.app.model

data class AuthResponse(
    val token: String,
    val username: String,
    val rol: String
)
