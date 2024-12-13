package com.example.batani.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.batani.R
import com.example.batani.databinding.FragmentFirstScreenBinding

class FirstScreen : Fragment() {

    private var _binding: FragmentFirstScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstScreenBinding.inflate(inflater, container, false)
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        setDraggableButtonVisibility(View.GONE)
        binding.next.setOnClickListener {
            viewPager?.currentItem = 1
        }

        return binding.root
    }
    private fun setDraggableButtonVisibility(visibility: Int) {

        val draggableButton = requireActivity().findViewById<View>(R.id.draggableIcon)
        draggableButton?.visibility = visibility
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
