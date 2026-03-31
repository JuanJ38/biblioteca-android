package com.biblioteca.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biblioteca.app.api.RetrofitClient
import com.biblioteca.app.model.Libro
import com.biblioteca.app.model.Prestamo
import com.biblioteca.app.model.Usuario
import com.biblioteca.app.utils.Resource
import kotlinx.coroutines.launch

class PrestamoViewModel : ViewModel() {

    private val _prestamos = MutableLiveData<Resource<List<Prestamo>>>()
    val prestamos: LiveData<Resource<List<Prestamo>>> = _prestamos

    private val _librosDisponibles = MutableLiveData<List<Libro>>()
    val librosDisponibles: LiveData<List<Libro>> = _librosDisponibles

    private val _usuarios = MutableLiveData<List<Usuario>>()
    val usuarios: LiveData<List<Usuario>> = _usuarios

    private val _operacion = MutableLiveData<Resource<String>>()
    val operacion: LiveData<Resource<String>> = _operacion

    fun cargarPrestamos(token: String) {
        _prestamos.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.listarPrestamos(token)
                if (response.isSuccessful) {
                    _prestamos.value = Resource.Success(response.body() ?: emptyList())
                } else {
                    _prestamos.value = Resource.Error("Error al cargar préstamos")
                }
            } catch (e: Exception) {
                _prestamos.value = Resource.Error("Error de conexión")
            }
        }
    }

    fun cargarLibrosYUsuarios(token: String) {
        viewModelScope.launch {
            try {
                val librosResp = RetrofitClient.instance.listarLibros(token)
                val usuariosResp = RetrofitClient.instance.listarUsuarios(token)
                if (librosResp.isSuccessful) {
                    _librosDisponibles.value = librosResp.body()?.filter { it.disponible } ?: emptyList()
                }
                if (usuariosResp.isSuccessful) {
                    _usuarios.value = usuariosResp.body() ?: emptyList()
                }
            } catch (e: Exception) {
                _operacion.value = Resource.Error("Error de conexión")
            }
        }
    }

    fun registrarPrestamo(token: String, idLibro: Int, idUsuario: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.registrarPrestamo(token, idLibro, idUsuario)
                if (response.isSuccessful) {
                    _operacion.value = Resource.Success("Préstamo registrado")
                    cargarPrestamos(token)
                } else {
                    _operacion.value = Resource.Error("Libro no disponible")
                }
            } catch (e: Exception) {
                _operacion.value = Resource.Error("Error de conexión")
            }
        }
    }

    fun devolverLibro(token: String, id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.devolverLibro(token, id)
                if (response.isSuccessful) {
                    _operacion.value = Resource.Success("Libro devuelto")
                    cargarPrestamos(token)
                } else {
                    _operacion.value = Resource.Error("Error al devolver")
                }
            } catch (e: Exception) {
                _operacion.value = Resource.Error("Error de conexión")
            }
        }
    }
}
