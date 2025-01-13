package com.example.group3_starry.utils

import android.content.Context
import android.location.Geocoder
import android.util.Log
import java.util.TimeZone
import kotlin.math.abs

class LocationUtils(private val context: Context) {

    fun getLocationInfo(place: String): LocationInfo? {
        return try {
            val geocoder = Geocoder(context)
            val addresses = geocoder.getFromLocationName(place, 1)

            addresses?.firstOrNull()?.let { address ->
                LocationInfo(
                    latitude = address.latitude.toFloat(),
                    longitude = address.longitude.toFloat(),
                    timezone = getTimezone(address.latitude, address.longitude)
                )
            }
        } catch (e: Exception) {
            Log.e("LocationUtils", "Error getting location info", e)
            null
        }
    }

    private fun getTimezone(latitude: Double, longitude: Double): Float {
        val tz = TimeZone.getDefault()
        return abs(tz.rawOffset) / 3600000f * if(tz.rawOffset > 0) 1 else -1
    }
}

data class LocationInfo(
    val latitude: Float,
    val longitude: Float,
    val timezone: Float
)