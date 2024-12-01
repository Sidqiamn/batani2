package com.example.batani.database

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LokasiRepository(application: Application) {
    private val mLokasiDao: LokasiDao

    init {
        val db = AppDatabase.getDatabase(application)
        mLokasiDao = db.lokasiDao()
    }

    fun getCityCode(cityName: String, callback: (String?) -> Unit) {
        // Menggunakan coroutine untuk menjalankan kode di background
        CoroutineScope(Dispatchers.IO).launch {
            val cityCode = mLokasiDao.getCityCode(cityName)
            withContext(Dispatchers.Main) {
                callback(cityCode)
            }
        }
    }
}
