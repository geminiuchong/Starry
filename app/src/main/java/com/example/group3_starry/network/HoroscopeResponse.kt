package com.example.group3_starry.network

data class HoroscopeResponse(
    val mood: String,
    val compatibility: String,
    val horoscope: String,
    val loveAndRelationship: String,
    val health: String,
    val moneyAndFinance: String,
    val career: String,
    val global: String,
    val luckyTime: String,
    val luckyNumber: Int,
    val translated: Translated
)

data class Translated(
    val compatibility: String,
    val mood: String,
    val zodiacSign: String
)

//data class HoroscopeResponse(
//    val areas: List<Area>?,
//    val color: String,
//    val date: String,
//    val horoscope: String,
//    val lucky_number: Int,
//    val lucky_time: String,
//    val mood: String,
//    val sunsign: String
//)
//
//data class Area(
//    val desc: String,
//    val title: String
//)
