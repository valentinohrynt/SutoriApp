package com.inoo.sutoriapp.data.di

import android.content.Context
import com.inoo.sutoriapp.data.local.StoryRepository
import com.inoo.sutoriapp.data.local.room.StoryDatabase
import com.inoo.sutoriapp.data.remote.retrofit.story.StoryApiConfig

object Injection {
    fun provideRepository(context: Context, token: String): StoryRepository {
        val apiService = StoryApiConfig.getApiService(token)
        val database = StoryDatabase.getInstance(context)
        return StoryRepository(database, apiService)
    }
}