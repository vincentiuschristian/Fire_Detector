package com.dev.firedetector.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.firedetector.data.model.DataFire
import com.dev.firedetector.databinding.HistoryListBinding

class HistoryAdapter(private val dataHistory: ArrayList<DataFire>): RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>(){
    inner class HistoryViewHolder(private val binding: HistoryListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataFire){
            binding.apply {
                tvTemperature.text = data.temp.toString()
                tvKelembapan.text = data.hum.toString()
                tvKualitasUdara.text = data.gasLevel.toString()
                tvApiTerdeteksi.text = data.flameDetected.toString()
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
    fun updateData(newData: List<DataFire>){
        dataHistory.clear()
        dataHistory.addAll(newData)
        notifyDataSetChanged()
    }

}