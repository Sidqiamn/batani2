package com.example.batani

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.batani.network.ApiService
import com.example.batani.network.QuoteResponseItem
import com.example.batani.pref.UserModel
import com.example.batani.pref.UserPreference
import kotlinx.coroutines.flow.Flow

class QuoteRepository(private val apiService: ApiService, private val userPreference: UserPreference) {

    fun getQuote(): LiveData<PagingData<QuoteResponseItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                pagingSource(apiService)
            }
        ).liveData
    }

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
