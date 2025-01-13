package com.example.group3_starry.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface HoroscopeApiService {
    @GET("Detailed-Horoscope/")
    suspend fun getDailyHoroscope(
        @Query("zodiacSign") zodiacSign: String,
        @Header("x-rapidapi-key") apiKey: String = "767f15b1c2mshfecb79717910644p1dc7efjsn5cc92bdf6264",
        @Header("x-rapidapi-host") host: String = "best-daily-astrology-and-horoscope-api.p.rapidapi.com"
    ): Response<HoroscopeResponse>
}
