package com.dev.firedetector.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.firedetector.data.response.SensorDataResponse
import com.dev.firedetector.databinding.HistoryListBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val dataHistory = mutableListOf<SensorDataResponse>()

    inner class HistoryViewHolder(private val binding: HistoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: SensorDataResponse) {
            binding.apply {
                tvTemperature.text = data.temperature.toString()
                tvKelembapan.text = "${data.humidity}%"
                tvKualitasUdara.text = data.mqStatus
                tvApiTerdeteksi.text = data.flameStatus
                data.timestamp.let { timestamp ->
                    tvTimeStamp.text = formatTimestamp(timestamp)
                }
            }
        }

        private fun formatTimestamp(timestamp: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                val date = inputFormat.parse(timestamp)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                timestamp
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = HistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataHistory.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(dataHistory[position])
    }

    fun updateData(newData: List<SensorDataResponse>) {
        dataHistory.clear()
        dataHistory.addAll(newData)
        notifyDataSetChanged()
    }
}