package com.example.batani.ui.graph

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.batani.databinding.FragmentNotificationsBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.util.Random

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var lineChart: LineChart

    // Data untuk setiap garis (misalnya, data untuk 5 garis)
    private val monthData = arrayOf(
        floatArrayOf(150f, 55f, 120f, 130f, 80f, 90f, 100f, 10f, 120f, 30f, 140f, 150f), // Garis 1
        floatArrayOf(70f, 65f, 55f, 85f, 95f, 100f, 110f, 120f, 130f, 140f, 150f, 160f), // Garis 2
        floatArrayOf(95f, 60f, 20f, 80f, 90f, 100f, 110f, 120f, 20f, 140f, 150f, 160f), // Garis 3
        floatArrayOf(40f, 55f, 150f, 120f, 70f, 80f, 10f, 100f, 110f, 20f, 30f, 140f), // Garis 4
        floatArrayOf(30f, 60f, 40f, 50f, 120f, 80f, 10f, 90f, 100f, 110f, 120f, 130f)  // Garis 5
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this)[NotificationsViewModel::class.java]

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Menginisialisasi LineChart
        lineChart = binding.lineChart

        // Bulan sebagai label untuk X-Axis
        val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val dataSets = ArrayList<LineDataSet>()

        // Membuat 5 garis data berdasarkan monthData
        for (i in monthData.indices) {
            val values = ArrayList<Entry>()
            for (j in months.indices) {
                // Menambahkan nilai yang sesuai dari array monthData untuk setiap garis
                values.add(Entry(j.toFloat(), monthData[i][j]))
            }

            val dataSet = LineDataSet(values, "Garis ${i + 1}")

            // Menghasilkan warna acak untuk setiap garis
            val randomColor = generateRandomColor()
            dataSet.setColor(randomColor) // Menggunakan warna acak
            dataSet.setValueTextColor(Color.BLACK) // Ubah warna teks

            // Mengatur ketebalan garis
            dataSet.lineWidth = 2f  // Ubah  sesuai ketebalan di inginkan
            dataSets.add(dataSet)
        }

        // Menambahkan data ke LineChart
        val lineData = LineData(dataSets as List<ILineDataSet>?)
        lineChart.data = lineData

        // Menambahkan pengaturan untuk X dan Y Axis
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(months) // Menggunakan bulan sebagai label X
        xAxis.granularity = 1f // Menentukan interval antar titik
        xAxis.setLabelCount(months.size, true)  // Menampilkan semua bulan

        // Menambahkan pengaturan untuk Y axis (harga)
        val yAxis = lineChart.axisLeft
        yAxis.setDrawGridLines(true)
        yAxis.setAxisMinimum(0f) // Menetapkan harga minimum 0
        yAxis.setAxisMaximum(160f) // Menetapkan harga maksimum sesuai dengan data yang ada
        yAxis.granularity = 10f // Menentukan interval antara nilai sumbu Y (misalnya setiap 10)
        yAxis.valueFormatter = object : ValueFormatter() {
            @SuppressLint("DefaultLocale")
            override fun getFormattedValue(value: Float): String {
                return String.format("%.1f", value) // Menampilkan angka dengan satu angka desimal
            }
        }
        lineChart.axisRight.isEnabled = false // Menonaktifkan sumbu Y sebelah kanan

        // Memperbarui grafik
        lineChart.invalidate()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showExitConfirmationDialog()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Apakah Anda yakin ingin keluar?")
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ -> requireActivity().finish() }
            .setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }

        // Menampilkan dialog
        val alert = builder.create()
        alert.show()
    }

    // Fungsi untuk menghasilkan warna acak
    private fun generateRandomColor(): Int {
        val random = Random()
        val r = random.nextInt(256) // Red
        val g = random.nextInt(256) // Green
        val b = random.nextInt(256) // Blue
        return Color.rgb(r, g, b)
    }
}
