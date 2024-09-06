package com.dev.firedetector.ui.profile

import androidx.lifecycle.ViewModel
import com.dev.firedetector.data.repository.FireRepository

class ProfileViewModel(private val repository: FireRepository) : ViewModel(){
    fun logout() = repository.logout()

}