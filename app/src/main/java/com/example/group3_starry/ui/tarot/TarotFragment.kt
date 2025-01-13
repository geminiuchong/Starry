package com.example.group3_starry.ui.tarot

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.group3_starry.databinding.FragmentTarotBinding

class TarotFragment : Fragment() {

    private var _binding: FragmentTarotBinding? = null
    private val binding get() = _binding!!

    private lateinit var tarotOptionsAdapter: TarotOptionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTarotBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize RecyclerView
        val tarotOptions = getTarotOptions()
        tarotOptionsAdapter = TarotOptionsAdapter(tarotOptions) { selectedOption ->
            navigateToGetCardsActivity(selectedOption)
        }

        binding.tarotRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tarotOptionsAdapter
        }

        return root
    }

    private fun getTarotOptions(): List<TarotOption> {
        return listOf(
            TarotOption(
                "Three Card Spread",
                "This spread uses three cards to show the past, present, and future or cause, effect, and advice. It’s great for linear story interpretations.",
                3
            ),
            TarotOption(
                "Four Card Spread",
                "The Four-Card Spread reveals the core issue, obstacles, strategies, and advantages/resources for the problem at hand.",
                4
            ),
            TarotOption(
                "Love Tree (Five Cards)",
                "This spread analyzes romantic relationships with five cards forming a T shape. Each card represents a key aspect of love.",
                5
            ),
            TarotOption(
                "Friendship Spread (Six Cards)",
                "A six-card spread to analyze friendships. Each pair of cards examines perspectives, relationships, and future expectations.",
                6
            ),
            TarotOption(
                "Hexagram (Seven Cards)",
                "The Hexagram Spread is a versatile layout to analyze any problem. Cards form a star pattern to show past, present, future, solutions, and results.",
                7
            ),
            TarotOption(
                "Celtic Cross (Ten Cards)",
                "The Celtic Cross is a complex spread for in-depth analysis. It covers current states, influences, hopes, fears, and outcomes.",
                10
            )
        )
    }

    private fun navigateToGetCardsActivity(option: TarotOption) {
        val intent = Intent(requireContext(), GetCardsActivity::class.java).apply {
            putExtra("spreadName", option.name)
            putExtra("cardCount", option.cardCount)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class TarotOption(
    val name: String,
    val description: String,
    val cardCount: Int
)
