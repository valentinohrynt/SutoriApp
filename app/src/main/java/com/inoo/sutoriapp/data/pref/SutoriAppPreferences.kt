package com.inoo.sutoriapp.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.Locale

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "SutoriAppPreferences")

class SutoriAppPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val languageKey = stringPreferencesKey("language_setting")
    private val tokenKey = stringPreferencesKey("token")
    private val nameKey = stringPreferencesKey("name")

    fun getLanguageSetting(): Flow<String> {
        return dataStore.data.map { preferences ->
            val lang = preferences[languageKey] ?: "en"
            lang
        }
    }

    suspend fun saveLanguageSetting(language: String) {
        dataStore.edit { preferences ->
            preferences[languageKey] = language
        }
    }

    fun getLangSettingsSync(): String {
        return runBlocking {
            dataStore.data.first()[languageKey] ?: Locale.getDefault().language
        }
    }

    suspend fun saveToken(token: String, name: String) {
        dataStore.edit { preferences ->
            preferences[tokenKey] = token
            preferences[nameKey] = name
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            val token = preferences[tokenKey] ?: ""
            token
        }
    }

    fun getName(): Flow<String> {
        return dataStore.data.map { preferences ->
            val name = preferences[nameKey] ?: ""
            name
        }
    }

    suspend fun invalidateToken () {
        dataStore.edit { preferences ->
            preferences.remove(tokenKey)
        }
    }

    suspend fun invalidateName () {
        dataStore.edit { preferences ->
            preferences.remove(nameKey)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SutoriAppPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SutoriAppPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SutoriAppPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}