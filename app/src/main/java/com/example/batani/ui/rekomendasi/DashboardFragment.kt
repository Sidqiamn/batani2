package com.example.batani.ui.rekomendasi

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.batani.R

import com.example.batani.databinding.FragmentDashboardBinding
import com.example.batani.network.RekomendasiWithInfoResponseItem
import com.example.batani.ui.detail.DetailActivity
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class DashboardFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private var progressDialog: AlertDialog? = null
    private val rekomendasiAdapter = RekomendasiWithInfoAdapter()
    val Context.dataStore by preferencesDataStore(name = "tanaman_prefs")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedPreferences = requireActivity().getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)
        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        binding.rvRekomendasitanaman.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rekomendasiAdapter
            rekomendasiAdapter.setOnItemClickCallback(object : RekomendasiWithInfoAdapter.OnItemClickCallback {
                override fun onItemClicked(data: RekomendasiWithInfoResponseItem) {
                    val intent = Intent(requireContext(), DetailActivity::class.java)
                    intent.putExtra(DetailActivity.DATA_TANAMAN, data) // Kirim data Parcelable
                    startActivity(intent)
                }
            })


        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if(isLoading){
                showLoadingDialog()
            } else {
                hideLoadingDialog()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                MotionToast.darkToast(
                    requireActivity(),
                    "Error !!",
                    "Server Error, Coba lagi !",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(requireContext(), R.font.poppins_medium)
                )
            }
        }

        val humidity = sharedPreferences.getString("Humidity", null)
        val rainfall = sharedPreferences.getString("Rainfall", null)
        val suhu = sharedPreferences.getString("Suhu", null)

        suhu?.let { suhuValue ->
            humidity?.let { humidityValue ->
                rainfall?.let { rainfallValue ->
                    viewModel.getRekomendasiTanaman(
                        suhuValue.toInt(),
                        humidityValue.toInt(),
                        rainfallValue.toInt()
                    )
                }
            }
        }
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("TanamanPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        viewModel.rekomendasiResponse.observe(viewLifecycleOwner) { rekomendasi ->

            if (rekomendasi.isNotEmpty()) {

                val namaTanamanList = rekomendasi.map { it.tanaman }


                Log.d("Rekomendasi Tanaman", "Nama Tanaman: $namaTanamanList")

                val jumlahTanaman = namaTanamanList.size

                for (i in 0 until jumlahTanaman) {
                    // Membuat kunci dinamis berdasarkan jumlah tanaman
                    val key = "tanaman_$i"
                    editor.putString(key, namaTanamanList[i])


                }

                val tanaman1 = sharedPreferences.getString("tanaman_1", null)
                Log.d("tanaman shared", tanaman1.toString())
                // Jangan lupa untuk commit atau apply perubahan
                editor.apply()

                // Perbarui data ke adapter
                rekomendasiAdapter.updateData(rekomendasi)
            } else {
                Log.d("Rekomendasi Tanaman", "Data rekomendasi kosong")
            }
        }



        return root
    }
    private fun showLoadingDialog() {
        // Pastikan hanya menampilkan dialog jika belum ada
        if (progressDialog == null) {

            val builder = AlertDialog.Builder(requireContext())
            builder.setView(R.layout.layout_loading) // Pastikan Anda memiliki layout dialog_loading.xml
            builder.setCancelable(false) // Agar dialog tidak bisa dibatalkan saat proses berlangsung
            progressDialog = builder.create()
            progressDialog?.show()

        }
    }
    private fun hideLoadingDialog() {

        progressDialog?.dismiss()
        progressDialog = null
    }
}


