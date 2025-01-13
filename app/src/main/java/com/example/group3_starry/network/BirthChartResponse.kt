package com.example.group3_starry.network

import com.example.group3_starry.utils.AstrologySymbols

data class BirthChartResponse(
    val planets: List<Planet>,
    val houses: List<House>,
    val aspects: List<Aspect>
)

data class Planet(
    val name: String,        // Planet name (e.g., "Sun", "Moon")
    val sign: String,        // Zodiac sign (e.g., "Aries", "Taurus")
    val house: Int,          // House number (1-12)
    val degree: Double       // Position in degrees
) {
    // Get symbol representation for display
    val planetSymbol: String
        get() = AstrologySymbols.getPlanetSymbol(name)

    val signSymbol: String
        get() = AstrologySymbols.getSignSymbol(sign)

    // Format for display in chart
    fun formatForDisplay(): Pair<String, String> {
        return Pair(signSymbol, "$planetSymbol ${degree.toInt()}°")
    }
}

data class House(
    val number: Int,         // House number (1-12)
    val sign: String,        // Zodiac sign on the cusp
    val degree: Double       // Cusp position in degrees
) {
    val signSymbol: String
        get() = AstrologySymbols.getSignSymbol(sign)
}

data class Aspect(
    val planet1: String,     // First planet
    val planet2: String,     // Second planet
    val type: String,        // Aspect type (conjunction, trine, etc.)
    val orb: Double         // Orb of the aspect in degrees
) {
    val planet1Symbol: String
        get() = AstrologySymbols.getPlanetSymbol(planet1)

    val planet2Symbol: String
        get() = AstrologySymbols.getPlanetSymbol(planet2)
}