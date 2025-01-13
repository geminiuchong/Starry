package com.example.group3_starry.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.group3_starry.network.ProfileChartData
import com.example.group3_starry.utils.AstrologyInterpretations
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    // User basic info
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _birthInfo = MutableLiveData<String>()
    val birthInfo: LiveData<String> = _birthInfo

    // Zodiac signs info
    private val _signInfo = MutableLiveData<Triple<String, String, String>>() // (Sun, Moon, Ascendant)
    val signInfo: LiveData<Triple<String, String, String>> = _signInfo

    // Chart data for display
    private val _chartData = MutableLiveData<List<ProfileChartData>>()
    val chartData: LiveData<List<ProfileChartData>> = _chartData

    // UI state
    private val _isChartSelected = MutableLiveData(true)
    val isChartSelected: LiveData<Boolean> = _isChartSelected

    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Error handling
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Load sample data for testing
    fun loadSampleData() {
        _userName.value = "Martin Lee"
        _birthInfo.value = "1 August 1998, 2 AM"
        _signInfo.value = Triple("☉ Leo", "☽ Virgo", "ASC Libra")

        _chartData.value = listOf(
            ProfileChartData(
                sign = "Libra",
                signSymbol = "♎",
                planetSymbol = "ASC",
                house = "1",
                analysis = AstrologyInterpretations.getPlanetInSignInterpretation("ASC", "Libra", "1")
            ),
            ProfileChartData(
                sign = "Sagittarius",
                signSymbol = "♐",
                planetSymbol = "♇",
                house = "2",
                analysis = AstrologyInterpretations.getPlanetInSignInterpretation("♇", "Sagittarius", "2")
            ),
            ProfileChartData(
                sign = "Aquarius",
                signSymbol = "♒",
                planetSymbol = "⛢",
                house = "4",
                analysis = AstrologyInterpretations.getPlanetInSignInterpretation("⛢", "Aquarius", "4")
            ),
            ProfileChartData(
                sign = "Pisces",
                signSymbol = "♓",
                planetSymbol = "♆",
                house = "6",
                analysis = AstrologyInterpretations.getPlanetInSignInterpretation("♆", "Pisces", "6")
            ),
            ProfileChartData(
                sign = "Taurus",
                signSymbol = "♉",
                planetSymbol = "♃",
                house = "7",
                analysis = AstrologyInterpretations.getPlanetInSignInterpretation("♃", "Taurus", "7")
            ),
            ProfileChartData(
                sign = "Cancer",
                signSymbol = "♋",
                planetSymbol = "♄",
                house = "9",
                analysis = AstrologyInterpretations.getPlanetInSignInterpretation("♄", "Cancer", "9")
            ),
            ProfileChartData(
                sign = "Leo",
                signSymbol = "♌",
                planetSymbol = "☉",
                house = "10",
                analysis = AstrologyInterpretations.getPlanetInSignInterpretation("☉", "Leo", "10")
            ),
            ProfileChartData(
                sign = "Virgo",
                signSymbol = "♍",
                planetSymbol = "☽",
                house = "2",
                analysis = AstrologyInterpretations.getPlanetInSignInterpretation("☽", "Virgo", "2")
            )
        ).sortedBy { it.house.toIntOrNull() ?: Int.MAX_VALUE }
    }

    // Load actual birth chart data
    fun loadBirthChart(
        name: String,
        dateString: String,    // Format: YYYY-MM-DD
        timeString: String,    // Format: HH:mm
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // TODO: Replace with actual API call when available
                loadSampleData()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Handle tab selection
    fun setChartSelected(selected: Boolean) {
        _isChartSelected.value = selected
    }

    // Handle settings click
    fun onSettingsClick() {
        // TODO: Implement settings navigation
    }

    // Handle pie chart view click
    fun onViewPieChartClick() {
        // TODO: Implement pie chart view navigation
    }
}