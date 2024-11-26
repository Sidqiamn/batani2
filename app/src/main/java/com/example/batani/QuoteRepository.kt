package com.example.batani

import android.util.Log
import com.example.batani.network.ApiService
import com.example.batani.network.ApiServiceTanamanApi

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
            // Menjalankan proses logout dan menghapus sesi
            userPreference.logout()
            // Mengembalikan true jika logout berhasil
            true
        } catch (e: Exception) {
            // Log error jika terjadi kesalahan
            Log.e("QuoteRepository", "Logout failed", e)
            // Mengembalikan false jika terjadi kesalahan
            false
        }
    }
}
