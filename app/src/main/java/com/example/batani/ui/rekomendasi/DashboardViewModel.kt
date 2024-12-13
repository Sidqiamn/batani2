package com.example.batani.ui.rekomendasi

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.example.batani.auth.LoginViewModel
import com.example.batani.di.Injection
import com.example.batani.network.ApiConfig

import com.example.batani.network.RekomendasiWithInfoResponseItem
import com.example.batani.splash.SplashViewModel
import com.example.batani.ui.settingss.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardViewModel : ViewModel() {

    private val _rekomendasiResponse = MutableLiveData<List<RekomendasiWithInfoResponseItem>>()
    val rekomendasiResponse: LiveData<List<RekomendasiWithInfoResponseItem>> get() = _rekomendasiResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val apiService = ApiConfig.getApiRekomendasi()

    fun getRekomendasiTanaman(temperature: Int, humidity: Int, rainfall: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            var attempt = 0
            val maxRetries = 5
            var success = false

            while (attempt < maxRetries && !success) {
                try {
                    attempt++

                    val response = withContext(Dispatchers.IO) {
                        apiService.getRekomendasiTanaman(temperature, humidity, rainfall)
                    }
                    _rekomendasiResponse.value = response
                    success = true
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Failed to fetch data: ${e.message}")
                    if (attempt == maxRetries) {
                        _errorMessage.value = "Failed to fetch data: ${e.message}"
                    }
                }
            }

            _isLoading.value = false
        }
    }

}


class ViewModelFactory(private val applicationContext: Context) : ViewModelProvider.Factory {

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            val appContext = context.applicationContext  // Use application context
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(appContext).also { instance = it }
            }
        }
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                DashboardViewModel() as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                LoginViewModel(Injection.provideRepository(applicationContext)) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                SettingsViewModel(Injection.provideRepository(applicationContext)) as T
            }
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                SplashViewModel(Injection.provideRepository(applicationContext)) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

