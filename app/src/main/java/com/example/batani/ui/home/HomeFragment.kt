package com.example.batani.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.batani.R

import com.example.batani.databinding.FragmentHomeBinding
import com.example.batani.network.ApiConfig
import com.example.batani.network.CityRequest
import com.example.batani.network.WeatherResponse
import com.example.batani.ui.predikisiCuaca.PrediksiCuacaActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.IOException
import java.time.LocalTime
import java.util.Locale
import kotlin.random.Random

class HomeFragment : Fragment() {

    private val bitmapSize = 500  // Ukuran bitmap
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private var progressDialog: AlertDialog? = null
    private var alertFirst : AlertDialog? = null
    private val pREFSNAME = "WeatherPrefs"
    private lateinit var sharedPreferences: SharedPreferences
    private var mediaPlayer: MediaPlayer? = null

    private lateinit var locationManager: LocationManager

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showLoadingDialog()

                getMyLocation()

            } else {
                showToast("Permission denied. Cannot access location.")
                hideLoadingDialog()

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(pREFSNAME, Context.MODE_PRIVATE)


        val root: View = binding.root
        checkIfGpsIsEnabled()


        binding.refreshLokasi.setOnClickListener {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.pop).apply {
                start()
            }
            showLoadingDialogMemuatUlangCuaca()
            getMyLocation()
        }

        binding.containerParameterCuaca.setOnClickListener {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.pop).apply {
                start()
            }
            startActivity(Intent(requireContext(), PrediksiCuacaActivity::class.java))
        }

        binding.containerParameterCuaca2.setOnClickListener {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.pop).apply {
                start()
            }
            startActivity(Intent(requireContext(), PrediksiCuacaActivity::class.java))
        }

        binding.containerParameterCuaca3.setOnClickListener {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.pop).apply {
                start()
            }
            startActivity(Intent(requireContext(), PrediksiCuacaActivity::class.java))
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showExitConfirmationDialog()
        }

        return root
    }
    private val locationSettingResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->

        checkIfGpsIsEnabled()
    }

    private fun checkIfGpsIsEnabled() {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            dialogFirst()

            showLoadingDialog()


            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

            // Periksa data cuaca dari SharedPreferences
            val windSpeed = sharedPreferences.getString("WindSpeed", null)
            val humidity = sharedPreferences.getString("Humidity", null)
            val rainfall = sharedPreferences.getString("Rainfall", null)
            val suhu = sharedPreferences.getString("Suhu", null)
            val partlyCloud = sharedPreferences.getString("PartlyCloud", null)
            val namakota = sharedPreferences.getString("namakota", null)

            if (windSpeed != null && humidity != null && rainfall != null) {
                val roundedBitmap1 = createRoundedBitmapWithText("Wind Speed", "$windSpeed m/s", R.drawable.iconswind)
                val roundedBitmap2 = createRoundedBitmapWithText("Humidity", "$humidity%", R.drawable.iconshumidity)
                val roundedBitmap3 = createRoundedBitmapWithText("Rainfall", rainfall, R.drawable.iconsrainfall)

                binding.namaKota.text = namakota ?: "..."
                binding.containerParameterCuaca.setImageBitmap(roundedBitmap1)
                binding.containerParameterCuaca2.setImageBitmap(roundedBitmap2)
                binding.containerParameterCuaca3.setImageBitmap(roundedBitmap3)

                binding.partlyCloud.text = partlyCloud
                binding.derajatAngka.text = getString(R.string.suhu_text, suhu.toString())
                hideLoadingFirts()
                hideLoadingDialog()

            } else {
                getMyLocation()
            }



        } else {

            Toast.makeText(requireContext(), "Aktifkan lokasi (GPS) untuk melanjutkan.", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            locationSettingResultLauncher.launch(intent) // Meluncurkan intent untuk membuka pengaturan GPS
        }
    }


    private fun fetchWeatherData(kodeWilayah: String) {
        Log.d("masuk le fetch", "fetchweather")
        val apiUrl = "https://api.bmkg.go.id/publik/prakiraan-cuaca?adm4=$kodeWilayah"

        showLoadingDialog()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(apiUrl).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val gson = Gson()

                    val weatherResponse = gson.fromJson(responseBody, WeatherResponse::class.java)
                    val weatherDataList = weatherResponse.data?.firstOrNull()?.cuaca?.get(0)

                    if (weatherDataList == null) {
                        Log.e("ERROR", "Data cuaca null atau tidak valid")
                        withContext(Dispatchers.Main) {
                            showToast("Data cuaca tidak tersedia.")
                            hideLoadingDialog()
                        }
                        return@launch
                    }

                    val currentHour = LocalTime.now().hour
                    var index = when (currentHour) {
                        in 7..9 -> 0
                        in 10..12 -> 1
                        in 13..15 -> 2
                        in 16..18 -> 3
                        in 19..21 -> 4
                        else -> 1
                    }

                    if (index !in weatherDataList.indices) {
                        Log.e("ERROR", "Indeks $index di luar batas")
                        withContext(Dispatchers.Main) {
                          index =1
                        }

                    }

                    val weatherData = weatherDataList[index]
                    val ws = weatherData?.ws
                    val hu = weatherData?.hu
                    val partlyCloud = weatherData?.weather_desc
                    val suhu = weatherData?.t

                    val rainfallDescription = when (partlyCloud) {
                        "Berawan" -> 0
                        "Hujan Ringan" -> Random.nextInt(1, 20)
                        "Hujan Sedang" -> Random.nextInt(20, 50)
                        "Hujan Lebat" -> Random.nextInt(50, 100)
                        "Hujan Sangat Lebat" -> Random.nextInt(100, 150)
                        "Hujan Ekstrem" -> Random.nextInt(150, 160)
                        else -> 0
                    }

                    // Simpan data cuaca di SharedPreferences
                    with(sharedPreferences.edit()) {
                        putString("WindSpeed", ws.toString())
                        putString("Humidity", hu.toString())
                        putString("Rainfall", rainfallDescription.toString())
                        putString("Suhu", suhu.toString())
                        putString("PartlyCloud", partlyCloud.toString())
                        apply()
                    }

                    // Update UI
                    withContext(Dispatchers.Main) {
                        binding.containerParameterCuaca.setImageBitmap(
                            createRoundedBitmapWithText("Wind Speed", "$ws m/s", R.drawable.iconswind)
                        )
                        binding.containerParameterCuaca2.setImageBitmap(
                            createRoundedBitmapWithText("Humidity", "$hu%", R.drawable.iconshumidity)
                        )
                        binding.containerParameterCuaca3.setImageBitmap(
                            createRoundedBitmapWithText("Rainfall", rainfallDescription.toString(), R.drawable.iconsrainfall)
                        )
                        binding.derajatAngka.text = getString(R.string.suhu_text, suhu.toString())
                        binding.partlyCloud.text = partlyCloud
                        hideLoadingDialog()
                        hideLoadingFirts()
                        MotionToast.darkToast(
                            requireActivity(),
                            "Sukses 😍",
                            "Cuaca Berhasil Diperbarui!",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(requireContext(), R.font.poppins_medium)
                        )
                    }
                } else {
                    Log.e("API_ERROR", "HTTP Error: ${response.code}")
                    withContext(Dispatchers.Main) {
                        showToast("Gagal mendapatkan data cuaca!")
                        hideLoadingDialog()
                    }
                }
            } catch (e: Exception) {
                Log.e("kesalahan", e.message.toString())
                withContext(Dispatchers.Main) {
                    showToast("Terjadi kesalahan!")
                    hideLoadingDialog()
                }
            }
        }
    }



    private fun getCityCode(cityName: String) {
        val cityNameWithDistrict = if (cityName.contains("Kecamatan", ignoreCase = true)) {
            cityName
        } else {
            "Kecamatan $cityName"
        }

        Log.d("kecamatan ada ga", cityNameWithDistrict)

        CoroutineScope(Dispatchers.IO).launch {
            try {

                Log.d("kecamatan ada ga", cityName)

                val request = CityRequest(name = cityNameWithDistrict)

                val response = ApiConfig.getCityCode().getCityCode(request)


                if (response.isSuccessful) {
                    val cityCode = response.body()?.cityCode
                    Log.d("HomeFragment", "Respons Body: ${response.body()}")
                    Log.d("HomeFragment", "City Code: $cityCode")

                    if (cityCode != null) {

                        withContext(Dispatchers.Main) {

                            sharedPreferences.edit().putString("Kodecity", cityCode).apply()


                            val savedCityCode = sharedPreferences.getString("Kodecity", null)
                            if (savedCityCode != null) {
                                Log.d("HomeFragment", "Kode kota berhasil disimpan: $savedCityCode")
                            } else {
                                Log.d("HomeFragment", "Gagal menyimpan kode kota ke SharedPreferences.")
                            }

                            fetchWeatherData(cityCode)
                        }
                    } else {

                        withContext(Dispatchers.Main) {
                            Log.d("HomeFragmentElse", "Kota $cityName tidak ditemukan.")
                            hideLoadingDialog()
                            val errorMessage = "Lokasi tidak terdaftar"
                            MotionToast.darkToast(
                                requireActivity(),
                                "Error !!",
                                errorMessage,
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(requireContext(), R.font.poppins_medium)
                            )
                        }
                    }
                } else {

                    withContext(Dispatchers.Main) {
                        Log.d("HomeFragment", "API Error: ${response.message()}")
                        val errorMessage = "Lokasi tidak terdaftar"
                        MotionToast.darkToast(
                            requireActivity(),
                            "Error !!",
                            errorMessage,
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(requireContext(), R.font.poppins_medium)
                        )
                    }
                }
            } catch (e: Exception) {

                withContext(Dispatchers.Main) {
                    Log.d("HomeFragment", "Exception: ${e.message}")
                    val errorMessage = "Lokasi tidak terdaftar"
                    MotionToast.darkToast(
                        requireActivity(),
                        "Error !!",
                        errorMessage,
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), R.font.poppins_medium)
                    )
                }
            }
        }
    }

    private fun createRoundedBitmapWithText(text1: String, text2: String, iconResId: Int): Bitmap {

        val bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)


        val backgroundColor = Color.argb(60, 32, 112, 68)
        canvas.drawColor(backgroundColor)

        val paint1 = Paint().apply {
            color = ResourcesCompat.getColor(resources, R.color.white, null)
            textSize = 50f // Ukuran teks pertama
            textAlign = Paint.Align.LEFT
            typeface = Typeface.DEFAULT_BOLD
        }

        val icon = BitmapFactory.decodeResource(resources, iconResId)
        val scaledIcon = Bitmap.createScaledBitmap(icon, 70, 70, true)

        val iconX = 50f
        val iconY = 50f
        val textX = iconX + scaledIcon.width + 10f
        val textY = iconY + scaledIcon.height / 2f + 15f

        canvas.drawBitmap(scaledIcon, iconX, iconY, null)
        canvas.drawText(text1, textX, textY, paint1)

        val paint2 = Paint().apply {
            color = ResourcesCompat.getColor(resources, R.color.white, null)
            textSize = 90f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }

        val centerX = bitmap.width / 2f
        val textBounds = Rect()
        paint2.getTextBounds(text2, 0, text2.length, textBounds)
        val centerY = bitmap.height / 2f - textBounds.exactCenterY()
        canvas.drawText(text2, centerX, centerY, paint2)

        return 50f.getRoundedBitmap(bitmap)
    }

    private fun Float.getRoundedBitmap(bitmap: Bitmap): Bitmap {
        val roundedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(roundedBitmap)


        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }


        val strokePaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }

        val rect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        canvas.drawRoundRect(rect, this, this, paint)


        val strokeRect = RectF(5f, 5f, bitmap.width - 5f, bitmap.height - 5f)
        canvas.drawRoundRect(strokeRect, this, this, strokePaint)

        return roundedBitmap
    }

    private fun showToast(message: String) {

        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        hideLoadingDialog()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLocation = location
            currentLocation?.let { loc ->
                getDistrictName(loc.latitude, loc.longitude)
            }
            hideLoadingDialog()

            stopLocationUpdates()

            Log.d("Location", "Lat: ${location.latitude}, Lng: ${location.longitude}")
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun stopLocationUpdates() {
        try {
            locationManager.removeUpdates(locationListener)
            Log.d("Location", "Location updates stopped")
        } catch (e: Exception) {
            Log.e("Location", "Error stopping location updates", e)
        }
    }
    private fun getMyLocation() {
        showLoadingDialog()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

            try {

                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0L,
                    0f,
                    locationListener
                )
            } catch (e: SecurityException) {
                Log.e("Location", "SecurityException: ${e.message}", e)
                hideLoadingDialog()
            }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            hideLoadingDialog()
        }
    }

    private fun getDistrictName(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addressList.isNullOrEmpty()) {
                val address = addressList[0]

                val district = address.subAdminArea
                val kecamatan = address.locality

                val cleanedCity = district?.replaceFirst("Kota ", "") ?: district
                binding.namaKota.text = cleanedCity ?: "Nama kota tidak ditemukan"
                sharedPreferences.edit().putString("namakota", cleanedCity).apply()

                kecamatan?.let {
                    getCityCode(it)
                }
            }
        } catch (e: IOException) {
            Log.e("Location", "Error fetching address", e)
            binding.namaKota.text = "..."
            hideLoadingDialog()

        }
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Apakah Anda yakin ingin keluar?")
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                requireActivity().finish()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }


        val alert = builder.create()
        alert.show()
    }
    private fun dialogFirst() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Loading...")
            .setCancelable(false)

        alertFirst = builder.create()
        alertFirst?.show()


        lifecycleScope.launch {
            delay(50000)
            alertFirst?.dismiss()
        }
    }
    private fun hideLoadingFirts() {

        alertFirst?.dismiss()
        alertFirst = null
    }


    private fun showLoadingDialogMemuatUlangCuaca() {
        if (progressDialog == null) {

            val inflater = LayoutInflater.from(requireContext())
            val dialogView = inflater.inflate(R.layout.layout_loading, null)


            val textView = dialogView.findViewById<TextView>(R.id.loadingText)
            textView.text = "Memuat ulang data cuaca..."

            val builder = AlertDialog.Builder(requireContext())
            builder.setView(dialogView)
            builder.setCancelable(false)
            progressDialog = builder.create()
            progressDialog?.show()
        }
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

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null

        stopLocationUpdates()

        with(sharedPreferences.edit()) {
            remove("WindSpeed")
            remove("Humidity")
            remove("Rainfall")
            remove("Suhu")
            remove("PartlyCloud")
            apply()
        }
        requireActivity().finish()
        Log.d("HomeFragment", "Data cuaca di SharedPreferences berhasil dihapus.")
    }



    private fun hideLoadingDialog() {

        progressDialog?.dismiss()
        progressDialog = null
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
