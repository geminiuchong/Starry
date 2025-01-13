package com.example.group3_starry.ui.tarot

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.group3_starry.R
import com.example.group3_starry.network.Card
import com.example.group3_starry.network.GptRepository
import com.example.group3_starry.network.TarotApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GetCardsActivity : AppCompatActivity() {

    private lateinit var tarotApi: TarotApi
    private lateinit var chatArea: LinearLayout
    private lateinit var cardsArea: FrameLayout
    private lateinit var userInputField: EditText
    private lateinit var sendButton: Button
    private lateinit var getCardsButton: Button
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var chatScrollView: ScrollView
    private lateinit var endGameButton: Button

    private val gptClient = GptRepository() // GPT API integration
    private var selectedSpread: String? = null

    //private var userInfo: UserInfo? = null
    private var userQuestion: String? = null
    private var numberOfCards: Int = 3

    private val cardBackImageResId = R.drawable.card_back_1 // Default card back image
    private var cardImages = mutableMapOf<Int, Int>()

    private var isInterpretationMode = false // Tracks whether follow-up questions
    private var cardInterpretationContext: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.get_cards_activity)

        tarotApi = TarotApi()

        // Initialize views
        chatArea = findViewById(R.id.chatArea)
        cardsArea = findViewById(R.id.cardsArea)

        userInputField = findViewById(R.id.userInputField)
        sendButton = findViewById(R.id.sendButton)
        getCardsButton = findViewById(R.id.getCardsButton)
        loadingSpinner = findViewById(R.id.loadingSpinner)
        chatScrollView = findViewById(R.id.chatScrollView)
        endGameButton = findViewById(R.id.endGameButton)
        endGameButton.visibility = View.GONE // Initially hidden
        endGameButton.layoutParams = getCardsButton.layoutParams // Use the same layout
        endGameButton.setOnClickListener {
            displayMessage("Fortune Teller", "Thank you for playing the Tarot game! See you next time.")
            finish() // End the activity
        }


        // Fetch selected spread and user info from intent
        selectedSpread = intent.getStringExtra("spreadName")
        numberOfCards = intent.getIntExtra("cardCount", 3)
        //userInfo = intent.getParcelableExtra("USER_INFO")

        getCardsButton.visibility = View.GONE

        initializeCardsArea()

        // Initiate conversation
        startConversation()

        sendButton.setOnClickListener {
            val userMessage = userInputField.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                displayMessage("Me", userMessage)
                scrollToBottom()
                userInputField.text.clear()

                if (isInterpretationMode) {
                    // Handle follow-up questions
                    displayMessage("Fortune Teller", "", isLoading = true)
                    CoroutineScope(Dispatchers.IO).launch {
                        val response = gptClient.getCardInterpretation(
                            "The user asked a follow-up: $userMessage. Based on the cards: $cardInterpretationContext." +
                                    "Provide short, concise, and to-the-point interpretations for follow-up questions."
                        )
                        withContext(Dispatchers.Main) {
                            chatArea.removeViewAt(chatArea.childCount - 1) // Remove the spinner
                            when (response) {
                                is GptRepository.ApiResponse.Success -> displayMessage("Fortune Teller", response.data)
                                is GptRepository.ApiResponse.Error -> displayMessage("Fortune Teller", response.message)
                            }
                            scrollToBottom()
                        }
                    }
                } else {
                    // Handle the initial question
                    displayMessage("Fortune Teller", "", isLoading = true)
                    CoroutineScope(Dispatchers.IO).launch {
                        val response = gptClient.getCardInterpretation("The user asked: $userMessage. Please ask user to click the get cards button to get the cards.")
                        withContext(Dispatchers.Main) {
                            chatArea.removeViewAt(chatArea.childCount - 1) // Remove the spinner
                            when (response) {
                                is GptRepository.ApiResponse.Success -> {
                                    displayMessage("Fortune Teller", response.data)
                                    getCardsButton.visibility = View.VISIBLE
                                }
                                is GptRepository.ApiResponse.Error -> displayMessage("Fortune Teller", response.message)
                            }
                            scrollToBottom()
                        }
                    }
                }
            }
        }

        getCardsButton.setOnClickListener {
            getCardsButton.visibility = View.GONE
            loadingSpinner.visibility = View.VISIBLE
            tarotApi.getCards(numberOfCards) { cards ->
                runOnUiThread {
                    updateCardsAreaWithActualCards(cards)
                    val fullPrompt = buildCardInterpretationPrompt(selectedSpread, cards, userQuestion)
                    CoroutineScope(Dispatchers.IO).launch {
                        val gptResponse = gptClient.getCardInterpretation(fullPrompt)
                        withContext(Dispatchers.Main) {
                            loadingSpinner.visibility = View.GONE
                            when (gptResponse) {
                                is GptRepository.ApiResponse.Success -> {
                                    displayCardInterpretations(cards, gptResponse.data)
                                    // displayMessage("Fortune Teller", gptResponse.data)
                                }
                                is GptRepository.ApiResponse.Error -> {
                                    displayMessage("Fortune Teller", gptResponse.message)
                                }
                            }
                            scrollToBottom()
                        }
                    }
                }
            }
        }
    }

    private fun initializeCardsArea() {
        val layoutHelper = TarotLayoutHelper(this)
        cardsArea.removeAllViews()

        // Default card images showing the card back
        cardImages = (1..numberOfCards).associateWith { cardBackImageResId }.toMutableMap()

        val spreadLayout = when (selectedSpread) {
            "Three Card Spread" -> layoutHelper.setupThreeCardSpread(cardsArea, cardImages) { index ->
                showCardDialog(index, "Card $index")
            }
            "Four Card Spread" -> layoutHelper.setupFourCardSpread(cardsArea, cardImages) { index ->
                showCardDialog(index, "Card $index")
            }
            "Love Tree (Five Cards)" -> layoutHelper.setupLoveTreeSpread(cardsArea, cardImages) { index ->
                showCardDialog(index, "Card $index")
            }
            "Friendship Spread (Six Cards)" -> layoutHelper.setupFriendshipSpread(cardsArea, cardImages) { index ->
                showCardDialog(index, "Card $index")
            }
            "Hexagram (Seven Cards)" -> layoutHelper.setupHexagramSpread(cardsArea, cardImages) { index ->
                showCardDialog(index, "Card $index")
            }
            "Celtic Cross (Ten Cards)" -> layoutHelper.setupCelticCrossSpread(cardsArea, cardImages) { index ->
                showCardDialog(index, "Card $index")
            }
            else -> layoutHelper.setupThreeCardSpread(cardsArea, cardImages) { index ->
                showCardDialog(index, "Card $index")
            }
        }

        cardsArea.addView(spreadLayout)
    }

    private fun updateCardsAreaWithActualCards(cards: List<Card>) {
        val layoutHelper = TarotLayoutHelper(this)
        cardsArea.removeAllViews() // Clear any existing views

        // Prepare a map of card index to their image resource IDs
        cardImages = cards.mapIndexed { index, card -> Pair(index + 1, card.imageResId) }.toMap().toMutableMap()


        // Add a hint text indicating that cards are clickable
        val hintTextView = TextView(this).apply {
            text = "Hint: Tap on a card to reveal its details."
            textSize = 16f
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 16, 16, 16)
                gravity = Gravity.TOP
            }
        }
        cardsArea.addView(hintTextView)

        // Setup the layout for the card spread
        val spreadLayout = when (selectedSpread) {
            "Three Card Spread" -> layoutHelper.setupThreeCardSpread(cardsArea, cardImages) { index ->
                showCardDialogWithCardInfo(cards[index - 1]) // Show card dialog with card details
            }
            "Four Card Spread" -> layoutHelper.setupFourCardSpread(cardsArea, cardImages) { index ->
                showCardDialogWithCardInfo(cards[index - 1])
            }
            "Love Tree (Five Cards)" -> layoutHelper.setupLoveTreeSpread(cardsArea, cardImages) { index ->
                showCardDialogWithCardInfo(cards[index - 1])
            }
            "Friendship Spread (Six Cards)" -> layoutHelper.setupFriendshipSpread(cardsArea, cardImages) { index ->
                showCardDialogWithCardInfo(cards[index - 1])
            }
            "Hexagram (Seven Cards)" -> layoutHelper.setupHexagramSpread(cardsArea, cardImages) { index ->
                showCardDialogWithCardInfo(cards[index - 1])
            }
            "Celtic Cross (Ten Cards)" -> layoutHelper.setupCelticCrossSpread(cardsArea, cardImages) { index ->
                showCardDialogWithCardInfo(cards[index - 1])
            }
            else -> layoutHelper.setupThreeCardSpread(cardsArea, cardImages) { index ->
                showCardDialogWithCardInfo(cards[index - 1])
            }
        }
        cardsArea.addView(spreadLayout)

    }


    private fun showCardDialog(cardIndex: Int, cardName: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_card_view)

        val cardImage = dialog.findViewById<ImageView>(R.id.fullSizeCardImage)
        val cardDescription = dialog.findViewById<TextView>(R.id.cardDescription)
        val clickHint = dialog.findViewById<TextView>(R.id.clickHint)

        cardImage.setImageResource(R.drawable.card_back) // Initially show the card back
        cardDescription.text = cardName
        clickHint.visibility = View.GONE
        dialog.show()
    }

    private fun showCardDialogWithCardInfo(card: Card) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_card_view)

        val cardImage = dialog.findViewById<ImageView>(R.id.fullSizeCardImage)
        val cardDescription = dialog.findViewById<TextView>(R.id.cardDescription)
        val clickHint = dialog.findViewById<TextView>(R.id.clickHint)

        cardImage.setImageResource(card.imageResId)
        cardDescription.text = card.info // Display the card's meaning

        // Set click listener for the card image
        cardImage.setOnClickListener {
            if (cardDescription.visibility == View.GONE) {
                // Show the card description and hide the hint
                cardDescription.visibility = View.VISIBLE
                clickHint.visibility = View.GONE
            } else {
                // Hide the card description and show the hint
                cardDescription.visibility = View.GONE
                clickHint.visibility = View.VISIBLE
            }
        }

        dialog.show()
    }

    private fun displayCardInterpretations(cards: List<Card>, gptResponse: String) {
        // val responses = gptResponse.split("\n").filter { it.isNotBlank() }
        val responses = gptResponse.split("###").map { it.trim() }.filter { it.isNotEmpty() }
        val interpretationBuilder = StringBuilder()

        // Separate card responses and conclusion
        //val cardResponses = responses.take(cards.size) // Take only the first N responses for cards
        //val conclusion = responses.find { it.startsWith("Conclusion:") } ?: "No conclusive prediction provided."

        CoroutineScope(Dispatchers.Main).launch {
            cards.forEachIndexed { index, card ->
                val cardResponse = if (index < responses.size) responses[index] else "No interpretation provided."
                val interpretation = "${card.name}: $cardResponse"
                interpretationBuilder.append(interpretation).append("\n")
                displayMessage("Fortune Teller", interpretation)
                scrollToBottom()
                delay(2000) // Wait for 2 seconds before showing the next card
            }
//            cards.forEachIndexed { index, card ->
//                // val cardResponse = if (index < responses.size) responses[index] else "No interpretation provided."
//                val cardResponse = cardResponses.getOrNull(index) ?: "No interpretation provided."
//                val interpretation = "${card.name}: $cardResponse"
//                interpretationBuilder.append(interpretation).append("\n")
//
//                displayMessage("Fortune Teller", interpretation)
//                scrollToBottom()
//                delay(2000) // Wait for 2 seconds before showing the next card
//            }

            // Add the conclusion message
            val conclusion = responses.lastOrNull { it.startsWith("Conclusion:") } ?: "No conclusive prediction provided."
            interpretationBuilder.append("Conclusion: $conclusion")
            displayMessage("Fortune Teller", "$conclusion")
            scrollToBottom()

            // Store interpretations for follow-up questions
            cardInterpretationContext = interpretationBuilder.toString()

            delay(2000)
            val followUpPrompt = "You can now choose to click the End Game Button to end the tarot game or ask any follow-up question about the interpretation"
            displayMessage("Fortune Teller", followUpPrompt)

            // Allow follow-up questions
            isInterpretationMode = true
            endGameButton.visibility = View.VISIBLE // Replace Get Cards button
            scrollToBottom()
        }
    }

    private fun scrollToBottom() {
        chatScrollView.post {
            chatScrollView.fullScroll(View.FOCUS_DOWN)
        }
    }


    private fun startConversation() {
        val introMessage =
            "You have chosen the $selectedSpread spread. ${spreadDescription(selectedSpread)} What is your question?"
        displayMessage("Fortune Teller", introMessage)
    }

    private fun displayMessage(sender: String, message: String, isLoading: Boolean = false) {
        val messageLayout = LinearLayout(this).apply {
            gravity = if (sender == "Me") Gravity.END else Gravity.START
            setPadding(8, 8, 8, 8)
            orientation = LinearLayout.HORIZONTAL
        }

        val icon = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(100, 100).apply {
                marginEnd = 8
            }
            setImageResource(
                if (sender == "Me") R.drawable.user_icon else R.drawable.fortune_teller
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        val textView = TextView(this).apply {
            text = if (isLoading) "" else "$sender: $message"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            setTextColor(ContextCompat.getColor(this@GetCardsActivity, android.R.color.black))
            background = ContextCompat.getDrawable(
                this@GetCardsActivity,
                if (sender == "Me") R.drawable.user_message_bg else R.drawable.gpt_message_bg
            )
            layoutParams = LinearLayout.LayoutParams(
                // LinearLayout.LayoutParams.WRAP_CONTENT,
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1f
            }
        }
        // Add a spinner if the message is still loading
        val spinner = ProgressBar(this).apply {
            visibility = if (isLoading) View.VISIBLE else View.GONE
            layoutParams = LinearLayout.LayoutParams(50, 50).apply {
                gravity = Gravity.CENTER_VERTICAL
                marginStart = 8
            }
        }

        if (sender == "Me") {
            messageLayout.addView(textView)
            messageLayout.addView(icon)
        } else {
            if (isLoading) {
                messageLayout.addView(spinner)
            }
            messageLayout.addView(icon)
            messageLayout.addView(textView)
        }

        chatArea.addView(messageLayout)
        if (!isLoading) userInputField.text.clear()
    }

    private fun spreadDescription(spread: String?): String {
        return when (spread) {
            "Three Card Spread" -> "This spread is great for understanding the past, present, and future of your situation. It helps explain the flow of events and possible outcomes."
            "Four Card Spread" -> "This spread helps you focus on the core issue of a problem, the obstacles you face, and the resources or strategies you can use to overcome them."
            "Love Tree (Five Cards)" -> "This spread is perfect for analyzing love or relationship questions. It provides insights into yourself, the past, the present, and the potential future of the relationship."
            "Friendship Spread (Six Cards)" -> "This spread helps you understand the dynamics of a friendship, including how you and your friend see each other, the current state of your relationship, and its future potential."
            "Hexagram (Seven Cards)" -> "This spread is useful for analyzing any question in depth, exploring the past, present, future, external influences, and the ultimate outcome."
            "Celtic Cross (Ten Cards)" -> "This spread is ideal for addressing complex situations. It offers a detailed exploration of your current situation, the challenges you face, and the likely outcome."
            else -> "A versatile tarot spread suitable for exploring various questions and insights."
        }
    }

    private fun buildCardInterpretationPrompt(spread: String?, cards: List<Card>, userQuestion: String?): String {
        val cardDetails = cards.joinToString(separator = ", ") { "${it.name}: ${it.info}" }

        return when (spread) {
            "Three Card Spread" -> {
                """
            You chose the Three Card Spread. This spread answers cause and effect questions. It represents 'Past → Present → Future' or 'Cause → Effect → Advice,' helping explain linear cause-and-effect relationships.
            The drawn cards are: $cardDetails.
            Based on the question '$userQuestion,' interpret these cards with a focus on linear cause-and-effect relationships.

            Response format:
            - Each card's interpretation must be on a new line and start with "Card X: " (where X is the card number).
            - Separate each card interpretation using "###" (three hash symbols, no spaces before or after).
            - The conclusion must start with "Conclusion: " and should summarize the overall interpretation. Place the conclusion after all the cards, on a new line.

            Example response format:
            Card 1: Interpretation of the first card. ###
            Card 2: Interpretation of the second card. ###
            Card 3: Interpretation of the third card. ###
            Conclusion: Summary of the entire spread.
            """.trimIndent()
            }
            "Four Card Spread" -> {
                """
            You chose the Four Card Spread, which identifies the core of the problem. The first card represents the 'Core Issue,' followed by 'Obstacle,' 'Solution,' and 'Strength/Resources' in sequence.
            The drawn cards are: $cardDetails.
            Based on the question '$userQuestion,' analyze the problem and suggest solutions.

            Response format:
            - Each card's interpretation must be on a new line and start with "Card X: " (where X is the card number).
            - Separate each card interpretation using "###" (three hash symbols, no spaces before or after).
            - The conclusion must start with "Conclusion: " and should summarize the overall interpretation. Place the conclusion after all the cards, on a new line.

            Example response format:
            Card 1: Interpretation of the first card. ###
            Card 2: Interpretation of the second card. ###
            Card 3: Interpretation of the third card. ###
            Card 4: Interpretation of the fourth card. ###
            Conclusion: Summary of the entire spread.
            """.trimIndent()
            }
            "Love Tree (Five Cards)" -> {
                """
            You chose the Love Tree Spread. This spread analyzes 'Self,' 'Past/Cause,' 'Present/Solution,' 'Future/Potential,' and 'External/Internal Influences.'
            The drawn cards are: $cardDetails.
            Based on the question '$userQuestion,' provide a detailed love analysis and prediction.

            Response format:
            - Each card's interpretation must be on a new line and start with "Card X: " (where X is the card number).
            - Separate each card interpretation using "###" (three hash symbols, no spaces before or after).
            - The conclusion must start with "Conclusion: " and should summarize the overall interpretation. Place the conclusion after all the cards, on a new line.

            Example response format:
            Card 1: Interpretation of the first card. ###
            Card 2: Interpretation of the second card. ###
            Card 3: Interpretation of the third card. ###
            Card 4: Interpretation of the fourth card. ###
            Card 5: Interpretation of the fifth card. ###
            Conclusion: Summary of the entire spread.
            """.trimIndent()
            }
            "Friendship Spread (Six Cards)" -> {
                """
            You chose the Friendship Spread. This spread explores 'Your Perspective,' 'Friend’s Perspective,' 'Your View of the Relationship,' 'Friend’s View of the Relationship,' 'Current State,' and 'Future Potential.'
            The drawn cards are: $cardDetails.
            Based on the question '$userQuestion,' interpret the relationship dynamics and future potential.

            Response format:
            - Each card's interpretation must be on a new line and start with "Card X: " (where X is the card number).
            - Separate each card interpretation using "###" (three hash symbols, no spaces before or after).
            - The conclusion must start with "Conclusion: " and should summarize the overall interpretation. Place the conclusion after all the cards, on a new line.

            Example response format:
            Card 1: Interpretation of the first card. ###
            Card 2: Interpretation of the second card. ###
            Card 3: Interpretation of the third card. ###
            Card 4: Interpretation of the fourth card. ###
            Card 5: Interpretation of the fifth card. ###
            Card 6: Interpretation of the sixth card. ###
            Conclusion: Summary of the entire spread.
            """.trimIndent()
            }
            "Hexagram (Seven Cards)" -> {
                """
            You chose the Hexagram Spread. This spread analyzes any issue in depth with a hexagram layout. Cards represent 'Past,' 'Present,' 'Future,' 'Solution,' 'External Factors,' 'Your Mindset,' and 'Outcome.'
            The drawn cards are: $cardDetails.
            Based on the question '$userQuestion,' provide a comprehensive analysis and conclusion.

            Response format:
            - Each card's interpretation must be on a new line and start with "Card X: " (where X is the card number).
            - Separate each card interpretation using "###" (three hash symbols, no spaces before or after).
            - The conclusion must start with "Conclusion: " and should summarize the overall interpretation. Place the conclusion after all the cards, on a new line.

            Example response format:
            Card 1: Interpretation of the first card. ###
            Card 2: Interpretation of the second card. ###
            Card 3: Interpretation of the third card. ###
            Card 4: Interpretation of the fourth card. ###
            Card 5: Interpretation of the fifth card. ###
            Card 6: Interpretation of the sixth card. ###
            Card 7: Interpretation of the seventh card. ###
            Conclusion: Summary of the entire spread.
            """.trimIndent()
            }
            "Celtic Cross (Ten Cards)" -> {
                """
            You chose the Celtic Cross Spread to address complex issues. Cards represent 'Current Situation,' 'Influencing Factors,' 'Aspirations,' 'Underlying Causes,' 'Past,' 'Future,' 'Self,' 'External Factors,' 'Hopes/Fears,' and 'Outcome.'
            The drawn cards are: $cardDetails.
            Based on the question '$userQuestion,' provide a thorough interpretation and prediction.

            Response format:
            - Each card's interpretation must be on a new line and start with "Card X: " (where X is the card number).
            - Separate each card interpretation using "###" (three hash symbols, no spaces before or after).
            - The conclusion must start with "Conclusion: " and should summarize the overall interpretation. Place the conclusion after all the cards, on a new line.

            Example response format:
            Card 1: Interpretation of the first card. ###
            Card 2: Interpretation of the second card. ###
            Card 3: Interpretation of the third card. ###
            Card 4: Interpretation of the fourth card. ###
            Card 5: Interpretation of the fifth card. ###
            Card 6: Interpretation of the sixth card. ###
            Card 7: Interpretation of the seventh card. ###
            Card 8: Interpretation of the eighth card. ###
            Card 9: Interpretation of the ninth card. ###
            Card 10: Interpretation of the tenth card. ###
            Conclusion: Summary of the entire spread.
            """.trimIndent()
            }
            else -> {
                """
            The chosen spread is not specified. The cards are: $cardDetails.
            Based on the question '$userQuestion,' provide a general interpretation.

            Response format:
            - Each card's interpretation must be on a new line and start with "Card X: " (where X is the card number).
            - Separate each card interpretation using "###" (three hash symbols, no spaces before or after).
            - The conclusion must start with "Conclusion: " and should summarize the overall interpretation. Place the conclusion after all the cards, on a new line.

            Example response format:
            Card 1: Interpretation of the first card. ###
            Card 2: Interpretation of the second card. ###
            Card 3: Interpretation of the third card. ###
            Conclusion: Summary of the entire spread.
            """.trimIndent()
            }
        }
    }


//    private fun buildCardInterpretationPrompt(spread: String?, cards: List<Card>, userQuestion: String?): String {
//        val cardDetails = cards.joinToString(separator = ", ") { "${it.name}: ${it.info}" }
//
//        return when (spread) {
//            "Three Card Spread" -> {
//                "You chose the Three Card Spread. This spread answers cause and effect questions. It represents 'Past → Present → Future' or 'Cause → Effect → Advice,' helping explain linear cause-and-effect relationships." +
//                        "The drawn cards are: $cardDetails. " +
//                        "Based on the question '$userQuestion,' interpret these cards with a focus on linear cause-and-effect relationships. Each card separated by \"###\" without additional newlines." +
//                        "Focus on key insights using concise and short language, clear conclusive prediction (start with Conclusion:) based on the drawn cards."
//            }
//            "Four Card Spread" -> {
//                "You chose the Four Card Spread, which identifies the core of the problem. The first card represents the 'Core Issue,' followed by 'Obstacle,' 'Solution,' and 'Strength/Resources' in sequence." +
//                        "The cards are: $cardDetails. " +
//                        "Based on the question '$userQuestion,' analyze the problem and suggest solutions. Each card separated by \"###\" without additional newlines." +
//                        "Focus on key insights using concise and short language, clear conclusive prediction (start with Conclusion:) based on the drawn cards."
//            }
//            "Love Tree (Five Cards)" -> {
//                "You chose the Love Tree Spread for analyzing love-related questions. This spread is suitable for analyzing love-related questions. Cards are represents: 'Self,' 'Past/Cause,' 'Present/Solution,' 'Future/Potential,' and 'External/Internal Influences.' " +
//                        "The cards are: $cardDetails. " +
//                        "Based on the question '$userQuestion,' provide a detailed love analysis and prediction. Each card separated by \"###\" without additional newlines." +
//                        "Focus on key insights using concise and short language, clear conclusive prediction (start with Conclusion:) based on the drawn cards."
//            }
//            "Friendship Spread (Six Cards)" -> {
//                "You chose the Friendship. This spread is ideal for resolving friendship issues. Cards are laid out in a 3x2 grid: 'Your Perspective,' 'Friend’s Perspective,' 'Your View of the Relationship,' 'Friend’s View of the Relationship,' and 'Future Expectations for the Relationship.'" +
//                        "The cards are: $cardDetails. " +
//                        "Based on the question '$userQuestion,' interpret the relationship dynamics and future potential. Each card separated by \"###\" without additional newlines." +
//                        "Focus on key insights using concise and short language, clear conclusive prediction (start with Conclusion:) based on the drawn cards."
//            }
//            "Hexagram (Seven Cards)" -> {
//                "You chose the Hexagram Spread. This spread analyzes any issue in depth with a hexagram layout. Cards represent 'Past,' 'Present,' 'Future,' 'Solution,' 'External Factors,' 'Your Mindset,' and 'Outcome.' The drawn cards represent: 'Past,' 'Present,' 'Future,' 'Solution,' 'External Factors,' 'Your Mindset,' and 'Outcome.' " +
//                        "The cards are: $cardDetails. " +
//                        "Based on the question '$userQuestion,' provide a comprehensive analysis and conclusion. Each card separated by \"###\" without additional newlines." +
//                        "Focus on key insights using concise and short language, clear conclusive prediction (start with Conclusion:) based on the drawn cards."
//            }
//            "Celtic Cross (Ten Cards)" -> {
//                "You chose the Celtic Cross Spread to address complex issues. Cards represent: 'Current Situation,' 'Influencing Factors,' 'Aspirations,' 'Underlying Causes,' 'Past,' 'Future,' 'Self,' 'External Factors,' 'Hopes/Fears,' and 'Outcome.'" +
//                        "The cards are: $cardDetails. " +
//                        "Based on the question '$userQuestion,' provide a thorough interpretation and prediction. Each card separated by \"###\" without additional newlines." +
//                        "Focus on key insights using concise and short language, clear conclusive prediction (start with Conclusion:) based on the drawn cards."
//            }
//            else -> {
//                "The chosen spread is not specified. The cards are: $cardDetails. " +
//                        "Based on the question '$userQuestion,' provide a general interpretation. Each card separated by \"###\" without additional newlines." +
//                        "Focus on key insights using concise and short language, clear conclusive prediction based on the drawn cards."
//            }
//        }
//    }
}

//class GetCardsActivity : AppCompatActivity() {
//
//    private lateinit var tarotApi: TarotApi
//    private lateinit var chatArea: LinearLayout
//    private lateinit var cardsArea: FrameLayout
//    private lateinit var userInputField: EditText
//    private lateinit var sendButton: Button
//    private lateinit var getCardsButton: Button
//    private lateinit var loadingSpinner: ProgressBar
//    private lateinit var chatScrollView: ScrollView
//
//    // private val gptClient = GptClient() // GPT API integration
//    private val gptClient = GptRepository() // GPT API integration
//    private var selectedSpread: String? = null
//
//    //private var userInfo: UserInfo? = null
//    private var userQuestion: String? = null
//    private var numberOfCards: Int = 3
//
//    private val cardBackImageResId = R.drawable.card_back_1 // Default card back image
//    private var cardImages = mutableMapOf<Int, Int>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.get_cards_activity)
//
//        tarotApi = TarotApi()
//
//        // Initialize views
//        chatArea = findViewById(R.id.chatArea)
//        cardsArea = findViewById(R.id.cardsArea)
//
//        userInputField = findViewById(R.id.userInputField)
//        sendButton = findViewById(R.id.sendButton)
//        getCardsButton = findViewById(R.id.getCardsButton)
//        loadingSpinner = findViewById(R.id.loadingSpinner)
//        chatScrollView = findViewById(R.id.chatScrollView)
//
//        // Fetch selected spread and user info from intent
//        selectedSpread = intent.getStringExtra("spreadName")
//        numberOfCards = intent.getIntExtra("cardCount", 3)
//        //userInfo = intent.getParcelableExtra("USER_INFO")
//
//        getCardsButton.visibility = View.GONE
//
//        initializeCardsArea()
//
//        // Initiate conversation
//        startConversation()
//
//        cardsArea.post {
//            Log.d("CardsArea", "Width: ${cardsArea.width}, Height: ${cardsArea.height}")
//        }
//
//
//        sendButton.setOnClickListener {
//            val userMessage = userInputField.text.toString().trim()
//            if (userMessage.isNotEmpty()) {
//                displayMessage("Me", userMessage)
//                userQuestion = userMessage
//                //loadingSpinner.visibility = View.VISIBLE
//
//                displayMessage("Fortune Teller", "", isLoading = true)
//
//                // Launch a coroutine to process the user question
//                CoroutineScope(Dispatchers.IO).launch {
//                    val response = gptClient.getCardInterpretation("The user asked: $userMessage. Please ask user to click the get cards button to get the cards.")
//                    withContext(Dispatchers.Main) {
//                        // loadingSpinner.visibility = View.GONE
//                        chatArea.removeViewAt(chatArea.childCount - 1)
//                        when (response) {
//                            is GptRepository.ApiResponse.Success -> {
//                                displayMessage("Fortune Teller", response.data)
//                                // Show the Get Cards button after GPT responds
//                                getCardsButton.visibility = View.VISIBLE
//                            }
//                            is GptRepository.ApiResponse.Error -> {
//                                displayMessage("Fortune Teller", response.message)
//                            }
//                        }
//                        scrollToBottom()
//                    }
//                }
//            }
//        }
//
//
//        getCardsButton.setOnClickListener {
//            getCardsButton.visibility = View.GONE
//            loadingSpinner.visibility = View.VISIBLE
//            tarotApi.getCards(numberOfCards) { cards ->
//                runOnUiThread {
//                    updateCardsAreaWithActualCards(cards)
//                    val fullPrompt = buildCardInterpretationPrompt(selectedSpread, cards, userQuestion)
//                    CoroutineScope(Dispatchers.IO).launch {
//                        val gptResponse = gptClient.getCardInterpretation(fullPrompt)
//                        withContext(Dispatchers.Main) {
//                            loadingSpinner.visibility = View.GONE
//                            when (gptResponse) {
//                                is GptRepository.ApiResponse.Success -> {
//                                    displayCardInterpretations(cards, gptResponse.data)
//                                    // displayMessage("Fortune Teller", gptResponse.data)
//                                }
//                                is GptRepository.ApiResponse.Error -> {
//                                    displayMessage("Fortune Teller", gptResponse.message)
//                                }
//                            }
//                            scrollToBottom()
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun initializeCardsArea() {
//        val layoutHelper = TarotLayoutHelper(this)
//        cardsArea.removeAllViews()
//
//        // Default card images showing the card back
//        cardImages = (1..numberOfCards).associateWith { cardBackImageResId }.toMutableMap()
//
//        val spreadLayout = when (selectedSpread) {
//            "Three Card Spread" -> layoutHelper.setupThreeCardSpread(cardsArea, cardImages) { index ->
//                showCardDialog(index, "Card $index")
//            }
//            "Four Card Spread" -> layoutHelper.setupFourCardSpread(cardsArea, cardImages) { index ->
//                showCardDialog(index, "Card $index")
//            }
//            "Love Tree (Five Cards)" -> layoutHelper.setupLoveTreeSpread(cardsArea, cardImages) { index ->
//                showCardDialog(index, "Card $index")
//            }
//            "Friendship Spread (Six Cards)" -> layoutHelper.setupFriendshipSpread(cardsArea, cardImages) { index ->
//                showCardDialog(index, "Card $index")
//            }
//            "Hexagram (Seven Cards)" -> layoutHelper.setupHexagramSpread(cardsArea, cardImages) { index ->
//                showCardDialog(index, "Card $index")
//            }
//            "Celtic Cross (Ten Cards)" -> layoutHelper.setupCelticCrossSpread(cardsArea, cardImages) { index ->
//                showCardDialog(index, "Card $index")
//            }
//            else -> layoutHelper.setupThreeCardSpread(cardsArea, cardImages) { index ->
//                showCardDialog(index, "Card $index")
//            }
//        }
//
//        cardsArea.addView(spreadLayout)
//    }
//
//    private fun updateCardsAreaWithActualCards(cards: List<Card>) {
//        val layoutHelper = TarotLayoutHelper(this)
//        cardsArea.removeAllViews() // Clear any existing views
//
//        // Prepare a map of card index to their image resource IDs
//        cardImages = cards.mapIndexed { index, card -> Pair(index + 1, card.imageResId) }.toMap().toMutableMap()
//
//
//        // Add a hint text indicating that cards are clickable
//        val hintTextView = TextView(this).apply {
//            text = "Hint: Tap on a card to reveal its details."
//            textSize = 16f
//            gravity = Gravity.CENTER
//            layoutParams = FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                setMargins(16, 16, 16, 16)
//                gravity = Gravity.TOP
//            }
//        }
//        cardsArea.addView(hintTextView)
//
//        // Setup the layout for the card spread
//        val spreadLayout = when (selectedSpread) {
//            "Three Card Spread" -> layoutHelper.setupThreeCardSpread(cardsArea, cardImages) { index ->
//                showCardDialogWithCardInfo(cards[index - 1]) // Show card dialog with card details
//            }
//            "Four Card Spread" -> layoutHelper.setupFourCardSpread(cardsArea, cardImages) { index ->
//                showCardDialogWithCardInfo(cards[index - 1])
//            }
//            "Love Tree (Five Cards)" -> layoutHelper.setupLoveTreeSpread(cardsArea, cardImages) { index ->
//                showCardDialogWithCardInfo(cards[index - 1])
//            }
//            "Friendship Spread (Six Cards)" -> layoutHelper.setupFriendshipSpread(cardsArea, cardImages) { index ->
//                showCardDialogWithCardInfo(cards[index - 1])
//            }
//            "Hexagram (Seven Cards)" -> layoutHelper.setupHexagramSpread(cardsArea, cardImages) { index ->
//                showCardDialogWithCardInfo(cards[index - 1])
//            }
//            "Celtic Cross (Ten Cards)" -> layoutHelper.setupCelticCrossSpread(cardsArea, cardImages) { index ->
//                showCardDialogWithCardInfo(cards[index - 1])
//            }
//            else -> layoutHelper.setupThreeCardSpread(cardsArea, cardImages) { index ->
//                showCardDialogWithCardInfo(cards[index - 1])
//            }
//        }
//        cardsArea.addView(spreadLayout)
//
//    }
//
//
//    private fun showCardDialog(cardIndex: Int, cardName: String) {
//        val dialog = Dialog(this)
//        dialog.setContentView(R.layout.dialog_card_view)
//
//        val cardImage = dialog.findViewById<ImageView>(R.id.fullSizeCardImage)
//        val cardDescription = dialog.findViewById<TextView>(R.id.cardDescription)
//        val clickHint = dialog.findViewById<TextView>(R.id.clickHint)
//
//        cardImage.setImageResource(R.drawable.card_back) // Initially show the card back
//        cardDescription.text = cardName
//        clickHint.visibility = View.GONE
//        dialog.show()
//    }
//
//    private fun showCardDialogWithCardInfo(card: Card) {
//        val dialog = Dialog(this)
//        dialog.setContentView(R.layout.dialog_card_view)
//
//        val cardImage = dialog.findViewById<ImageView>(R.id.fullSizeCardImage)
//        val cardDescription = dialog.findViewById<TextView>(R.id.cardDescription)
//        val clickHint = dialog.findViewById<TextView>(R.id.clickHint)
//
//        cardImage.setImageResource(card.imageResId)
//        cardDescription.text = card.info // Display the card's meaning
//
//        // Set click listener for the card image
//        cardImage.setOnClickListener {
//            if (cardDescription.visibility == View.GONE) {
//                // Show the card description and hide the hint
//                cardDescription.visibility = View.VISIBLE
//                clickHint.visibility = View.GONE
//            } else {
//                // Hide the card description and show the hint
//                cardDescription.visibility = View.GONE
//                clickHint.visibility = View.VISIBLE
//            }
//        }
//
//        dialog.show()
//    }
//
//    private fun displayCardInterpretations(cards: List<Card>, gptResponse: String) {
//        val responses = gptResponse.split("\n").filter { it.isNotBlank() }
//        CoroutineScope(Dispatchers.Main).launch {
//            cards.forEachIndexed { index, card ->
//                val cardResponse = if (index < responses.size) responses[index] else "No interpretation provided."
//                displayMessage("Fortune Teller", "${card.name}: $cardResponse")
//                scrollToBottom()
//                delay(2000) // Wait for 2 seconds before showing the next card
//            }
//
//            // Add the conclusion message
//            val conclusion = responses.lastOrNull() ?: "No conclusive prediction provided."
//            displayMessage("Fortune Teller", "$conclusion")
//            scrollToBottom()
//        }
//    }
//
//    private fun scrollToBottom() {
//        chatScrollView.post {
//            chatScrollView.fullScroll(View.FOCUS_DOWN)
//        }
//    }
//
//
//    private fun startConversation() {
//        val introMessage =
//            "You have chosen the $selectedSpread spread. ${spreadDescription(selectedSpread)} What is your question?"
//        displayMessage("Fortune Teller", introMessage)
//    }
//
//    private fun displayMessage(sender: String, message: String, isLoading: Boolean = false) {
//        val messageLayout = LinearLayout(this).apply {
//            gravity = if (sender == "Me") Gravity.END else Gravity.START
//            setPadding(8, 8, 8, 8)
//            orientation = LinearLayout.HORIZONTAL
//        }
//
//        val icon = ImageView(this).apply {
//            layoutParams = LinearLayout.LayoutParams(100, 100).apply {
//                marginEnd = 8
//            }
//            setImageResource(
//                if (sender == "Me") R.drawable.user_icon else R.drawable.fortune_teller
//            )
//            scaleType = ImageView.ScaleType.CENTER_CROP
//        }
//
//        val textView = TextView(this).apply {
//            text = if (isLoading) "" else "$sender: $message"
//            textSize = 16f
//            setPadding(16, 12, 16, 12)
//            setTextColor(ContextCompat.getColor(this@GetCardsActivity, android.R.color.black))
//            background = ContextCompat.getDrawable(
//                this@GetCardsActivity,
//                if (sender == "Me") R.drawable.user_message_bg else R.drawable.gpt_message_bg
//            )
//            layoutParams = LinearLayout.LayoutParams(
//                // LinearLayout.LayoutParams.WRAP_CONTENT,
//                0,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                weight = 1f
//            }
//        }
//        // Add a spinner if the message is still loading
//        val spinner = ProgressBar(this).apply {
//            visibility = if (isLoading) View.VISIBLE else View.GONE
//            layoutParams = LinearLayout.LayoutParams(50, 50).apply {
//                gravity = Gravity.CENTER_VERTICAL
//                marginStart = 8
//            }
//        }
//
//        if (sender == "Me") {
//            messageLayout.addView(textView)
//            messageLayout.addView(icon)
//        } else {
//            if (isLoading) {
//                messageLayout.addView(spinner)
//            }
//            messageLayout.addView(icon)
//            messageLayout.addView(textView)
//        }
//
//        chatArea.addView(messageLayout)
//        if (!isLoading) userInputField.text.clear()
////        messageLayout.addView(if (sender == "Me") textView else icon)
////        messageLayout.addView(if (sender == "Me") icon else textView)
////
////
////        chatArea.addView(messageLayout)
////        userInputField.text.clear()
//    }
//
//    private fun spreadDescription(spread: String?): String {
//        return when (spread) {
//            "Three Card Spread" -> "This spread is great for understanding the past, present, and future of your situation. It helps explain the flow of events and possible outcomes."
//            "Four Card Spread" -> "This spread helps you focus on the core issue of a problem, the obstacles you face, and the resources or strategies you can use to overcome them."
//            "Love Tree (Five Cards)" -> "This spread is perfect for analyzing love or relationship questions. It provides insights into yourself, the past, the present, and the potential future of the relationship."
//            "Friendship Spread (Six Cards)" -> "This spread helps you understand the dynamics of a friendship, including how you and your friend see each other, the current state of your relationship, and its future potential."
//            "Hexagram (Seven Cards)" -> "This spread is useful for analyzing any question in depth, exploring the past, present, future, external influences, and the ultimate outcome."
//            "Celtic Cross (Ten Cards)" -> "This spread is ideal for addressing complex situations. It offers a detailed exploration of your current situation, the challenges you face, and the likely outcome."
//            else -> "A versatile tarot spread suitable for exploring various questions and insights."
//        }
//    }
//
//    private fun buildCardInterpretationPrompt(spread: String?, cards: List<Card>, userQuestion: String?): String {
//        val cardDetails = cards.joinToString(separator = ", ") { "${it.name}: ${it.info}" }
//
//        return when (spread) {
//            "Three Card Spread" -> {
//                "You chose the Three Card Spread. This spread answers cause and effect questions. It represents 'Past → Present → Future' or 'Cause → Effect → Advice,' helping explain linear cause-and-effect relationships." +
//                        "The drawn cards are: $cardDetails. " +
//                        "Based on the question '$userQuestion,' interpret these cards with a focus on linear cause-and-effect relationships. " +
//                        "Focus on key insights using concise language, clear conclusive prediction based on the drawn cards."
//            }
//            "Four Card Spread" -> {
//                "You chose the Four Card Spread, which identifies the core of the problem. The first card represents the 'Core Issue,' followed by 'Obstacle,' 'Solution,' and 'Strength/Resources' in sequence." +
//                        "The cards are: $cardDetails. Based on the question '$userQuestion,' analyze the problem and suggest solutions. " +
//                        "Focus on key insights using concise language, clear conclusive prediction based on the drawn cards."
//            }
//            "Love Tree (Five Cards)" -> {
//                "You chose the Love Tree Spread for analyzing love-related questions. This spread is suitable for analyzing love-related questions. Cards are represents: 'Self,' 'Past/Cause,' 'Present/Solution,' 'Future/Potential,' and 'External/Internal Influences.' " +
//                        "The cards are: $cardDetails. Based on the question '$userQuestion,' provide a detailed love analysis and prediction. " +
//                        "Focus on key insights using concise language, clear conclusive prediction based on the drawn cards."
//            }
//            "Friendship Spread (Six Cards)" -> {
//                "You chose the Friendship. This spread is ideal for resolving friendship issues. Cards are laid out in a 3x2 grid: 'Your Perspective,' 'Friend’s Perspective,' 'Your View of the Relationship,' 'Friend’s View of the Relationship,' and 'Future Expectations for the Relationship.'" +
//                        "The cards are: $cardDetails. Based on the question '$userQuestion,' interpret the relationship dynamics and future potential. " +
//                        "Focus on key insights using concise language, clear conclusive prediction based on the drawn cards."
//            }
//            "Hexagram (Seven Cards)" -> {
//                "You chose the Hexagram Spread. This spread analyzes any issue in depth with a hexagram layout. Cards represent 'Past,' 'Present,' 'Future,' 'Solution,' 'External Factors,' 'Your Mindset,' and 'Outcome.' The drawn cards represent: 'Past,' 'Present,' 'Future,' 'Solution,' 'External Factors,' 'Your Mindset,' and 'Outcome.' " +
//                        "The cards are: $cardDetails. Based on the question '$userQuestion,' provide a comprehensive analysis and conclusion. " +
//                        "Focus on key insights using concise language, clear conclusive prediction based on the drawn cards."
//            }
//            "Celtic Cross (Ten Cards)" -> {
//                "You chose the Celtic Cross Spread to address complex issues. Cards represent: 'Current Situation,' 'Influencing Factors,' 'Aspirations,' 'Underlying Causes,' 'Past,' 'Future,' 'Self,' 'External Factors,' 'Hopes/Fears,' and 'Outcome.'" +
//                        "The cards are: $cardDetails. Based on the question '$userQuestion,' provide a thorough interpretation and prediction. " +
//                        "Focus on key insights using concise language, clear conclusive prediction based on the drawn cards."
//            }
//            else -> {
//                "The chosen spread is not specified. The cards are: $cardDetails. Based on the question '$userQuestion,' provide a general interpretation. " +
//                        "Focus on key insights using concise language, clear conclusive prediction based on the drawn cards."
//            }
//        }
//    }
//}

//class GetCardsActivity : AppCompatActivity() {
//
//    private lateinit var tarotApi: TarotApi
//    private lateinit var chatArea: LinearLayout
//    private lateinit var cardsArea: LinearLayout
//    private lateinit var userInputField: EditText
//    private lateinit var sendButton: Button
//    private lateinit var getCardsButton: Button
//    private lateinit var loadingSpinner: ProgressBar
//    private lateinit var chatScrollView: ScrollView
//
//    // private val gptClient = GptClient() // GPT API integration
//    private val gptClient = GptRepository() // GPT API integration
//    private var selectedSpread: String? = null
//
//    //private var userInfo: UserInfo? = null
//    private var userQuestion: String? = null
//    private var numberOfCards: Int = 3
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.get_cards_activity)
//
//        tarotApi = TarotApi()
//
//        // Initialize views
//        chatArea = findViewById(R.id.chatArea)
//        cardsArea = findViewById(R.id.cardsArea)
//        userInputField = findViewById(R.id.userInputField)
//        sendButton = findViewById(R.id.sendButton)
//        getCardsButton = findViewById(R.id.getCardsButton)
//        loadingSpinner = findViewById(R.id.loadingSpinner)
//        chatScrollView = findViewById(R.id.chatScrollView)
//
//        // Fetch selected spread and user info from intent
//        selectedSpread = intent.getStringExtra("spreadName")
//        numberOfCards = intent.getIntExtra("cardCount", 3)
//        //userInfo = intent.getParcelableExtra("USER_INFO")
//
//        getCardsButton.visibility = View.GONE
//
//        initializeCardsArea()
//
//        // Initiate conversation
//        startConversation()
//
//        sendButton.setOnClickListener {
//            val userMessage = userInputField.text.toString().trim()
//            if (userMessage.isNotEmpty()) {
//                displayMessage("Me", userMessage)
//                userQuestion = userMessage
//                loadingSpinner.visibility = View.VISIBLE
//
//                // Launch a coroutine to process the user question
//                CoroutineScope(Dispatchers.IO).launch {
//                    val response = gptClient.getCardInterpretation("The user asked: $userMessage. Please ask user to click the get cards button to get the cards.")
//                    withContext(Dispatchers.Main) {
//                        loadingSpinner.visibility = View.GONE
//                        when (response) {
//                            is GptRepository.ApiResponse.Success -> {
//                                displayMessage("Fortune Teller", response.data)
//                                // Show the Get Cards button after GPT responds
//                                getCardsButton.visibility = View.VISIBLE
//                            }
//                            is GptRepository.ApiResponse.Error -> {
//                                displayMessage("Fortune Teller", response.message)
//                            }
//                        }
//                        scrollToBottom()
//                    }
//                }
//            }
//        }
//
//
//        getCardsButton.setOnClickListener {
//            getCardsButton.visibility = View.GONE
//            loadingSpinner.visibility = View.VISIBLE
//            tarotApi.getCards(numberOfCards) { cards ->
//                runOnUiThread {
//                    updateCardsAreaWithActualCards(cards)
//                    val fullPrompt = buildCardInterpretationPrompt(selectedSpread, cards, userQuestion)
//                    CoroutineScope(Dispatchers.IO).launch {
//                        val gptResponse = gptClient.getCardInterpretation(fullPrompt)
//                        withContext(Dispatchers.Main) {
//                            loadingSpinner.visibility = View.GONE
//                            when (gptResponse) {
//                                is GptRepository.ApiResponse.Success -> {
//                                    displayCardInterpretations(cards, gptResponse.data)
//                                    // displayMessage("Fortune Teller", gptResponse.data)
//                                }
//                                is GptRepository.ApiResponse.Error -> {
//                                    displayMessage("Fortune Teller", gptResponse.message)
//                                }
//                            }
//                            scrollToBottom()
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
//    private fun initializeCardsArea() {
//        val layoutHelper = TarotLayoutHelper(this)
//        cardsArea.removeAllViews() // Clear any existing views
//
//        for (i in 1..numberOfCards) {
//            val cardView = ImageView(this).apply {
//                layoutParams = LinearLayout.LayoutParams(150, 200).apply {
//                    marginEnd = 8
//                }
//                setImageResource(R.drawable.card_back_1) // Set the card back as a placeholder
//                contentDescription = "Card $i"
//            }
//
//            // Add click listener to show full-screen dialog for this card
//            cardView.setOnClickListener {
//                showCardDialog(cardIndex = i, cardName = "Unknow")
//            }
//
//            cardsArea.addView(cardView)
//        }
//    }
//
//    private fun updateCardsAreaWithActualCards(cards: List<Card>) {
//        cardsArea.removeAllViews() // Clear placeholders
//        cards.forEachIndexed { index, card ->
//            val cardView = ImageView(this).apply {
//                layoutParams = LinearLayout.LayoutParams(150, 200).apply {
//                    marginEnd = 8
//                }
//                setImageResource(card.imageResId) // Map card name to image
//                contentDescription = card.name
//
//                // Click listener to show card details in dialog
//                setOnClickListener {
//                    showCardDialogWithCardInfo(card)
//                }
//            }
//            cardsArea.addView(cardView)
//        }
//    }
//
//
//    private fun showCardDialog(cardIndex: Int, cardName: String) {
//        val dialog = Dialog(this)
//        dialog.setContentView(R.layout.dialog_card_view)
//
//        val cardImage = dialog.findViewById<ImageView>(R.id.fullSizeCardImage)
//        val cardDescription = dialog.findViewById<TextView>(R.id.cardDescription)
//        val clickHint = dialog.findViewById<TextView>(R.id.clickHint)
//
//        cardImage.setImageResource(R.drawable.card_back) // Initially show the card back
//        cardDescription.text = cardName
//        clickHint.visibility = View.GONE
//        dialog.show()
//    }
//
//    private fun showCardDialogWithCardInfo(card: Card) {
//        val dialog = Dialog(this)
//        dialog.setContentView(R.layout.dialog_card_view)
//
//        val cardImage = dialog.findViewById<ImageView>(R.id.fullSizeCardImage)
//        val cardDescription = dialog.findViewById<TextView>(R.id.cardDescription)
//        val clickHint = dialog.findViewById<TextView>(R.id.clickHint)
//
//        cardImage.setImageResource(card.imageResId)
//        cardDescription.text = card.info // Display the card's meaning
//
//        // Set click listener for the card image
//        cardImage.setOnClickListener {
//            if (cardDescription.visibility == View.GONE) {
//                // Show the card description and hide the hint
//                cardDescription.visibility = View.VISIBLE
//                clickHint.visibility = View.GONE
//            } else {
//                // Hide the card description and show the hint
//                cardDescription.visibility = View.GONE
//                clickHint.visibility = View.VISIBLE
//            }
//        }
//
//        dialog.show()
//    }
//
//    private fun displayCardInterpretations(cards: List<Card>, gptResponse: String) {
//        val responses = gptResponse.split("\n").filter { it.isNotBlank() }
//        CoroutineScope(Dispatchers.Main).launch {
//            cards.forEachIndexed { index, card ->
//                val cardResponse = if (index < responses.size) responses[index] else "No interpretation provided."
//                displayMessage("Fortune Teller", "${card.name}: $cardResponse")
//                scrollToBottom()
//                delay(2000) // Wait for 2 seconds before showing the next card
//            }
//
//            // Add the conclusion message
//            val conclusion = responses.lastOrNull() ?: "No conclusive prediction provided."
//            displayMessage("Fortune Teller", "$conclusion")
//            scrollToBottom()
//        }
//    }
//
//    private fun scrollToBottom() {
//        chatScrollView.post {
//            chatScrollView.fullScroll(View.FOCUS_DOWN)
//        }
//    }
//
//
//    private fun startConversation() {
//        val introMessage =
//            "You have chosen the $selectedSpread spread. ${spreadDescription(selectedSpread)} What is your question?"
//        displayMessage("Fortune Teller", introMessage)
//    }
//
//    private fun displayMessage(sender: String, message: String) {
//        val messageLayout = LinearLayout(this).apply {
//            gravity = if (sender == "Me") Gravity.END else Gravity.START
//            setPadding(8, 8, 8, 8)
////            layoutParams = LinearLayout.LayoutParams(
////                LinearLayout.LayoutParams.MATCH_PARENT,
////                LinearLayout.LayoutParams.WRAP_CONTENT
////            ).apply {
////                setMargins(8, 8, 8, 8)
////            }
//            orientation = LinearLayout.HORIZONTAL
//        }
//
//        val icon = ImageView(this).apply {
//            layoutParams = LinearLayout.LayoutParams(100, 100).apply {
//                marginEnd = 8
//            }
//            setImageResource(
//                if (sender == "Me") R.drawable.user_icon else R.drawable.fortune_teller
//            )
//            scaleType = ImageView.ScaleType.CENTER_CROP
//        }
//
//        val textView = TextView(this).apply {
//            text = "$sender: $message"
//            textSize = 16f
//            setPadding(16, 12, 16, 12)
//            setTextColor(ContextCompat.getColor(this@GetCardsActivity, android.R.color.black))
//            background = ContextCompat.getDrawable(
//                this@GetCardsActivity,
//                if (sender == "Me") R.drawable.user_message_bg else R.drawable.gpt_message_bg
//            )
//            layoutParams = LinearLayout.LayoutParams(
//                // LinearLayout.LayoutParams.WRAP_CONTENT,
//                0,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                weight = 1f
//            }
//        }
//        messageLayout.addView(if (sender == "Me") textView else icon)
//        messageLayout.addView(if (sender == "Me") icon else textView)
//
//
//        chatArea.addView(messageLayout)
//        userInputField.text.clear()
//    }
//
//    private fun spreadDescription(spread: String?): String {
//        return when (spread) {
//            "Three Card Spread" -> "This spread is great for understanding the past, present, and future of your situation. It helps explain the flow of events and possible outcomes."
//            "Four Card Spread" -> "This spread helps you focus on the core issue of a problem, the obstacles you face, and the resources or strategies you can use to overcome them."
//            "Love Tree (Five Cards)" -> "This spread is perfect for analyzing love or relationship questions. It provides insights into yourself, the past, the present, and the potential future of the relationship."
//            "Friendship Spread (Six Cards)" -> "This spread helps you understand the dynamics of a friendship, including how you and your friend see each other, the current state of your relationship, and its future potential."
//            "Hexagram (Seven Cards)" -> "This spread is useful for analyzing any question in depth, exploring the past, present, future, external influences, and the ultimate outcome."
//            "Celtic Cross (Ten Cards)" -> "This spread is ideal for addressing complex situations. It offers a detailed exploration of your current situation, the challenges you face, and the likely outcome."
//            else -> "A versatile tarot spread suitable for exploring various questions and insights."
//        }
//    }
//
//    private fun buildCardInterpretationPrompt(spread: String?, cards: List<Card>, userQuestion: String?): String {
//        val cardDetails = cards.joinToString(separator = ", ") { "${it.name}: ${it.info}" }
//
//        return when (spread) {
//            "Three Card Spread" -> {
//                "You chose the Three Card Spread. This spread answers cause and effect questions. It represents 'Past → Present → Future' or 'Cause → Effect → Advice,' helping explain linear cause-and-effect relationships." +
//                        "The drawn cards are: $cardDetails. " +
//                        "Based on the question '$userQuestion,' interpret these cards with a focus on linear cause-and-effect relationships. " +
//                        "Focus on key insights using concise language, clear conclusive prediction based on the drawn cards."
//            }
//            "Four Card Spread" -> {
//                "You chose the Four Card Spread, which identifies the core of the problem. The first card represents the 'Core Issue,' followed by 'Obstacle,' 'Solution,' and 'Strength/Resources' in sequence." +
//                        "The cards are: $cardDetails. Based on the question '$userQuestion,' analyze the problem and suggest solutions. " +
//                        "Focus on key insights using concise language, clear conclusive prediction based on the drawn cards."
//            }
//            "Love Tree (Five Cards)" -> {
//                "You chose the Love Tree Spread for analyzing love-related questions. This spread is suitable for analyzing love-related questions. Cards are represents: 'Self,' 'Past/Cause,' 'Present/Solution,' 'Future/Potential,' and 'External/Internal Influences.' " +
//                        "The cards are: $cardDetails. Based on the question '$userQuestion,' provide a detailed love analysis and prediction. " +
//                        "Focus on key insights using concise language, clear conclusive prediction based on the drawn cards."
//            }
//            "Friendship Spread (Six Cards)" -> {
//                "You chose the Friendship. This spread is ideal for resolving friendship issues. Cards are laid out in a 3x2 grid: 'Your Perspective,' 'Friend’s Perspective,' 'Your View of the Relationship,' 'Friend’s View of the Relationship,' and 'Future Expectations for the Relationship.'" +
//                        "The cards are: $cardDetails. Based on the question '$userQuestion,' interpret the relationship dynamics and future potential. " +
//                        "Focus on key insights using concise language, clear conclusive prediction based on the drawn cards."
//            }
//            "Hexagram (Seven Cards)" -> {
//                "You chose the Hexagram Spread. This spread analyzes any issue in depth with a hexagram layout. Cards represent 'Past,' 'Present,' 'Future,' 'Solution,' 'External Factors,' 'Your Mindset,' and 'Outcome.' The drawn cards represent: 'Past,' 'Present,' 'Future,' 'Solution,' 'External Factors,' 'Your Mindset,' and 'Outcome.' " +
//                        "The cards are: $cardDetails. Based on the question '$userQuestion,' provide a comprehensive analysis and conclusion. " +
//                        "Focus on key insights using concise language, clear conclusive prediction based on the drawn cards."
//            }
//            "Celtic Cross (Ten Cards)" -> {
//                "You chose the Celtic Cross Spread to address complex issues. Cards represent: 'Current Situation,' 'Influencing Factors,' 'Aspirations,' 'Underlying Causes,' 'Past,' 'Future,' 'Self,' 'External Factors,' 'Hopes/Fears,' and 'Outcome.'" +
//                        "The cards are: $cardDetails. Based on the question '$userQuestion,' provide a thorough interpretation and prediction. " +
//                        "Focus on key insights using concise language, clear conclusive prediction based on the drawn cards."
//            }
//            else -> {
//                "The chosen spread is not specified. The cards are: $cardDetails. Based on the question '$userQuestion,' provide a general interpretation. " +
//                        "Focus on key insights using concise language, clear conclusive prediction based on the drawn cards."
//            }
//        }
//    }
//
//}
//    private fun displayCards(cards: List<Card>) {
//        cardsArea.removeAllViews()
//        cards.forEach { card ->
//            val cardView = ImageView(this).apply {
//                layoutParams = LinearLayout.LayoutParams(150, 200).apply {
//                    marginEnd = 8
//                }
//                setImageResource(card.imageResId)
//                contentDescription = card.name
//            }
//            cardsArea.addView(cardView)
//        }
//    }

//private fun getCardImageResource(cardName: String): Int {
//    val cardMap = mapOf(
//        "The Fool" to R.drawable.the_fool_great_0,
//        "The Magician" to R.drawable.the_magician_great_1,
//        "The High Priestess" to R.drawable.the_high_priestess_great_2,
//        "The Empress" to R.drawable.the_empress_great_3,
//        "The Emperor" to R.drawable.the_emperor_great_4,
//        "The Hierophant" to R.drawable.the_hierophant_great_5,
//        "The Lovers" to R.drawable.the_lovers_great_6,
//        "The Chariot" to R.drawable.the_chariot_great_7,
//        "Strength" to R.drawable.strength_great_8,
//        "The Hermit" to R.drawable.the_hermit_great_9,
//        "Wheel of Fortune" to R.drawable.the_wheel_of_fortune_great_10,
//        "Justice" to R.drawable.justice_great_11,
//        "The Hanged Man" to R.drawable.the_hanged_man_great_12,
//        "Death" to R.drawable.death_great_13,
//        "Temperance" to R.drawable.temperance_great_14,
//        "The Devil" to R.drawable.the_devil_great_15,
//        "The Tower" to R.drawable.the_tower_great_16,
//        "The Star" to R.drawable.the_star_great_17,
//        "The Moon" to R.drawable.the_moon_great_18,
//        "The Sun" to R.drawable.the_sun_great_19,
//        "Judgement" to R.drawable.judgement_great_20,
//        "The World" to R.drawable.the_world_great_21,
//        "Ace of Wands" to R.drawable.ace_of_wands_22,
//        "Two of Wands" to R.drawable.two_of_wands_23,
//        "Three of Wands" to R.drawable.three_of_wands_24,
//        "Four of Wands" to R.drawable.four_of_wands_25,
//        "Five of Wands" to R.drawable.five_of_wands_26,
//        "Six of Wands" to R.drawable.six_of_wands_27,
//        "Seven of Wands" to R.drawable.seven_of_wands_28,
//        "Eight of Wands" to R.drawable.eight_of_wands_29,
//        "Nine of Wands" to R.drawable.nine_of_wands_30,
//        "Ten of Wands" to R.drawable.ten_of_wands_31,
//        "Page of Wands" to R.drawable.page_of_wands_32,
//        "Knight of Wands" to R.drawable.knight_of_wands_33,
//        "Queen of Wands" to R.drawable.queen_of_wands_34,
//        "King of Wands" to R.drawable.king_of_wands_35,
//        "Ace of Cups" to R.drawable.ace_of_cups_36,
//        "Two of Cups" to R.drawable.two_of_cups_37,
//        "Three of Cups" to R.drawable.three_of_cups_38,
//        "Four of Cups" to R.drawable.four_of_Cups_39,
//        "Five of Cups" to R.drawable.five_of_cups_40,
//        "Six of Cups" to R.drawable.six_of_cups_41,
//        "Seven of Cups" to R.drawable.seven_of_cups_42,
//        "Eight of Cups" to R.drawable.eight_of_cups_43,
//        "Nine of Cups" to R.drawable.nine_of_cups_44,
//        "Ten of Cups" to R.drawable.ten_of_cups_45,
//        "Page of Cups" to R.drawable.page_of_cups_46,
//        "Knight of Cups" to R.drawable.knight_of_cups_47,
//        "Queen of Cups" to R.drawable.queen_of_cups_48,
//        "King of Cups" to R.drawable.king_of_cups_49,
//        "Ace of Swords" to R.drawable.ace_of_swords_50,
//        "Two of Swords" to R.drawable.two_of_swords_51,
//        "Three of Swords" to R.drawable.three_of_swords_52,
//        "Four of Swords" to R.drawable.four_of_swords_53,
//        "Five of Swords" to R.drawable.five_of_swords_54,
//        "Six of Swords" to R.drawable.six_of_swords_55,
//        "Seven of Swords" to R.drawable.seven_of_swords_56,
//        "Eight of Swords" to R.drawable.eight_of_swords_57,
//        "Nine of Swords" to R.drawable.nine_of_swords_58,
//        "Ten of Swords" to R.drawable.ten_of_swords_59,
//        "Page of Swords" to R.drawable.page_of_swords_60,
//        "Knight of Swords" to R.drawable.knight_of_swords_61,
//        "Queen of Swords" to R.drawable.queen_of_swords_62,
//        "King of Swords" to R.drawable.king_of_swords_63,
//        "Ace of Pentacles" to R.drawable.ace_of_pentacles_64,
//        "Two of Pentacles" to R.drawable.two_of_pentacles_65,
//        "Three of Pentacles" to R.drawable.three_of_pentacles_66,
//        "Four of Pentacles" to R.drawable.four_of_pentacles_67,
//        "Five of Pentacles" to R.drawable.five_of_pentacles_68,
//        "Six of Pentacles" to R.drawable.six_of_pentacles_69,
//        "Seven of Pentacles" to R.drawable.seven_of_pentacles_70,
//        "Eight of Pentacles" to R.drawable.eight_of_pentacles_71,
//        "Nine of Pentacles" to R.drawable.nine_of_pentacles_72,
//        "Ten of Pentacles" to R.drawable.ten_of_pentacles_73,
//        "Page of Pentacles" to R.drawable.page_of_pentacles_74,
//        "Knight of Pentacles" to R.drawable.knight_of_pentacles_75,
//        "Queen of Pentacles" to R.drawable.queen_of_pentacles_76,
//        "King of Pentacles" to R.drawable.king_of_pentacles_77,
//    )
//    return cardMap[cardName] ?: R.drawable.card_back_1 // Fallback to a default image
//}


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
////        // Initialize the GPT guide with user question prompt
////        gptGuide("What would you like to know?")
////
////        // Card click listeners
////        binding.drawCardButton.setOnClickListener {
////            if (drawnCards.size < cardCount) {
////                drawCard()
////            } else {
////                interpretCards()
////            }
////        }
//    }
//
////    private fun gptGuide(prompt: String) {
////        // Use GPT API to generate guidance text based on prompt
////        GptRepository().getGptResponse(prompt) { response ->
////            gptResponse.text = response
////            if (drawnCards.size == 0) {
////                // Start with a question for the user
////                askUserQuestion()
////            }
////        }
////    }
////
////    private fun askUserQuestion() {
////        // Prompt user to enter their question
////        val dialog = AlertDialog.Builder(this)
////            .setTitle("Ask Your Question")
////            .setView(EditText(this))
////            .setPositiveButton("Submit") { dialog, _ ->
////                val questionInput = (dialog as AlertDialog).findViewById<EditText>(android.R.id.text1)
////                userQuestion = questionInput?.text.toString()
////                gptGuide("Think about your question while drawing your cards.")
////            }
////            .create()
////        dialog.show()
////    }
////
////    private fun drawCard() {
////        TarotRepository().getCard { card ->
////            drawnCards.add(card.name)
////            binding.cardDisplay.text = drawnCards.joinToString(", ")
////
////            // Prompt user to continue or finish drawing
////            if (drawnCards.size == cardCount) {
////                gptGuide("Now let's interpret your cards.")
////            } else {
////                gptGuide("Draw the next card.")
////            }
////        }
////    }
////
////    private fun interpretCards() {
////        val prompt = "Based on the cards: ${drawnCards.joinToString(", ")} and the user's question: '$userQuestion', please provide an interpretation."
////        gptGuide(prompt)
////    }
//}
