package com.biblioteca.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biblioteca.app.api.RetrofitClient
import com.biblioteca.app.model.Libro
import com.biblioteca.app.utils.Resource
import kotlinx.coroutines.launch

class LibroViewModel : ViewModel() {

    private val _libros = MutableLiveData<Resource<List<Libro>>>()
    val libros: LiveData<Resource<List<Libro>>> = _libros

    private val _operacion = MutableLiveData<Resource<String>>()
    val operacion: LiveData<Resource<String>> = _operacion

    fun cargarLibros(token: String) {
        _libros.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.listarLibros(token)
                if (response.isSuccessful) {
                    _libros.value = Resource.Success(response.body() ?: emptyList())
                } else {
                    val mensaje = when (response.code()) {
                        401 -> "Sesión expirada, vuelve a iniciar sesión"
                        403 -> "No tienes permiso para ver los libros"
                        404 -> "Recurso no encontrado"
                        500 -> "Error en el servidor, intenta más tarde"
                        else -> "Error ${response.code()} al cargar libros"
                    }
                    _libros.value = Resource.Error(mensaje)
                }
            } catch (e: Exception) {
                _libros.value = Resource.Error("Error de conexión")
            }
        }
    }

    fun crearLibro(token: String, libro: Libro) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.crearLibro(token, libro)
                if (response.isSuccessful) {
                    _operacion.value = Resource.Success("Libro creado correctamente")
                    cargarLibros(token)
                } else {
                    _operacion.value = Resource.Error("Error al crear libro")
                }
            } catch (e: Exception) {
                _operacion.value = Resource.Error("Error de conexión")
            }
        }
    }

    fun eliminarLibro(token: String, id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.eliminarLibro(token, id)
                if (response.isSuccessful) {
                    _operacion.value = Resource.Success("Libro eliminado")
                    cargarLibros(token)
                } else {
                    _operacion.value = Resource.Error("Error al eliminar")
                }
            } catch (e: Exception) {
                _operacion.value = Resource.Error("Error de conexión")
            }
        }
    }
}
