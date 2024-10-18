package com.inoo.sutoriapp.data.local.remotemediator

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys (
    @PrimaryKey val storyId: String,
    var prevKey: Int? = null,
    var nextKey: Int? = null
)