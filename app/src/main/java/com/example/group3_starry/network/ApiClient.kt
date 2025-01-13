package com.example.group3_starry.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Base URL for the API
    private const val BASE_URL = "https://best-daily-astrology-and-horoscope-api.p.rapidapi.com/api/"

    // Create a Retrofit instance with the base URL and Gson converter
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON parsing
            .build()
    }

    // Provide the HoroscopeApiService instance
    val horoscopeService: HoroscopeApiService by lazy {
        retrofit.create(HoroscopeApiService::class.java)
    }
}