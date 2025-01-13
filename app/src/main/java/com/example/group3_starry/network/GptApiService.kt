package com.example.group3_starry.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Define the OpenAI API service interface
interface GptApiService {

    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun getGptResponse(
        @Body request: GptRequest
    ): Response<GptChatResponse>
}

// Request model for the Chat API
data class GptRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Map<String, String>>,
    val max_tokens: Int = 150,
    val temperature: Double = 0.7
)

data class GptChatResponse(
    val choices: List<ChatChoice>
)

data class ChatChoice(
    val message: Message
)

data class Message(
    val role: String,
    val content: String
)
