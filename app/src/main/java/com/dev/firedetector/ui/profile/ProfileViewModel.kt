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

    private val _userResult = MutableLiveData<Result<UserResponse>>()
    val userResult: LiveData<Result<UserResponse>> = _userResult

    fun loadUserProfile() {
        viewModelScope.launch {
            _userResult.value = Result.Loading
            _userResult.value = repository.getUser()
        }
    }

    fun clearIdSaved() {
        viewModelScope.launch {
            repository.logout()
            repository.deleteIdPerangkat()
        }
    }
}
