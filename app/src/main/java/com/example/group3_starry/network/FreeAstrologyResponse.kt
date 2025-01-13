package com.example.group3_starry.network

data class FreeAstrologyResponse(
    val statusCode: Int,
    val output: PlanetPositions
)

data class PlanetPositions(
    val Ascendant: PlanetInfo,
    val Sun: PlanetInfo,
    val Moon: PlanetInfo,
    val Mars: PlanetInfo,
    val Mercury: PlanetInfo,
    val Jupiter: PlanetInfo,
    val Venus: PlanetInfo,
    val Saturn: PlanetInfo,
    val Rahu: PlanetInfo,
    val Ketu: PlanetInfo,
    val Uranus: PlanetInfo,
    val Neptune: PlanetInfo,
    val Pluto: PlanetInfo
)

data class PlanetInfo(
    val current_sign: Int,
    val house_number: Int,
    val fullDegree: Double,
    val normDegree: Double,
    val isRetro: String,
    val zodiac_sign_name: String,
    val zodiac_sign_lord: String,
    val nakshatra_number: Int,
    val nakshatra_name: String,
    val nakshatra_pada: Int,
    val nakshatra_vimsottari_lord: String
)