package com.dev.firedetector.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.data.model.DataAlatModel
import com.dev.firedetector.data.pref.IDPerangkatModel
import com.dev.firedetector.data.repository.FireRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: FireRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _sensorData = MutableLiveData<DataAlatModel?>()
    val sensorData: LiveData<DataAlatModel?> = _sensorData

    fun fetchLatestSensorData() {
        _loading.value = true
        viewModelScope.launch {
            try {
                repository.getSensorData(
                    onDataChanged = { data ->
                        _sensorData.value = data.firstOrNull()
                    },
                    onError = {
                        _sensorData.value = null
                    }
                )
            } catch (e: Exception) {
                _sensorData.value = null
            } finally {
                _loading.value = false
            }
        }

    }

    fun getId(): LiveData<IDPerangkatModel> {
        val idPerangkatLiveData = MutableLiveData<IDPerangkatModel>()
        viewModelScope.launch {
            repository.getIdPerangkat().collect { userModel ->
                idPerangkatLiveData.postValue(userModel)
            }
        }
        return idPerangkatLiveData
    }

}