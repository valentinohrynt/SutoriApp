package com.inoo.sutoriapp.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inoo.sutoriapp.data.remote.response.auth.RegisterResponse
import com.inoo.sutoriapp.data.remote.retrofit.auth.AuthApiConfig
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> get() = _registerResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: MutableLiveData<String?> get() = _error

    fun handleRegisterResult(name: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = AuthApiConfig.getApiService().postRegister(name, email, password)
                _isLoading.value = false
                if (response.isSuccessful) {
                    _registerResponse.value = response.body()
                } else {
                    if (response.code() == 400) {
                        _error.value = "emailTaken"
                    } else {
                        _error.value = "Register failed: ${response.message()}"
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
