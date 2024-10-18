package com.inoo.sutoriapp.ui.story.ui.addstory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inoo.sutoriapp.data.remote.response.story.AddStoryResponse
import com.inoo.sutoriapp.data.remote.retrofit.story.StoryApiConfig
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel : ViewModel() {
    private val _uploadResponse = MutableLiveData<AddStoryResponse?>()
    val uploadResponse: MutableLiveData<AddStoryResponse?> get() = _uploadResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: MutableLiveData<String?> get() = _error

    private lateinit var headerAuthToken: String

    fun postAddStory(token: String, description: RequestBody, photo: MultipartBody.Part, lat: RequestBody, long: RequestBody) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = StoryApiConfig.getApiService(token).postAddStory(description, photo, lat, long)
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