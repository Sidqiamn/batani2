package com.example.batani.ui.rekomendasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.app.AlertDialog
import com.example.batani.databinding.FragmentDashboardBinding


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Mengambil instance ViewModel yang sudah didefinisikan dengan benar
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root


        binding.rvQuote.layoutManager = LinearLayoutManager(requireContext())

        // Memanggil fungsi untuk mendapatkan data
        getData()

        // Menambahkan callback untuk tombol back
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showExitConfirmationDialog()
        }

        return root
    }


    private fun getData() {
        val adapter = rekomendasiAdapter() // Sesuaikan adapter sesuai kebutuhan Anda
        binding.rvQuote.adapter = adapter
        viewModel.quote.observe(viewLifecycleOwner) { // Gunakan viewLifecycleOwner untuk observasi
            adapter.submitData(lifecycle, it)
        }
    }

    private fun showExitConfirmationDialog() {
        // Membuat AlertDialog untuk konfirmasi keluar
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Apakah Anda yakin ingin keluar?")
            .setCancelable(false) // Dialog tidak bisa dibatalkan dengan menekan luar dialog
            .setPositiveButton("Ya") { _, _ ->
                // Jika Ya, keluar dari aplikasi atau kembali ke HomeFragment
                requireActivity().finish() // Atau bisa menggunakan navigasi ke HomeFragment
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss() // Menutup dialog jika tekan "Tidak"
            }

        // Menampilkan dialog
        val alert = builder.create()
        alert.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
