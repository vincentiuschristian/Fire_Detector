package com.dev.firedetector.ui.register

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.dev.firedetector.R
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.databinding.ActivityRegisterBinding
import com.dev.firedetector.ui.login.LoginActivity
import com.dev.firedetector.util.Reference
import com.dev.firedetector.util.Result
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {
    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }

        setupListeners()
        observeViewModel()
    }

    private fun observeViewModel() {
        authViewModel.registerResult.observe(this) { result ->
            when(result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    navigateToLogin()
                }
                is Result.Error -> showLoading(false)
            }
        }

        authViewModel.snackbarMessage.observe(this) { message ->
            message?.let {
                showSnackbar(it)
                authViewModel.resetSnackbar()
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            tvMoveRegister.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            }

            btnRegister.setOnClickListener {
                val username = etUsername.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()
                val location = etLokasi.text.toString().trim()

                when {
                    username.isEmpty() -> {
                        etUsername.requestFocus()
                        showSnackbar("Username harus diisi")
                    }
                    !Reference.isEmailValid(applicationContext, email) -> {
                        etEmail.requestFocus()
                    }
                    !Reference.isPasswordValid(applicationContext, password) -> {
                        etPassword.requestFocus()
                    }
                    location.isEmpty() -> {
                        etLokasi.requestFocus()
                        showSnackbar("Lokasi harus diisi")
                    }
                    else -> {
                        authViewModel.registerUser(username, email, password, location)
                    }
                }
            }
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Tutup") { /* Optional action */ }
            .show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnRegister.isEnabled = !isLoading
            etUsername.isEnabled = !isLoading
            etEmail.isEnabled = !isLoading
            etPassword.isEnabled = !isLoading
            etLokasi.isEnabled = !isLoading
        }
    }
}