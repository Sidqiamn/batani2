package com.example.batani.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
}

interface CityApiServiceCode {
    @POST("get-code")
    suspend fun getCityCode(@Body request: CityRequest): Response<CodeResponse>
}

interface ApiServiceTanamanApi {

    @GET("predict_crop")
    suspend fun getRekomendasiTanaman(
        @Query("temperature") temperature: Int,
        @Query("humidity") humidity: Int,
        @Query("rainfall") rainfall: Int
    ): List<RekomendasiWithInfoResponseItem>
}

interface ForecastApiService {
    @GET("forecast")
    fun getForecast(
        @Query("tanaman") tanaman: List<String>,
        @Query("days2forecast") days: Int
    ): Call<List<ForecastResponse>>
}

