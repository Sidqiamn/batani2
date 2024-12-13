package com.example.batani.di

import android.content.Context
import com.example.batani.QuoteRepository
import com.example.batani.network.ApiConfig
import com.example.batani.pref.UserPreference
import com.example.batani.pref.dataStore

object Injection {
    fun provideRepository(context: Context): QuoteRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return QuoteRepository(apiService, pref)
    }
}