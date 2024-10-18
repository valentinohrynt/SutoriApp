package com.inoo.sutoriapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inoo.sutoriapp.data.remote.response.story.ListStoryItem
import com.inoo.sutoriapp.data.remote.retrofit.story.StoryApiConfig
import kotlinx.coroutines.launch

class MapsViewModel : ViewModel() {
    private val _storyList = MutableLiveData<List<ListStoryItem>?>()
    val storyList: LiveData<List<ListStoryItem>?> get() = _storyList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchStoriesWithLocation(token: String, location: Int) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = StoryApiConfig.getApiService(token).getStoriesWithLocation(location)
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _storyList.value = response.body()?.listStory
                }
                else {
                    _error.value = "Failed to fetch stories: ${response.message()}"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message
            }
        }

    }


}