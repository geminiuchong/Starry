package com.example.group3_starry.network

data class HoroscopeResponse(
    val status: Boolean,
    val prediction: String,
    val number: String,
    val color: String,
    val strength: String,
    val weakness: String
)