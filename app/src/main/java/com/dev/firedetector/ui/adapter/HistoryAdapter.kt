package com.dev.firedetector.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dev.firedetector.data.response.SensorDataResponse
import com.dev.firedetector.databinding.HistoryListBinding
import java.text.SimpleDateFormat

class HistoryAdapter : ListAdapter<SensorDataResponse, HistoryAdapter.HistoryViewHolder>(DiffCallback()) {

    inner class HistoryViewHolder(private val binding: HistoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: SensorDataResponse) {
            binding.apply {
                tvTemperature.text = data.temperature.toString()
                tvKelembapan.text = "${data.humidity}%"
                tvKualitasUdara.text = data.mqStatus
                tvApiTerdeteksi.text = data.flameStatus
                val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
                isoFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
                val localFormat = SimpleDateFormat("dd MMM yyyy HH:mm", java.util.Locale("id", "ID"))
                localFormat.timeZone = java.util.TimeZone.getTimeZone("Asia/Jakarta")

                val formatted = try {
                    val date = isoFormat.parse(data.timestamp)
                    localFormat.format(date!!)
                } catch (e: Exception) {
                    data.timestamp
                }

                tvTimeStamp.text = "Waktu: $formatted"
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = HistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<SensorDataResponse>() {
        override fun areItemsTheSame(oldItem: SensorDataResponse, newItem: SensorDataResponse): Boolean {
            return oldItem.macAddress == newItem.macAddress
        }

        override fun areContentsTheSame(oldItem: SensorDataResponse, newItem: SensorDataResponse): Boolean {
            return oldItem == newItem
        }
    }
}