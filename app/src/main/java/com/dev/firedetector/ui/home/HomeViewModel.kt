package com.dev.firedetector.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dev.firedetector.data.repository.FireRepository
import com.dev.firedetector.data.response.SensorDataResponse
import com.dev.firedetector.util.Result

class HomeViewModel(private val repository: FireRepository) : ViewModel() {

    private val sensorListLiveData = repository.getMqttLiveData()

    fun getSensorListLiveData(): LiveData<Result<List<SensorDataResponse>>> = sensorListLiveData

}