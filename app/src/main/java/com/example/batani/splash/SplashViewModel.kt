package com.example.batani.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.batani.QuoteRepository
import com.example.batani.pref.UserModel

class SplashViewModel (private val repository: QuoteRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

}