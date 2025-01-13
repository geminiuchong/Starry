package com.example.group3_starry.network

class HoroscopeRepository {
    private val apiService = ApiClient.horoscopeService

    suspend fun getDailyHoroscope(zodiacSign: String): HoroscopeResponse? {
        return try {
            val response = apiService.getDailyHoroscope(zodiacSign)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }
}
