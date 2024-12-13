package com.example.batani.ui.predikisiCuaca

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.batani.R
import com.example.batani.databinding.ItemCuacaBinding
import com.example.batani.network.WeatherDetail

class PrediksiAdapter(private val listCuaca: ArrayList<WeatherDetail>) : RecyclerView.Adapter<PrediksiAdapter.ListCuacaViewHolder>() {

    fun updateData(newData: List<WeatherDetail>) {
        listCuaca.clear()
        listCuaca.addAll(newData)
        notifyDataSetChanged()
    }

    class ListCuacaViewHolder(private val binding: ItemCuacaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(weatherDetail: WeatherDetail) {
            with(binding) {
                localtime.text = weatherDetail.local_datetime
                kecepataAngin.text = "Kecepatan Angin : ${weatherDetail.ws} km/jam"
                suhu.text = "Suhu : ${weatherDetail.t}°C"
                tutupanAwan.text = "Tutupan Awan: ${weatherDetail.tcc}%"
                weatherDesc.text = weatherDetail.weather_desc


                val imageResId = when {
                    weatherDetail.weather_desc.contains("Cerah", ignoreCase = true) -> R.drawable.haricerah
                    weatherDetail.weather_desc.contains("Cerah Berawan", ignoreCase = true) -> R.drawable.partly_cloudy
                    weatherDetail.weather_desc.contains("Berawan", ignoreCase = true) -> R.drawable.cloudy
                    weatherDetail.weather_desc.contains("Hujan Ringan", ignoreCase = true) -> R.drawable.rainy
                    weatherDetail.weather_desc.contains("Hujan Sedang", ignoreCase = true) -> R.drawable.rainstorm
                    weatherDetail.weather_desc.contains("Hujan Lebar", ignoreCase = true) -> R.drawable.hujanlebat
                    else -> R.drawable.hujanlebat
                }

                val url = "https://github.com/batani2024/Machine-Learning/raw/main/folder%20gmbr/gambar_anggur.jpg"
                 Glide.with(imgItemPhoto.context)
                    .load(imageResId)
                    .into(imgItemPhoto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCuacaViewHolder {
        val binding = ItemCuacaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListCuacaViewHolder(binding)
    }

    override fun getItemCount(): Int = listCuaca.size

    override fun onBindViewHolder(holder: ListCuacaViewHolder, position: Int) {
        val weatherDetail = listCuaca[position]
        holder.bind(weatherDetail)
    }
}
