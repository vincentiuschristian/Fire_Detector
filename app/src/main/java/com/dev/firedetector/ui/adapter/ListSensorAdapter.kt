package com.dev.firedetector.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dev.firedetector.R
import com.dev.firedetector.core.data.source.remote.response.SensorDataResponse
import com.dev.firedetector.databinding.ListItemBinding
import com.dev.firedetector.ui.history.HistoryActivity
import com.dev.firedetector.util.DangerConditions

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

                btnHistory.setOnClickListener {
                    val intent = Intent(binding.root.context, HistoryActivity::class.java).apply {
                        putExtra("mac_address", item.macAddress)
                    }
                    binding.root.context.startActivity(intent)
                }

                btnMaps.setOnClickListener {
                    onMapClick(item)
                }

                val dangerConditions = DangerConditions.fromSensorData(
                    item.temperature,
                    item.mqStatus,
                    item.flameStatus
                )

                applyDangerUI(dangerConditions)
            }
        }

        private fun applyDangerUI(dangerConditions: DangerConditions) {
            val redColor = ContextCompat.getColor(binding.root.context, R.color.red)
            val normalColor = ContextCompat.getColor(binding.root.context, R.color.cardview_color)

            binding.apply {
                cvTemperature.setCardBackgroundColor(
                    if (dangerConditions.isHighTemperature) redColor else normalColor
                )
                cvAirQuality.setCardBackgroundColor(
                    if (dangerConditions.isGasDetected) redColor else normalColor
                )
                cvIsFire.setCardBackgroundColor(
                    if (dangerConditions.isFireDetected) redColor else normalColor
                )
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
