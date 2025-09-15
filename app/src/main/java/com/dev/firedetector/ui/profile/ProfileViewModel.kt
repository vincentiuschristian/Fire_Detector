package com.dev.firedetector.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.core.data.source.remote.response.UserResponse
import com.dev.firedetector.core.domain.usecase.FireUseCase
import com.dev.firedetector.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val useCase: FireUseCase
) : ViewModel() {

    private val _userResult = MutableLiveData<Result<UserResponse>>()
    val userResult: LiveData<Result<UserResponse>> = _userResult

    fun loadUserProfile() {
        viewModelScope.launch {
            _userResult.value = Result.Loading
            _userResult.value = useCase.getUser()
        }
    }

    fun clearIdSaved() {
        viewModelScope.launch {
            useCase.logout()
            useCase.clearSavedIds()
        }
    }
}