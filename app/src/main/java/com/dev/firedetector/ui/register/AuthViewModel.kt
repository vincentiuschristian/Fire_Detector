package com.dev.firedetector.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.data.model.LoginResponse
import com.dev.firedetector.data.model.RegisterResponse
import com.dev.firedetector.data.pref.IDPerangkatModel
import com.dev.firedetector.data.repository.FireRepository
import com.dev.firedetector.util.Result
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: FireRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> get() = _registerResult

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> get() = _loginResult

    fun registerUser(deviceId: String, username: String, email: String, password: String, location: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.registerUser(deviceId, username, email, password, location)
            _registerResult.postValue(result)
            _loading.value = false
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.loginUser(email, password)
            _loginResult.postValue(result)
            _loading.value = false
        }
    }


    fun saveId(idPerangkatModel: IDPerangkatModel) {
        viewModelScope.launch {
            repository.saveIdPerangkat(idPerangkatModel)
        }
    }

}