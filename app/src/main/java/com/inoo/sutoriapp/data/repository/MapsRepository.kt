package com.inoo.sutoriapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.inoo.sutoriapp.data.remote.response.story.ListStoryItem
import com.inoo.sutoriapp.data.remote.retrofit.story.StoryApiService

class MapsRepository (
    private val apiService: StoryApiService
) {
     fun getStoriesWithLocation(location: Int): LiveData<Result<List<ListStoryItem>>> = liveData{
         emit(Result.Loading)
         try {
             val response = apiService.getStoriesWithLocation(location)
             if (response.isSuccessful) {
                 emit(Result.Success(response.body()?.listStory ?: emptyList()))
             } else {
                 emit(Result.Error("Failed to fetch stories: ${response.message()}"))
             }
         } catch (e: Exception) {
             emit(Result.Error(e.message ?: "Unknown error"))
         }
     }
}