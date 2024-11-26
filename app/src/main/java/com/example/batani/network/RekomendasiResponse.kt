package com.example.batani.network

import com.google.gson.annotations.SerializedName

data class RekomendasiResponse(
    @SerializedName("Rekomendasi ke-1") val rekomendasi1: String = "",
    @SerializedName("Rekomendasi ke-2") val rekomendasi2: String = "",
    @SerializedName("Rekomendasi ke-3") val rekomendasi3: String = ""
)
