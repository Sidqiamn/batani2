package com.example.batani.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.batani.QuoteRepository
import com.example.batani.pref.UserModel

import kotlinx.coroutines.launch

class LoginViewModel(private val repository: QuoteRepository) : ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}