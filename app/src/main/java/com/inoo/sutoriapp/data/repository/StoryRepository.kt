package com.inoo.sutoriapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import androidx.lifecycle.liveData
import com.inoo.sutoriapp.data.local.remotemediator.StoryRemoteMediator
import com.inoo.sutoriapp.data.local.room.StoryDatabase
import com.inoo.sutoriapp.data.remote.response.story.AddStoryResponse
import com.inoo.sutoriapp.data.remote.response.story.ListStoryItem
import com.inoo.sutoriapp.data.remote.retrofit.story.StoryApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: StoryApiService) {
    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize=10,
                enablePlaceholders = false
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = { storyDatabase.storyDao().getStories() }
        ).liveData
    }

    fun getStoryDetail(storyId: String): LiveData<Result<ListStoryItem>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailStory(storyId)
            if (response.isSuccessful) {
                emit(Result.Success(response.body()?.story ?: ListStoryItem()))
            } else {
                emit(Result.Error("Failed to fetch stories: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }

    fun postStory(description: RequestBody, photo: MultipartBody.Part, lat: RequestBody, long: RequestBody): LiveData<Result<AddStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.postAddStory(description, photo, lat, long)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: AddStoryResponse()))
            } else {
                emit(Result.Error("Failed to upload story: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }
}