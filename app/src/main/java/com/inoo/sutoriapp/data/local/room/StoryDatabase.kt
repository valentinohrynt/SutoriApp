package com.inoo.sutoriapp.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import com.inoo.sutoriapp.data.local.remotemediator.RemoteKeys
import com.inoo.sutoriapp.data.local.remotemediator.RemoteKeysDao
import com.inoo.sutoriapp.data.remote.response.story.ListStoryItem


@Database(
    entities = [ListStoryItem::class, RemoteKeys::class],
    version = 2,
    exportSchema = false
)

abstract class StoryDatabase : androidx.room.RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        fun getInstance(context: Context): StoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java,
                    "story_database"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}