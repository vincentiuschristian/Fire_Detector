package com.dev.firedetector.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dev.firedetector.data.model.User
import com.dev.firedetector.data.repository.FireRepository

class AuthViewModel(private val repository: FireRepository) : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _loggedInUser = MutableLiveData<String?>()
    val loggedInUser: LiveData<String?> = _loggedInUser

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    init {
        autoLogin()
    }

    private fun autoLogin() {
        _loggedInUser.value = repository.getUser()
    }

    fun login(email: String, pass: String) {
        _loading.value = true
        repository.login(
            email, pass,
            onSuccess = {
                autoLogin()
                _message.value = "Login Success"
                _loading.value = false
            },
            onFailure = {
                _message.value = it.cause?.message ?: it.message ?: "There was an error"
                _loading.value = false
            }
        )
    }

    fun register(email: String, pass: String, userData: User) {
        _loading.value = true
        repository.register(userData, email, pass) { _, it ->
            if (it == null) {
                login(email, pass)
                _loading.value = false
                _message.value = "Register Success, Logging In..."
            } else {
                _loading.value = false
                _message.value = it.cause?.message ?: it.message ?: "There was an error"
            }
        }
    }

}