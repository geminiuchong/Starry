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
import com.example.group3_starry.ui.result.ResultFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.text.Editable
import android.text.TextWatcher
import androidx.navigation.findNavController

class MatchFragment : Fragment() {

    private var _binding: FragmentMatchBinding? = null
    private val binding get() = _binding!!

    private var userName: String? = null
    private var userDob: String? = null
    private var partner1Name: String? = null
    private var partner1Dob: String? = null
    private var partner2Name: String? = null // Optional
    private var partner2Dob: String? = null // Optional
    private var partner3Name: String? = null // Optional
    private var partner3Dob: String? = null // Optional

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up click listener for "View Match Result"
        binding.buttonViewMatchResult.setOnClickListener {
            if (validateInputs()) {
                navigateToResults()
            } else {
                Toast.makeText(requireContext(), "Please fill in your details and at least one partner's details", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up click listener for user details
        binding.plusIconUser.setOnClickListener {
            showInputDialogForUser()
        }

        // Set up click listener for partner details (all handled in one dialog)
        binding.plusIconPartner.setOnClickListener {
            showInputDialogForPartners()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showInputDialogForUser() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_person_detail, null)
        val inputName = dialogView.findViewById<EditText>(R.id.input_name)
        val inputDob = dialogView.findViewById<EditText>(R.id.input_dob)

        // Add TextWatcher for date formatting
        addDateInputFormatter(inputDob)

        AlertDialog.Builder(requireContext())
            .setTitle("Enter Your Details")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = inputName.text.toString()
                val dob = inputDob.text.toString()
                if (name.isNotBlank() && dob.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))) {
                    userName = name
                    userDob = dob
                    Toast.makeText(requireContext(), "Your details saved!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Please enter valid details", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showInputDialogForPartners() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_multiple_partners, null)
        val inputPartner1Name = dialogView.findViewById<EditText>(R.id.input_partner1_name)
        val inputPartner1Dob = dialogView.findViewById<EditText>(R.id.input_partner1_dob)
        val inputPartner2Name = dialogView.findViewById<EditText>(R.id.input_partner2_name)
        val inputPartner2Dob = dialogView.findViewById<EditText>(R.id.input_partner2_dob)
        val inputPartner3Name = dialogView.findViewById<EditText>(R.id.input_partner3_name)
        val inputPartner3Dob = dialogView.findViewById<EditText>(R.id.input_partner3_dob)

        // Add TextWatcher for date formatting
        addDateInputFormatter(inputPartner1Dob)
        addDateInputFormatter(inputPartner2Dob)
        addDateInputFormatter(inputPartner3Dob)

        AlertDialog.Builder(requireContext())
            .setTitle("Enter Partner Details")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                partner1Name = inputPartner1Name.text.toString()
                partner1Dob = inputPartner1Dob.text.toString()
                partner2Name = inputPartner2Name.text.toString().takeIf { it.isNotBlank() }
                partner2Dob = inputPartner2Dob.text.toString().takeIf { it.matches(Regex("\\d{2}/\\d{2}/\\d{4}")) }
                partner3Name = inputPartner3Name.text.toString().takeIf { it.isNotBlank() }
                partner3Dob = inputPartner3Dob.text.toString().takeIf { it.matches(Regex("\\d{2}/\\d{2}/\\d{4}")) }

                if (partner1Name.isNullOrBlank() || partner1Dob.isNullOrBlank()) {
                    Toast.makeText(requireContext(), "Please provide details for the first partner", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Partner details saved!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addDateInputFormatter(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
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

                editText.setText(formatted)
                editText.setSelection(formatted.length) // Move cursor to the end
                isFormatting = false
            }
        })
    }

    private fun validateInputs(): Boolean {
        return !userName.isNullOrBlank() &&
                !userDob.isNullOrBlank() &&
                !partner1Name.isNullOrBlank() &&
                !partner1Dob.isNullOrBlank()
    }

    private fun navigateToResults() {
        val bundle = Bundle().apply {
            putString("userName", userName)
            putString("userDob", userDob)
            putString("partner1Name", partner1Name)
            putString("partner1Dob", partner1Dob)
            putString("partner2Name", partner2Name) // Optional
            putString("partner2Dob", partner2Dob) // Optional
            putString("partner3Name", partner3Name) // Optional
            putString("partner3Dob", partner3Dob) // Optional
        }

        /**
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, ResultFragment::class.java, bundle)
            .addToBackStack(null)
            .commit() **/

        binding.root.findNavController().navigate(R.id.action_nav_match_to_resultFragment, bundle)


    }
}











