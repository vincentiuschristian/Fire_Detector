package com.dev.firedetector.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.data.repository.FireRepository
import com.dev.firedetector.data.response.SensorDataResponse
import com.dev.firedetector.util.Result
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: FireRepository) : ViewModel() {
    private val _latestSensorDataZona1 = MutableLiveData<Result<SensorDataResponse>>()
    val latestSensorDataZona1: LiveData<Result<SensorDataResponse>> get() = _latestSensorDataZona1

    private val _latestSensorDataZona2 = MutableLiveData<Result<SensorDataResponse>>()
    val latestSensorDataZona2: LiveData<Result<SensorDataResponse>> get() = _latestSensorDataZona2

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    init {
        getLatestDataZona1()
        getLatestDataZona2()
    }

    fun getLatestDataZona1() {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.getLatestDataZona1()
            _latestSensorDataZona1.postValue(result)
            _loading.value = false
        }
    }

    fun getLatestDataZona2() {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.getLatestDataZona2()
            _latestSensorDataZona2.postValue(result)
            _loading.value = false
        }
    }
}