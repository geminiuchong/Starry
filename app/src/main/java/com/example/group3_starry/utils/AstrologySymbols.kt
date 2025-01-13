package com.example.group3_starry.utils

object AstrologySymbols {
    // Zodiac signs
    const val ARIES = "♈"
    const val TAURUS = "♉"
    const val GEMINI = "♊"
    const val CANCER = "♋"
    const val LEO = "♌"
    const val VIRGO = "♍"
    const val LIBRA = "♎"
    const val SCORPIO = "♏"
    const val SAGITTARIUS = "♐"
    const val CAPRICORN = "♑"
    const val AQUARIUS = "♒"
    const val PISCES = "♓"

    // Planets and points
    const val SUN = "☉"
    const val MOON = "☽"
    const val MERCURY = "☿"
    const val VENUS = "♀"
    const val MARS = "♂"
    const val JUPITER = "♃"
    const val SATURN = "♄"
    const val URANUS = "⛢"
    const val NEPTUNE = "♆"
    const val PLUTO = "♇"
    const val ASCENDANT = "ASC"

    // Helper function to convert planet name to symbol
    fun getPlanetSymbol(planetName: String): String {
        return when (planetName.uppercase()) {
            "SUN" -> SUN
            "MOON" -> MOON
            "MERCURY" -> MERCURY
            "VENUS" -> VENUS
            "MARS" -> MARS
            "JUPITER" -> JUPITER
            "SATURN" -> SATURN
            "URANUS" -> URANUS
            "NEPTUNE" -> NEPTUNE
            "PLUTO" -> PLUTO
            "ASCENDANT" -> ASCENDANT
            else -> planetName
        }
    }

    // Helper function to convert sign name to symbol
    fun getSignSymbol(signName: String): String {
        return when (signName.uppercase()) {
            "ARIES" -> ARIES
            "TAURUS" -> TAURUS
            "GEMINI" -> GEMINI
            "CANCER" -> CANCER
            "LEO" -> LEO
            "VIRGO" -> VIRGO
            "LIBRA" -> LIBRA
            "SCORPIO" -> SCORPIO
            "SAGITTARIUS" -> SAGITTARIUS
            "CAPRICORN" -> CAPRICORN
            "AQUARIUS" -> AQUARIUS
            "PISCES" -> PISCES
            else -> signName
        }
    }

    // Helper function to get zodiac signs array
    fun getAllZodiacSigns(): Array<String> {
        return arrayOf(
            ARIES, TAURUS, GEMINI, CANCER, LEO, VIRGO,
            LIBRA, SCORPIO, SAGITTARIUS, CAPRICORN, AQUARIUS, PISCES
        )
    }

    // Helper function to get planet symbols array
    fun getAllPlanetSymbols(): Array<String> {
        return arrayOf(
            SUN, MOON, MERCURY, VENUS, MARS,
            JUPITER, SATURN, URANUS, NEPTUNE, PLUTO
        )
    }
}