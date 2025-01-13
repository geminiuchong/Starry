package com.example.group3_starry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

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
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_match, R.id.nav_tarot
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
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
            repository.getCardInterpretation(query)
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

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}