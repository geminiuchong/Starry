package com.example.group3_starry.ui.tarot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.group3_starry.R

class TarotOptionsAdapter(
    private val options: List<TarotOption>,
    private val onOptionSelected: (TarotOption) -> Unit
) : RecyclerView.Adapter<TarotOptionsAdapter.TarotOptionViewHolder>() {

    inner class TarotOptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tarotOptionName)
        val description: TextView = itemView.findViewById(R.id.tarotOptionDescription)
        val playButton: Button = itemView.findViewById(R.id.tarotOptionPlayButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TarotOptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tarot_option, parent, false)
        return TarotOptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TarotOptionViewHolder, position: Int) {
        val option = options[position]
        holder.name.text = option.name
        holder.description.text = option.description
        holder.playButton.setOnClickListener { onOptionSelected(option) }
    }

    override fun getItemCount() = options.size
}
