package com.biblioteca.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biblioteca.app.api.RetrofitClient
import com.biblioteca.app.model.AuthRequest
import com.biblioteca.app.model.AuthResponse
import com.biblioteca.app.utils.Resource
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<Resource<AuthResponse>>()
    val loginResult: LiveData<Resource<AuthResponse>> = _loginResult

    fun login(username: String, password: String) {
        _loginResult.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.login(AuthRequest(username, password))
                if (response.isSuccessful && response.body() != null) {
                    _loginResult.value = Resource.Success(response.body()!!)
                } else {
                    _loginResult.value = Resource.Error("Usuario o contraseña incorrectos")
                }
            } catch (e: Exception) {
                _loginResult.value = Resource.Error("Error de conexión: ${e.message}")
            }
        }
    }
}
