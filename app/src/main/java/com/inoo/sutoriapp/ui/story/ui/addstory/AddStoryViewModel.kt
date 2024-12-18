package com.inoo.sutoriapp.ui.story.ui.addstory

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.inoo.sutoriapp.data.di.Injection
import com.inoo.sutoriapp.data.remote.response.story.AddStoryResponse
import com.inoo.sutoriapp.data.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.inoo.sutoriapp.data.repository.Result

class AddStoryViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    private val _uploadResponse = MutableLiveData<AddStoryResponse?>()
    val uploadResponse: MutableLiveData<AddStoryResponse?> get() = _uploadResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: MutableLiveData<String?> get() = _error

    fun postAddStory(
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody,
        long: RequestBody
    ) {
        storyRepository.postStory(description, photo, lat, long).observeForever { result ->
            _isLoading.value = true
            when (result) {
                is Result.Success -> {
                    _uploadResponse.value = result.data
                    _isLoading.value = false
                }

                is Result.Error -> {
                    _error.value = result.error
                    _isLoading.value = false
                }

                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}

class AddStoryViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddStoryViewModel(Injection.provideStoryRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}