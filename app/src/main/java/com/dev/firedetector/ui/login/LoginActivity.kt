package com.dev.firedetector.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dev.firedetector.MainActivity
import com.dev.firedetector.R
import com.dev.firedetector.databinding.ActivityLoginBinding
import com.dev.firedetector.ui.register.AuthViewModel
import com.dev.firedetector.ui.register.RegisterActivity
import com.dev.firedetector.util.Result
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val authViewModel: AuthViewModel by viewModels()

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

    private fun setupListeners() {
        binding.apply {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()
                authViewModel.loginUser(email, password)
            }

            tvMoveRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
    }

    private fun observeViewModel() {
        authViewModel.loginResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    navigateToMain()
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

    private fun navigateToMain() {
        startActivity(Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }


    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Tutup") {  }
            .show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnLogin.isEnabled = !isLoading
            etEmail.isEnabled = !isLoading
            etPassword.isEnabled = !isLoading
        }
    }
}