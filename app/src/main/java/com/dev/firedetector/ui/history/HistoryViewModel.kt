package com.dev.firedetector.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.data.model.DataAlatModel
import com.dev.firedetector.data.pref.UserModel
import com.dev.firedetector.data.repository.FireRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: FireRepository) : ViewModel() {

    private val _dataHistory = MutableLiveData<List<DataAlatModel>>()
    val dataHistory: LiveData<List<DataAlatModel>> get() = _dataHistory

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading


    fun getDataHistory() {
        viewModelScope.launch {
            _loading.postValue(true)
            val data = repository.getSensorData()
            _dataHistory.postValue(data)
            _loading.postValue(false)
        }
    }

    fun getId(): LiveData<UserModel> {
        val idPerangkatLiveData = MutableLiveData<UserModel>()
        viewModelScope.launch {
            repository.getIdPerangkat().collect { userModel ->
                idPerangkatLiveData.postValue(userModel)
            }
        }
        return idPerangkatLiveData
    }
}
