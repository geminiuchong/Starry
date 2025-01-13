package com.example.group3_starry.network

data class MatchMakingResponse(
    val statusCode: Int?,
    val output: MatchMakingOutput? // Match Making output details
)

data class MatchMakingOutput(
    val out_of: Int?,
    val total_score: Double?,
    val varna_kootam: KootaDetails?,
    val yoni_kootam: KootaDetails?,
    val graha_maitri_kootam: SimpleKootaDetails?,
    // Add other koota details as needed
)

data class KootaDetails(
    val bride: KootaParticipant?,
    val groom: KootaParticipant?,
    val out_of: Int?,
    val score: Double?
)

data class SimpleKootaDetails(
    val out_of: Int?,
    val score: Double?
)

data class KootaParticipant(
    val varnam_name: String?,
    val yoni: String?
)
