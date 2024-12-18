package com.inoo.sutoriapp.ui.auth.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inoo.sutoriapp.data.di.Injection
import com.inoo.sutoriapp.data.remote.response.auth.LoginResponse
import com.inoo.sutoriapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.inoo.sutoriapp.data.repository.Result

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> get() = _loginResponse

    private val _token = MutableStateFlow<String>("")
    val token: StateFlow<String> = _token

    private val _name = MutableStateFlow<String>("")
    val name: StateFlow<String> = _name

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: MutableLiveData<String?> get() = _error

    init {
        getToken()
        getName()
    }

    fun handleLoginResult(email: String, password: String) {
        authRepository.login(email, password).observeForever { result ->
            _isLoading.value = true
            when (result) {
                is Result.Success -> {
                    _loginResponse.value = result.data
                    _isLoading.value = false
                }

                is Result.Error -> {
                    _error.value = result.error
                    _isLoading.value = false
                }

                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    fun getToken() {
        viewModelScope.launch {
            authRepository.getToken().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _token.value = result.data
                        _isLoading.value = false
                    }

                    is Result.Error -> {
                        _error.value = result.error
                        _isLoading.value = false
                    }

                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }

    fun getName() {
        viewModelScope.launch {
            authRepository.getName().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _isLoading.value = false
                        _name.value = result.data
                    }

                    is Result.Error -> {
                        _isLoading.value = false
                        _error.value = result.error
                    }

                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }

//    fun saveSession(token: String, name: String) {
//        viewModelScope.launch {
//            authRepository.saveSession(token, name).observeForever { result ->
//                when (result) {
//                    is Result.Success -> {
//                        _isLoading.value = false
//                    }
//
//                    is Result.Error -> {
//                        _isLoading.value = false
//                    }
//
//                    is Result.Loading -> {
//                        _isLoading.value = true
//                    }
//                }
//            }
//        }
//    }

    fun clearError() {
        _error.value = null
    }
}

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(Injection.provideAuthRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}