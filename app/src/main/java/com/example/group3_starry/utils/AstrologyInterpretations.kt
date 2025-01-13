package com.example.group3_starry.utils

object AstrologyInterpretations {
    fun getPlanetInSignInterpretation(planet: String, sign: String, house: String): String {
        val planetName = when(planet) {
            "☉" -> "Sun"
            "☽" -> "Moon"
            "☿" -> "Mercury"
            "♀" -> "Venus"
            "♂" -> "Mars"
            "♃" -> "Jupiter"
            "♄" -> "Saturn"
            "⛢" -> "Uranus"
            "♆" -> "Neptune"
            "♇" -> "Pluto"
            "☊" -> "Rahu"
            "☋" -> "Ketu"
            "ASC" -> "Ascendant"
            else -> planet
        }

        return """
            $planetName in $sign in House $house
            
            Key Influences:
            • ${getPlanetMeaning(planetName)}
            • ${getSignInfluence(sign)}
            • ${getHouseEffect(house)}
            
            ${getCombinedEffect(planetName, sign, house)}
        """.trimIndent()
    }

    private fun getPlanetMeaning(planet: String): String = when(planet) {
        "Sun" -> "Represents your core identity, ego, and life force"
        "Moon" -> "Governs your emotions, instincts, and subconscious patterns"
        "Mercury" -> "Rules your communication style, thinking process, and learning abilities"
        "Venus" -> "Influences your relationships, values, and artistic expression"
        "Mars" -> "Determines your drive, energy, and how you assert yourself"
        "Jupiter" -> "Shapes your growth, abundance, and philosophical outlook"
        "Saturn" -> "Affects your discipline, responsibilities, and life lessons"
        "Uranus" -> "Brings unexpected changes and innovative thinking"
        "Neptune" -> "Influences spirituality, dreams, and intuition"
        "Pluto" -> "Represents transformation, power, and regeneration"
        "Rahu" -> "Shows your karmic desires and future direction"
        "Ketu" -> "Indicates spiritual liberation and past life abilities"
        "Ascendant" -> "Shapes your outer personality and approach to life"
        else -> "Influences your overall astrological makeup"
    }

    private fun getSignInfluence(sign: String): String = when(sign) {
        "Aries" -> "Adds pioneering energy and leadership qualities"
        "Taurus" -> "Brings stability, persistence, and material focus"
        "Gemini" -> "Adds versatility, curiosity, and communication skills"
        "Cancer" -> "Brings emotional depth, nurturing, and intuition"
        "Leo" -> "Adds creativity, confidence, and self-expression"
        "Virgo" -> "Brings analytical skills, perfectionism, and service orientation"
        "Libra" -> "Adds diplomacy, balance, and relationship focus"
        "Scorpio" -> "Brings intensity, depth, and transformative power"
        "Sagittarius" -> "Adds optimism, adventure, and philosophical thinking"
        "Capricorn" -> "Brings ambition, discipline, and practical wisdom"
        "Aquarius" -> "Adds innovation, independence, and humanitarian values"
        "Pisces" -> "Brings intuition, compassion, and spiritual connection"
        else -> "Influences your expression in unique ways"
    }

    private fun getHouseEffect(house: String): String = when(house) {
        "1" -> "Manifests in your identity and personal approach to life"
        "2" -> "Affects your values, resources, and self-worth"
        "3" -> "Influences communication, learning, and local environment"
        "4" -> "Shapes your emotional foundation and private life"
        "5" -> "Affects creativity, pleasure, and self-expression"
        "6" -> "Influences work, health, and daily routines"
        "7" -> "Shapes partnerships and one-to-one relationships"
        "8" -> "Affects transformation and shared resources"
        "9" -> "Influences higher learning and long-distance matters"
        "10" -> "Shapes career and public image"
        "11" -> "Affects friendships and future goals"
        "12" -> "Influences spiritual growth and hidden matters"
        else -> "Affects various life areas in unique ways"
    }

    private fun getCombinedEffect(planet: String, sign: String, house: String): String {
        return "This combination creates a unique influence in your chart, particularly affecting how you express ${planet.lowercase()} energy through ${sign}'s qualities in matters of ${getHouseTheme(house)}."
    }

    private fun getHouseTheme(house: String): String = when(house) {
        "1" -> "personal identity"
        "2" -> "material resources"
        "3" -> "communication"
        "4" -> "home and family"
        "5" -> "creativity"
        "6" -> "daily work"
        "7" -> "relationships"
        "8" -> "transformation"
        "9" -> "higher learning"
        "10" -> "career"
        "11" -> "social connections"
        "12" -> "spirituality"
        else -> "life experience"
    }
}