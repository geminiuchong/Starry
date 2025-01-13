package com.example.group3_starry.network

data class BirthChartRequest(
    val year: Int,
    val month: Int,
    val date: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val latitude: Float,
    val longitude: Float,
    val timezone: Float,
    val settings: FreeAstrologySettings
)

data class FreeAstrologySettings(
    val observation_point: String,
    val ayanamsha: String,
    val language: String
)