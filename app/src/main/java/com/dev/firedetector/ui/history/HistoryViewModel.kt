package com.dev.firedetector.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.data.model.DataAlatModel
import com.dev.firedetector.data.repository.FireRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: FireRepository) : ViewModel() {

    private val _dataHistory = MutableLiveData<List<DataAlatModel>>()
    val dataHistory: LiveData<List<DataAlatModel>> get() = _dataHistory

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun getDataHistory() {
        _loading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                // Get device ID from the repository
                val idPerangkat = repository.getUserDeviceId()
                // Fetch sensor data using the device ID
                val data = repository.getSensorData(idPerangkat)
                _dataHistory.postValue(data)
            } catch (e: Exception) {
                _errorMessage.postValue("Error fetching data: ${e.message}")
                _dataHistory.postValue(emptyList())
            } finally {
                _loading.postValue(false)
            }
        }
    }
}
