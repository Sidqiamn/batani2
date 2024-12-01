package com.example.batani.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LokasiViewModel(private val repository: LokasiRepository) : ViewModel() {
    fun getCityCode(cityName: String, callback: (String?) -> Unit) {
        viewModelScope.launch {
            val cityCode = withContext(Dispatchers.IO) {
                repository.getCityCode(cityName) { callback(it) }
            }
        }
    }
}
