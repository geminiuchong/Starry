package com.example.group3_starry.ui.match

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import com.example.group3_starry.R
import com.example.group3_starry.databinding.FragmentMatchBinding
import com.example.group3_starry.network.ApiClient
import com.example.group3_starry.network.BirthDetails
import com.example.group3_starry.network.MatchConfig
import com.example.group3_starry.network.MatchMakingRequest
import com.example.group3_starry.network.MatchMakingResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MatchFragment : Fragment() {

    private var _binding: FragmentMatchBinding? = null
    private val binding get() = _binding!!

    private var user1Details: BirthDetails? = null
    private var user2Details: BirthDetails? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up ViewModel
        val matchViewModel = ViewModelProvider(this).get(MatchViewModel::class.java)
        matchViewModel.text.observe(viewLifecycleOwner) {
            binding.textTitle.text = it
        }

        // Set up click listener for "View Match Result"
        binding.buttonViewMatchResult.setOnClickListener {
            if (user1Details != null && user2Details != null) {
                fetchMatchMakingScore(user1Details!!, user2Details!!)
            } else {
                Toast.makeText(requireContext(), "Please fill both users' details", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up click listener for left "+" button
        binding.plusIconLeft.setOnClickListener {
            showInputDialog("Enter Your Details") { year, month, date, hours, minutes ->
                user1Details = BirthDetails(
                    year = year,
                    month = month,
                    date = date,
                    hours = hours,
                    minutes = minutes
                )
                Toast.makeText(requireContext(), "User 1 details saved!", Toast.LENGTH_SHORT).show()
                binding.plusIconLeft.setImageResource(R.drawable.ic_add_circle) // Update icon
            }
        }

        // Set up click listener for right "+" button
        binding.plusIconRight.setOnClickListener {
            showInputDialog("Enter Other Person's Details") { year, month, date, hours, minutes ->
                user2Details = BirthDetails(
                    year = year,
                    month = month,
                    date = date,
                    hours = hours,
                    minutes = minutes
                )
                Toast.makeText(requireContext(), "User 2 details saved!", Toast.LENGTH_SHORT).show()
                binding.plusIconRight.setImageResource(R.drawable.ic_add_circle) // Update icon
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showInputDialog(
        title: String,
        onDetailsEntered: (Int, Int, Int, Int, Int) -> Unit
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_birth_details, null)
        val inputYear = dialogView.findViewById<EditText>(R.id.input_year)
        val inputMonth = dialogView.findViewById<EditText>(R.id.input_month)
        val inputDate = dialogView.findViewById<EditText>(R.id.input_date)
        val inputHours = dialogView.findViewById<EditText>(R.id.input_hours)
        val inputMinutes = dialogView.findViewById<EditText>(R.id.input_minutes)

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                try {
                    val year = inputYear.text.toString().toInt()
                    val month = inputMonth.text.toString().toInt()
                    val date = inputDate.text.toString().toInt()
                    val hours = inputHours.text.toString().toInt()
                    val minutes = inputMinutes.text.toString().toInt()

                    onDetailsEntered(year, month, date, hours, minutes)
                } catch (e: NumberFormatException) {
                    Toast.makeText(requireContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun fetchMatchMakingScore(female: BirthDetails, male: BirthDetails) {
        val config = MatchConfig(language = "en") // Set language to English
        val requestBody = MatchMakingRequest(female, male, config)

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiClient.matchMakingService.getMatchMakingScore(requestBody)
                }
                if (response.isSuccessful) {
                    displayMatchMakingResult(response.body())
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayMatchMakingResult(result: MatchMakingResponse?) {
        if (result != null) {
            val output = result.output
            if (output != null) {
                val message = """
                    Total Score: ${output.total_score} / ${output.out_of}
                    
                    Varna Koota:
                    Bride Varnam: ${output.varna_kootam?.bride?.varnam_name}
                    Groom Varnam: ${output.varna_kootam?.groom?.varnam_name}
                    Score: ${output.varna_kootam?.score} / ${output.varna_kootam?.out_of}
                    
                    Yoni Koota:
                    Bride Yoni: ${output.yoni_kootam?.bride?.yoni}
                    Groom Yoni: ${output.yoni_kootam?.groom?.yoni}
                    Score: ${output.yoni_kootam?.score} / ${output.yoni_kootam?.out_of}
                    
                    Graha Maitri Koota:
                    Score: ${output.graha_maitri_kootam?.score} / ${output.graha_maitri_kootam?.out_of}
                """.trimIndent()

                AlertDialog.Builder(requireContext())
                    .setTitle("Match Making Result")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                Toast.makeText(requireContext(), "No output received from API", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Failed to fetch match making result", Toast.LENGTH_SHORT).show()
        }
    }
}







