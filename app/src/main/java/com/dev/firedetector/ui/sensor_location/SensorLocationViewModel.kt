package com.dev.firedetector.ui.sensor_location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.data.repository.FireRepository
import com.dev.firedetector.data.response.DeviceLocationResponse
import com.dev.firedetector.data.response.DeviceLocationUpdate
import com.dev.firedetector.util.Result
import kotlinx.coroutines.launch

class SensorLocationViewModel (private val repository: FireRepository) : ViewModel() {
/*    private val _locationsState = MutableLiveData<Result<List<DeviceLocationResponse>>>()
    val locationsState: LiveData<Result<List<DeviceLocationResponse>>> = _locationsState

    private val _updateState = MutableLiveData<Result<String>>()
    val updateState: LiveData<Result<String>> = _updateState

    fun getDeviceLocations() {
        viewModelScope.launch {
            _locationsState.value = Result.Loading
            _locationsState.value = repository.getDeviceLocations()
        }
    }

    fun updateDeviceLocations(locations: List<DeviceLocationUpdate>) {
        viewModelScope.launch {
            _updateState.value = Result.Loading
            try {
                val response = repository.updateDeviceLocations(locations)
                if (response is Result.Success) {
                    _locationsState.value = repository.getDeviceLocations()
                    _updateState.value = Result.Success("Update successful")
                } else {
                    _updateState.value = response
                }
            } catch (e: Exception) {
                _updateState.value = Result.Error(e.message ?: "Unknown error")
            }
        }
    }*/
}