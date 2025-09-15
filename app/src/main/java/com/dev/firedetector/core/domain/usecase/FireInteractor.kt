package com.dev.firedetector.core.domain.usecase

import androidx.lifecycle.LiveData
import com.dev.firedetector.core.data.source.remote.response.HistoryResponse
import com.dev.firedetector.core.data.source.remote.response.SensorDataResponse
import com.dev.firedetector.core.data.source.remote.response.UserResponse
import com.dev.firedetector.core.domain.model.UserModel
import com.dev.firedetector.data.repository.IFireRepository
import com.dev.firedetector.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FireInteractor @Inject constructor(
    private val repo: IFireRepository
) : FireUseCase {

    override suspend fun loginUser(email: String, password: String) =
        repo.loginUser(email, password)

    override suspend fun registerUser(username: String, email: String, password: String, location: String, role: String) =
        repo.registerUser(username, email, password, location, role)

    override suspend fun getUser(): Result<UserResponse> =
        repo.getUser()

    override fun getMqttLiveData(): LiveData<Result<List<SensorDataResponse>>> =
        repo.getMqttLiveData()

    override fun getHistory(macAddress: String): LiveData<Result<HistoryResponse>> =
        repo.getSensorHistory(macAddress)

    override fun getFilteredHistory(macAddress: String, range: String): LiveData<Result<HistoryResponse>> =
        repo.getFilteredHistory(macAddress, range)

    override suspend fun logout(): Result<String> =
        repo.logout()

    override fun session(): Flow<UserModel> =
        repo.getSession()

    override suspend fun initializeMqttSubscriptionsIfLoggedIn() =
        repo.initializeMqttSubscriptionsIfLoggedIn()

    override suspend fun clearSavedIds() =
        repo.deleteIdPerangkat()
}