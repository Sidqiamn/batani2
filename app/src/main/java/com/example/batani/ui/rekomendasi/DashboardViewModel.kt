package com.example.batani.ui.rekomendasi

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.example.batani.QuoteRepository
import com.example.batani.auth.LoginViewModel
import com.example.batani.di.Injection
import com.example.batani.network.ApiConfig
import com.example.batani.network.RekomendasiResponse
import com.example.batani.splash.SplashViewModel
import com.example.batani.ui.settingss.SettingsViewModel
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val _rekomendasiResponse = MutableLiveData<RekomendasiResponse>()
    val rekomendasiResponse: LiveData<RekomendasiResponse> get() = _rekomendasiResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val apiService = ApiConfig.getApiRekomendasi()

    // Fungsi untuk mengambil data rekomendasi tanaman
    fun getRekomendasiTanaman(temperature: Int, humidity: Int, rainfall: Int) {
        viewModelScope.launch {
            _isLoading.value = true // Progress bar mulai tampil
            try {
                val response = apiService.getRekomendasiTanaman(temperature, humidity, rainfall)
                _rekomendasiResponse.value = response
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error fetching recommendations: ${e.message}")
            } finally {
                _isLoading.value = false // Progress bar berhenti
            }
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

