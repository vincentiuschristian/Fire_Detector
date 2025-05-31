package com.dev.firedetector.data

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dev.firedetector.MainViewModel
import com.dev.firedetector.data.repository.FireRepository
import com.dev.firedetector.di.Injection
import com.dev.firedetector.ui.history.HistoryViewModel
import com.dev.firedetector.ui.home.HomeViewModel
import com.dev.firedetector.ui.profile.ProfileViewModel
import com.dev.firedetector.ui.register.AuthViewModel
import com.dev.firedetector.ui.sensor_location.SensorLocationViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repository: FireRepository,
    private val application: Application? = null
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                requireNotNull(application) {
                    "Application context is required for AuthViewModel"
                }
                AuthViewModel(repository, application) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SensorLocationViewModel::class.java) -> {
                SensorLocationViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val application = context.applicationContext as Application
                INSTANCE = ViewModelFactory(
                    repository = Injection.provideRepository(context),
                    application = application
                )
                INSTANCE!!
            }
        }
    }
}