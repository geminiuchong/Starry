package com.example.group3_starry.network

data class SynastryResponse(
    val status: Boolean, // Indicates success or failure of the API call
    val compatibility_score: Int, // Numeric score indicating compatibility level
    val compatibility_message: String, // A textual message about the compatibility
    val partner1: PartnerDetails, // Details about the first partner
    val partner2: PartnerDetails // Details about the second partner
)

data class PartnerDetails(
    val name: String, // Partner's name
    val sun_sign: String, // Partner's sun sign
    val moon_sign: String // Partner's moon sign
)

