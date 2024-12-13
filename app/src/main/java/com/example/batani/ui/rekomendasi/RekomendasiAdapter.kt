package com.example.batani.ui.rekomendasi

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.batani.databinding.ItemRekomendasiBinding
import com.example.batani.network.RekomendasiWithInfoResponseItem

class RekomendasiWithInfoAdapter :
    RecyclerView.Adapter<RekomendasiWithInfoAdapter.RekomendasiViewHolder>() {

    private val rekomendasiList = mutableListOf<RekomendasiWithInfoResponseItem>()
    private lateinit var onItemClickCallback: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    fun updateData(newData: List<RekomendasiWithInfoResponseItem>) {
        rekomendasiList.clear()
        rekomendasiList.addAll(newData)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RekomendasiViewHolder {
        val binding = ItemRekomendasiBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RekomendasiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RekomendasiViewHolder, position: Int) {
        val item = rekomendasiList[position]

        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(item)
        }
    }

    override fun getItemCount(): Int = rekomendasiList.size

    inner class RekomendasiViewHolder(private val binding: ItemRekomendasiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RekomendasiWithInfoResponseItem) {
            binding.tvNamaTanaman.text = item.tanaman
            binding.tvDeskripsiTanaman.text = item.info?.deskripsi ?: "Deskripsi tidak tersedia"
            Glide.with(binding.root.context)
                .load(item.info?.gambar ?: "")
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.gambarTanamanan)
        }
    }
    interface OnItemClickCallback {
        fun onItemClicked(data: RekomendasiWithInfoResponseItem)
    }

}
