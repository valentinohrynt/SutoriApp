package com.inoo.sutoriapp.ui.story.ui.detailstory

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.inoo.sutoriapp.data.di.Injection
import com.inoo.sutoriapp.data.remote.response.story.ListStoryItem
import com.inoo.sutoriapp.data.repository.StoryRepository
import com.inoo.sutoriapp.data.repository.Result

class DetailStoryViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    private val _story = MutableLiveData<ListStoryItem?>()
    val story: LiveData<ListStoryItem?> get() = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchStoryDetail(storyId: String) {
        storyRepository.getStoryDetail(storyId).observeForever { result ->
            _isLoading.value = true
            when (result) {
                is Result.Success -> {
                    _story.value = result.data
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
}

class DetailStoryViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailStoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailStoryViewModel(Injection.provideStoryRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}