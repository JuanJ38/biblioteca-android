package com.biblioteca.app.api

import com.biblioteca.app.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    // Libros
    @GET("api/libros")
    suspend fun listarLibros(@Header("Authorization") token: String): Response<List<Libro>>

    @POST("api/libros")
    suspend fun crearLibro(
        @Header("Authorization") token: String,
        @Body libro: Libro
    ): Response<Libro>

    @PUT("api/libros/{id}")
    suspend fun actualizarLibro(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body libro: Libro
    ): Response<Libro>

    @DELETE("api/libros/{id}")
    suspend fun eliminarLibro(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Void>

    // Usuarios
    @GET("api/usuarios")
    suspend fun listarUsuarios(@Header("Authorization") token: String): Response<List<Usuario>>

    @POST("api/usuarios")
    suspend fun crearUsuario(
        @Header("Authorization") token: String,
        @Body usuario: Usuario
    ): Response<Usuario>

    @DELETE("api/usuarios/{id}")
    suspend fun eliminarUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Void>

    // Prestamos
    @GET("api/prestamos")
    suspend fun listarPrestamos(@Header("Authorization") token: String): Response<List<Prestamo>>

    @POST("api/prestamos")
    suspend fun registrarPrestamo(
        @Header("Authorization") token: String,
        @Query("idLibro") idLibro: Int,
        @Query("idUsuario") idUsuario: Int
    ): Response<String>

    @PUT("api/prestamos/devolver/{id}")
    suspend fun devolverLibro(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<String>
}
