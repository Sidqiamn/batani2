package com.example.batani.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.batani.R
import com.example.batani.databinding.FragmentHomeBinding
import com.example.batani.ui.maps.MapsActivity

class HomeFragment : Fragment() {

    private val bitmapSize = 500  // Ukuran bitmap
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

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

        return root
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

}

