package com.example.group3_starry.ui.match

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.group3_starry.databinding.FragmentMatchBinding

class MatchFragment : Fragment() {

    private var _binding: FragmentMatchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val matchViewModel = ViewModelProvider(this).get(MatchViewModel::class.java)
        matchViewModel.text.observe(viewLifecycleOwner) {
            binding.textTitle.text = it // Assuming `textTitle` is the ID for your title TextView
        }

        // Set up button click listener
        binding.buttonViewMatchResult.setOnClickListener {
            // Handle match result button click
        }

        // Set up click listeners for plus icons if needed
        binding.plusIconLeft.setOnClickListener {
            // Handle left circle click
        }
        binding.plusIconRight.setOnClickListener {
            // Handle right circle click
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
