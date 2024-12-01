package com.example.batani.ui.rekomendasi

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

import com.example.batani.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private lateinit var viewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]


        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }


        viewModel.rekomendasiResponse.observe(viewLifecycleOwner) { rekomendasi ->
            binding.rekomendasi1.text = rekomendasi.rekomendasi1
            binding.rekomendasi2.text = rekomendasi.rekomendasi2
            binding.rekomendasi3.text = rekomendasi.rekomendasi3
        }


        viewModel.getRekomendasiTanaman(80, 90, 10)

        return root
    }

}

