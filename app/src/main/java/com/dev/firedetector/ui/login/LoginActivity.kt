package com.dev.firedetector.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dev.firedetector.MainActivity
import com.dev.firedetector.R
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.data.pref.UserModel
import com.dev.firedetector.databinding.ActivityLoginBinding
import com.dev.firedetector.ui.register.AuthViewModel
import com.dev.firedetector.ui.register.RegisterActivity
import com.dev.firedetector.util.Reference

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

                authViewModel.loading.observe(this@LoginActivity) {
                    showLoading(it)
                }

                if (idPerangkat.isNotEmpty() && Reference.isEmailValid(
                        applicationContext,
                        email
                    ) && Reference.isPasswordValid(
                        applicationContext,
                        password
                    )
                ) {
                    authViewModel.loading.observe(this@LoginActivity) {
                        showLoading(it)
                    }
                    saveId(idPerangkat)
                    authViewModel.getId().observe(this@LoginActivity) { userModel ->
                        Log.d("LoginActivity ID", "Saved ID Perangkat: ${userModel.idPerangkat}")
                    }
                    authViewModel.login(email, password)
                    showToast("Login Success")
                    navigate()
                } else {
                    showToast(resources.getString(R.string.empty_field))
                }
            }

            tvMoveRegister.setOnClickListener {
                startActivity(Intent(applicationContext, RegisterActivity::class.java))
            }
        }

    }

    private fun saveId(idPerangkat: String){
        authViewModel.saveId(UserModel(idPerangkat))
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message!!, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun navigate() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}