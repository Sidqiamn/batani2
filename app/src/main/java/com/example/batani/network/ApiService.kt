package com.example.batani.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
}

interface ApiServiceLogin {
}
interface ApiServiceRegister {
}


interface ApiServiceTanamanApi {

    @GET("tanaman")
    suspend fun getRekomendasiTanaman(
        @Query("temperature") temperature: Int,
        @Query("humidity") humidity: Int,
        @Query("rainfall") rainfall: Int
    ): RekomendasiResponse
}

