package com.example.batani.splash

import android.animation.ObjectAnimator
import android.content.Context

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.batani.R

import com.example.batani.databinding.FragmentThirdScreenBinding

class ThirdScreen : Fragment() {

    private var _binding: FragmentThirdScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThirdScreenBinding.inflate(inflater, container, false)


        playBlinkAnimation()

        binding.finish.setOnClickListener {

            onBoardingFinished()

            findNavController().navigate(R.id.action_viewPagerFragment_to_loginActivity)
        }

        val parentActivity = activity
        if (parentActivity != null) {
            Log.d("FragmentCheck", "Fragment ini menempel ke Activity: ${parentActivity.javaClass.simpleName}")
        } else {
            Log.d("FragmentCheck", "Fragment belum menempel ke Activity mana pun.")
        }

        return binding.root
    }

    private fun playBlinkAnimation() {

        ObjectAnimator.ofFloat(binding.finish, View.ALPHA, 1f, 0f, 1f).apply {
            duration = 2500 // Durasi animasi (dalam milidetik)
            repeatCount = ObjectAnimator.INFINITE // Animasi berulang
            repeatMode = ObjectAnimator.RESTART // Animasi dimulai kembali setelah selesai
            start()
        }
    }

    private fun onBoardingFinished() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
