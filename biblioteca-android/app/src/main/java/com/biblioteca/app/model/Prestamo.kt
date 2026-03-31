package com.biblioteca.app.model

data class Prestamo(
    val id: Int = 0,
    val tituloLibro: String = "",
    val nombreUsuario: String = "",
    val fechaPrestamo: String = "",
    val fechaDevolucion: String? = null
)
