package com.inoo.sutoriapp.ui.story.ui.detailstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inoo.sutoriapp.data.remote.response.story.ListStoryItem
import com.inoo.sutoriapp.data.remote.retrofit.story.StoryApiConfig
import kotlinx.coroutines.launch

class DetailStoryViewModel(private val storyId: String) : ViewModel() {
    private val _story = MutableLiveData<ListStoryItem?>()
    val story: LiveData<ListStoryItem?> get() = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchStoryDetail(token: String) {
        if (_story.value != null) return

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = StoryApiConfig.getApiService(token).getDetailStory(storyId)
                if (response.isSuccessful) {
                    _story.value = response.body()?.story
                } else {
                    _error.value = "Failed to fetch stories: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class DetailStoryViewModelFactory(private val storyId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailStoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailStoryViewModel(storyId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}