package com.inoo.sutoriapp.data.pref

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class SessionViewModelFactory(
    private val pref: SutoriAppPreferences
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
            return SessionViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: SessionViewModelFactory? = null
        fun getInstance(context: Context, pref: SutoriAppPreferences): SessionViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: SessionViewModelFactory(pref)
            }.also { instance = it }
    }
}