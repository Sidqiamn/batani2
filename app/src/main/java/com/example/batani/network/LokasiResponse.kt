package com.example.batani.network

import com.google.gson.annotations.SerializedName

data class LokasiResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("lokasi")
	val lokasi: Lokasi? = null
)

data class DataItem(

	@field:SerializedName("cuaca")
	val cuaca: List<List<CuacaItemItem?>?>? = null,

	@field:SerializedName("lokasi")
	val lokasi: Lokasi? = null
)

data class CuacaItemItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("vs_text")
	val vsText: String? = null,

	@field:SerializedName("analysis_date")
	val analysisDate: String? = null,

	@field:SerializedName("time_index")
	val timeIndex: String? = null,

	@field:SerializedName("local_datetime")
	val localDatetime: String? = null,

	@field:SerializedName("wd_deg")
	val wdDeg: Int? = null,

	@field:SerializedName("wd")
	val wd: String? = null,

	@field:SerializedName("hu")
	val hu: Int? = null,

	@field:SerializedName("utc_datetime")
	val utcDatetime: String? = null,

	@field:SerializedName("datetime")
	val datetime: String? = null,

	@field:SerializedName("t")
	val t: Int? = null,

	@field:SerializedName("tcc")
	val tcc: Int? = null,

	@field:SerializedName("weather_desc_en")
	val weatherDescEn: String? = null,

	@field:SerializedName("wd_to")
	val wdTo: String? = null,

	@field:SerializedName("weather")
	val weather: Int? = null,

	@field:SerializedName("weather_desc")
	val weatherDesc: String? = null,

	@field:SerializedName("tp")
	val tp: Int? = null,

	@field:SerializedName("ws")
	val ws: Any? = null,

	@field:SerializedName("vs")
	val vs: Int? = null
)

data class Lokasi(

	@field:SerializedName("provinsi")
	val provinsi: String? = null,

	@field:SerializedName("desa")
	val desa: String? = null,

	@field:SerializedName("kota")
	val kota: String? = null,

	@field:SerializedName("adm3")
	val adm3: String? = null,

	@field:SerializedName("adm2")
	val adm2: String? = null,

	@field:SerializedName("timezone")
	val timezone: String? = null,

	@field:SerializedName("adm4")
	val adm4: String? = null,

	@field:SerializedName("kecamatan")
	val kecamatan: String? = null,

	@field:SerializedName("adm1")
	val adm1: String? = null,

	@field:SerializedName("lon")
	val lon: Any? = null,

	@field:SerializedName("lat")
	val lat: Any? = null,

	@field:SerializedName("kotkab")
	val kotkab: String? = null,

	@field:SerializedName("type")
	val type: String? = null
)
