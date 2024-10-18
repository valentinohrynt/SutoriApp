package com.inoo.sutoriapp.data.remote.retrofit.story

import com.inoo.sutoriapp.data.remote.response.story.AddStoryResponse
import com.inoo.sutoriapp.data.remote.response.story.DetailStoryResponse
import com.inoo.sutoriapp.data.remote.response.story.GetAllStoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface StoryApiService {
    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ) : Response<GetAllStoriesResponse>

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location: Int
    ) : Response<GetAllStoriesResponse>

    @Multipart
    @POST("stories")
    suspend fun postAddStory(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") long: RequestBody? = null
    ) : Response<AddStoryResponse>

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Path("id") id: String
    ) : Response<DetailStoryResponse>
}