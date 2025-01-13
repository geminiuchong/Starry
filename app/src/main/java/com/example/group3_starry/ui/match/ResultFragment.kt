package com.example.group3_starry.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.group3_starry.R
import com.example.group3_starry.network.ApiClient
import com.example.group3_starry.network.SynastryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultFragment : Fragment() {

    private lateinit var tableLayout: TableLayout
    private lateinit var mostCompatibleTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.activity_result_fragment, container, false)

        // Initialize views
        tableLayout = root.findViewById(R.id.results_table)
        mostCompatibleTextView = root.findViewById(R.id.most_compatible_text)

        // Retrieve data passed from MatchFragment
        val userName = arguments?.getString("userName") ?: ""
        val userDob = arguments?.getString("userDob") ?: ""
        val partners = listOfNotNull(
            arguments?.getString("partner1Name") to arguments?.getString("partner1Dob"),
            arguments?.getString("partner2Name") to arguments?.getString("partner2Dob"),
            arguments?.getString("partner3Name") to arguments?.getString("partner3Dob")
        ).filter { it.first != null && it.second != null }

        // Fetch and display results
        lifecycleScope.launch {
            val results = fetchSynastryScores(
                userName, userDob,
                partners.map { Pair(it.first!!, it.second!!) }
            )
            displayResults(results)
        }

        return root
    }

    private suspend fun fetchSynastryScores(
        userName: String,
        userDob: String,
        partners: List<Pair<String, String>>
    ): List<SynastryResponse> {
        val responses = mutableListOf<SynastryResponse>()

        try {
            for ((partnerName, partnerDob) in partners) {
                val response = withContext(Dispatchers.IO) {
                    ApiClient.starLoveMatchService.getSynastry(
                        name = userName,
                        dob = userDob,
                        partnerName = partnerName,
                        partnerDob = partnerDob
                    )
                }
                if (response.isSuccessful) {
                    response.body()?.firstOrNull()?.let { responses.add(it) }
                }
            }
        } catch (e: Exception) {
            // Handle any network errors
        }

        return responses
    }

    private fun displayResults(results: List<SynastryResponse>) {
        if (results.isEmpty()) {
            mostCompatibleTextView.text = "No synastry data found."
            return
        }

        // Add headers to the table
        val headerRow = TableRow(context).apply {
            addCell("Category", isHeader = true)
            results.forEach { addCell(it.name, isHeader = true) }
        }
        tableLayout.addView(headerRow)

        // Add rows for compatibility categories
        val categories = listOf(
            "Love Compatibility" to { r: SynastryResponse -> r.love },
            "Intellectual Compatibility" to { r: SynastryResponse -> r.intellectual },
            "Physical Compatibility" to { r: SynastryResponse -> r.physical },
            "Strength of Connection" to { r: SynastryResponse -> r.strength },
            "Conflicts" to { r: SynastryResponse -> r.bad },
            "Overall Score" to { r: SynastryResponse -> r.overall }
        )

        categories.forEach { (label, extractor) ->
            val row = TableRow(context).apply {
                addCell(label)
                results.forEach { addCell("%.2f".format(extractor(it))) }
            }
            tableLayout.addView(row)
        }

        // Find the most compatible partner
        val mostCompatible = results.maxByOrNull { it.overall }
        mostCompatible?.let {
            mostCompatibleTextView.text = "Most Compatible Partner:\nName: ${it.name}\nOverall Score: %.2f".format(it.overall)
        }
    }

    private fun TableRow.addCell(text: String, isHeader: Boolean = false) {
        val textView = TextView(context).apply {
            this.text = text
            setPadding(16, 8, 16, 8)
            textSize = if (isHeader) 16f else 14f
            setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isHeader) R.color.black else R.color.dark_gray
                )
            )
        }
        addView(textView)
    }
}




