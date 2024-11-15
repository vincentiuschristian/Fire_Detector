package com.dev.firedetector.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dev.firedetector.data.repository.FireRepository
import com.dev.firedetector.di.Injection
import com.dev.firedetector.ui.profile.ProfileViewModel
import com.dev.firedetector.ui.register.AuthViewModel

class ViewModelFactory(private val repository: FireRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T = when(modelClass){

//        HomeViewModel::class.java -> HomeViewModel(repository)
        AuthViewModel::class.java -> AuthViewModel(repository)
        ProfileViewModel::class.java -> ProfileViewModel(repository)

        else ->  throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)

    } as T

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}