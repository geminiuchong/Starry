package com.example.group3_starry.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.group3_starry.R
import java.time.LocalDate

object ZodiacUtils {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getZodiacSign(birthDate: LocalDate): String {
        val day = birthDate.dayOfMonth
        val month = birthDate.monthValue

        return when (month) {
            1 -> if (day < 20) "capricorn" else "aquarius"
            2 -> if (day < 19) "aquarius" else "pisces"
            3 -> if (day < 21) "pisces" else "aries"
            4 -> if (day < 20) "aries" else "taurus"
            5 -> if (day < 21) "taurus" else "gemini"
            6 -> if (day < 21) "gemini" else "cancer"
            7 -> if (day < 23) "cancer" else "leo"
            8 -> if (day < 23) "leo" else "virgo"
            9 -> if (day < 23) "virgo" else "libra"
            10 -> if (day < 23) "libra" else "scorpio"
            11 -> if (day < 22) "scorpio" else "sagittarius"
            12 -> if (day < 22) "sagittarius" else "capricorn"
            else -> "aries"
        }
    }

    fun getZodiacIcon(sign: String): Int {
        return when (sign.lowercase()) {
            "aries" -> R.drawable.aries
            "taurus" -> R.drawable.taurus
            "gemini" -> R.drawable.gemini
            "cancer" -> R.drawable.cancer
            "leo" -> R.drawable.leo
            "virgo" -> R.drawable.virgo
            "libra" -> R.drawable.libra
            "scorpio" -> R.drawable.scorpio
            "sagittarius" -> R.drawable.sagittarius
            "capricorn" -> R.drawable.capricorn
            "aquarius" -> R.drawable.aquarius
            "pisces" -> R.drawable.pisces
            else -> R.mipmap.ic_launcher_starry // Default icon
        }

    }
}
