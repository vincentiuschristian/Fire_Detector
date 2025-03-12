package com.dev.firedetector.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dev.firedetector.MainActivity
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

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.apply {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()


                if (Reference.isEmailValid(applicationContext, email) &&
                    Reference.isPasswordValid(applicationContext, password)
                ) {
                    authViewModel.loginUser(email, password)
                } else {
                    showToast("Email atau kata sandi tidak valid")
                }
            }

            // Navigasi ke halaman register
            tvMoveRegister.setOnClickListener {
                startActivity(Intent(applicationContext, RegisterActivity::class.java))
            }
        }
    }

    private fun observeViewModel() {

        authViewModel.loginResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    val token = result.data.token
                    Log.d("LoginActivity", "Login berhasil, token: $token")
                    showToast("Login Berhasil")
                    navigateToMain()
                }
                is Result.Error -> {
                    Log.e("LoginActivity", "Login gagal: ${result.error}")
                }
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun showToast(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
