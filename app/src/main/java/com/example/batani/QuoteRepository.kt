package com.example.batani

import android.util.Log
import com.example.batani.network.ApiService

import com.example.batani.pref.UserModel
import com.example.batani.pref.UserPreference
import kotlinx.coroutines.flow.Flow

class QuoteRepository(private val apiService: ApiService, private val userPreference: UserPreference) {





    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout(): Boolean {
        return try {

            userPreference.logout()

            true
        } catch (e: Exception) {

            Log.e("QuoteRepository", "Logout failed", e)

            false
        }
    }
}
