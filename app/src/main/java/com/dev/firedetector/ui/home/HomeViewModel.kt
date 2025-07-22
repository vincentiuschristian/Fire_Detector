package com.dev.firedetector.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dev.firedetector.data.repository.FireRepository
import com.dev.firedetector.data.response.SensorDataResponse
import com.dev.firedetector.util.Result

class HomeViewModel(private val repository: FireRepository) : ViewModel() {

    private val sensorListLiveData = repository.getMqttLiveData()
    private val _sensorDataList = MutableLiveData<List<SensorDataResponse>>(emptyList())
    val sensorDataList: LiveData<List<SensorDataResponse>> = _sensorDataList

    fun getSensorListLiveData(): LiveData<Result<List<SensorDataResponse>>> = sensorListLiveData

    fun onNewSensorData(data: SensorDataResponse) {
        val currentList = _sensorDataList.value?.toMutableList() ?: mutableListOf()
        val index = currentList.indexOfFirst { it.macAddress == data.macAddress }

        if (index != -1) {
            currentList[index] = data
        } else {
            currentList.add(data)
        }
        _sensorDataList.postValue(currentList)
    }

}