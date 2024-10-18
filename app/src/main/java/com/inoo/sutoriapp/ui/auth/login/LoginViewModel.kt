package com.inoo.sutoriapp.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inoo.sutoriapp.data.remote.response.auth.LoginResponse
import com.inoo.sutoriapp.data.remote.retrofit.auth.AuthApiConfig
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> get() = _loginResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: MutableLiveData<String?> get() = _error

    fun handleLoginResult(email: String, password: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = AuthApiConfig.getApiService().postLogin(email, password)
                _isLoading.value = false
                if (response.isSuccessful) {
                    _loginResponse.value = response.body()
                } else {
                    if (response.code() == 401) {
                        _error.value = "Unauthorized"
                    } else {
                        _error.value = "Login failed: ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}