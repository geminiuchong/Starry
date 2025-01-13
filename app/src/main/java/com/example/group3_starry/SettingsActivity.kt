package com.example.group3_starry

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var birthPlaceInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize Google Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyCNIWM0uM3jLqOG-eP8_ouuV54I76gn_1I")
        }

        val birthDateInput = findViewById<EditText>(R.id.settingBirthDateInput)
        val birthDateIcon = findViewById<ImageView>(R.id.settingBirthDateIcon)
        val birthTimeInput = findViewById<EditText>(R.id.settingBirthTimeInput)
        val birthTimeIcon = findViewById<ImageView>(R.id.settingBirthTimeIcon)
        birthPlaceInput = findViewById(R.id.settingBirthPlaceInput)
        val birthPlaceIcon = findViewById<ImageView>(R.id.settingBirthPlaceIcon)
        val saveButton = findViewById<ImageView>(R.id.saveButton)
        val cancelButton = findViewById<ImageView>(R.id.cancelButton)

        // Populate fields with existing user data
        loadUserInfo(birthDateInput, birthTimeInput, birthPlaceInput)

        // Date picker logic
        birthDateIcon.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                birthDateInput.setText("$selectedDay - ${selectedMonth + 1} - $selectedYear")
            }, year, month, day)
            datePicker.show()
        }

        // Time picker logic
        birthTimeIcon.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                birthTimeInput.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
            }, hour, minute, true)
            timePicker.show()
        }

        birthPlaceIcon.setOnClickListener {
            if (!Places.isInitialized()) {
                Toast.makeText(this, "Google Places API is not initialized.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS
            )
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }



        // Save updated information
        saveButton.setOnClickListener {
            if (birthDateInput.text.isNullOrEmpty() ||
                birthTimeInput.text.isNullOrEmpty() ||
                birthPlaceInput.text.isNullOrEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            } else {
                saveUpdatedUserInfo(
                    birthDateInput.text.toString(),
                    birthTimeInput.text.toString(),
                    birthPlaceInput.text.toString()
                )
            }
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun loadUserInfo(birthDateInput: EditText, birthTimeInput: EditText, birthPlaceInput: EditText) {
        val databaseReference = Firebase.database.reference
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            databaseReference.child("users").child(uid).get()
                .addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        birthDateInput.setText(dataSnapshot.child("birthDate").value.toString())
                        birthTimeInput.setText(dataSnapshot.child("birthTime").value.toString())
                        birthPlaceInput.setText(dataSnapshot.child("birthPlace").value.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load user information.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveUpdatedUserInfo(birthDate: String, birthTime: String, birthPlace: String) {
        val databaseReference = Firebase.database.reference
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val userData = mapOf(
                "birthDate" to birthDate,
                "birthTime" to birthTime,
                "birthPlace" to birthPlace
            )
            databaseReference.child("users").child(uid).updateChildren(userData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "User information updated.", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to update information.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    // Handle result in onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    birthPlaceInput.setText(place.address)
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Toast.makeText(this, "Error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
                }
                RESULT_CANCELED -> {}
            }
        }
    }

    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }
}
