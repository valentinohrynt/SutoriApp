package com.inoo.sutoriapp.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.inoo.sutoriapp.data.remote.response.story.ListStoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stories: List<ListStoryItem>)

    @Query("SELECT * FROM story ORDER BY createdAt DESC")
    fun getStories(): PagingSource<Int, ListStoryItem>

    @Query("DELETE FROM story")
    suspend fun clearAll()
}