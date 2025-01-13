package com.example.group3_starry.ui.tarot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TarotViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is tarotViewModel Fragment"
    }
    val text: LiveData<String> = _text
}