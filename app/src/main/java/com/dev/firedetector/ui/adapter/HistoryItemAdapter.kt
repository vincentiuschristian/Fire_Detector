//package com.dev.firedetector.ui.adapter
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.dev.firedetector.R
//import com.dev.firedetector.data.response.SensorDataResponse
//
//class HistoryItemAdapter(
//    private val items: List<SensorDataResponse>,
//    private val onItemClickListener: (SensorDataResponse) -> Unit
//) : RecyclerView.Adapter<HistoryItemAdapter.ViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = HistoryItemDetailBinding.inflate(
//            LayoutInflater.from(parent.context), parent, false
//        )
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(items[position], onItemClickListener)
//    }
//
//    override fun getItemCount() = items.size
//
//    inner class ViewHolder(private val binding: HistoryItemDetailBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(data: SensorDataResponse, clickListener: (SensorDataResponse) -> Unit) {
//            binding.apply {
//                tvTemperature.text = data.temperature.toString()
//                tvKelembapan.text = "${data.humidity}%"
//                tvKualitasUdara.text = data.mqStatus
//                tvApiTerdeteksi.text = data.flameStatus
//                data.timestamp?.let { timestamp ->
//                    tvTimeStamp.text = formatTimestamp(timestamp)
//                }
//            }
//
//            binding.root.setOnClickListener {
//                clickListener(data)
//            }
//        }
//    }
//
//    private fun formatTimestamp(timestamp: String): String {
//        // Contoh: ubah format timestamp jika perlu
//        return timestamp.replace("T", " ").substringBefore(".")
//    }
//}