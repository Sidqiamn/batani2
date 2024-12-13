package com.example.batani.network



data class WeatherResponse(
	val lokasi: Location,
	val data: List<WeatherData>
)

data class Location(
	val adm1: String,
	val adm2: String,
	val adm3: String,
	val adm4: String,
	val provinsi: String,
	val kota: String,
	val kecamatan: String,
	val desa: String,
	val lon: Double,
	val lat: Double,
	val timezone: String
)

data class WeatherData(
	val lokasi: Location?,
	val cuaca: List<List<WeatherDetail>>
)

data class WeatherDetail(
	val datetime: String,
	val t: Int,
	val tcc: Int,
	val tp: Double, // Ubah dari Int ke Double
	val weather: Int,
	val weather_desc: String,
	val weather_desc_en: String,
	val wd_deg: Int,
	val wd: String,
	val wd_to: String,
	val ws: Double,
	val hu: Int,
	val vs: Int,
	val vs_text: String,
	val time_index: String,
	val analysis_date: String,
	val image: String,
	val utc_datetime: String,
	val local_datetime: String
)


