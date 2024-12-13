package com.example.batani.ui.tentangAplikasi

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.batani.R
import com.example.batani.databinding.ActivityInformasiAplikasiBinding

class InformasiAplikasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInformasiAplikasiBinding
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformasiAplikasiBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mediaPlayer = MediaPlayer.create(this, R.raw.soundinformasiaplikasi).apply {
            isLooping = true
            start()
        }

        binding.backIcon.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer?.release()
        mediaPlayer = null
    }
}
