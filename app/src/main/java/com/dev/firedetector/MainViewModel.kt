package com.dev.firedetector

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.data.model.UserModel
import com.dev.firedetector.data.repository.FireRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: FireRepository) : ViewModel() {
    init {
        viewModelScope.launch {
            repository.initializeMqttSubscriptionsIfLoggedIn()
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

}