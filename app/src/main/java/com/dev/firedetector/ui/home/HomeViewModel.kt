package com.dev.firedetector.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.data.model.DataAlatModel
import com.dev.firedetector.data.pref.IDPerangkatModel
import com.dev.firedetector.data.repository.FireRepository
import kotlinx.coroutines.launch
class HomeViewModel(private val repository: FireRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _sensorData = MutableLiveData<DataAlatModel?>()
    val sensorData: LiveData<DataAlatModel?> get() = _sensorData

    private val _idPerangkat = MutableLiveData<IDPerangkatModel>()
    val idPerangkat: LiveData<IDPerangkatModel> get() = _idPerangkat

    init {
        fetchIdPerangkat()
        fetchLatestSensorData()
    }

    fun fetchLatestSensorData() {
        _loading.value = true
        repository.getSensorData(
            onDataChanged = { data ->
                _sensorData.postValue(data.firstOrNull())
                _loading.postValue(false)
            },
            onError = {
                _sensorData.postValue(null)
                _loading.postValue(false)
            }
        )
    }

    private fun fetchIdPerangkat() {
        viewModelScope.launch {
            repository.getIdPerangkat().collect { id ->
                _idPerangkat.postValue(id)
            }
        }
    }
}