package com.example.group3_starry.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // Base URLs
    private const val BASE_URL_HOROSCOPE = "https://horoscope-astrology.p.rapidapi.com/"
    private const val BASE_URL_STARLOVEMATCH = "https://www.starlovematch.com/api/"
    private const val BASE_URL_GPT = "https://api.openai.com/"
    private const val BASE_URL_ASTROLOGER = "https://astrologer.p.rapidapi.com/"

    // Shared OkHttpClient with logging for debugging
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    // Retrofit instance for the Horoscope API
    private val horoscopeRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_HOROSCOPE)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Retrofit instance for the StarLoveMatch API
    private val starLoveMatchRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_STARLOVEMATCH)
            .client(createStarLoveMatchClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Retrofit instance for the GPT API
    private val gptRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_GPT)
            .client(createGptClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Retrofit instance for the Astrologer API
    private val astrologerRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_ASTROLOGER)
            .client(createAstrologerClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // OkHttpClient specifically for the StarLoveMatch API with required headers
    private fun createStarLoveMatchClient(): OkHttpClient {
        return client.newBuilder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("x-rapidapi-key", "2b6d239c23msh3b1c18f70e0d6b7p18957ajsn192817ad6a58")
                    .addHeader("x-rapidapi-host", "starlovematch.p.rapidapi.com")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    // OkHttpClient specifically for the GPT API with Authorization header added
    private fun createGptClient(): OkHttpClient {
        return client.newBuilder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${ApiKeyProvider.OPENAI_API_KEY}")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    // OkHttpClient specifically for the Astrologer API
    private fun createAstrologerClient(): OkHttpClient {
        return client.newBuilder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-RapidAPI-Key", "2b6d239c23msh3b1c18f70e0d6b7p18957ajsn192817ad6a58")
                    .addHeader("X-RapidAPI-Host", "astrologer.p.rapidapi.com")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    // Provide the HoroscopeApiService instance
    val horoscopeService: HoroscopeApiService by lazy {
        horoscopeRetrofit.create(HoroscopeApiService::class.java)
    }

    // Provide the StarLoveMatchApiService instance
    val starLoveMatchService: StarLoveMatchApiService by lazy {
        starLoveMatchRetrofit.create(StarLoveMatchApiService::class.java)
    }

    // Provide the GptApiService instance
    val gptService: GptApiService by lazy {
        gptRetrofit.create(GptApiService::class.java)
    }

    // Provide the AstrologerApiService instance
    val astrologerService: AstrologerApiService by lazy {
        astrologerRetrofit.create(AstrologerApiService::class.java)
    }
}