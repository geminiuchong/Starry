package com.example.group3_starry.network

class GptRepository {
    private val gptService = ApiClient.gptService

    suspend fun getCardInterpretation(prompt: String): String? {
        return try {
            val request = GptRequest(
                model = "gpt-3.5-turbo",
                messages = listOf(
                    mapOf("role" to "user", "content" to prompt)
                )
            )
            val response = gptService.getGptResponse(request)
            if (response.isSuccessful) {
                response.body()?.choices?.firstOrNull()?.message?.content
            } else {
                "Error: ${response.errorBody()?.string()}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
