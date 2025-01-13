package com.example.group3_starry.network

class AstrologerRepository {

    // Get birth chart data for a specific person
    suspend fun getBirthChart(
        name: String,
        dateString: String,     // Format: YYYY-MM-DD
        timeString: String,     // Format: HH:mm
        latitude: Double,
        longitude: Double
    ): Result<BirthChartResponse> {
        return try {
            // Make API call using ApiClient
            val response = ApiClient.astrologerService.getBirthChart(
                latitude = latitude,
                longitude = longitude,
                name = name,
                dateString = dateString,
                timeString = timeString
            )

            // Check if response is successful
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("API call failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Convert birth chart data for display
    fun formatChartDataForDisplay(response: BirthChartResponse): List<ProfileChartData> {
        return response.planets.map { planet ->
            ProfileChartData(
                sign = planet.sign,
                signSymbol = planet.signSymbol,
                planetSymbol = planet.planetSymbol,
                house = planet.house.toString()
            )
        }.sortedBy { it.house.toIntOrNull() ?: Int.MAX_VALUE }
    }
}

// Data class for formatted display data
data class ChartDisplayData(
    val sign: String,           // Original sign name
    val signSymbol: String,     // Sign symbol (e.g., ♈)
    val planetSymbol: String,   // Planet symbol (e.g., ☉)
    val house: String           // House number
)