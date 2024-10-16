package com.inoo.sutoriapp.ui.story.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.inoo.sutoriapp.data.pref.SutoriAppPreferences
import kotlinx.coroutines.launch

class SettingsViewModel(private val pref: SutoriAppPreferences) : ViewModel() {

    fun getLangSettings(): LiveData<String> {
        return pref.getLanguageSetting().asLiveData()
    }

    fun saveLangSettings(language: String) {
        viewModelScope.launch {
            pref.saveLanguageSetting(language)
        }
    }
}
