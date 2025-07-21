package com.dev.firedetector.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dev.firedetector.data.response.SensorDataResponse
import com.dev.firedetector.databinding.ListItemBinding

class ListSensorAdapter(private val onMapClick: (SensorDataResponse) -> Unit) :
    ListAdapter<SensorDataResponse, ListSensorAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: SensorDataResponse) {
            binding.apply {
                txtMacAddress.text = item.macAddress
                txtTemperature.text = "${item.temperature}°C"
                txtHumidity.text = "${item.humidity}%"
                txtAirQuality.text = item.mqStatus
                txtFireDetection.text = item.flameStatus
                txtTimestampHome.text = "Last Updated: ${item.timestamp}"
                txtMacAddress.text = " Mac Address: ${item.macAddress}"

                btnMaps.setOnClickListener {
                    onMapClick(item)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SensorDataResponse>() {
        override fun areItemsTheSame(
            oldItem: SensorDataResponse,
            newItem: SensorDataResponse
        ): Boolean {
            return oldItem.macAddress == newItem.macAddress
        }

        override fun areContentsTheSame(
            oldItem: SensorDataResponse,
            newItem: SensorDataResponse
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

}
