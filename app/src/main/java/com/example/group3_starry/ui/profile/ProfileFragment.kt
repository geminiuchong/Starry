package com.example.group3_starry.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.group3_starry.R
import com.example.group3_starry.databinding.FragmentProfileBinding
import com.example.group3_starry.network.ProfileChartData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel
    private lateinit var chartAdapter: ChartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Apply Profile specific theme
        activity?.theme?.applyStyle(R.style.Theme_Group3_Starry_Profile, true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        // Initialize RecyclerView and adapter
        setupRecyclerView()

        // Set up click listeners
        setupClickListeners()

        // Set up observers
        setupObservers()

        // Load sample data for now
        viewModel.loadSampleData()
    }

    private fun setupRecyclerView() {
        chartAdapter = ChartAdapter()
        binding.chartRecyclerView.apply {
            adapter = chartAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        // Tab clicks
        binding.chartTab.setOnClickListener {
            viewModel.setChartSelected(true)
        }

        binding.aspectsTab.setOnClickListener {
            viewModel.setChartSelected(false)
        }

        // Settings click
        binding.settingsIcon.setOnClickListener {
            viewModel.onSettingsClick()
        }

        // View pie chart click
        binding.viewPieChartButton.setOnClickListener {
            viewModel.onViewPieChartClick()
        }
    }

    private fun setupObservers() {
        // User info observers
        viewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.userName.text = name
        }

        viewModel.birthInfo.observe(viewLifecycleOwner) { info ->
            binding.birthDateTime.text = info
        }

        // Sign info observer
        viewModel.signInfo.observe(viewLifecycleOwner) { (sun, moon, asc) ->
            binding.sunSign.text = sun
            binding.moonSign.text = moon
            binding.ascendantSign.text = asc
        }

        // Chart data observer
        viewModel.chartData.observe(viewLifecycleOwner) { chartData ->
            chartAdapter.updateItems(chartData)
        }

        // Tab selection observer
        viewModel.isChartSelected.observe(viewLifecycleOwner) { isChartSelected ->
            updateTabSelection(isChartSelected)
        }
    }

    private fun updateTabSelection(isChartSelected: Boolean) {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.tab_selected)
        val unselectedColor = ContextCompat.getColor(requireContext(), R.color.tab_unselected)

        binding.chartTab.setTextColor(if (isChartSelected) selectedColor else unselectedColor)
        binding.aspectsTab.setTextColor(if (!isChartSelected) selectedColor else unselectedColor)

        binding.chartRecyclerView.visibility = if (isChartSelected) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        // Restore original theme
        activity?.theme?.applyStyle(R.style.Theme_Group3_Starry_NoActionBar, true)
        _binding = null
    }
}