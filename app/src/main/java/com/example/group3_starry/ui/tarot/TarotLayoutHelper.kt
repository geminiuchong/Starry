package com.example.group3_starry.ui.tarot

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import com.example.group3_starry.R

class TarotLayoutHelper(private val context: Context) {

    fun createCard(index: Int, onClick: (Int) -> Unit, imageResId: Int = R.drawable.card_back_1): ImageView {
        return ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(150, 200).apply {
                setMargins(4, 4, 4, 4)
            }
            setImageResource(imageResId)
            contentDescription = "Card $index"
            tag = "card_$index"

            setOnClickListener {
                onClick(index)
            }
        }
    }

    fun setupThreeCardSpread(parent: ViewGroup, cardImages: Map<Int, Int>, onCardClick: (Int) -> Unit): RelativeLayout {
        val layout = RelativeLayout(context)
        val cardPositions = mapOf(
            1 to Pair(0.6f, 0.9f),
            2 to Pair(0.9f, 0.9f),
            3 to Pair(1.2f, 0.9f)
        )
        positionCards(layout, cardPositions, cardImages, onCardClick)
        return layout
    }

    fun setupFourCardSpread(parent: ViewGroup,cardImages: Map<Int, Int>, onCardClick: (Int) -> Unit): ViewGroup {
        val layout = RelativeLayout(context)
        val cardPositions = mapOf(
            1 to Pair(0.6f, 0.9f),
            2 to Pair(0.9f, 1.4f),
            3 to Pair(0.9f, 0.4f),
            4 to Pair(1.2f, 0.9f)
        )
        positionCards(layout, cardPositions, cardImages, onCardClick)
        return layout
    }

    fun setupLoveTreeSpread(parent: ViewGroup,cardImages: Map<Int, Int>, onCardClick: (Int) -> Unit): RelativeLayout {
        val layout = RelativeLayout(context)
        val cardPositions = mapOf(
            1 to Pair(0.9f, 0.2f),  // Top
            2 to Pair(0.6f, 0.7f),  // Left
            3 to Pair(1.2f, 0.7f),  // Right
            4 to Pair(0.9f, 1.2f),  // Bottom
            5 to Pair(0.9f, 0.7f)   // Center
        )
        positionCards(layout, cardPositions, cardImages, onCardClick)
        return layout
    }

    fun setupFriendshipSpread(parent: ViewGroup,cardImages: Map<Int, Int>, onCardClick: (Int) -> Unit): RelativeLayout {
        val layout = RelativeLayout(context)
        val cardPositions = mapOf(
            1 to Pair(0.5f, 0.5f),
            2 to Pair(0.8f, 0.5f),
            3 to Pair(1.1f, 0.5f),
            4 to Pair(0.5f, 1f),
            5 to Pair(0.8f, 1f),
            6 to Pair(1.1f, 1f)
        )
        positionCards(layout, cardPositions, cardImages, onCardClick)
        return layout
    }

    fun setupHexagramSpread(parent: ViewGroup,cardImages: Map<Int, Int>, onCardClick: (Int) -> Unit): ViewGroup {
        val layout = RelativeLayout(context)
        val cardPositions = mapOf(
            1 to Pair(0.9f, 0.3f),
            2 to Pair(1.25f, 0.55f),
            3 to Pair(1.25f, 1.05f),
            4 to Pair(0.9f, 1.3f),
            5 to Pair(0.55f, 1.05f),
            6 to Pair(0.55f, 0.55f),
            7 to Pair(0.9f, 0.8f)
        )
        positionCards(layout, cardPositions, cardImages, onCardClick)
        return layout
    }

    fun setupCelticCrossSpread(parent: ViewGroup,cardImages: Map<Int, Int>, onCardClick: (Int) -> Unit): ViewGroup {
        val layout = RelativeLayout(context)
        val cardPositions = mapOf(
            1 to Pair(0.7f, 0.7f),  // Center
            2 to Pair(0.75f, 0.9f),  // Crossing card (overlapping card 1)
            3 to Pair(0.7f, 0.2f),  // Above
            4 to Pair(0.7f, 1.4f),  // Below
            5 to Pair(0.3f, 0.7f),  // Left
            6 to Pair(1.15f, 0.7f),  // Right
            7 to Pair(1.55f, 0.2f),  // Bottom right (far)
            8 to Pair(1.55f, 0.6f),  // Middle right (far)
            9 to Pair(1.55f, 1f),  // Top right (far)
            10 to Pair(1.55f, 1.4f)  // Topmost right (far)
        )
        positionCards(layout, cardPositions, cardImages, onCardClick)
        return layout
    }

    fun positionCards(
        layout: RelativeLayout,
        cardPositions: Map<Int, Pair<Float, Float>>,
        cardImages: Map<Int, Int>,
        onCardClick:(Int) -> Unit
    ) {
        layout.post {
            val parentWidth = layout.width.toFloat()/2
            val parentHeight = layout.height.toFloat()/2

            cardPositions.forEach { (index, position) ->
                val imageResId = cardImages[index] ?: R.drawable.card_back_1
                val cardView = createCard(index, onCardClick, imageResId)
                cardView.tag = "card_$index"
                Log.d("PositionCards", "Adding card_$index at position (${position.first}, ${position.second})")
                val params = RelativeLayout.LayoutParams(150, 200)
                cardView.layoutParams = params
                cardView.x = position.first * parentWidth
                cardView.y = position.second * parentHeight
                layout.addView(cardView, params)
            }
        }
    }

}



