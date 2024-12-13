package com.example.batani.ui.graph

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.batani.databinding.ItemGraphBinding
import com.example.batani.network.ForecastResponse
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.json.JSONObject

class ChartAdapter(
    private val context: Context,
    forecasts: List<ForecastResponse> // Menggunakan variabel ini langsung
) : RecyclerView.Adapter<ChartAdapter.ChartViewHolder>() {

    // Filter data pada saat penerimaan
    private val filteredForecasts = forecasts.filter {
        it.tanaman != null && it.plot != null && it.plot.isNotEmpty()
    }

    inner class ChartViewHolder(val binding: ItemGraphBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartViewHolder {
        val binding = ItemGraphBinding.inflate(LayoutInflater.from(context), parent, false)
        return ChartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChartViewHolder, position: Int) {
        val forecast = filteredForecasts[position]

        Log.d("rekomendasi adapter", forecast.toString())

        holder.binding.textView.text = "Grafik ${forecast.tanaman}"

        val truePriceEntries = mutableListOf<Entry>()
        val predictedPriceEntries = mutableListOf<Entry>()

        try {
            val plotData = JSONObject(forecast.plot)
            val dataArray = plotData.getJSONArray("data")

            for (i in 0 until dataArray.length()) {
                val dataSet = dataArray.getJSONObject(i)
                val yValues = dataSet.getJSONArray("y")

                for (j in 0 until yValues.length()) {
                    val x = j.toFloat()
                    val y = yValues.getDouble(j).toFloat()
                    if (dataSet.getString("name") == "True Price") {
                        truePriceEntries.add(Entry(x, y))
                    } else if (dataSet.getString("name") == "Predicted Price") {
                        predictedPriceEntries.add(Entry(x, y))
                    }
                }
            }
        } catch (e: Exception) {
            println("Error parsing plot: ${e.message}")
        }

        val truePriceDataSet = LineDataSet(truePriceEntries, "True Price").apply {
            color = context.getColor(android.R.color.holo_blue_dark)
            setCircleColor(context.getColor(android.R.color.holo_blue_dark))
            lineWidth = 2f
            circleRadius = 4f
        }

        val predictedPriceDataSet = LineDataSet(predictedPriceEntries, "Predicted Price").apply {
            color = context.getColor(android.R.color.holo_red_dark)
            setCircleColor(context.getColor(android.R.color.holo_red_dark))
            lineWidth = 2f
            circleRadius = 4f
        }

        val lineData = LineData(truePriceDataSet, predictedPriceDataSet)
        holder.binding.lineChart.apply {
            data = lineData
            description.isEnabled = false
            animateX(1000)
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            invalidate()
        }
    }

    override fun getItemCount(): Int = filteredForecasts.size
}
