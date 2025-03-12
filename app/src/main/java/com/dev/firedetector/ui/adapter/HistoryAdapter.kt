package com.dev.firedetector.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.firedetector.data.response.SensorDataResponse
import com.dev.firedetector.databinding.HistoryListBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    // List untuk menyimpan data history sensor
    private val dataHistory = mutableListOf<SensorDataResponse>()

    // ViewHolder untuk HistoryAdapter
    inner class HistoryViewHolder(private val binding: HistoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: SensorDataResponse) {
            binding.apply {
                tvTemperature.text = data.temperature.toString() // Menampilkan suhu
                tvKelembapan.text = "${data.humidity}%" // Menampilkan kelembapan
                tvKualitasUdara.text = data.mqStatus // Menampilkan status kualitas udara
                tvApiTerdeteksi.text = data.flameStatus // Menampilkan status api terdeteksi

                // Format timestamp jika tersedia
                data.timestamp?.let { timestamp ->
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
                timestamp // Jika gagal, tampilkan timestamp asli
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        // Inflate layout item history
        val binding = HistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataHistory.size // Jumlah item dalam list
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        // Bind data ke ViewHolder
        holder.bind(dataHistory[position])
    }

    // Fungsi untuk mengupdate data di adapter
    fun updateData(newData: List<SensorDataResponse>) {
        dataHistory.clear() // Hapus data lama
        dataHistory.addAll(newData) // Tambahkan data baru
        notifyDataSetChanged() // Beri tahu adapter bahwa data telah berubah
    }
}