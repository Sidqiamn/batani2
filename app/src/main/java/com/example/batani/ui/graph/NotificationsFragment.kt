package com.example.batani.ui.graph

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.batani.R
import com.example.batani.databinding.FragmentNotificationsBinding
import com.example.batani.network.ForecastApiService
import com.example.batani.network.ForecastResponse
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private var progressDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences("TanamanPrefs", Context.MODE_PRIVATE)
        val listoftanaman = mutableListOf<String>()

        for (i in 0 until 4) {
            val tanaman = sharedPreferences.getString("tanaman_${i}", null)
            tanaman?.let { listoftanaman.add(it) }
        }

        if (listoftanaman.isNotEmpty()) {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS) // 30 detik
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://af-856805603330.asia-southeast2.run.app/")
                .client(okHttpClient) // Gunakan OkHttpClient yang sudah dikonfigurasi
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ForecastApiService::class.java)

            // Tampilkan loading sebelum mulai API call
            showLoadingDialog()

            apiService.getForecast(listoftanaman, 10)
                .enqueue(object : Callback<List<ForecastResponse>> {
                    override fun onResponse(
                        call: Call<List<ForecastResponse>>,
                        response: Response<List<ForecastResponse>>
                    ) {
                        // Sembunyikan loading setelah mendapat respons
                        hideLoadingDialog()

                        if (response.isSuccessful) {
                            val jsonResponse = response.body()
                            val plots = mutableListOf<JSONObject>()
                            val filteredResponse = jsonResponse?.filter { forecast ->
                                !forecast.tanaman.isNullOrEmpty() && !forecast.plot.isNullOrEmpty()
                            }
                            if (jsonResponse.isNullOrEmpty()) {
                                Toast.makeText(
                                    requireContext(),
                                    "Data tidak tersedia",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return
                            }

                            jsonResponse.forEach { forecast ->
                                try {
                                    if (!forecast.plot.isNullOrEmpty()) {
                                        val plotObject = JSONObject(forecast.plot)
                                        plots.add(plotObject)
                                    } else {
                                        println("Plot is null or empty for forecast: $forecast")
                                    }
                                } catch (e: Exception) {
                                    println("Error parsing plot: ${e.message}")
                                }
                            }

                            if (plots.isEmpty()) {
                                Toast.makeText(
                                    requireContext(),
                                    "Prediksi harga tanaman tersebut tidak tersedia",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return
                            }

                            binding.recyclerView.layoutManager =
                                LinearLayoutManager(requireContext())
                            val adapter = ChartAdapter(requireContext(), jsonResponse)
                            binding.recyclerView.adapter = adapter
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<List<ForecastResponse>>, t: Throwable) {

                        hideLoadingDialog()

                        if (t is java.net.SocketTimeoutException) {

                            Toast.makeText(requireContext(), "Server error: Waktu habis", Toast.LENGTH_SHORT).show()
                        } else {

                            Toast.makeText(requireContext(), "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        } else {
            Toast.makeText(
                requireContext(),
                "Tidak ada tanaman untuk diprediksi",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoadingDialog() {
        if (progressDialog == null) {
            val builder = AlertDialog.Builder(requireContext())
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
}
