package com.example.group3_starry.network

// Data class for Match Making Request
data class MatchMakingRequest(
    val female: BirthDetails, // Birth details for the first person
    val male: BirthDetails, // Birth details for the second person
    val config: MatchConfig // Additional configuration options
)

// Data class for Birth Details
data class BirthDetails(
    val year: Int,        // Year of birth
    val month: Int,       // Month of birth (1-12)
    val date: Int,        // Day of the month
    val hours: Int,       // Hours (0-23)
    val minutes: Int,     // Minutes (0-59)
    val seconds: Int = 0, // Optional seconds, default is 0.0
    val latitude: Double = 37.7749, // Default latitude (San Francisco, California)
    val longitude: Double = -122.4194, // Default longitude (San Francisco, California)
    val timezone: Double = -8.0  // Default timezone (Pacific Standard Time)
)

// Data class for Configuration Options
data class MatchConfig(
    val observation_point: String = "topocentric", // Options: "geocentric" or "topocentric"
    val language: String = "en",                  // Language options: "en", "te", "hi"
    val ayanamsha: String = "lahiri"              // Options: "lahiri" or "sayana"
)


