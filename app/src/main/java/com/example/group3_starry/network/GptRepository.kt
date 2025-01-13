package com.example.group3_starry.network

class GptRepository {
    private val gptService = ApiClient.gptService

    sealed class ApiResponse<out T> {
        data class Success<out T>(val data: T) : ApiResponse<T>()
        data class Error(val message: String) : ApiResponse<Nothing>()
    }

    suspend fun getCardInterpretation(prompt: String): ApiResponse<String> {
        return try {
            val request = GptRequest(
                model = "gpt-3.5-turbo",
                // model = "gpt-4",
                messages = mutableListOf(
                    mapOf("role" to "system", "content" to "You are a tarot card expert."),
                    mapOf("role" to "user", "content" to prompt)
                    //mapOf("role" to "user", "content" to prompt)
                    )
            )
            val response = gptService.getGptResponse(request)
            if (response.isSuccessful) {
//                response.body()?.choices?.firstOrNull()?.message?.content
                // ApiResponse.Success(response.body()?.choices?.firstOrNull()?.message?.content ?: "")
                val content = response.body()?.choices?.firstOrNull()?.message?.content
                ApiResponse.Success(content ?: "No response from GPT.")
            } else {
//                "Error: ${response.errorBody()?.string()}"
                ApiResponse.Error("Error: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.localizedMessage ?: "Unknown error occurred.")
//            e.printStackTrace()
//            null
        }
    }
}
