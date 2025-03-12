package com.dev.firedetector

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.data.pref.UserPreference
import com.dev.firedetector.databinding.ActivityMainBinding
import com.dev.firedetector.ui.register.RegisterActivity
import com.dev.firedetector.util.NotificationHelper
import com.google.android.material.bottomnavigation.BottomNavigationView


private val Context.dataStore by preferencesDataStore(name = "session")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var userPreference: UserPreference
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(dataStore)

        notificationHelper = NotificationHelper(this, userPreference)

        notificationHelper.createNotificationChannel()

        notificationHelper.checkNotificationPermission()

        notificationHelper.startListening()

        notificationHelper.registerNotificationReceiver()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        getSession()


    }

    private fun getSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(applicationContext, RegisterActivity::class.java))
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationHelper.unregisterNotificationReceiver()
    }
}

