package com.example.group3_starry

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.group3_starry.network.ApiClient
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.group3_starry.databinding.ActivityMainBinding
import com.example.group3_starry.network.GptRepository
import com.example.group3_starry.utils.ZodiacUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val SETTINGS_REQUEST_CODE = 2
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener {
            showGptChatDialog()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_match, R.id.nav_tarot, R.id.resultFragment,R.id.nav_sign_out
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Load user information and display it
        loadUserInfo()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadUserInfo() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val databaseReference = Firebase.database.reference

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            databaseReference.child("users").child(uid).get()
                .addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        val birthDate = dataSnapshot.child("birthDate").value.toString()
                        val birthTime = dataSnapshot.child("birthTime").value.toString()
                        val birthPlace = dataSnapshot.child("birthPlace").value.toString()

                        // Update ImageView with the zodiac sign icon
                        val formatter = DateTimeFormatter.ofPattern("d - M - yyyy")
                        val userZodiacSign = ZodiacUtils.getZodiacSign(LocalDate.parse(birthDate, formatter))

                        // Access the navigation header
                        val navView: NavigationView = binding.navView
                        val headerView = navView.getHeaderView(0) // Get the header view at index 0

                        headerView.findViewById<ImageView>(R.id.imageView)
                            ?.setImageResource(ZodiacUtils.getZodiacIcon(userZodiacSign))

                        // Update TextViews in the header
                        headerView.findViewById<TextView>(R.id.textView_birth_date)?.text = "Birth Date: $birthDate"
                        headerView.findViewById<TextView>(R.id.textView_birth_time)?.text = "Birth Time: $birthTime"
                        headerView.findViewById<TextView>(R.id.textView_birth_place)?.text = "Birth Place: $birthPlace"

                    } else {
                        Toast.makeText(this, "No user information found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to fetch user information", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No logged-in user", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showGptChatDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_gpt_chat, null)
        val queryEditText = dialogView.findViewById<EditText>(R.id.editTextQuery)
        val responseTextView = dialogView.findViewById<TextView>(R.id.textViewResponse)
        val sendButton = dialogView.findViewById<Button>(R.id.buttonSend)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Ask GPT")
            .setView(dialogView)
            .setNegativeButton("Close", null)
            .create()

        sendButton.setOnClickListener {
            val query = queryEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                responseTextView.visibility = View.VISIBLE
                responseTextView.text = "Loading..."

                // Launch a coroutine to perform the network request
                lifecycleScope.launch {
                    val gptResponse = askGpt(query)
                    responseTextView.text = gptResponse ?: "Error: Unable to fetch response."
                }
            }
        }

        dialog.show()
    }

    private suspend fun askGpt(query: String): String? {
        return try {
            val repository = GptRepository()
            val response = repository.getCardInterpretation(query)
            when (response) {
                is GptRepository.ApiResponse.Success -> response.data // Extract the successful response
                is GptRepository.ApiResponse.Error -> "Error: ${response.message}" // Handle errors
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(intent, SETTINGS_REQUEST_CODE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            // Reload user information after changes in SettingsActivity
            loadUserInfo()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
