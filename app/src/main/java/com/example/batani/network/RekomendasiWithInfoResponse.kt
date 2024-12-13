package com.example.batani.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class RekomendasiWithInfoResponse(

	@field:SerializedName("RekomendasiWithInfoResponse")
	val rekomendasiWithInfoResponse: List<RekomendasiWithInfoResponseItem?>? = null
)

@Parcelize
data class RekomendasiWithInfoResponseItem(

	@field:SerializedName("Tanaman")
	val tanaman: String? = null,

	@field:SerializedName("Info")
	val info: Info? = null,

	@field:SerializedName("Probabilitas")
	val probabilitas: Double? = null
): Parcelable

@Parcelize
data class Info(

	@field:SerializedName("Gambar")
	val gambar: String? = null,

	@field:SerializedName("Cara merawat")
	val caraMerawat: String? = null,

	@field:SerializedName("Jenis tanah")
	val jenisTanah: String? = null,

	@field:SerializedName("Waktu penanaman")
	val waktuPenanaman: String? = null,

	@field:SerializedName("Deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("pH tanah")
	val pHTanah: String? = null
): Parcelable
