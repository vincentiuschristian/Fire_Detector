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
    val userModelData: LiveData<DataUserModel?> get() = _userModelData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    // Fungsi untuk mengambil data pengguna
    fun fetchData() {
        viewModelScope.launch {
            _loading.value = true // Mulai proses loading
            try {
                val userData = repository.getUserData()
                if (userData != null) {
                    _userModelData.postValue(userData)
                } else {
                    _error.postValue("User data not found.")
                }
            } catch (e: Exception) {
                _error.postValue("Failed to fetch data: ${e.message}")
            } finally {
                _loading.postValue(false) // Selesai proses loading
            }
        }
    }

    // Fungsi untuk logout
    fun logout() = repository.logout()

    // Fungsi untuk menghapus ID perangkat yang tersimpan
    fun clearIdSaved() {
        viewModelScope.launch {
            try {
                repository.deleteIdPerangkat()
            } catch (e: Exception) {
                _error.postValue("Failed to clear saved ID: ${e.message}")
            }
        }
    }
}
