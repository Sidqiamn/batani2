package com.example.batani.ui.settingss

import androidx.lifecycle.ViewModel
import com.example.batani.QuoteRepository
import com.example.batani.pref.UserModel
import kotlinx.coroutines.flow.Flow

class SettingsViewModel(private val repository: QuoteRepository) : ViewModel() {

    suspend fun logout(): Boolean {
        return repository.logout()
    }

    fun getSession(): Flow<UserModel> {
        return repository.getSession()
    }
}
