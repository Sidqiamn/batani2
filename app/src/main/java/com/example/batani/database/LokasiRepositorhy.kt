package com.example.batani.database

import android.app.Application
import android.util.Log
import com.example.batani.network.LokasiResponse
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

    fun insertCity(name: String, code: String) {
        val city = City(name = name, code = code)

        // Jalankan operasi insert di thread latar belakang
        CoroutineScope(Dispatchers.IO).launch {
            try {
                mLokasiDao.insertCity(city)
            } catch (e: Exception) {
                // Tambahkan logging atau error handling sesuai kebutuhan
                e.printStackTrace()
            }
        }
    }

    fun getCityCode(cityName: String, callback: (String?) -> Unit) {
        // Menggunakan coroutine untuk menjalankan kode di background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cityCode = mLokasiDao.getCityCode(cityName)
                withContext(Dispatchers.Main) {
                    callback(cityCode)
                }
            } catch (e: Exception) {
                // Tambahkan logging atau error handling sesuai kebutuhan
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    callback(null) // Kembalikan nilai null jika terjadi kesalahan
                }
            }
        }
    }


}
