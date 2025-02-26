package com.dev.firedetector.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.data.model.SensorDataResponse
import com.dev.firedetector.data.repository.FireRepository
import com.dev.firedetector.util.Result
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: FireRepository) : ViewModel() {
    private val _latestSensorData = MutableLiveData<Result<SensorDataResponse>>()
    val latestSensorData: LiveData<Result<SensorDataResponse>> get() = _latestSensorData

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun getLatestSensorData() {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.getLatestSensorData()
            _latestSensorData.postValue(result)
            _loading.value = false
        }
    }
}