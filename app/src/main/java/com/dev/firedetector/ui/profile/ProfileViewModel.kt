package com.dev.firedetector.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dev.firedetector.data.model.User
import com.dev.firedetector.data.repository.FireRepository

class ProfileViewModel(private val repository: FireRepository) : ViewModel() {
    private val _userData = MutableLiveData<User?>()
    val userData: MutableLiveData<User?> get() = _userData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun fetchData() {
        repository.getUserData{ user, exception ->
            if (exception != null) {
                _error.value = "Failed to fetch data: ${exception.message}"
                _loading.value = true
            } else {
                _userData.value = user
                _loading.value = false
            }
        }
    }

    fun logout() = repository.logout()

}