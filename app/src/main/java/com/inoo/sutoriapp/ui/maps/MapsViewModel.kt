package com.inoo.sutoriapp.ui.maps

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.inoo.sutoriapp.data.di.Injection
import com.inoo.sutoriapp.data.remote.response.story.ListStoryItem
import com.inoo.sutoriapp.data.repository.MapsRepository
import com.inoo.sutoriapp.data.repository.Result

class MapsViewModel(
    private val mapsRepository: MapsRepository
) : ViewModel() {
    private val _storyList = MutableLiveData<List<ListStoryItem>?>()
    val storyList: LiveData<List<ListStoryItem>?> get() = _storyList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchStoriesWithLocation(location: Int) {
        mapsRepository.getStoriesWithLocation(location).observeForever { result ->
            _isLoading.value = true
            when (result) {
                is Result.Success -> {
                    _storyList.value = result.data
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

class MapsViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapsViewModel(Injection.provideMapsRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}