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
    private val _latestSensorDataRuangTamu = MutableLiveData<Result<SensorDataResponse>>()
    val latestSensorDataRuangTamu: LiveData<Result<SensorDataResponse>> get() = _latestSensorDataRuangTamu

    private val _latestSensorDataKamar = MutableLiveData<Result<SensorDataResponse>>()
    val latestSensorDataKamar: LiveData<Result<SensorDataResponse>> get() = _latestSensorDataRuangTamu

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    init {
        getLatestDataRuangTamu()
        getLatestDataKamar()
    }

    fun getLatestDataRuangTamu() {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.getLatestDataRuangTamu()
            _latestSensorDataRuangTamu.postValue(result)
            _loading.value = false
        }
    }

    fun getLatestDataKamar() {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.getLatestDataKamar()
            _latestSensorDataKamar.postValue(result)
            _loading.value = false
        }
    }
}