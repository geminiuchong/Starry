package com.example.group3_starry.network

data class HoroscopeResponse(
    val areas: List<Area>?,
    val color: String,
    val date: String,
    val horoscope: String,
    val lucky_number: Int,
    val lucky_time: String,
    val mood: String,
    val sunsign: String
)

data class Area(
    val desc: String,
    val title: String
)
