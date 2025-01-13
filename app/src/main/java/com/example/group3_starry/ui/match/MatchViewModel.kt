package com.example.group3_starry.ui.match

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MatchViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Synastry"
    }
    val text: LiveData<String> = _text
}
