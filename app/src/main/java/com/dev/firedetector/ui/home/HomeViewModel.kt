package com.dev.firedetector.ui.home

import androidx.lifecycle.ViewModel
import com.dev.firedetector.core.domain.usecase.FireUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    useCase: FireUseCase
) : ViewModel() {

    private val sensorListLiveData = useCase.getMqttLiveData()
    fun getSensorListLiveData() = sensorListLiveData
}
