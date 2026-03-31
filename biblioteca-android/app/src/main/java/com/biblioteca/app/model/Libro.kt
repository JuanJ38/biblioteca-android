package com.biblioteca.app.model

import com.google.gson.annotations.SerializedName

data class Libro(
    val id: Int = 0,
    val titulo: String = "",
    val autor: String = "",
    val disponible: Boolean = true
)
