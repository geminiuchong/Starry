package com.example.group3_starry.network

import com.example.group3_starry.BuildConfig


object ApiKeyProvider {
    val OPENAI_API_KEY: String
        get() = BuildConfig.OPENAI_API_KEY
}