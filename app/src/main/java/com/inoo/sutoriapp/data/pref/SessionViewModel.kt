package com.inoo.sutoriapp.data.pref

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SessionViewModel(private val pref: SutoriAppPreferences) : ViewModel() {
    fun saveSession(token: String, name: String) {
        viewModelScope.launch {
            pref.saveToken(token, name)
        }
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun getName(): LiveData<String> {
        return pref.getName().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.invalidateToken()
            pref.invalidateName()
        }
    }
}
