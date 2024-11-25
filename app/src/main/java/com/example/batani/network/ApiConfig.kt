package com.example.batani.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
   companion object{
       fun getApiService(): ApiService {
           val loggingInterceptor =
               HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
           val client = OkHttpClient.Builder()
               .addInterceptor(loggingInterceptor)
               .build()
           val retrofit = Retrofit.Builder()
               .baseUrl("https://quote-api.dicoding.dev/")
               .addConverterFactory(GsonConverterFactory.create())
               .client(client)
               .build()
           return retrofit.create(ApiService::class.java)
       }
       fun getApiServiceLogin(): ApiServiceLogin {
           val loggingInterceptor =
               HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
           val client = OkHttpClient.Builder()
               .addInterceptor(loggingInterceptor)
               .build()
           val retrofit = Retrofit.Builder()
               .baseUrl("https://story-api.dicoding.dev/v1/")
               .addConverterFactory(GsonConverterFactory.create())
               .client(client)
               .build()
           return retrofit.create(ApiServiceLogin::class.java)
       }

       fun getApiServiceRegister(): ApiServiceRegister {
           val loggingInterceptor =
               HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
           val client = OkHttpClient.Builder()
               .addInterceptor(loggingInterceptor)
               .build()
           val retrofit = Retrofit.Builder()
               .baseUrl("https://story-api.dicoding.dev/v1/")
               .addConverterFactory(GsonConverterFactory.create())
               .client(client)
               .build()
           return retrofit.create(ApiServiceRegister::class.java)
       }
   }

}