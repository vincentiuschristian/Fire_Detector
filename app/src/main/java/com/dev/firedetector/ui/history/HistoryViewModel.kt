package com.dev.firedetector.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dev.firedetector.data.repository.FireRepository
import com.dev.firedetector.data.response.HistoryResponse
import com.dev.firedetector.util.Result

class HistoryViewModel(private val repository: FireRepository) : ViewModel() {

    fun getHistory(macAddress: String): LiveData<Result<HistoryResponse>> {
        return repository.getSensorHistory(macAddress)
    }

    fun getFilteredHistory(macAddress: String, range: String): LiveData<Result<HistoryResponse>> {
        return repository.getFilteredHistory(macAddress, range)
    }
}
