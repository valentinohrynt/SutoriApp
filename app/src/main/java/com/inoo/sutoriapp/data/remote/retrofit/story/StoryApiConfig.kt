package com.inoo.sutoriapp.data.remote.retrofit.story

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.inoo.sutoriapp.BuildConfig
import okhttp3.Interceptor
import retrofit2.converter.gson.GsonConverterFactory

class StoryApiConfig {
    companion object {
        private const val BASE_URL: String = BuildConfig.BASE_URL
        fun getApiService(token: String): StoryApiService {
            val authInterceptor = Interceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(newRequest)
            }
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(StoryApiService::class.java)
        }
    }
}