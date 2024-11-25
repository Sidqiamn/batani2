package com.example.batani.splash

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.batani.R
import com.example.batani.ui.rekomendasi.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private val viewModel by viewModels<SplashViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onResume() {
        super.onResume()

        // Sembunyikan BottomNavigationView saat SplashFragment aktif
        setBottomNavigationVisibility(View.GONE)
        setDraggableButtonVisibility(View.GONE)
        // Gunakan coroutine untuk penundaan sebelum navigasi
        navigateAfterDelay()
    }

    private fun setDraggableButtonVisibility(visibility: Int) {
        // Cari tombol draggable berdasarkan ID
        val draggableButton = requireActivity().findViewById<View>(R.id.draggableIcon)
        draggableButton?.visibility = visibility
    }

    private fun navigateAfterDelay() {
        lifecycleScope.launch {
            delay(3000)
            viewModel.getSession().observe(viewLifecycleOwner) { user ->
                val destination = if (user.isLogin && user.token.isNotEmpty()) {
                    R.id.action_splashFragment_to_navigation_home
                } else {
                    R.id.action_splashFragment_to_viewPagerFragment
                }
                findNavController().navigate(destination)
            }
        }
    }


    private fun onBoardingFinished(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }

    private fun setBottomNavigationVisibility(visibility: Int) {
        requireActivity().findViewById<View>(R.id.nav_view)?.visibility = visibility
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Pastikan BottomNavigationView ditampilkan kembali setelah SplashFragment dihancurkan
        setBottomNavigationVisibility(View.VISIBLE)
        setDraggableButtonVisibility(View.VISIBLE)
    }
}
