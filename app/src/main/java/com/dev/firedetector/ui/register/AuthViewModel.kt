package com.dev.firedetector.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.data.model.DataUserModel
import com.dev.firedetector.data.pref.IDPerangkatModel
import com.dev.firedetector.data.repository.FireRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: FireRepository) : ViewModel() {
    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _loggedInUser = MutableLiveData<String?>()
    val loggedInUser: LiveData<String?> get() = _loggedInUser

    init {
        autoLogin()
    }

    private fun autoLogin() {
        _loggedInUser.value = repository.getUser()
    }

    fun login(email: String, pass: String) {
        _loading.value = true
        repository.login(email, pass, onSuccess = {
            autoLogin()
            _message.postValue("Login Successful")
            _loading.postValue(false)
        }, onFailure = { exception ->
            _loading.postValue(false)
            _message.postValue(parseFirebaseError(exception))
        })
    }

    fun register(email: String, pass: String, dataUserModel: DataUserModel, idPerangkat: String) {
        _loading.value = true
        repository.register(
            dataUserModel,
            email,
            pass,
            idPerangkat,
            onResult = { success, exception ->
                if (success) {
                    login(email, pass)
                    _message.postValue("Registration Successful")
                } else {
                    _loading.postValue(false)
                    _message.postValue(parseFirebaseError(exception!!))
                }
            })

    }

    private fun parseFirebaseError(exception: Exception): String {
        return when (exception) {
            is FirebaseAuthInvalidCredentialsException -> "Periksa kembali email atau kata sandi Anda!"
            is FirebaseAuthUserCollisionException -> "Email ini sudah terdaftar. Silakan gunakan email lain atau coba masuk."
            is FirebaseAuthInvalidUserException -> "Pengguna tidak ditemukan. Silakan periksa kembali email yang Anda masukkan."
            else -> exception.message
                ?: "Terjadi kesalahan yang tidak diketahui. Silakan coba lagi."
        }
    }

    fun saveId(idPerangkatModel: IDPerangkatModel) {
        viewModelScope.launch {
            repository.saveIdPerangkat(idPerangkatModel)
        }
    }

}