package com.example.group3_starry

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LogInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users") // Reference to 'users' node in Firebase

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val passwordToggle = findViewById<ImageView>(R.id.passwordVisibilityToggle)
        val signInButton = findViewById<Button>(R.id.signInButton)
        val backButton = findViewById<ImageView>(R.id.backButton)

        backButton.setOnClickListener {
            finish() // 返回上一页面
        }


        // Toggle password visibility
        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_visibility)
            } else {
                passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_visibility_off)
            }
            passwordInput.setSelection(passwordInput.text.length)
        }

        // Login logic
        signInButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (email.isBlank()) {
                emailInput.error = "Email is required"
                emailInput.requestFocus()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.error = "Invalid email format"
                emailInput.requestFocus()
                return@setOnClickListener
            }

            if (password.isBlank()) {
                passwordInput.error = "Password is required"
                passwordInput.requestFocus()
                return@setOnClickListener
            }

            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Logging in...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    progressDialog.dismiss()
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            // Fetch user data from Firebase Database
                            fetchUserData(userId)
                        } else {
                            Toast.makeText(this, "Login successful, but user ID is null.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val exception = task.exception
                        when (exception) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                            }
                            is FirebaseAuthInvalidUserException -> {
                                Toast.makeText(this, "No account found with this email", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(this, "Login failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
        }
    }

    private fun fetchUserData(userId: String) {
        database.child(userId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot.exists()) {
                    // Retrieve user data
                    val birthDate = snapshot.child("birthDate").getValue(String::class.java)
                    val birthTime = snapshot.child("birthTime").getValue(String::class.java)
                    val birthPlace = snapshot.child("birthPlace").getValue(String::class.java)

                    // Pass the retrieved data to MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("birthDate", birthDate)
                    intent.putExtra("birthTime", birthTime)
                    intent.putExtra("birthPlace", birthPlace)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "No user data found.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Failed to retrieve user data: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
