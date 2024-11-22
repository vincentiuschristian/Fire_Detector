package com.dev.firedetector.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.firedetector.data.model.History

class HistoryAdapter(private val dataHistory: ArrayList<History>): RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>(){
    inner class HistoryViewHolder(private val binding: HistoryListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(history: History){
            binding.apply {
                tvJobVacancy.text = history.jobText
                tvResult.text = history.resultScan
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ResultFragment::class.java)
                intent.putExtra(ResultFragment.KEY_DETAIL, history)
                itemView.context.startActivity(intent)
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
    fun updateData(newData: List<History>){
        dataHistory.clear()
        dataHistory.addAll(newData)
        notifyDataSetChanged()
    }

}