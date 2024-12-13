package com.example.batani.ui.predikisiCuaca

import android.app.AlertDialog
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.batani.R
import com.example.batani.databinding.ActivityPrediksiCuacaBinding

import com.example.batani.network.WeatherResponse

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PrediksiCuacaActivity : AppCompatActivity() {
    private var progressDialog: AlertDialog? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityPrediksiCuacaBinding
    private lateinit var adapter: PrediksiAdapter

    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPrediksiCuacaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sharedPreferences = getSharedPreferences("WeatherPrefs", MODE_PRIVATE)

        adapter = PrediksiAdapter(arrayListOf())
        binding.backIcon.setOnClickListener {
            finish()
        }
        binding.recycleCuaca.apply {
            layoutManager = LinearLayoutManager(this@PrediksiCuacaActivity)
            adapter = this@PrediksiCuacaActivity.adapter
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.rainsound).apply {
            isLooping = true
            start()
        }
        val savedCityCode = sharedPreferences.getString("Kodecity", null)
        Log.d("kode city", savedCityCode.toString())
        if (savedCityCode !=null) {
            fetchWeatherData(savedCityCode)
        } else {
            showToast("Kode kota tidak ditemukan!")
        }
    }

    private fun fetchWeatherData(kodeWilayah: String) {
        val apiUrl = "https://api.bmkg.go.id/publik/prakiraan-cuaca?adm4=$kodeWilayah"

        showLoadingDialog()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val client = okhttp3.OkHttpClient()
                val request = okhttp3.Request.Builder().url(apiUrl).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val gson = Gson()
                        val weatherResponse = gson.fromJson(responseBody, WeatherResponse::class.java)

                        val weatherDetails = weatherResponse.data.firstOrNull()?.cuaca?.flatten() ?: listOf()


                        val filteredDetails = weatherDetails.filterIndexed { index, _ -> index >= 6 }

                        withContext(Dispatchers.Main) {
                            adapter.updateData(filteredDetails)
                            hideLoadingDialog()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showToast("Gagal mendapatkan data cuaca!")
                        hideLoadingDialog()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Terjadi kesalahan!")
                    hideLoadingDialog()
                }
            }
        }
    }


    private fun showLoadingDialog() {

        if (progressDialog == null) {
            val builder = AlertDialog.Builder(this)
            builder.setView(R.layout.layout_loading)
            builder.setCancelable(false)
            progressDialog = builder.create()
            progressDialog?.show()

        }
    }

    private fun hideLoadingDialog() {

        progressDialog?.dismiss()
        progressDialog = null
    }
    private fun showToast(message: String) {

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        hideLoadingDialog()

    }
    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer?.release()
        mediaPlayer = null
    }
}
