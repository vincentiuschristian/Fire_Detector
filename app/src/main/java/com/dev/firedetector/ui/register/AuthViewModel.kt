package com.dev.firedetector.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.data.model.DataUserModel
import com.dev.firedetector.data.pref.UserModel
import com.dev.firedetector.data.repository.FireRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: FireRepository) : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _loggedInUser = MutableLiveData<String?>()
    val loggedInUser: LiveData<String?> = _loggedInUser

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    init {
        autoLogin()
    }

    private fun autoLogin() {
        _loggedInUser.value = repository.getUser()
    }

    fun login(email: String, pass: String) {
        _loading.value = true
        repository.login(
            email, pass,
            onSuccess = {
                autoLogin()
                _message.value = "Login Success"
                _loading.value = false
            },
            onFailure = {
                _message.value = it.cause?.message ?: it.message ?: "There was an error"
                _loading.value = false
            }
        )
    }

    fun register(email: String, pass: String, dataUserModelData: DataUserModel, idPerangkat: String) {
        _loading.value = true
        repository.register(dataUserModelData, email, pass, idPerangkat) { _, it ->
            if (it == null) {
                login(email, pass)
                _loading.value = false
                _message.value = "Register Success, Logging In..."
            } else {
                _loading.value = false
                _message.value = it.cause?.message ?: it.message ?: "There was an error"
            }
        }
    }

    fun saveId(userModel: UserModel){
        viewModelScope.launch {
            repository.saveIdPerangkat(userModel)
        }
    }

    fun getId(): LiveData<UserModel> {
        val idPerangkatLiveData = MutableLiveData<UserModel>()
        viewModelScope.launch {
            repository.getIdPerangkat().collect { userModel ->
                idPerangkatLiveData.postValue(userModel)
            }
        }
        return idPerangkatLiveData
    }



}