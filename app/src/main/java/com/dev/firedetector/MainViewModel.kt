package com.dev.firedetector

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dev.firedetector.data.model.UserModel
import com.dev.firedetector.data.repository.FireRepository

class MainViewModel(private val repository: FireRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}