package com.example.group3_starry.ui.profile

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.group3_starry.network.FreeAstrologyRepository
import com.example.group3_starry.network.ProfileChartData
import com.example.group3_starry.utils.AstrologyInterpretations
import com.example.group3_starry.utils.LocationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import androidx.lifecycle.AndroidViewModel
import android.graphics.Color


class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.database.reference
    private val freeAstrologyRepository = FreeAstrologyRepository()
    private val locationUtils = LocationUtils(application)

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _birthInfo = MutableLiveData<String>()
    val birthInfo: LiveData<String> = _birthInfo

    private val _signInfo = MutableLiveData<Triple<String, String, String>>()
    val signInfo: LiveData<Triple<String, String, String>> = _signInfo

    private val _chartData = MutableLiveData<List<ProfileChartData>>()
    val chartData: LiveData<List<ProfileChartData>> = _chartData

    private val _pieChartData = MutableLiveData<List<PieChartView.PlanetPosition>>()
    val pieChartData: LiveData<List<PieChartView.PlanetPosition>> = _pieChartData

    private val _isChartSelected = MutableLiveData(true)
    val isChartSelected: LiveData<Boolean> = _isChartSelected

    private val _showPieChart = MutableLiveData(false)
    val showPieChart: LiveData<Boolean> = _showPieChart

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadData() {
        loadUserData()
    }

    private fun loadUserData() {
        _isLoading.value = true
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val email = user.email ?: ""
            val username = email.substringBefore("@")
            _userName.value = username

            database.child("users").child(user.uid).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val birthDate = snapshot.child("birthDate").value.toString()
                        val birthTime = snapshot.child("birthTime").value.toString()
                        val birthPlace = snapshot.child("birthPlace").value.toString()
                        _birthInfo.value = "$birthDate, $birthTime"

                        viewModelScope.launch {
                            try {
                                val locationInfo = locationUtils.getLocationInfo(birthPlace)
                                    ?: throw Exception("Unable to get location info")

                                val result = freeAstrologyRepository.getBirthChart(
                                    birthDate = birthDate,
                                    birthTime = birthTime,
                                    latitude = locationInfo.latitude,
                                    longitude = locationInfo.longitude,
                                    timezone = locationInfo.timezone
                                )

                                result.onSuccess { data ->
                                    _chartData.value = data
                                    updateSignInfo(data)
                                    updatePieChartData(data)
                                }.onFailure { exception ->
                                    _error.value = "Failed to get chart data: ${exception.message}"
                                    loadSampleData()
                                }
                            } catch (e: Exception) {
                                _error.value = "Error calculating positions: ${e.message}"
                                loadSampleData()
                            }
                        }
                    }
                    _isLoading.value = false
                }
                .addOnFailureListener {
                    _error.value = "Failed to load user data"
                    _isLoading.value = false
                    loadSampleData()
                }
        } ?: run {
            _error.value = "No user logged in"
            _isLoading.value = false
            loadSampleData()
        }
    }

    private fun updateSignInfo(chartData: List<ProfileChartData>) {
        val sun = chartData.find { it.planetSymbol == "☉" }?.let { "☉ ${it.sign}" } ?: "☉ Unknown"
        val moon = chartData.find { it.planetSymbol == "☽" }?.let { "☽ ${it.sign}" } ?: "☽ Unknown"
        val asc = chartData.find { it.planetSymbol == "ASC" }?.let { "ASC ${it.sign}" } ?: "ASC Unknown"
        _signInfo.value = Triple(sun, moon, asc)
    }

    private fun updatePieChartData(chartData: List<ProfileChartData>) {
        val positions = chartData.map { data ->
            PieChartView.PlanetPosition(
                symbol = data.planetSymbol,
                degree = getPlanetDegree(data.sign, data.house.toInt()),
                color = getPlanetColor(data.planetSymbol)
            )
        }
        _pieChartData.value = positions
    }

    private fun getPlanetDegree(sign: String, house: Int): Float {
        val baseAngle = (house - 1) * 30f
        val signOffset = getSignOffset(sign)
        return baseAngle + signOffset
    }

    private fun getSignOffset(sign: String): Float = when(sign) {
        "Aries" -> 0f
        "Taurus" -> 30f
        "Gemini" -> 60f
        "Cancer" -> 90f
        "Leo" -> 120f
        "Virgo" -> 150f
        "Libra" -> 180f
        "Scorpio" -> 210f
        "Sagittarius" -> 240f
        "Capricorn" -> 270f
        "Aquarius" -> 300f
        "Pisces" -> 330f
        else -> 0f
    }

    private fun getPlanetColor(symbol: String): Int = when(symbol) {
        "☉" -> Color.parseColor("#FFD700")  // Sun - Gold
        "☽" -> Color.parseColor("#C0C0C0")  // Moon - Silver
        "☿" -> Color.parseColor("#4169E1")  // Mercury - Blue
        "♀" -> Color.parseColor("#FF69B4")  // Venus - Pink
        "♂" -> Color.parseColor("#FF0000")  // Mars - Red
        "♃" -> Color.parseColor("#800080")  // Jupiter - Purple
        "♄" -> Color.parseColor("#696969")  // Saturn - Gray
        "⛢" -> Color.parseColor("#00FFFF")  // Uranus - Cyan
        "♆" -> Color.parseColor("#4B0082")  // Neptune - Indigo
        "♇" -> Color.parseColor("#8B4513")  // Pluto - Brown
        "ASC" -> Color.parseColor("#32CD32") // Ascendant - Lime Green
        else -> Color.BLACK
    }

    fun setChartSelected(selected: Boolean) {
        _isChartSelected.value = selected
    }

    private fun loadSampleData() {
        _userName.value = "User"
        _birthInfo.value = "1 August 1998, 2 AM"
        _signInfo.value = Triple("☉ Leo", "☽ Virgo", "ASC Libra")

        _chartData.value = listOf(
            ProfileChartData(
                sign = "Libra",
                signSymbol = "♎",
                planetSymbol = "ASC",
                house = "1",
                analysis = AstrologyInterpretations.getPlanetInSignInterpretation("ASC", "Libra", "1")
            )
        )
    }

    fun onViewPieChartClick() {
        _showPieChart.value = !(_showPieChart.value ?: false)
    }
}