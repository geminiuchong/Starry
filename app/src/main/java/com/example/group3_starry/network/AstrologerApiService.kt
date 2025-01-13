package com.example.group3_starry.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface AstrologerApiService {
    // Get birth chart data including planet positions, houses, and aspects
    @GET("chart/birth/get")
    suspend fun getBirthChart(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("name") name: String,
        @Query("dateString") dateString: String,    // Format: YYYY-MM-DD
        @Query("timeString") timeString: String,    // Format: HH:mm
        @Header("X-RapidAPI-Key") apiKey: String = "YOUR_API_KEY",
        @Header("X-RapidAPI-Host") host: String = "astrologer.p.rapidapi.com"
    ): Response<BirthChartResponse>
}