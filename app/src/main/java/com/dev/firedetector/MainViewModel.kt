package com.dev.firedetector

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.core.domain.model.UserModel
import com.dev.firedetector.core.domain.usecase.FireUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCase: FireUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            useCase.initializeMqttSubscriptionsIfLoggedIn()
        }
    }

    fun getSession(): LiveData<UserModel> = useCase.session().asLiveData()
}