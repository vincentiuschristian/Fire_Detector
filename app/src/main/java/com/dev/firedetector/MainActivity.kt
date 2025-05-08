package com.dev.firedetector

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.databinding.ActivityMainBinding
import com.dev.firedetector.ui.register.RegisterActivity
import com.dev.firedetector.util.NotificationHelper
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationHelper: NotificationHelper
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fireRepository = viewModel.getRepository()
        notificationHelper = NotificationHelper(this, fireRepository)
        notificationHelper.createNotificationChannel()
        notificationHelper.checkNotificationPermission()
        notificationHelper.registerNotificationReceiver()
        notificationHelper.startListening()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        getSession()
    }

    private fun getSession() {
        viewModel.getSession().observe(this) { user ->
            Log.d("MainActivity", "Session token: ${user.token}, isLogin: ${user.isLogin}")
            if (!user.isLogin || user.token.isEmpty()) {
                startActivity(Intent(this, RegisterActivity::class.java))
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationHelper.unregisterNotificationReceiver()
    }
}

