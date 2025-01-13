package com.example.group3_starry.network

import com.example.group3_starry.BuildConfig


object ApiKeyProvider {
    val OPENAI_API_KEY: String
        get() = BuildConfig.OPENAI_API_KEY
    
    const val FREE_ASTROLOGY_API_KEY = "vLLB7zqEMK23FDsDkdBD6aSoity5kasr86gizQzf"
}