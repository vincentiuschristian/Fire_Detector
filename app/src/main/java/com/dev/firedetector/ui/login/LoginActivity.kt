package com.dev.firedetector.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dev.firedetector.MainActivity
import com.dev.firedetector.R
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.data.pref.IDPerangkatModel
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

        binding.apply {
            btnLogin.setOnClickListener {
                val idPerangkat = etIdPerangkat.text.toString().trim()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                // Validasi input
                if (Reference.isEmailValid(applicationContext, email) && Reference.isPasswordValid(
                        applicationContext,
                        password
                    )
                ) {
                    // Simpan ID perangkat dan lakukan login
                    authViewModel.saveId(IDPerangkatModel(idPerangkat))
                    authViewModel.loginUser(email, password)
                } else {
                    showToast(getString(R.string.empty_field))
                }
            }

            // Navigasi ke halaman register
            tvMoveRegister.setOnClickListener {
                startActivity(Intent(applicationContext, RegisterActivity::class.java))
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        authViewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observasi hasil login
        authViewModel.loginResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    showToast(result.data?.message ?: "Login berhasil!")
                    navigateToAuth()
                }
                is Result.Error -> {
                    showToast(result.message ?: "Terjadi kesalahan saat login!")
                }
            }
        }
    }

    private fun navigateToAuth() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun showToast(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}