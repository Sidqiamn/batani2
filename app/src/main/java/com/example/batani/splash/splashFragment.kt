package com.example.batani.splash


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

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onResume() {
        super.onResume()

        setBottomNavigationVisibility(View.GONE)
        setDraggableButtonVisibility(View.GONE)

        navigateAfterDelay()
    }

    private fun setDraggableButtonVisibility(visibility: Int) {

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



    private fun setBottomNavigationVisibility(visibility: Int) {
        requireActivity().findViewById<View>(R.id.nav_view)?.visibility = visibility
    }

    override fun onDestroyView() {
        super.onDestroyView()

        setBottomNavigationVisibility(View.VISIBLE)
        setDraggableButtonVisibility(View.VISIBLE)
    }
}
