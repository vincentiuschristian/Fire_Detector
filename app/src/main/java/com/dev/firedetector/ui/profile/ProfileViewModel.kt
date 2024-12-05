package com.dev.firedetector.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.data.model.DataUserModel
import com.dev.firedetector.data.repository.FireRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: FireRepository) : ViewModel() {
    private val _userModelData = MutableLiveData<DataUserModel?>()
    val dataUserModelData: MutableLiveData<DataUserModel?> get() = _userModelData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun fetchData() {
//        repository.getUserData{ user, exception ->
//            if (exception != null) {
//                _error.value = "Failed to fetch data: ${exception.message}"
//                _loading.value = true
//            } else {
//                _userData.value = user
//                _loading.value = false
//            }
//        }
    }

    fun logout() = repository.logout()

    fun clearIdSaved(){
        viewModelScope.launch {
            repository.deleteIdPerangkat()
        }
    }
}