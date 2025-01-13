package com.example.group3_starry.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.group3_starry.R
import com.example.group3_starry.databinding.FragmentProfileBinding
import com.example.group3_starry.utils.ViewModelFactory

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel
    private lateinit var chartAdapter: ChartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        viewModel = ViewModelProvider(this, ViewModelFactory(requireActivity().application))[ProfileViewModel::class.java]
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
        viewModel.loadData()
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
        binding.apply {
            chartTab.setOnClickListener {
                viewModel.setChartSelected(true)
            }

//            aspectsTab.setOnClickListener {
//                viewModel.setChartSelected(false)
//            }

            viewPieChartButton.setOnClickListener {
                viewModel.onViewPieChartClick()
            }
        }
    }

    private fun setupObservers() {
        viewModel.apply {
            isLoading.observe(viewLifecycleOwner) { isLoading ->
                binding.chartRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
            }

            error.observe(viewLifecycleOwner) { error ->
                error?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }

            userName.observe(viewLifecycleOwner) { name ->
                binding.userName.text = name
            }

            birthInfo.observe(viewLifecycleOwner) { info ->
                binding.birthDateTime.text = info
            }

            signInfo.observe(viewLifecycleOwner) { (sun, moon, asc) ->
                binding.apply {
                    sunSign.text = sun
                    moonSign.text = moon
                    ascendantSign.text = asc
                }
            }

            chartData.observe(viewLifecycleOwner) { chartData ->
                chartAdapter.updateItems(chartData)
            }

            isChartSelected.observe(viewLifecycleOwner) { isChartSelected ->
                updateTabSelection(isChartSelected)
            }

            showPieChart.observe(viewLifecycleOwner) { show ->
                binding.chartRecyclerView.visibility = if (show) View.GONE else View.VISIBLE
                binding.pieChartView.visibility = if (show) View.VISIBLE else View.GONE
            }

            pieChartData.observe(viewLifecycleOwner) { data ->
                binding.pieChartView.setPlanetPositions(data)
            }
        }
    }

    private fun updateTabSelection(isChartSelected: Boolean) {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.tab_selected)
        val unselectedColor = ContextCompat.getColor(requireContext(), R.color.tab_unselected)

        binding.apply {
            chartTab.setTextColor(if (isChartSelected) selectedColor else unselectedColor)
//            aspectsTab.setTextColor(if (!isChartSelected) selectedColor else unselectedColor)
            chartRecyclerView.visibility = if (isChartSelected) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.theme?.applyStyle(R.style.Theme_Group3_Starry_NoActionBar, true)
        _binding = null
    }
}