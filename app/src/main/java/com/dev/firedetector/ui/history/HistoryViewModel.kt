package com.dev.firedetector.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.data.response.SensorDataResponse
import com.dev.firedetector.data.repository.FireRepository
import com.dev.firedetector.util.Result
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: FireRepository) : ViewModel() {


    private val _sensorHistory = MutableLiveData<Result<List<SensorDataResponse>>>()
    val sensorHistory: LiveData<Result<List<SensorDataResponse>>> get() = _sensorHistory

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun getSensorHistory() {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.getSensorHistory()
            _sensorHistory.postValue(result)
            _loading.value = false
        }
    }

}
