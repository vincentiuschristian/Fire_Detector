package com.dev.firedetector.ui.register

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.firedetector.R
import com.dev.firedetector.core.data.source.remote.response.LoginResponse
import com.dev.firedetector.core.data.source.remote.response.RegisterResponse
import com.dev.firedetector.core.domain.usecase.FireUseCase
import com.dev.firedetector.util.Reference
import com.dev.firedetector.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val useCase: FireUseCase,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> get() = _registerResult

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> get() = _loginResult

    private val _snackbarMessage = MutableLiveData<String?>()
    val snackbarMessage: LiveData<String?> get() = _snackbarMessage

    fun registerUser(username: String, email: String, password: String, location: String) {
        viewModelScope.launch {
            _registerResult.value = Result.Loading

            when {
                email.isEmpty() -> {
                    _registerResult.value = Result.Error("Email harus diisi")
                    _snackbarMessage.value = appContext.getString(R.string.email_warning)
                }
                !Reference.isEmailValid(appContext, email) -> {
                    _registerResult.value = Result.Error("Format email tidak valid")
                }
                password.isEmpty() -> {
                    _registerResult.value = Result.Error("Password harus diisi")
                    _snackbarMessage.value = appContext.getString(R.string.password_warning)
                }
                !Reference.isPasswordValid(appContext, password) -> {
                    _registerResult.value = Result.Error("Password minimal 6 karakter")
                }
                else -> {
                    try {
                        val result = useCase.registerUser(username, email, password, location)
                        _registerResult.value = result

                        if (result is Result.Error) {
                            _snackbarMessage.value = when {
                                result.error.contains("already registered", true) ->
                                    appContext.getString(R.string.email_already_registered)
                                result.error.contains("username already taken", true) ->
                                    appContext.getString(R.string.username_taken)
                                result.error.contains("network", true) ->
                                    appContext.getString(R.string.network_error)
                                else -> result.error
                            }
                        }
                    } catch (e: Exception) {
                        val errorMessage = appContext.getString(R.string.general_error, e.message ?: "")
                        _registerResult.value = Result.Error(errorMessage)
                        _snackbarMessage.value = errorMessage
                    }
                }
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = Result.Loading

            when {
                email.isEmpty() -> {
                    _loginResult.value = Result.Error("Email harus diisi")
                    _snackbarMessage.value = appContext.getString(R.string.email_warning)
                }
                !Reference.isEmailValid(appContext, email) -> {
                    _loginResult.value = Result.Error("Format email tidak valid")
                }
                password.isEmpty() -> {
                    _loginResult.value = Result.Error("Password harus diisi")
                    _snackbarMessage.value = appContext.getString(R.string.password_warning)
                }
                else -> {
                    try {
                        val result = useCase.loginUser(email, password)
                        _loginResult.value = result

                        if (result is Result.Error) {
                            _snackbarMessage.value = when {
                                result.error.contains("invalid credentials", true) ->
                                    appContext.getString(R.string.invalid_credentials)
                                result.error.contains("network", true) ->
                                    appContext.getString(R.string.network_error)
                                result.error.contains("timeout", true) ->
                                    appContext.getString(R.string.timeout_error)
                                else -> result.error
                            }
                        }
                    } catch (e: Exception) {
                        val errorMessage = appContext.getString(R.string.general_error, e.message ?: "")
                        _loginResult.value = Result.Error(errorMessage)
                        _snackbarMessage.value = errorMessage
                    }
                }
            }
        }
    }

    fun resetSnackbar() { _snackbarMessage.value = null }
}