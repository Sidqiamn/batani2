package com.example.batani.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.batani.QuoteRepository
import com.example.batani.pref.UserModel
import kotlinx.coroutines.launch

class SplashViewModel (private val repository: QuoteRepository) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

}