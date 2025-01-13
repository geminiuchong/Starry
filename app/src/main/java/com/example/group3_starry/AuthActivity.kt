// AuthActivity.kt
package com.example.group3_starry

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.group3_starry.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Group3_Starry)
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignIn.setOnClickListener { showSignInDialog() }
        binding.btnSignUp.setOnClickListener { showSignUpDialog() }
    }

    private fun showSignInDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_sign_in, null)
        val usernameInput = dialogView.findViewById<EditText>(R.id.etUsername)
        val passwordInput = dialogView.findViewById<EditText>(R.id.etPassword)

        AlertDialog.Builder(this)
            .setTitle("Sign In")
            .setView(dialogView)
            .setPositiveButton("Sign In") { _, _ ->
                val username = usernameInput.text.toString()
                val password = passwordInput.text.toString()
                if (authenticateUser(username, password)) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Incorrect username or password", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSignUpDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_sign_up, null)
        val usernameInput = dialogView.findViewById<EditText>(R.id.etUsername)
        val passwordInput = dialogView.findViewById<EditText>(R.id.etPassword)

        AlertDialog.Builder(this)
            .setTitle("Sign Up")
            .setView(dialogView)
            .setPositiveButton("Sign Up") { _, _ ->
                val username = usernameInput.text.toString()
                val password = passwordInput.text.toString()
                saveUserCredentials(username, password)
                Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun authenticateUser(username: String, password: String): Boolean {
        val sharedPref = getSharedPreferences("user_credentials", Context.MODE_PRIVATE)
        val savedPassword = sharedPref.getString(username, null)
        return savedPassword == password
    }

    private fun saveUserCredentials(username: String, password: String) {
        val sharedPref = getSharedPreferences("user_credentials", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(username, password)
            apply()
        }
    }
}
