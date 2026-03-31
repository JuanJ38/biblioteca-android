package com.biblioteca.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biblioteca.app.api.RetrofitClient
import com.biblioteca.app.model.Usuario
import com.biblioteca.app.utils.Resource
import kotlinx.coroutines.launch

class UsuarioViewModel : ViewModel() {

    private val _usuarios = MutableLiveData<Resource<List<Usuario>>>()
    val usuarios: LiveData<Resource<List<Usuario>>> = _usuarios

    private val _operacion = MutableLiveData<Resource<String>>()
    val operacion: LiveData<Resource<String>> = _operacion

    fun cargarUsuarios(token: String) {
        _usuarios.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.listarUsuarios(token)
                if (response.isSuccessful) {
                    _usuarios.value = Resource.Success(response.body() ?: emptyList())
                } else {
                    _usuarios.value = Resource.Error("Error al cargar usuarios")
                }
            } catch (e: Exception) {
                _usuarios.value = Resource.Error("Error de conexión")
            }
        }
    }

    fun crearUsuario(token: String, usuario: Usuario) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.crearUsuario(token, usuario)
                if (response.isSuccessful) {
                    _operacion.value = Resource.Success("Usuario creado correctamente")
                    cargarUsuarios(token)
                } else {
                    _operacion.value = Resource.Error("Error al crear usuario")
                }
            } catch (e: Exception) {
                _operacion.value = Resource.Error("Error de conexión")
            }
        }
    }

    fun eliminarUsuario(token: String, id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.eliminarUsuario(token, id)
                if (response.isSuccessful) {
                    _operacion.value = Resource.Success("Usuario eliminado")
                    cargarUsuarios(token)
                } else {
                    _operacion.value = Resource.Error("Error al eliminar")
                }
            } catch (e: Exception) {
                _operacion.value = Resource.Error("Error de conexión")
            }
        }
    }
}
