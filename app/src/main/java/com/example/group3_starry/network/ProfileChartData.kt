package com.example.group3_starry.network

data class ProfileChartData(
    val sign: String,
    val signSymbol: String,
    val planetSymbol: String,
    val house: String,
    var analysis: String = "",
    var isExpanded: Boolean = false
)