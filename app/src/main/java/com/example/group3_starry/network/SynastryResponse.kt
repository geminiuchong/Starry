package com.example.group3_starry.network

data class SynastryResponse(
    val name: String, // Partner's name
    val love: Double, // Love compatibility score
    val intellectual: Double, // Intellectual compatibility score
    val physical: Double, // Physical compatibility score
    val strength: Double, // Strength of the connection
    val bad: Double, // Conflicts or "bad points"
    val overall: Double // Overall compatibility score
)


