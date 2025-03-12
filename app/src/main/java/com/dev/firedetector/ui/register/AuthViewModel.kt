package com.dev.firedetector.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.data.repository.FireRepository
import com.dev.firedetector.data.response.LoginResponse
import com.dev.firedetector.data.response.RegisterResponse
import com.dev.firedetector.util.Result
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: FireRepository
) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> get() = _registerResult

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> get() = _loginResult

    fun registerUser(deviceId: String, username: String, email: String, password: String, location: String) {
        viewModelScope.launch {
            _registerResult.postValue(Result.Loading)

            val result = repository.registerUser(deviceId, username, email, password, location)
            _registerResult.postValue(result)
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.postValue(Result.Loading)

            val result = repository.loginUser(email, password)
            _loginResult.postValue(result)
        }
    }
}