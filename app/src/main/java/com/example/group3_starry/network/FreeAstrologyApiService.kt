package com.example.group3_starry.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FreeAstrologyApiService {
    @POST("planets/extended")
    suspend fun getPlanetPositions(
        @Body request: BirthChartRequest
    ): Response<FreeAstrologyResponse>
}