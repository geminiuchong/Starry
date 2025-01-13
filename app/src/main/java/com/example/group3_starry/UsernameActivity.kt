package com.example.group3_starry

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UsernameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_username)

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val nextButton = findViewById<ImageView>(R.id.nextButton)
        val backButton = findViewById<ImageView>(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        // Handle next button click
        nextButton.setOnClickListener {
            val email = emailInput.text.toString().trim()

            // Validate email
            if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // Proceed to PasswordActivity
                val intent = Intent(this, PasswordActivity::class.java)
                intent.putExtra("email", email) // Pass the email to the next activity
                startActivity(intent)
            } else {
                // Show error for invalid email
                Toast.makeText(this, "Invalid email address. Please enter a valid email.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
