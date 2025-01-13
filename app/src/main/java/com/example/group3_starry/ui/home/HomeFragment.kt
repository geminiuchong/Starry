package com.example.group3_starry.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.group3_starry.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up the Spinner with zodiac signs
        val zodiacSigns = listOf("aries", "taurus", "gemini", "cancer", "leo", "virgo", "libra", "scorpio", "sagittarius", "capricorn", "aquarius", "pisces")
        val spinner = binding.zodiacSpinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, zodiacSigns)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Observe the horoscope data and update the UI
        homeViewModel.horoscope.observe(viewLifecycleOwner) { horoscope ->
            binding.textHome.text = horoscope.prediction
            binding.textColor.text = "Lucky Colors: ${horoscope.color}"
            binding.textNumber.text = "Lucky Numbers: ${horoscope.number}"
            binding.textStrength.text = "Strengths: ${horoscope.strength}"
            binding.textWeakness.text = "Weaknesses: ${horoscope.weakness}"
        }

        // Set the Spinner's OnItemSelectedListener
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedSign = zodiacSigns[position]
                homeViewModel.fetchDailyHoroscope(selectedSign)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional: handle if no selection is made
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}