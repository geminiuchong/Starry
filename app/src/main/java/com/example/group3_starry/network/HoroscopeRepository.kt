package com.example.group3_starry.network

class HoroscopeRepository {
    private val apiService = ApiClient.horoscopeService

    suspend fun getHoroscope(sign: String, timeframe: String): HoroscopeResponse? {
        return try {
            val response = apiService.getHoroscope(sign, timeframe)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

//class HoroscopeRepository {
//    private val apiService = ApiClient.horoscopeService
//
//    suspend fun getDailyHoroscope(day: String, sunsign: String): HoroscopeResponse? {
//        return try {
//            val response = apiService.getDailyHoroscope(day = day, sunsign = sunsign)
//            if (response.isSuccessful) response.body() else null
//        } catch (e: Exception) {
//            null
//        }
//    }
//}
