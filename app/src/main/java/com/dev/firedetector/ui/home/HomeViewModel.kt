package com.dev.firedetector.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.data.model.DataAlatModel
import com.dev.firedetector.data.pref.IDPerangkatModel
import com.dev.firedetector.data.repository.FireRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: FireRepository) : ViewModel() {

    // State untuk loading
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    // State untuk data sensor terbaru
    val sensorData: LiveData<List<DataAlatModel>> = repository.sensorData
        .map { it.take(100) } // Ambil 100 data terbaru
        .asLiveData()

    // State untuk ID perangkat
    private val _idPerangkat = MutableLiveData<IDPerangkatModel>()
    val idPerangkat: LiveData<IDPerangkatModel> get() = _idPerangkat

    // State untuk error
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        fetchIdPerangkat()
    }

    /**
     * Mengambil ID perangkat dari preferences
     */
    private fun fetchIdPerangkat() {
        viewModelScope.launch {
            repository.getIdPerangkat().collect { id ->
                _idPerangkat.postValue(id)
            }
        }
    }

    /**
     * Memulai koneksi MQTT
     */
    fun connectToMQTT() {
        _loading.value = true
        repository.connectToMQTT()
        _loading.value = false
    }

    /**
     * Memutuskan koneksi MQTT
     */
    fun disconnectFromMQTT() {
        repository.disconnectFromMQTT()
    }

}