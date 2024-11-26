package com.example.batani.network

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
   @GET("list")
   suspend fun getQuote(
           @Query("page") page: Int,
           @Query("size") size: Int
   ): List<QuoteResponseItem>
}

interface ApiServiceLogin {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse
}
interface ApiServiceRegister {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse
}


interface ApiServiceTanamanApi {

    @GET("tanaman")
    suspend fun getRekomendasiTanaman(
        @Query("temperature") temperature: Int,
        @Query("humidity") humidity: Int,
        @Query("rainfall") rainfall: Int
    ): RekomendasiResponse
}

