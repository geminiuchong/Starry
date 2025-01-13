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
import com.example.group3_starry.R
import com.example.group3_starry.databinding.FragmentMatchBinding
import com.example.group3_starry.network.ApiClient
import com.example.group3_starry.network.SynastryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.text.TextWatcher
import android.text.Editable

class MatchFragment : Fragment() {

    private var _binding: FragmentMatchBinding? = null
    private val binding get() = _binding!!

    private var userName: String? = null
    private var userDob: String? = null
    private var partnerName: String? = null
    private var partnerDob: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up click listener for "View Match Result"
        binding.buttonViewMatchResult.setOnClickListener {
            if (!userName.isNullOrBlank() && !userDob.isNullOrBlank() && !partnerName.isNullOrBlank() && !partnerDob.isNullOrBlank()) {
                fetchSynastryScore(userName!!, userDob!!, partnerName!!, partnerDob!!)
            } else {
                Toast.makeText(requireContext(), "Please fill in all details", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up click listener for left "+" button
        binding.plusIconLeft.setOnClickListener {
            showInputDialog("Enter Your Details") { name, dob ->
                userName = name
                userDob = dob
                Toast.makeText(requireContext(), "Your details saved!", Toast.LENGTH_SHORT).show()
                binding.plusIconLeft.setImageResource(R.drawable.ic_add_circle)
            }
        }

        // Set up click listener for right "+" button
        binding.plusIconRight.setOnClickListener {
            showInputDialog("Enter Partner's Details") { name, dob ->
                partnerName = name
                partnerDob = dob
                Toast.makeText(requireContext(), "Partner's details saved!", Toast.LENGTH_SHORT).show()
                binding.plusIconRight.setImageResource(R.drawable.ic_add_circle)
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
        onDetailsEntered: (String, String) -> Unit
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_person_detail, null)
        val inputName = dialogView.findViewById<EditText>(R.id.input_name)
        val inputDob = dialogView.findViewById<EditText>(R.id.input_dob)

        // Add a TextWatcher to automatically insert slashes into the date field
        inputDob.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            private var previousLength = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                previousLength = s?.length ?: 0
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val text = s.toString().replace("/", "") // Remove any existing slashes
                val formatted = when {
                    text.length > 4 -> "${text.substring(0, 2)}/${text.substring(2, 4)}/${text.substring(4)}"
                    text.length > 2 -> "${text.substring(0, 2)}/${text.substring(2)}"
                    else -> text
                }

                inputDob.setText(formatted)
                inputDob.setSelection(formatted.length) // Move cursor to the end
                isFormatting = false
            }
        })

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = inputName.text.toString()
                val dob = inputDob.text.toString()
                if (name.isNotBlank() && dob.isNotBlank() && dob.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))) {
                    onDetailsEntered(name, dob)
                } else {
                    Toast.makeText(requireContext(), "Please enter a valid date (MM/DD/YYYY)", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun fetchSynastryScore(userName: String, userDob: String, partnerName: String, partnerDob: String) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiClient.starLoveMatchService.getSynastry(
                        name = userName,
                        dob = userDob,
                        partnerName = partnerName,
                        partnerDob = partnerDob
                    )
                }
                if (response.isSuccessful) {
                    displaySynastryResult(response.body())
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displaySynastryResult(result: List<SynastryResponse>?) {
        if (!result.isNullOrEmpty()) {
            val partnerResult = result.first() // Display the first partner's result
            val message = """
                Name: ${partnerResult.name}
                Love Compatibility: ${partnerResult.love}
                Intellectual Compatibility: ${partnerResult.intellectual}
                Physical Compatibility: ${partnerResult.physical}
                Strength of Connection: ${partnerResult.strength}
                Conflicts: ${partnerResult.bad}
                Overall Score: ${partnerResult.overall}
            """.trimIndent()

            AlertDialog.Builder(requireContext())
                .setTitle("Synastry Result")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        } else {
            Toast.makeText(requireContext(), "No synastry data found", Toast.LENGTH_SHORT).show()
        }
    }
}









