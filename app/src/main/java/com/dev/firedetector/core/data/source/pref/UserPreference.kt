package com.dev.firedetector.core.data.source.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dev.firedetector.core.domain.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class UserPreference @Inject constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val MAC_ADDRESS_KEY = stringPreferencesKey("mac_address_list")
    }

    suspend fun saveSession(user: UserModel, macList: List<String>) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = true
            preferences[MAC_ADDRESS_KEY] = macList.joinToString(",") // Save as string
        }
    }

    fun getToken(): Flow<String> = dataStore.data.map { it[TOKEN_KEY] ?: "" }

    fun getSession(): Flow<UserModel> = dataStore.data.map {
        UserModel(
            token = it[TOKEN_KEY] ?: "",
            isLogin = it[IS_LOGIN_KEY] ?: false
        )
    }

    fun getMacList(): Flow<List<String>> = dataStore.data.map {
        it[MAC_ADDRESS_KEY]?.split(",")?.filter { mac -> mac.isNotBlank() } ?: emptyList()
    }

    suspend fun logout() {
        dataStore.edit { it.clear() }
    }
}
