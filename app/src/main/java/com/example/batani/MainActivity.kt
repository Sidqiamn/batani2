package com.example.batani

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.batani.databinding.ActivityMainBinding
import com.example.batani.ui.chatbot.ChatBotActivity

import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private var startX = 0f
    private var startY = 0f
    private val cLICKTHRESHOLD = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )

        navView.setupWithNavController(navController)


        val draggableIcon = binding.root.findViewById<View>(R.id.draggableIcon)

        draggableIcon.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Simpan posisi awal
                    startX = event.rawX
                    startY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {

                    view.animate()
                        .x(event.rawX - (view.width / 2))
                        .y(event.rawY - (view.height / 2))
                        .setDuration(0)
                        .start()
                }
                MotionEvent.ACTION_UP -> {

                    val deltaX = abs(event.rawX - startX)
                    val deltaY = abs(event.rawY - startY)

                    if (deltaX < cLICKTHRESHOLD && deltaY <cLICKTHRESHOLD) {
                        view.performClick()
                    }
                }
            }
            true
        }

        draggableIcon.setOnClickListener {
            val intent = Intent(this, ChatBotActivity::class.java)
            startActivity(intent)
        }
    }

}