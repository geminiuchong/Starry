package com.example.group3_starry

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var birthPlaceInput: EditText
    private var email: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Retrieve email and password from Intent
        email = intent.getStringExtra("email")
        password = intent.getStringExtra("password")

        // Initialize Google Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyCNIWM0uM3jLqOG-eP8_ouuV54I76gn_1I")
        }

        val birthDateInput = findViewById<EditText>(R.id.birthDateInput)
        val birthDateIcon = findViewById<ImageView>(R.id.birthDateIcon)
        val birthTimeInput = findViewById<EditText>(R.id.birthTimeInput)
        val birthTimeIcon = findViewById<ImageView>(R.id.birthTimeIcon)
        birthPlaceInput = findViewById(R.id.birthPlaceInput)
        val birthPlaceIcon = findViewById<ImageView>(R.id.birthPlaceIcon)
        val nextButton = findViewById<ImageView>(R.id.nextButton)
        val backButton = findViewById<ImageView>(R.id.backButton)

        backButton.setOnClickListener {
            finish() // 返回上一页面
        }


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

        // Place picker logic using Google Places API
        birthPlaceIcon.setOnClickListener {
            val fields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS
            )
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        // Finalize registration
        nextButton.setOnClickListener {
            if (birthDateInput.text.isNullOrEmpty() ||
                birthTimeInput.text.isNullOrEmpty() ||
                birthPlaceInput.text.isNullOrEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            } else {
                registerUser()
            }
        }
    }

    private fun registerUser() {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            return
        }

        val birthDate = findViewById<EditText>(R.id.birthDateInput).text.toString()
        val birthTime = findViewById<EditText>(R.id.birthTimeInput).text.toString()
        val birthPlace = findViewById<EditText>(R.id.birthPlaceInput).text.toString()

        auth.createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    saveUserDataToDatabase(userId, email!!, birthDate, birthTime, birthPlace)
                } else {
                    val errorMessage = task.exception?.message ?: "Unknown error occurred"
                    Toast.makeText(this, "Registration failed: $errorMessage", Toast.LENGTH_SHORT).show()
                    Log.e("SignUpActivity", "Registration error: $errorMessage")
                }
            }
    }

    private fun saveUserDataToDatabase(userId: String, email: String, birthDate: String, birthTime: String, birthPlace: String) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("users").child(userId)

        val userData = mapOf(
            "email" to email,
            "birthDate" to birthDate,
            "birthTime" to birthTime,
            "birthPlace" to birthPlace
        )

        userRef.setValue(userData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "User data saved successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to save user data: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                Log.e("SignUpActivity", "Database save error: ${task.exception?.message}")
            }
        }
    }

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
