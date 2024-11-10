package com.inoo.sutoriapp.data.local

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.inoo.sutoriapp.data.local.remotemediator.StoryRemoteMediator
import com.inoo.sutoriapp.data.local.room.StoryDatabase
import com.inoo.sutoriapp.data.remote.response.story.ListStoryItem
import com.inoo.sutoriapp.data.remote.retrofit.story.StoryApiService

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
}