package com.inoo.sutoriapp.ui.story.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inoo.sutoriapp.data.remote.response.story.AddStoryResponse
import com.inoo.sutoriapp.data.remote.response.story.ListStoryItem
import com.inoo.sutoriapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel : ViewModel() {
    private val _storyList = MutableLiveData<List<ListStoryItem>?>()
    val storyList: MutableLiveData<List<ListStoryItem>?> get() = _storyList

    private val _story = MutableLiveData<ListStoryItem?>()
    val story: MutableLiveData<ListStoryItem?> get() = _story

    private val _uploadResponse = MutableLiveData<AddStoryResponse?>()
    val uploadResponse: MutableLiveData<AddStoryResponse?> get() = _uploadResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: MutableLiveData<String?> get() = _error

    private lateinit var headerAuthToken: String

    fun fetchStories(token: String, page: Int, size: Int, location: Int) {
        _isLoading.value = true

        headerAuthToken = "Bearer $token"

        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getStories(headerAuthToken, page, size, location)
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

    fun fetchStoryDetail(token: String, storyId: String){
        _isLoading.value = true

        headerAuthToken = "Bearer $token"

        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getDetailStory(headerAuthToken, storyId)
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _story.value = response.body()?.story
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

    fun postAddStory(token: String, description: RequestBody, photo: MultipartBody.Part) {
        _isLoading.value = true
        headerAuthToken = "Bearer $token"
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().postAddStory(headerAuthToken, description, photo)
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _uploadResponse.value = response.body()
                } else {
                    _error.value = "Failed to upload story: ${response.message()}"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}