package com.inoo.sutoriapp.data.remote.response.story

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class AddStoryResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class GetAllStoriesResponse(

	@field:SerializedName("listStory")
	val listStory: List<ListStoryItem>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class DetailStoryResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("story")
	val story: ListStoryItem = ListStoryItem()
)

@Entity(tableName = "story")
data class ListStoryItem(

	@field:SerializedName("photoUrl")
	val photoUrl: String = "",

	@field:SerializedName("createdAt")
	val createdAt: String = "",

	@field:SerializedName("name")
	val name: String = "Item not found",

	@field:SerializedName("description")
	val description: String = "",

	@field:SerializedName("lon")
	val lon: Double = 0.0,

	@PrimaryKey
	@field:SerializedName("id")
	val id: String = "",

	@field:SerializedName("lat")
	val lat: Double = 0.0
)