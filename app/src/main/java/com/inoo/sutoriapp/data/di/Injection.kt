package com.inoo.sutoriapp.data.di

import android.content.Context
import com.inoo.sutoriapp.data.repository.StoryRepository
import com.inoo.sutoriapp.data.local.room.StoryDatabase
import com.inoo.sutoriapp.data.pref.SutoriAppPreferences
import com.inoo.sutoriapp.data.pref.dataStore
import com.inoo.sutoriapp.data.remote.retrofit.auth.AuthApiConfig
import com.inoo.sutoriapp.data.remote.retrofit.story.StoryApiConfig
import com.inoo.sutoriapp.data.repository.AuthRepository
import com.inoo.sutoriapp.data.repository.MapsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val sutoriAppPreferences = SutoriAppPreferences.getInstance(context.dataStore)
        val token = runBlocking{
            sutoriAppPreferences.getToken().first()
        }
        val apiService = StoryApiConfig.getApiService(token)
        val database = StoryDatabase.getInstance(context)
        return StoryRepository(database, apiService)
    }

    fun provideMapsRepository(context: Context): MapsRepository {
        val sutoriAppPreferences = SutoriAppPreferences.getInstance(context.dataStore)
        val token = runBlocking{
            sutoriAppPreferences.getToken().first()
        }
        val apiService = StoryApiConfig.getApiService(token)
        return MapsRepository(apiService)
    }

    fun provideAuthRepository(context: Context): AuthRepository {
        val sutoriAppPreferences = SutoriAppPreferences.getInstance(context.dataStore)
        val apiService = AuthApiConfig.getApiService()
        return AuthRepository(apiService, sutoriAppPreferences)
    }
}