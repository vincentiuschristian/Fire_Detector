package com.dev.firedetector.ui.history

import androidx.lifecycle.ViewModel
import com.dev.firedetector.core.domain.usecase.FireUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val useCase: FireUseCase
) : ViewModel() {

    fun getHistory(macAddress: String) = useCase.getHistory(macAddress)

    fun getFilteredHistory(macAddress: String, range: String) =
        useCase.getFilteredHistory(macAddress, range)
}
