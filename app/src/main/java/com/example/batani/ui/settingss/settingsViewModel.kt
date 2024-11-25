package com.example.batani.ui.settingss

import androidx.lifecycle.ViewModel
import com.example.batani.QuoteRepository

class SettingsViewModel(private val repository: QuoteRepository) : ViewModel() {


    suspend fun logout(): Boolean {
        return repository.logout()
    }
}
