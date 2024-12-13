package com.example.batani.ui.detail

import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

import com.example.batani.databinding.ActivityDetailBinding
import com.example.batani.network.RekomendasiWithInfoResponseItem

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tanamanData = intent.getParcelableExtra<RekomendasiWithInfoResponseItem>(DATA_TANAMAN)
        Log.d("tanamanData", tanamanData.toString())

        tanamanData?.let {
            binding.namaTanaman.text = it.tanaman
            binding.dekripsiTanaman.text = it.info?.deskripsi ?: "Deskripsi tidak tersedia"
            Glide.with(this)
                .load(it.info?.gambar)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.gambarDetail)
            binding.isiCara.text = it.info?.caraMerawat
            binding.watuPenanaman.text = it.info?.waktuPenanaman
            binding.jenisTanah.text = it.info?.jenisTanah
            binding.phTanah.text = it.info?.pHTanah
        }
    }
    companion object {
        const val DATA_TANAMAN = "data_tanaman"
    }

}