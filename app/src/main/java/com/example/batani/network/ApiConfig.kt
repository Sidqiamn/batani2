package com.example.batani.network


import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiService(): ApiService {
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
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

        fun getApiRekomendasi(): ApiServiceTanamanApi {
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            // Gunakan GsonBuilder untuk mendaftarkan InstanceCreator
            val gson = GsonBuilder()
                .registerTypeAdapter(RekomendasiResponse::class.java, RekomendasiResponseInstanceCreator())
                .create()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://cropdeploy-856805603330.asia-southeast2.run.app/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()

            return retrofit.create(ApiServiceTanamanApi::class.java)
        }

    }
}
