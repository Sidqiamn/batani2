package com.example.batani.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.batani.R
import com.example.batani.database.LokasiRepository
import com.example.batani.databinding.FragmentHomeBinding
import com.example.batani.ui.maps.MapsActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.IOException
import java.util.Locale

class HomeFragment : Fragment() {

    private val bitmapSize = 500  // Ukuran bitmap
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var lokasiRepository: LokasiRepository
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            } else {
                showToast("Permission denied. Cannot access location.")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inisialisasi repository dan fusedLocationClient
        lokasiRepository = LokasiRepository(requireActivity().application)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        loadAndSaveCitiesAsync {
            getMyLocation()
        }

        // Buat dan tampilkan rounded Bitmap dengan teks dan ikon untuk ketiga ImageView
        val roundedBitmap1 = createRoundedBitmapWithText("Wind", "70", R.drawable.iconswind)
        val roundedBitmap2 = createRoundedBitmapWithText("Rainfall", "30 m/s", R.drawable.iconsrainfall)
        val roundedBitmap3 = createRoundedBitmapWithText("Humidity", "1 m/s", R.drawable.iconshumidity)

        binding.namaKota.setOnClickListener { goToMaps() }

        // Set bitmap ke masing-masing ImageView
        binding.containerParameterCuaca.setImageBitmap(roundedBitmap1)
        binding.containerParameterCuaca2.setImageBitmap(roundedBitmap2)
        binding.containerParameterCuaca3.setImageBitmap(roundedBitmap3)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showExitConfirmationDialog()
        }

        getMyLocation()
        return root
    }
    private fun loadAndSaveCitiesAsync(onComplete: () -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val inputStream = requireContext().assets.open("cities.json")
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                val cities = JSONArray(jsonString)

                for (i in 0 until cities.length()) {
                    val city = cities.getJSONObject(i)
                    val name = city.getString("name")
                    val code = city.getString("code")

                    lokasiRepository.insertCity(name, code)
                }

                withContext(Dispatchers.Main) {
                    onComplete()
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Failed to load or save cities.json", e)
            }
        }
    }


    private fun getCityCode(cityName: String) {
        lokasiRepository.getCityCode(cityName) { cityCode ->
            // Tampilkan hasil dalam log atau toast
            if (cityCode != null) {
                Log.d("HomeFragment", "Kode Kota $cityName: $cityCode")
                showToast("Kode Kota $cityName: $cityCode")
            } else {
                Log.d("HomeFragment", "Kota $cityName tidak ditemukan di database.")
                showToast("Kota $cityName tidak ditemukan di database.")
            }
        }
    }

    private fun createRoundedBitmapWithText(text1: String, text2: String, iconResId: Int): Bitmap {
        // Membuat bitmap dengan transparansi
        val bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Mengatur latar belakang canvas menjadi warna #207044 dengan 50% opacity
        val backgroundColor = Color.argb(60, 32, 112, 68)
        canvas.drawColor(backgroundColor)

        // Buat objek Paint untuk teks pertama
        val paint1 = Paint().apply {
            color = ResourcesCompat.getColor(resources, R.color.white, null)
            textSize = 50f // Ukuran teks pertama
            textAlign = Paint.Align.LEFT
            typeface = Typeface.DEFAULT_BOLD
        }

        // Muat ikon dari drawable berdasarkan iconResId dan atur ukurannya
        val icon = BitmapFactory.decodeResource(resources, iconResId)
        val scaledIcon = Bitmap.createScaledBitmap(icon, 70, 70, true)

        // Posisi untuk menggambar ikon dan teks pertama
        val iconX = 50f
        val iconY = 50f
        val textX = iconX + scaledIcon.width + 10f
        val textY = iconY + scaledIcon.height / 2f + 15f

        // Gambar ikon di sebelah kiri teks pertama
        canvas.drawBitmap(scaledIcon, iconX, iconY, null)
        canvas.drawText(text1, textX, textY, paint1)

        // Buat objek Paint untuk teks kedua dengan ukuran yang lebih besar
        val paint2 = Paint().apply {
            color = ResourcesCompat.getColor(resources, R.color.white, null)
            textSize = 100f // Ukuran teks kedua yang lebih besar
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }

        // Gambar teks kedua di tengah Canvas
        val centerX = bitmap.width / 2f
        val textBounds = Rect()
        paint2.getTextBounds(text2, 0, text2.length, textBounds)
        val centerY = bitmap.height / 2f - textBounds.exactCenterY()
        canvas.drawText(text2, centerX, centerY, paint2)

        // Buat bitmap dengan sudut melengkung dan stroke
        return 50f.getRoundedBitmap(bitmap)
    }

    // Fungsi untuk menerapkan rounded corners dan stroke pada Bitmap
    private fun Float.getRoundedBitmap(bitmap: Bitmap): Bitmap {
        val roundedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(roundedBitmap)

        // Paint untuk isi bitmap dengan shader
        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        // Paint untuk stroke putih
        val strokePaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE // Warna putih untuk stroke
            style = Paint.Style.STROKE
            strokeWidth = 1f // Ketebalan stroke
        }

        // Rect untuk isi bitmap
        val rect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        canvas.drawRoundRect(rect, this, this, paint)

        // Rect untuk stroke sedikit lebih kecil agar pas di tepi luar
        val strokeRect = RectF(5f, 5f, bitmap.width - 5f, bitmap.height - 5f)
        canvas.drawRoundRect(strokeRect, this, this, strokePaint)

        return roundedBitmap
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

        // Menampilkan dialog
        val alert = builder.create()
        alert.show()
    }

    private fun goToMaps() {
        val intent = Intent(requireContext(), MapsActivity::class.java)
        startActivity(intent)
    }

    private fun getMyLocation() {
        binding.progressBar.visibility = View.VISIBLE
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                    currentLocation?.let { location ->
                        getDistrictName(location.latitude, location.longitude)
                    }
                    binding.progressBar.visibility = View.GONE
                    Log.d("Location", "Lat: ${location.latitude}, Lng: ${location.longitude}")
                } else {
                    Log.d("Location", "Location is null")
                    binding.progressBar.visibility = View.GONE
                }
            }.addOnFailureListener { exception ->
                Log.e("Location", "Failed to get location", exception)
                binding.progressBar.visibility = View.GONE
            }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getDistrictName(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addressList.isNullOrEmpty()) {
                val address = addressList[0]

                // Ambil nama kota
                val district = address.subAdminArea  // Nama kecamatan atau distrik
                val kecamatan = address.locality

                // Menghilangkan kata "Kota" dari nama kota jika ada
                val cleanedCity = district?.replaceFirst("Kota ", "") ?: district

                // Menampilkan nama kota setelah dihapus kata "Kota"
                binding.namaKota.text = cleanedCity ?: "Nama kota tidak ditemukan"

                // Memanggil getCityCode dengan nama kota yang telah dibersihkan
                kecamatan?.let {
                    getCityCode(it) // Menggunakan nama kota untuk mendapatkan kode kota
                }
            }
        } catch (e: IOException) {
            Log.e("Location", "Error fetching address", e)
            binding.namaKota.text = "Tidak bisa mendapatkan nama kota"  // Tangani kesalahan
        }
    }



}
