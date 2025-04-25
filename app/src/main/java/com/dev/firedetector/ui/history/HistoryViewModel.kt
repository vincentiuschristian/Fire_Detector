package com.dev.firedetector.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.data.repository.FireRepository
import com.dev.firedetector.data.response.SensorDataResponse
import com.dev.firedetector.util.Result
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: FireRepository) : ViewModel() {


    private val _historyRuangTamu = MutableLiveData<Result<List<SensorDataResponse>>>()
    val historyRuangTamu: LiveData<Result<List<SensorDataResponse>>> get() = _historyRuangTamu

    private val _historyKamar = MutableLiveData<Result<List<SensorDataResponse>>>()
    val historyKamar: LiveData<Result<List<SensorDataResponse>>> get() = _historyKamar

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun getHistoryRuangTamu() {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.getSensorHistoryRuangTamu()
            _historyRuangTamu.postValue(result)
            _loading.value = false
        }
    }

    fun getHistoryKamar() {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.getSensorHistoryKamar()
            _historyKamar.postValue(result)
            _loading.value = false
        }
    }

}
