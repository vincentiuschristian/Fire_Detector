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

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getDataHistory() {
//        viewModelScope.launch {
//            repository.getSensorData(
//                onDataChanged = { data ->
//                    _dataHistory.postValue(data)
//                },
//                onError = { error ->
//                    println("Error in listener: ${error.message}")
//                }
//            )
//        }
    }

}
