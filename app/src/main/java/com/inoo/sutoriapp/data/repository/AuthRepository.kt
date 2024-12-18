package com.inoo.sutoriapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.inoo.sutoriapp.data.pref.SutoriAppPreferences
import com.inoo.sutoriapp.data.remote.response.auth.LoginResponse
import com.inoo.sutoriapp.data.remote.retrofit.auth.AuthApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class AuthRepository(
    private val apiService: AuthApiService,
    private val sutoriAppPreferences: SutoriAppPreferences
) {
    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.postLogin(email, password)
            if (response.isSuccessful) {
                Log.d("AuthRepository", "Login response: ${response.body()}")
                sutoriAppPreferences.saveToken(
                    token = response.body()?.loginResult?.token ?: "",
                )
                sutoriAppPreferences.saveName(
                    name = response.body()?.loginResult?.name ?: "",
                )
                val token = sutoriAppPreferences.getToken().first()
                val name = sutoriAppPreferences.getName().first()
                if (token.isNotEmpty() && name.isNotEmpty()) {
                    emit(Result.Success(response.body() ?: LoginResponse()))
                } else {
                    emit(Result.Error("Failed to save session"))
                }
            } else {
                emit(Result.Error("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }

    fun getToken(): Flow<Result<String>> = flow {
        emit(Result.Loading)
        val token = sutoriAppPreferences.getToken().first()
        if (token.isNotEmpty()) {
            emit(Result.Success(token))
        } else {
            emit(Result.Error("You're not logged in"))
        }
    }.catch { e ->
        emit(Result.Error(e.message ?: "Unknown error"))
    }

//    fun saveSession(token: String, name: String): LiveData<Result<Boolean>> = liveData {
//        emit(Result.Loading)
//        try {
//            sutoriAppPreferences.saveToken(token = token)
//            sutoriAppPreferences.saveName(name = name)
//            val token = sutoriAppPreferences.getToken().first()
//            val name = sutoriAppPreferences.getName().first()
//            if (token.isNotEmpty() && name.isNotEmpty()) {
//                emit(Result.Success(true))
//            } else {
//                emit(Result.Error("Failed to save session"))
//            }
//        } catch (e: Exception) {
//            emit(Result.Error(e.message ?: "Unknown error"))
//        }
//    }

    fun getName(): Flow<Result<String>> = flow {
        emit(Result.Loading)
        val name = sutoriAppPreferences.getName().first()
        if (name.isNotEmpty()) {
            emit(Result.Success(name))
        } else {
            emit(Result.Error("No name found"))
        }
    }.catch { e ->
        emit(Result.Error(e.message ?: "Unknown error"))
    }

    fun logout(): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)
        sutoriAppPreferences.saveToken(token = "")
        sutoriAppPreferences.saveName(name = "")
        val token = sutoriAppPreferences.getToken().first()
        val name = sutoriAppPreferences.getName().first()
        if (token.isEmpty() && name.isEmpty()) {
            emit(Result.Success(true))
        } else {
            emit(Result.Error("Failed to logout"))
        }
    }.catch { e ->
        emit(Result.Error(e.message ?: "Unknown error"))
    }

}