package com.example.group3_starry.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.group3_starry.network.HoroscopeRepository
import com.example.group3_starry.network.HoroscopeResponse
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = HoroscopeRepository()

    // LiveData for the horoscope response
    private val _horoscope = MutableLiveData<HoroscopeResponse?>()
    val horoscope: LiveData<HoroscopeResponse?> get() = _horoscope

    // Fetch horoscope for a specific zodiac sign
    fun fetchHoroscope(sign: String, timeframe: String) {
        viewModelScope.launch {
            val result = repository.getHoroscope(sign, timeframe)
            _horoscope.value = result
        }
    }
}



//class HomeViewModel : ViewModel() {
//
//    private val repository = HoroscopeRepository()
//
//    // LiveData for the horoscope response
//    private val _horoscope = MutableLiveData<HoroscopeResponse>()
//    val horoscope: LiveData<HoroscopeResponse> get() = _horoscope
//
//    // Fetch daily horoscope for a specific zodiac sign (e.g., "leo")
//    fun fetchDailyHoroscope(day: String = "today", sunsign: String) {
//        viewModelScope.launch {
//            val result = repository.getDailyHoroscope(day, sunsign)
//            result?.let {
//                _horoscope.value = it
//            }
//        }
//    }
//}