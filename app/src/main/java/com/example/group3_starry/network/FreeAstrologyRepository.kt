package com.example.group3_starry.network

import android.util.Log
import com.example.group3_starry.utils.AstrologyInterpretations
import com.example.group3_starry.utils.AstrologySymbols
import retrofit2.Response

class FreeAstrologyRepository {
    suspend fun getBirthChart(
        birthDate: String,
        birthTime: String,
        latitude: Float,
        longitude: Float,
        timezone: Float
    ): Result<List<ProfileChartData>> {
        return try {
            val (year, month, day) = parseBirthDate(birthDate)
            val (hour, minute) = parseBirthTime(birthTime)
            val adjustedTimezone = if (timezone == 0f) 5.5f else timezone

            val request = BirthChartRequest(
                year = year,
                month = month,
                date = day,
                hours = hour,
                minutes = minute,
                seconds = 0,
                latitude = latitude,
                longitude = longitude,
                timezone = adjustedTimezone,
                settings = FreeAstrologySettings(
                    observation_point = "topocentric",
                    ayanamsha = "lahiri",
                    language = "en"
                )
            )

            Log.d("API_REQUEST", "Request: $request")
            val response = ApiClient.freeAstrologyService.getPlanetPositions(request)
            Log.d("API_RESPONSE", "Raw Response: ${response.raw()}")

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d("API_RESPONSE", "Response Body: $it")
                    Result.success(convertToProfileChartData(it))
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("API call failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error getting birth chart", e)
            Result.failure(e)
        }
    }

    private fun convertToProfileChartData(response: FreeAstrologyResponse): List<ProfileChartData> {
        val output = response.output
        val ascendantHouse = output.Ascendant.house_number

        fun adjustHouseNumber(originalHouse: Int): Int {
            if (originalHouse == 0) return 1
            var adjusted = originalHouse - ascendantHouse + 1
            if (adjusted <= 0) adjusted += 12
            return adjusted
        }

        return listOf(
            createChartData(output.Ascendant, "Ascendant", 1),
            createChartData(output.Sun, "Sun", adjustHouseNumber(output.Sun.house_number)),
            createChartData(output.Moon, "Moon", adjustHouseNumber(output.Moon.house_number)),
            createChartData(output.Mars, "Mars", adjustHouseNumber(output.Mars.house_number)),
            createChartData(output.Mercury, "Mercury", adjustHouseNumber(output.Mercury.house_number)),
            createChartData(output.Jupiter, "Jupiter", adjustHouseNumber(output.Jupiter.house_number)),
            createChartData(output.Venus, "Venus", adjustHouseNumber(output.Venus.house_number)),
            createChartData(output.Saturn, "Saturn", adjustHouseNumber(output.Saturn.house_number)),
            createChartData(output.Rahu, "Rahu", adjustHouseNumber(output.Rahu.house_number)),
            createChartData(output.Ketu, "Ketu", adjustHouseNumber(output.Ketu.house_number)),
            createChartData(output.Uranus, "Uranus", adjustHouseNumber(output.Uranus.house_number)),
            createChartData(output.Neptune, "Neptune", adjustHouseNumber(output.Neptune.house_number)),
            createChartData(output.Pluto, "Pluto", adjustHouseNumber(output.Pluto.house_number))
        ).sortedBy { it.house.toIntOrNull() ?: Int.MAX_VALUE }
    }

    private fun createChartData(planetInfo: PlanetInfo, planetName: String, adjustedHouse: Int): ProfileChartData {
        return ProfileChartData(
            sign = planetInfo.zodiac_sign_name,
            signSymbol = AstrologySymbols.getSignSymbol(planetInfo.zodiac_sign_name),
            planetSymbol = AstrologySymbols.getPlanetSymbol(planetName),
            house = adjustedHouse.toString(),
            analysis = AstrologyInterpretations.getPlanetInSignInterpretation(
                AstrologySymbols.getPlanetSymbol(planetName),
                planetInfo.zodiac_sign_name,
                adjustedHouse.toString()
            )
        )
    }

    private fun parseBirthDate(birthDate: String): Triple<Int, Int, Int> {
        val parts = birthDate.split("-").map { it.trim() }
        Log.d("DATE_PARSING", "Parsing date: $birthDate, Parts: $parts")
        return Triple(
            parts[2].toInt(),  // year
            parts[1].toInt(),  // month
            parts[0].toInt()   // day
        )
    }

    private fun parseBirthTime(birthTime: String): Pair<Int, Int> {
        val parts = birthTime.split(":")
        Log.d("TIME_PARSING", "Parsing time: $birthTime, Parts: $parts")
        return Pair(
            parts[0].toInt(),  // hour
            parts[1].toInt()   // minute
        )
    }
}