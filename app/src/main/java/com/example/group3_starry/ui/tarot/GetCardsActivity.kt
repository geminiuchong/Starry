package com.example.group3_starry.ui.tarot

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.group3_starry.databinding.ActivityGetCardsBinding
import com.example.group3_starry.network.GptRepository

//class GetCardsActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityGetCardsBinding
//    private lateinit var gptResponse: TextView
//
//    private var cardCount: Int = 3
//    private var drawnCards = mutableListOf<String>()
//    private var userQuestion = ""
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityGetCardsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        cardCount = intent.getIntExtra("cardCount", 3)
//        gptResponse = binding.gptResponse
//
//        // Initialize the GPT guide with user question prompt
//        gptGuide("What would you like to know?")
//
//        // Card click listeners
//        binding.drawCardButton.setOnClickListener {
//            if (drawnCards.size < cardCount) {
//                drawCard()
//            } else {
//                interpretCards()
//            }
//        }
//    }
//
//    private fun gptGuide(prompt: String) {
//        // Use GPT API to generate guidance text based on prompt
//        GptRepository().getGptResponse(prompt) { response ->
//            gptResponse.text = response
//            if (drawnCards.size == 0) {
//                // Start with a question for the user
//                askUserQuestion()
//            }
//        }
//    }
//
//    private fun askUserQuestion() {
//        // Prompt user to enter their question
//        val dialog = AlertDialog.Builder(this)
//            .setTitle("Ask Your Question")
//            .setView(EditText(this))
//            .setPositiveButton("Submit") { dialog, _ ->
//                val questionInput = (dialog as AlertDialog).findViewById<EditText>(android.R.id.text1)
//                userQuestion = questionInput?.text.toString()
//                gptGuide("Think about your question while drawing your cards.")
//            }
//            .create()
//        dialog.show()
//    }
//
//    private fun drawCard() {
//        TarotRepository().getCard { card ->
//            drawnCards.add(card.name)
//            binding.cardDisplay.text = drawnCards.joinToString(", ")
//
//            // Prompt user to continue or finish drawing
//            if (drawnCards.size == cardCount) {
//                gptGuide("Now let's interpret your cards.")
//            } else {
//                gptGuide("Draw the next card.")
//            }
//        }
//    }
//
//    private fun interpretCards() {
//        val prompt = "Based on the cards: ${drawnCards.joinToString(", ")} and the user's question: '$userQuestion', please provide an interpretation."
//        gptGuide(prompt)
//    }
//}
