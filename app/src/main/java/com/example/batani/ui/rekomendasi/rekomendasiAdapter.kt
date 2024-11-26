package com.example.batani.ui.rekomendasi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.batani.R

class TanamanAdapter(private val tanamanList: List<String>) : RecyclerView.Adapter<TanamanAdapter.TanamanViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TanamanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tanaman_item, parent, false)
        return TanamanViewHolder(view)
    }

    override fun onBindViewHolder(holder: TanamanViewHolder, position: Int) {
        val tanaman = tanamanList[position]
        holder.bind(tanaman)
    }

    override fun getItemCount(): Int = tanamanList.size

    inner class TanamanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.title_tanaman)

        fun bind(tanaman: String) {
            nameTextView.text = tanaman
        }
    }
}
