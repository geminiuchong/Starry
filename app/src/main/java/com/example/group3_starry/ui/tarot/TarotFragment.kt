package com.example.group3_starry.ui.tarot

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.group3_starry.databinding.FragmentTarotBinding

//class TarotFragment : Fragment() {
//
//    private var _binding: FragmentTarotBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val viewModel = ViewModelProvider(this).get(TarotViewModel::class.java)
//        _binding = FragmentTarotBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        // Set up button click listeners
//        binding.btnGet3Cards.setOnClickListener {
//            navigateToGetCardsActivity(3)
//        }
//        binding.btnGetXCards.setOnClickListener {
//            navigateToGetCardsActivity(chooseCardCount())
//        }
//
//        return root
//    }
//
//    private fun navigateToGetCardsActivity(cardCount: Int) {
//        val intent = Intent(requireContext(), GetCardsActivity::class.java)
//        intent.putExtra("cardCount", cardCount)
//        startActivity(intent)
//    }
//
//    private fun chooseCardCount(): Int {
//        // Logic to choose card count based on user preference for "X cards"
//        return 5 // Example
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
