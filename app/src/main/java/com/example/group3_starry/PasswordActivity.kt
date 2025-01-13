package com.example.group3_starry

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PasswordActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        val email = intent.getStringExtra("email") // Retrieve the email passed from UsernameActivity
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val passwordToggle = findViewById<ImageView>(R.id.passwordVisibilityToggle)
        val nextButton = findViewById<ImageView>(R.id.nextButton)
        val backButton = findViewById<ImageView>(R.id.backButton)

        backButton.setOnClickListener {
            finish() // 返回上一页面
        }


        // Toggle password visibility
        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                // Show password
                passwordInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_visibility) // Update to visible icon
            } else {
                // Hide password
                passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_visibility_off) // Update to hidden icon
            }
            passwordInput.setSelection(passwordInput.text.length) // Keep cursor position
        }

        // Handle next button click
        nextButton.setOnClickListener {
            val password = passwordInput.text.toString()

            if (email.isNullOrEmpty()) {
                // Handle missing email (this should not happen if flow is correct)
                Toast.makeText(this, "Email is missing. Please start again.", Toast.LENGTH_SHORT).show()
                finish()
                return@setOnClickListener
            }

            // Validate password
            if (password.length >= 6) {
                // Proceed to SignUpActivity with email and password
                val intent = Intent(this, SignUpActivity::class.java)
                intent.putExtra("email", email) // Pass the email
                intent.putExtra("password", password) // Pass the password
                startActivity(intent)
            } else {
                // Show error for invalid password
                Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
