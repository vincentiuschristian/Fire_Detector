package com.dev.firedetector.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.firedetector.data.model.DataAlatModel
import com.dev.firedetector.databinding.HistoryListBinding

class HistoryAdapter(private val dataHistory: ArrayList<DataAlatModel>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    inner class HistoryViewHolder(private val binding: HistoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: DataAlatModel) {
            binding.apply {
                tvTemperature.text = data.temp.toString()
                tvKelembapan.text = "${data.hum.toString()}%"
                tvKualitasUdara.text = data.mqValue.toString()
                tvApiTerdeteksi.text = data.flameDetected.toString()
                tvTimeStamp.text =
                    data.timestamp.toString()
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

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<DataAlatModel>) {
        dataHistory.clear()
        dataHistory.addAll(newData)
        notifyDataSetChanged()
    }

}