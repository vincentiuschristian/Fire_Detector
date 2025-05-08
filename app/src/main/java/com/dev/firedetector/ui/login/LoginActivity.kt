package com.dev.firedetector.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dev.firedetector.MainActivity
import com.dev.firedetector.R
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.databinding.ActivityLoginBinding
import com.dev.firedetector.ui.register.AuthViewModel
import com.dev.firedetector.ui.register.RegisterActivity
import com.dev.firedetector.util.Reference
import com.dev.firedetector.util.Result
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

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
        observeLoginResult()
    }

    private fun setupListeners() {
        binding.apply {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()

                if (email.isEmpty() || password.isEmpty()) {
                    showSnackbar("Email dan password tidak boleh kosong")
                    return@setOnClickListener
                }

                if (!Reference.isEmailValid(applicationContext, email)) {
                    showSnackbar("Format email tidak valid")
                    return@setOnClickListener
                }

                if (!Reference.isPasswordValid(applicationContext, password)) {
                    showSnackbar("Kata sandi harus minimal 6 karakter")
                    return@setOnClickListener
                }

                authViewModel.loginUser(email, password)
            }

            tvMoveRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
    }

    private fun observeLoginResult() {
        authViewModel.loginResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    showSnackbar("Login berhasil")
                    navigateToMain()
                }

                is Result.Error -> {
                    showLoading(false)
                    Log.e("LoginActivity", "Login gagal: ${result.error}")
                    showSnackbar("Login gagal: ${result.error}")
                }
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun showSnackbar(message: String) =
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
