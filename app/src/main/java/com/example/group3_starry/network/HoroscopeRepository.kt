package com.example.group3_starry.network

class HoroscopeRepository {
    private val apiService = ApiClient.horoscopeService

    suspend fun getDailyHoroscope(day: String, sunsign: String): HoroscopeResponse? {
        return try {
            val response = apiService.getDailyHoroscope(day = day, sunsign = sunsign)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }
}
