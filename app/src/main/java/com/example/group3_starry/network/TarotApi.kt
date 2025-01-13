package com.example.group3_starry.network

import com.example.group3_starry.R
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class TarotApi {
    private val client = OkHttpClient()
    private val apiKey = "767f15b1c2mshfecb79717910644p1dc7efjsn5cc92bdf6264"
    private val apiHost = "oracle15.p.rapidapi.com"

    fun getCards(number: Int, callback: (List<Card>) -> Unit) {
        val url = "https://oracle15.p.rapidapi.com/api/v1/draw-cards?number=$number"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("x-rapidapi-key", apiKey)
            .addHeader("x-rapidapi-host", apiHost)
            .build()

        Thread {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                val jsonResponse = JSONObject(responseBody)
                val cards = parseCards(jsonResponse)
                callback(cards)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(emptyList())
            }
        }.start()
    }

    private fun parseCards(jsonResponse: JSONObject): List<Card> {
        val cards = mutableListOf<Card>()
        val dataArray = jsonResponse.getJSONArray("data")
        val cardArray = dataArray.getJSONObject(0).getJSONArray("cards")

        for (i in 0 until cardArray.length()) {
            val cardJson = cardArray.getJSONObject(i)
            val cardName = cardJson.getString("name")
            val cardInfo = cardJson.getString("info")
            val imageResId = getCardImageResource(cardName)
            cards.add(Card(name = cardName , info = cardInfo, imageResId = imageResId))
        }

        return cards
    }
}

data class Card(
    val name: String,
    val info: String,
    val imageResId: Int
)

private fun getCardImageResource(cardName: String): Int {
    val cardMap = mapOf(
        "The Fool" to R.drawable.the_fool_great_0,
        "The Magician" to R.drawable.the_magician_great_1,
        "The High Priestess" to R.drawable.the_high_priestess_great_2,
        "The Empress" to R.drawable.the_empress_great_3,
        "The Emperor" to R.drawable.the_emperor_great_4,
        "The Hierophant" to R.drawable.the_hierophant_great_5,
        "The Lovers" to R.drawable.the_lovers_great_6,
        "The Chariot" to R.drawable.the_chariot_great_7,
        "Strength" to R.drawable.strength_great_8,
        "The Hermit" to R.drawable.the_hermit_great_9,
        "Wheel of Fortune" to R.drawable.the_wheel_of_fortune_great_10,
        "Justice" to R.drawable.justice_great_11,
        "The Hanged Man" to R.drawable.the_hanged_man_great_12,
        "Death" to R.drawable.death_great_13,
        "Temperance" to R.drawable.temperance_great_14,
        "The Devil" to R.drawable.the_devil_great_15,
        "The Tower" to R.drawable.the_tower_great_16,
        "The Star" to R.drawable.the_star_great_17,
        "The Moon" to R.drawable.the_moon_great_18,
        "The Sun" to R.drawable.the_sun_great_19,
        "Judgement" to R.drawable.judgement_great_20,
        "The World" to R.drawable.the_world_great_21,
        "Ace of Wands" to R.drawable.ace_of_wands_22,
        "Two of Wands" to R.drawable.two_of_wands_23,
        "Three of Wands" to R.drawable.three_of_wands_24,
        "Four of Wands" to R.drawable.four_of_wands_25,
        "Five of Wands" to R.drawable.five_of_wands_26,
        "Six of Wands" to R.drawable.six_of_wands_27,
        "Seven of Wands" to R.drawable.seven_of_wands_28,
        "Eight of Wands" to R.drawable.eight_of_wands_29,
        "Nine of Wands" to R.drawable.nine_of_wands_30,
        "Ten of Wands" to R.drawable.ten_of_wands_31,
        "Page of Wands" to R.drawable.page_of_wands_32,
        "Knight of Wands" to R.drawable.knight_of_wands_33,
        "Queen of Wands" to R.drawable.queen_of_wands_34,
        "King of Wands" to R.drawable.king_of_wands_35,
        "Ace of Cups" to R.drawable.ace_of_cups_36,
        "Two of Cups" to R.drawable.two_of_cups_37,
        "Three of Cups" to R.drawable.three_of_cups_38,
        "Four of Cups" to R.drawable.four_of_cups_39,
        "Five of Cups" to R.drawable.five_of_cups_40,
        "Six of Cups" to R.drawable.six_of_cups_41,
        "Seven of Cups" to R.drawable.seven_of_cups_42,
        "Eight of Cups" to R.drawable.eight_of_cups_43,
        "Nine of Cups" to R.drawable.nine_of_cups_44,
        "Ten of Cups" to R.drawable.ten_of_cups_45,
        "Page of Cups" to R.drawable.page_of_cups_46,
        "Knight of Cups" to R.drawable.knight_of_cups_47,
        "Queen of Cups" to R.drawable.queen_of_cups_48,
        "King of Cups" to R.drawable.king_of_cups_49,
        "Ace of Swords" to R.drawable.ace_of_swords_50,
        "Two of Swords" to R.drawable.two_of_swords_51,
        "Three of Swords" to R.drawable.three_of_swords_52,
        "Four of Swords" to R.drawable.four_of_swords_53,
        "Five of Swords" to R.drawable.five_of_swords_54,
        "Six of Swords" to R.drawable.six_of_swords_55,
        "Seven of Swords" to R.drawable.seven_of_swords_56,
        "Eight of Swords" to R.drawable.eight_of_swords_57,
        "Nine of Swords" to R.drawable.nine_of_swords_58,
        "Ten of Swords" to R.drawable.ten_of_swords_59,
        "Page of Swords" to R.drawable.page_of_swords_60,
        "Knight of Swords" to R.drawable.knight_of_swords_61,
        "Queen of Swords" to R.drawable.queen_of_swords_62,
        "King of Swords" to R.drawable.king_of_swords_63,
        "Ace of Pentacles" to R.drawable.ace_of_pentacles_64,
        "Two of Pentacles" to R.drawable.two_of_pentacles_65,
        "Three of Pentacles" to R.drawable.three_of_pentacles_66,
        "Four of Pentacles" to R.drawable.four_of_pentacles_67,
        "Five of Pentacles" to R.drawable.five_of_pentacles_68,
        "Six of Pentacles" to R.drawable.six_of_pentacles_69,
        "Seven of Pentacles" to R.drawable.seven_of_pentacles_70,
        "Eight of Pentacles" to R.drawable.eight_of_pentacles_71,
        "Nine of Pentacles" to R.drawable.nine_of_pentacles_72,
        "Ten of Pentacles" to R.drawable.ten_of_pentacles_73,
        "Page of Pentacles" to R.drawable.page_of_pentacles_74,
        "Knight of Pentacles" to R.drawable.knight_of_pentacles_75,
        "Queen of Pentacles" to R.drawable.queen_of_pentacles_76,
        "King of Pentacles" to R.drawable.king_of_pentacles_77,
    )
    return cardMap[cardName] ?: R.drawable.card_back_1 // Fallback to a default image
}
