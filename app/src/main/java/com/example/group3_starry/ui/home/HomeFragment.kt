package com.example.group3_starry.ui.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.group3_starry.R
import com.example.group3_starry.databinding.FragmentHomeBinding
import com.example.group3_starry.network.HoroscopeResponse
import com.example.group3_starry.utils.ZodiacUtils
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private var selectedTimeframe: String = "daily"
    private lateinit var progressBar: ProgressBar
    private var isTabInitialized = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        progressBar = binding.loadingIndicator

        // Fetch user information
        loadUserInfo()

        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        // Fetch user information when the fragment is resumed
        loadUserInfo()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadUserInfo() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val databaseReference = Firebase.database.reference

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            databaseReference.child("users").child(uid).get()
                .addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        val birthDateString = dataSnapshot.child("birthDate").value.toString()

                        // Parse the birth date
                        val formatter = DateTimeFormatter.ofPattern("d - M - yyyy")
                        val userBirthDate = LocalDate.parse(birthDateString, formatter) // Format: yyyy-MM-dd
                        val userZodiacSign = ZodiacUtils.getZodiacSign(userBirthDate)
                        val zodiacIcon = ZodiacUtils.getZodiacIcon(userZodiacSign)

                        // Update UI
                        if (!isTabInitialized) {
                            setupTabLayout(userZodiacSign)
                            isTabInitialized = true
                        }
                        // setupTabLayout(userZodiacSign)
                        binding.userSignIcon.setImageResource(zodiacIcon)

                        // Fetch horoscope data for the user's zodiac sign
                        homeViewModel.fetchHoroscope(userZodiacSign, selectedTimeframe)
                        observeHoroscope()
                    } else {
                        Toast.makeText(requireContext(), "User information not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to load user information", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "No logged-in user", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTabLayout(zodiacSign: String) {
        val tabLayout = binding.tabLayout

        // Add tabs for daily, weekly, monthly, yearly
        tabLayout.addTab(tabLayout.newTab().setText("Daily"))
        tabLayout.addTab(tabLayout.newTab().setText("Weekly"))
        tabLayout.addTab(tabLayout.newTab().setText("Monthly"))
        tabLayout.addTab(tabLayout.newTab().setText("Yearly"))

        // Set default tab
        tabLayout.selectTab(tabLayout.getTabAt(0))

        // Tab selection listener
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTimeframe = when (tab?.text) {
                    "Daily" -> "daily"
                    "Weekly" -> "weekly"
                    "Monthly" -> "monthly"
                    "Yearly" -> "yearly"
                    else -> "daily"
                }

                updateTitle(selectedTimeframe)

                // Show the ProgressBar while fetching data
                progressBar.visibility = View.VISIBLE

                // Fetch horoscope for the selected timeframe
                homeViewModel.fetchHoroscope(zodiacSign, selectedTimeframe)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Fetch initial data for the default tab
        homeViewModel.fetchHoroscope(zodiacSign, selectedTimeframe)
    }

    private fun updateTitle(timeframe: String) {
        val title = when (timeframe) {
            "daily" -> "Your Today"
            "weekly" -> "This Week"
            "monthly" -> "This Month"
            "yearly" -> "This Year"
            else -> "Your Today"
        }
        binding.titleOfHome.text = title
    }

    private fun observeHoroscope() {
        homeViewModel.horoscope.observe(viewLifecycleOwner) { horoscope ->
            progressBar.postDelayed({
                progressBar.visibility = View.GONE
            }, 500) // Delay for 500ms

            if (horoscope != null) {
                updateUI(horoscope)
            } else {
                Toast.makeText(requireContext(), "Failed to load horoscope", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(horoscope: HoroscopeResponse) {
        binding.mood.text = "Mood: ${horoscope.mood}"
        binding.compatibility.text = "Compatibility: ${horoscope.compatibility}"
        binding.textNumber.text = "Lucky Number: ${horoscope.luckyNumber}"
        binding.luckyTime.text = "Lucky Time: ${horoscope.luckyTime}"

        // Tip of the Day (Show dialog on click)
        binding.tipOfTheDay.text = horoscope.horoscope
        binding.tipCard.setOnClickListener {
            showFullContentDialog("Tip for the Day", horoscope.horoscope)
        }

        binding.loveDesc.text = horoscope.loveAndRelationship
        binding.loveDesc.maxLines = 2
        binding.loveCard.setOnClickListener {
            toggleTextExpansion(binding.loveDesc)
        }

        binding.healthDesc.text = horoscope.health
        binding.healthDesc.maxLines = 2
        binding.healthCard.setOnClickListener {
            toggleTextExpansion(binding.healthDesc)
        }

        binding.financeDesc.text = horoscope.moneyAndFinance
        binding.financeDesc.maxLines = 2
        binding.financeCard.setOnClickListener {
            toggleTextExpansion(binding.financeDesc)
        }

        binding.careerDesc.text = horoscope.career
        binding.careerDesc.maxLines = 2
        binding.careerCard.setOnClickListener {
            toggleTextExpansion(binding.careerDesc)
        }

        binding.globalDesc.text = horoscope.global
        binding.globalDesc.maxLines = 2
        binding.globalCard.setOnClickListener {
            toggleTextExpansion(binding.globalDesc)
        }
    }

    private fun toggleTextExpansion(textView: TextView) {
        if (textView.maxLines == 2) {
            textView.maxLines = Int.MAX_VALUE
        } else {
            textView.maxLines = 2
        }
    }

    private fun showFullContentDialog(title: String, content: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_full_content, null)

        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val dialogContent = dialogView.findViewById<TextView>(R.id.dialogContent)
        dialogTitle.text = title
        dialogContent.text = content

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

//class HomeFragment : Fragment() {
//
//    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var homeViewModel: HomeViewModel
//    private var selectedTimeframe: String = "daily"
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//
//        val userBirthDate = getUserBirthDate()
//        val userZodiacSign = ZodiacUtils.getZodiacSign(userBirthDate)
//        val zodiacIcon = ZodiacUtils.getZodiacIcon(userZodiacSign)
//
//        setupTabLayout(userZodiacSign)
//
//        // Fetch horoscope data based on the user's zodiac sign
//        // homeViewModel.fetchHoroscope(userZodiacSign, "weekly")
//
//        // Observe LiveData and update the UI
//        homeViewModel.horoscope.observe(viewLifecycleOwner) { horoscope ->
//            if (horoscope != null) {
//                updateUI(horoscope)
//            } else {
//                Toast.makeText(requireContext(), "Failed to load horoscope", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        return root
//    }
//
//    private fun setupTabLayout(zodiacSign: String) {
//        val tabLayout = binding.tabLayout
//
//        // Add tabs for daily, weekly, monthly, yearly
//        tabLayout.addTab(tabLayout.newTab().setText("Daily"))
//        tabLayout.addTab(tabLayout.newTab().setText("Weekly"))
//        tabLayout.addTab(tabLayout.newTab().setText("Monthly"))
//        tabLayout.addTab(tabLayout.newTab().setText("Yearly"))
//
//        // Set default tab
//        tabLayout.selectTab(tabLayout.getTabAt(0))
//
//        // Tab selection listener
//        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                selectedTimeframe = when (tab?.text) {
//                    "Daily" -> "daily"
//                    "Weekly" -> "weekly"
//                    "Monthly" -> "monthly"
//                    "Yearly" -> "yearly"
//                    else -> "daily"
//                }
//                // Fetch horoscope for the selected timeframe
//                homeViewModel.fetchHoroscope(zodiacSign, selectedTimeframe)
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {}
//            override fun onTabReselected(tab: TabLayout.Tab?) {}
//        })
//
//        // Fetch initial data for the default tab
//        homeViewModel.fetchHoroscope(zodiacSign, selectedTimeframe)
//    }
//
//    private fun updateUI(horoscope: HoroscopeResponse) {
////        binding.userSignIcon.setImageResource(getZodiacIcon(horoscope.translated.zodiacSign))
//        binding.userSignIcon.setImageResource(zodiacIcon)
//
//
//        binding.mood.text = "Mood: ${horoscope.mood}"
//        binding.compatibility.text = "Compatibility: ${horoscope.compatibility}"
//        binding.textNumber.text = "Lucky Number: ${horoscope.luckyNumber}"
//        binding.luckyTime.text = "Lucky Time: ${horoscope.luckyTime}"
//
//        // Tip of the Day (Show dialog on click)
//        binding.tipOfTheDay.text = horoscope.horoscope
//        binding.tipCard.setOnClickListener {
//            showFullContentDialog("Tip for the Day", horoscope.horoscope)
//        }
//
//        binding.loveDesc.text = horoscope.loveAndRelationship
//        binding.loveDesc.maxLines = 2
//        binding.loveCard.setOnClickListener {
//            toggleTextExpansion(binding.loveDesc)
//        }
//
//        binding.healthDesc.text = horoscope.health
//        binding.healthDesc.maxLines = 2
//        binding.healthCard.setOnClickListener {
//            toggleTextExpansion(binding.healthDesc)
//        }
//        binding.financeDesc.text = horoscope.moneyAndFinance
//        binding.financeDesc.maxLines = 2
//        binding.financeCard.setOnClickListener {
//            toggleTextExpansion(binding.financeDesc)
//        }
//
//        binding.careerDesc.text = horoscope.career
//        binding.careerDesc.maxLines = 2
//        binding.careerCard.setOnClickListener {
//            toggleTextExpansion(binding.careerDesc)
//        }
//
//        binding.globalDesc.text = horoscope.global
//        binding.globalDesc.maxLines = 2
//        binding.globalCard.setOnClickListener {
//            toggleTextExpansion(binding.globalDesc)
//        }
//    }
//
//    private fun showFullContentDialog(title: String, content: String) {
//        // Inflate the custom layout
//        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_full_content, null)
//
//        // Set the title and content
//        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
//        val dialogContent = dialogView.findViewById<TextView>(R.id.dialogContent)
//        dialogTitle.text = title
//        dialogContent.text = content
//
//        // Create and show the dialog
//        val dialog = AlertDialog.Builder(requireContext())
//            .setView(dialogView) // Set custom layout
//            .create()
//
//        // Make the dialog dismissible by tapping outside
//        dialog.setCancelable(true)
//        dialog.setCanceledOnTouchOutside(true)
//
//        dialog.show()
//    }
//
//    private fun toggleTextExpansion(textView: TextView) {
//        if (textView.maxLines == 2) {
//            // Expand the content
//            textView.maxLines = Int.MAX_VALUE
//        } else {
//            // Collapse the content
//            textView.maxLines = 2
//        }
//    }
//
//
//
//    private fun getUserBirthDate(): LocalDate {
//        // Replace this with your implementation to retrieve user's birth date
//        return  LocalDate.of(1999, 6, 15)// Example: June 15, 1995
//    }
//
//
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

//    private fun getZodiacSign(birthDate: LocalDate): String {
//        val day = birthDate.dayOfMonth
//        val month = birthDate.monthValue
//
//        return when (month) {
//            1 -> if (day < 20) "capricorn" else "aquarius"
//            2 -> if (day < 19) "aquarius" else "pisces"
//            3 -> if (day < 21) "pisces" else "aries"
//            4 -> if (day < 20) "aries" else "taurus"
//            5 -> if (day < 21) "taurus" else "gemini"
//            6 -> if (day < 21) "gemini" else "cancer"
//            7 -> if (day < 23) "cancer" else "leo"
//            8 -> if (day < 23) "leo" else "virgo"
//            9 -> if (day < 23) "virgo" else "libra"
//            10 -> if (day < 23) "libra" else "scorpio"
//            11 -> if (day < 22) "scorpio" else "sagittarius"
//            12 -> if (day < 22) "sagittarius" else "capricorn"
//            else -> "aries"
//        }
//    }

// Get Zodiac Icon Based on Sunsign
//    private fun getZodiacIcon(sign: String): Int {
//        return when (sign.lowercase()) {
//            "aries" -> R.drawable.aries
//            "taurus" -> R.drawable.taurus
//            "gemini" -> R.drawable.gemini
//            "cancer" -> R.drawable.cancer
//            "leo" -> R.drawable.leo
//            "virgo" -> R.drawable.virgo
//            "libra" -> R.drawable.libra
//            "scorpio" -> R.drawable.scorpio
//            "sagittarius" -> R.drawable.sagittarius
//            "capricorn" -> R.drawable.capricorn
//            "aquarius" -> R.drawable.aquarius
//            "pisces" -> R.drawable.pisces
//            else -> R.mipmap.ic_launcher_starry
//        }
//    }

//class HomeFragment : Fragment() {
//
//    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var homeViewModel: HomeViewModel
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        // Set up the Spinner with zodiac signs
//        val zodiacSigns = listOf("aries", "taurus", "gemini", "cancer", "leo", "virgo", "libra", "scorpio", "sagittarius", "capricorn", "aquarius", "pisces")
//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, zodiacSigns)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.zodiacSpinner.adapter = adapter
//
//        // Observe the horoscope data and update the UI
//        homeViewModel.horoscope.observe(viewLifecycleOwner) { horoscope ->
//            binding.planetsTodayTitle.text = getString(R.string.planets_today)
//            binding.tipOfTheDay.text = horoscope.horoscope
//            binding.textNumber.text = "${horoscope.lucky_number}"
////            binding.healthTitle.text = getString(R.string.health)
////            binding.healthDesc.text = horoscope.areas?.getOrNull(1)?.desc ?: getString(R.string.no_data)
////            binding.financeTitle.text = getString(R.string.finance)
////            binding.financeDesc.text = horoscope.areas?.getOrNull(2)?.desc ?: getString(R.string.no_data)
////            binding.relationshipTitle.text = getString(R.string.relationship)
////            binding.relationshipDesc.text = horoscope.areas?.getOrNull(3)?.desc ?: getString(R.string.no_data)
//        }
//
//        // Set the Spinner's OnItemSelectedListener
//        binding.zodiacSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                val selectedSign = zodiacSigns[position]
//                homeViewModel.fetchDailyHoroscope(sunsign = selectedSign)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // Optional: handle if no selection is made
//            }
//        }
//
//        return root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}



//class HomeFragment : Fragment() {
//
//    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var homeViewModel: HomeViewModel
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        // Set up the Spinner with zodiac signs
//        val zodiacSigns = listOf("aries", "taurus", "gemini", "cancer", "leo", "virgo", "libra", "scorpio", "sagittarius", "capricorn", "aquarius", "pisces")
//        val spinner = binding.zodiacSpinner
//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, zodiacSigns)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinner.adapter = adapter
//
//        // Observe the horoscope data and update the UI
//        homeViewModel.horoscope.observe(viewLifecycleOwner) { horoscope ->
//            binding.textHome.text = horoscope.horoscope
//            binding.textColor.text = "Lucky Colors: ${horoscope.color}"
//            binding.textNumber.text = "Lucky Number: ${horoscope.lucky_number}"
//            binding.textStrength.text = "Mood: ${horoscope.mood}"
//            binding.textWeakness.text = "Lucky Time: ${horoscope.lucky_time}"
//
//            // Check if areas are not null and not empty before accessing
//            if (!horoscope.areas.isNullOrEmpty()) {
//                binding.textArea1Title.text = horoscope.areas[0].title
//                binding.textArea1Desc.text = horoscope.areas[0].desc
//                // Repeat for other areas if needed
//            } else {
//                // Hide or clear the area fields if `areas` is null or empty
//                binding.textArea1Title.text = ""
//                binding.textArea1Desc.text = "No detailed areas available."
//            }
//        }
//
//
//        // Set the Spinner's OnItemSelectedListener
//        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                val selectedSign = zodiacSigns[position]
//                homeViewModel.fetchDailyHoroscope(sunsign = selectedSign)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // Optional: handle if no selection is made
//            }
//        }
//
//        return root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

//class HomeFragment : Fragment() {
//
//    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var homeViewModel: HomeViewModel
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        // Set up the Spinner with zodiac signs
//        val zodiacSigns = listOf("aries", "taurus", "gemini", "cancer", "leo", "virgo", "libra", "scorpio", "sagittarius", "capricorn", "aquarius", "pisces")
//        val spinner = binding.zodiacSpinner
//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, zodiacSigns)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinner.adapter = adapter
//
//        // Observe the horoscope data and update the UI
//        homeViewModel.horoscope.observe(viewLifecycleOwner) { horoscope ->
//            binding.textHome.text = horoscope.prediction
//            binding.textColor.text = "Lucky Colors: ${horoscope.color}"
//            binding.textNumber.text = "Lucky Numbers: ${horoscope.number}"
//            binding.textStrength.text = "Strengths: ${horoscope.strength}"
//            binding.textWeakness.text = "Weaknesses: ${horoscope.weakness}"
//        }
//
//        // Set the Spinner's OnItemSelectedListener
//        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                val selectedSign = zodiacSigns[position]
//                homeViewModel.fetchDailyHoroscope(selectedSign)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // Optional: handle if no selection is made
//            }
//        }
//
//        return root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}