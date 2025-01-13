package com.example.group3_starry.utils

object AstrologyInterpretations {
    fun getPlanetInSignInterpretation(planet: String, sign: String, house: String): String {
        return when {
            // Sun interpretations
            planet == "☉" && sign == "Leo" -> """
                Your Sun in Leo in House ${house}
                
                Core Traits:
                • Natural leadership abilities
                • Strong creative expression
                • Warm and generous personality
                • Confident self-expression
                
                This placement suggests you naturally shine in positions of leadership and creative endeavors. You have a warm, generous nature and a natural flair for drama and self-expression. Your need to be recognized and appreciated is strong, and you often find yourself at the center of attention.

                Key Life Themes:
                • Personal identity and self-expression
                • Creative pursuits and artistic talents
                • Leadership and authority
                • Recognition and appreciation
                """.trimIndent()

            // Moon interpretations
            planet == "☽" && sign == "Virgo" -> """
                Your Moon in Virgo in House ${house}
                
                Emotional Nature:
                • Analytical approach to emotions
                • Need for order and routine
                • Detail-oriented emotional processing
                • Service-oriented nature
                
                This placement indicates you find emotional security through order, routine, and practical analysis. You have a strong need to be useful and productive, and your emotional well-being is often tied to your ability to help others and maintain order in your environment.

                Key Emotional Needs:
                • Organization and cleanliness
                • Practical problem-solving
                • Being of service to others
                • Analysis and improvement
                """.trimIndent()

            // Ascendant interpretations
            planet == "ASC" && sign == "Libra" -> """
                Your Libra Ascendant (Rising Sign)
                
                First Impressions:
                • Naturally diplomatic and graceful
                • Charming and pleasant demeanor
                • Keen sense of balance and harmony
                • Aesthetic awareness
                
                Your Libra Ascendant shapes how others first perceive you and your natural approach to new situations. You present yourself as diplomatic, graceful, and harmony-seeking. Your natural charm and ability to see multiple perspectives makes you excellent at handling social situations.

                Key Personality Traits:
                • Social grace and diplomacy
                • Desire for harmony and balance
                • Appreciation for beauty
                • Partnership-oriented approach
                """.trimIndent()

            // Pluto interpretations
            planet == "♇" && sign == "Sagittarius" -> """
                Pluto in Sagittarius in House ${house}
                
                Transformative Themes:
                • Belief systems and philosophy
                • Higher education and wisdom
                • Cultural understanding
                • Truth-seeking
                
                This placement indicates profound transformation through expanding beliefs and seeking truth. You're part of a generation focused on transforming educational systems, religious beliefs, and cultural understanding.

                Areas of Deep Change:
                • Personal philosophy and beliefs
                • International relations
                • Higher learning and wisdom
                • Cultural boundaries
                """.trimIndent()

            // Uranus interpretations
            planet == "⛢" && sign == "Aquarius" -> """
                Uranus in Aquarius in House ${house}
                
                Revolutionary Aspects:
                • Technological innovation
                • Social reform
                • Humanitarian causes
                • Group consciousness
                
                This placement brings revolutionary thinking and innovative approaches to technology and social structures. You're part of a generation that naturally embraces technological change and social progress.

                Key Areas of Innovation:
                • Social networks and communities
                • Technological advancement
                • Humanitarian causes
                • Group dynamics
                """.trimIndent()

            // Other planet interpretations...
            else -> """
                ${planet} in ${sign} in House ${house}
                
                This unique placement brings special qualities to your chart:
                • Influences how you express this planetary energy
                • Affects the areas of life governed by House ${house}
                • Creates specific patterns in your life experiences
                • Contributes to your overall astrological profile
                
                This placement's effects are best understood in context with the rest of your birth chart.
                """.trimIndent()
        }
    }
}