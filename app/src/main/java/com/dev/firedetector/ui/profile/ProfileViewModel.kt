package com.dev.firedetector.ui.profile

import androidx.lifecycle.ViewModel
import com.dev.firedetector.data.repository.FireRepository

class ProfileViewModel(private val repository: FireRepository) : ViewModel() {
//    private val _userModelData = MutableLiveData<DataUserModel?>()
//    val userModelData: LiveData<DataUserModel?> get() = _userModelData
//
//    private val _error = MutableLiveData<String>()
//    val error: LiveData<String> get() = _error
//
//    private val _loading = MutableLiveData<Boolean>()
//    val loading: LiveData<Boolean> get() = _loading
//
//    fun fetchData() {
//        viewModelScope.launch {
//            _loading.value = true
//            try {
//                val userData = repository.getUserData()
//                if (userData != null) {
//                    _userModelData.postValue(userData)
//                } else {
//                    _error.postValue("User data not found.")
//                }
//            } catch (e: Exception) {
//                _error.postValue("Failed to fetch data: ${e.message}")
//            } finally {
//                _loading.postValue(false)
//            }
//        }
//    }
//
//    fun logout() = repository.logout()
//
//    fun clearIdSaved() {
//        viewModelScope.launch {
//            try {
//                repository.deleteIdPerangkat()
//            } catch (e: Exception) {
//                _error.postValue("Failed to clear saved ID: ${e.message}")
//            }
//        }
//    }
}
