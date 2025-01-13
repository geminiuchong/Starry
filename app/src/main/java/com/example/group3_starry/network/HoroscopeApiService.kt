package com.example.group3_starry.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HoroscopeApiService {

//    @GET("horoscope")
//    suspend fun getDailyHoroscope(
//        @Query("day") day: String = "today",
//        @Query("sunsign") sunsign: String,
//        @Header("x-rapidapi-key") apiKey: String = "767f15b1c2mshfecb79717910644p1dc7efjsn5cc92bdf6264",
//        @Header("x-rapidapi-host") host: String = "horoscope-astrology.p.rapidapi.com"
//    ): Response<HoroscopeResponse>
    @GET("api/horoscope/{sign}/{timeframe}")
    suspend fun getHoroscope(
    @Path("sign") sign: String,
    @Path("timeframe") timeframe: String = "daily"
    ): Response<HoroscopeResponse>

    // Synastry Endpoint
    @GET("synastry")
    suspend fun getSynastryReport(
        @Query("user1") user1Details: String,
        @Query("user2") user2Details: String,
        @Header("Authorization") apiKey: String = "Bearer UkwsWvPFvuHUAGlwK4Sg3l2DVdnZQWC79RB9Xjif"
    ): Response<SynastryResponse>

    // Match Making Endpoint
    @POST("match-making/ashtakoot-score")
    suspend fun getMatchMakingScore(
        @Body body: MatchMakingRequest
    ): Response<MatchMakingResponse>
}



