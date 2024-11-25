package com.example.batani.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.batani.R
import com.example.batani.databinding.FragmentViewPagerBinding

class ViewPagerFragment : Fragment() {

    private var _binding: FragmentViewPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)

        // Daftar fragment onboarding
        val fragmentList = arrayListOf(
            FirstScreen(),
            SecondScreen(),
            ThirdScreen()
        )

        // Adapter untuk ViewPager
        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Sembunyikan BottomNavigationView saat fragment aktif
        requireActivity().findViewById<View>(R.id.nav_view)?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Tampilkan kembali BottomNavigationView ketika fragment dihancurkan
        requireActivity().findViewById<View>(R.id.nav_view)?.visibility = View.VISIBLE
        _binding = null
    }
}
