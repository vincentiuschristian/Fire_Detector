package com.dev.firedetector.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.data.model.DataFire
import com.dev.firedetector.data.repository.FireRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: FireRepository) : ViewModel(){

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _sensorData = MutableLiveData<DataFire?>()
    val sensorData: LiveData<DataFire?> = _sensorData

    fun fetchLatestSensorData() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val data = repository.getSensorData()
                // Ambil data terbaru, misalnya item terakhir dari list
                _sensorData.value = data.lastOrNull()
            } catch (e: Exception) {
                // Handle error
                _sensorData.value = null
            } finally {
                _loading.value = false
            }
        }
    }

}