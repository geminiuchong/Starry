package com.example.group3_starry.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StarLoveMatchApiService {
    @GET("match.php")
    suspend fun getSynastry(
        @Query("name") name: String,
        @Query("dob") dob: String,
        @Query("name1") partnerName: String,
        @Query("dob1") partnerDob: String,
        @Query("sort") sort: String = "O", // Default to Overall compatibility
        @Query("NC") nc: String = "C",    // Default to Current compatibility
        @Query("ryr") ryr: String = "2024", // Default year
        @Query("details") details: String = "N" // Default details
    ): Response<List<SynastryResponse>>
}


