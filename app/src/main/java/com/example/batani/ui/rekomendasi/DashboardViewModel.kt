package com.example.batani.ui.rekomendasi

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn

import com.example.batani.QuoteRepository
import com.example.batani.auth.LoginViewModel
import com.example.batani.di.Injection
import com.example.batani.network.QuoteResponseItem
import com.example.batani.splash.SplashViewModel
import com.example.batani.ui.settingss.SettingsViewModel

class DashboardViewModel(quoteRepository: QuoteRepository) : ViewModel() {

    val quote: LiveData<PagingData<QuoteResponseItem>> =
        quoteRepository.getQuote().cachedIn(viewModelScope)

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
                DashboardViewModel(Injection.provideRepository(applicationContext)) as T
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

