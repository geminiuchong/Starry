package com.example.group3_starry.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Base URLs
    private const val BASE_URL_HOROSCOPE = "https://horoscope-astrology.p.rapidapi.com/"
    private const val BASE_URL_SYNASTRY = "https://freeastrologyapi.com/api/"
    private const val BASE_URL_MATCH_MAKING = "https://json.apiastro.com/" // Match Making API
    private const val BASE_URL_GPT = "https://api.openai.com/"

    // Shared OkHttpClient with logging for debugging
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
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

    // Retrofit instance for the Synastry API
    private val synastryRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_SYNASTRY)
            .client(createSynastryClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Retrofit instance for the Match Making API
    private val matchMakingRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_MATCH_MAKING)
            .client(createMatchMakingClient())
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

    // OkHttpClient specifically for the Synastry API with Authorization header added
    private fun createSynastryClient(): OkHttpClient {
        return client.newBuilder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer UkwsWvPFvuHUAGlwK4Sg3l2DVdnZQWC79RB9Xjif") // Hardcoded API key
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    // OkHttpClient specifically for the Match Making API
    private fun createMatchMakingClient(): OkHttpClient {
        return client.newBuilder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("x-api-key", "UkwsWvPFvuHUAGlwK4Sg3l2DVdnZQWC79RB9Xjif") // Your actual API key
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

    // Provide the HoroscopeApiService instance
    val horoscopeService: HoroscopeApiService by lazy {
        horoscopeRetrofit.create(HoroscopeApiService::class.java)
    }

    // Provide the SynastryApiService instance
    val synastryService: HoroscopeApiService by lazy {
        synastryRetrofit.create(HoroscopeApiService::class.java)
    }

    // Provide the MatchMakingApiService instance
    val matchMakingService: HoroscopeApiService by lazy {
        matchMakingRetrofit.create(HoroscopeApiService::class.java)
    }

    // Provide the GptApiService instance
    val gptService: GptApiService by lazy {
        gptRetrofit.create(GptApiService::class.java)
    }
}



//object ApiClient {
//    // Base URLs
//    private const val BASE_URL_HOROSCOPE = "https://horoscope-astrology.p.rapidapi.com/"
//    private const val BASE_URL_GPT = "https://api.openai.com/"
//
//    // Shared OkHttpClient with logging for debugging
//    private val client = OkHttpClient.Builder()
//        .addInterceptor(HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        })
//        .build()
//
//    // Retrofit instance for the Horoscope API
//    private val horoscopeRetrofit: Retrofit by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL_HOROSCOPE)
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    // Retrofit instance for the GPT API
//    private val gptRetrofit: Retrofit by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL_GPT)
//            .client(
//                client.newBuilder()
//                    .addInterceptor { chain ->
//                        val request = chain.request().newBuilder()
//                            .addHeader("Authorization", "Bearer ${ApiKeyProvider.OPENAI_API_KEY}")
//                            .build()
//                        chain.proceed(request)
//                    }
//                    .build()
//            )
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    // Provide the HoroscopeApiService instance
//    val horoscopeService: HoroscopeApiService by lazy {
//        horoscopeRetrofit.create(HoroscopeApiService::class.java)
//    }
//
//    // Provide the GptApiService instance
//    val gptService: GptApiService by lazy {
//        gptRetrofit.create(GptApiService::class.java)
//    }
//}


//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//object ApiClient {
//    // Base URL for the API
//    private const val BASE_URL = "https://best-daily-astrology-and-horoscope-api.p.rapidapi.com/api/"
//
//    // Create a Retrofit instance with the base URL and Gson converter
//    private val retrofit: Retrofit by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON parsing
//            .build()
//    }
//
//    // Provide the HoroscopeApiService instance
//    val horoscopeService: HoroscopeApiService by lazy {
//        retrofit.create(HoroscopeApiService::class.java)
//    }
//}