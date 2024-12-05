package com.dev.firedetector.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
class UserPreference private constructor(private val dataStore: DataStore<Preferences>){

    suspend fun saveIdPerangkat(userModel: UserModel) {
        dataStore.edit { preferences ->
            preferences[ID_PERANGKAT] = userModel.idPerangkat
        }
    }

    suspend fun getIdPerangkat() : Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[ID_PERANGKAT] ?: ""
            )
        }
    }

    suspend fun logout(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val ID_PERANGKAT = stringPreferencesKey("id_perangkat")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

}