package com.dev.firedetector.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.data.repository.FireRepository
import com.dev.firedetector.data.response.UserResponse
import com.dev.firedetector.util.Result
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: FireRepository) : ViewModel() {

    private val _userData = MutableLiveData<Result<UserResponse>>()
    val userData: LiveData<Result<UserResponse>> get() = _userData

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading


    fun fetchData() {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.getUser()
            _userData.postValue(result)
            _loading.value = false
        }
    }

    fun clearIdSaved() {
        viewModelScope.launch {
            repository.deleteIdPerangkat()
        }
    }
}
