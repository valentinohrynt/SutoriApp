package com.inoo.sutoriapp.data.remote.retrofit.auth

import com.inoo.sutoriapp.data.remote.response.auth.LoginResponse
import com.inoo.sutoriapp.data.remote.response.auth.RegisterResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun postRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : Response<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun postLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>
}