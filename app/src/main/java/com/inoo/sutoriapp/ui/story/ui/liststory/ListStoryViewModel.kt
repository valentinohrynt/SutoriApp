package com.inoo.sutoriapp.ui.story.ui.liststory

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.inoo.sutoriapp.data.di.Injection
import com.inoo.sutoriapp.data.local.StoryRepository
import com.inoo.sutoriapp.data.remote.response.story.ListStoryItem
import com.inoo.sutoriapp.data.remote.retrofit.story.StoryApiConfig
import kotlinx.coroutines.launch

class ListStoryViewModel(storyRepository: StoryRepository) : ViewModel() {
    val listStory : LiveData<PagingData<ListStoryItem>> = storyRepository.getStory().cachedIn(viewModelScope)
}

class ViewModelFactory(private val context: Context, var token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListStoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListStoryViewModel(Injection.provideRepository(context, token)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}