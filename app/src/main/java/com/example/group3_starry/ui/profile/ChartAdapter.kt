package com.example.group3_starry.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.group3_starry.R
import com.example.group3_starry.network.ProfileChartData

class ChartAdapter : RecyclerView.Adapter<ChartAdapter.ChartViewHolder>() {
    private var items = listOf<ProfileChartData>()

    fun updateItems(newItems: List<ProfileChartData>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chart_expandable, parent, false)
        return ChartViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ChartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val signText: TextView = itemView.findViewById(R.id.signText)
        private val planetText: TextView = itemView.findViewById(R.id.planetText)
        private val houseText: TextView = itemView.findViewById(R.id.houseText)
        private val analysisText: TextView = itemView.findViewById(R.id.analysisText)
        private val headerLayout: LinearLayout = itemView.findViewById(R.id.headerLayout)
        private val expandableLayout: LinearLayout = itemView.findViewById(R.id.expandableLayout)

        fun bind(item: ProfileChartData) {
            signText.text = "${item.signSymbol} ${item.sign}"
            planetText.text = item.planetSymbol
            houseText.text = item.house
            analysisText.text = item.analysis

            expandableLayout.visibility = if (item.isExpanded) View.VISIBLE else View.GONE

            headerLayout.setOnClickListener {
                val newExpandedState = !item.isExpanded
                item.isExpanded = newExpandedState
                expandableLayout.visibility = if (newExpandedState) View.VISIBLE else View.GONE
            }
        }
    }
}