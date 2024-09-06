package com.dev.firedetector.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dev.firedetector.AuthActivity
import com.dev.firedetector.R
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.data.model.User
import com.dev.firedetector.databinding.ActivityRegisterBinding
import com.dev.firedetector.ui.login.LoginActivity
import com.dev.firedetector.util.Reference.isEmailValid
import com.dev.firedetector.util.Reference.isPasswordValid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {

    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.apply {
            btnRegister.setOnClickListener {
                val username = etUsername.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if (username.isNotEmpty() && isEmailValid(applicationContext, email) && isPasswordValid(applicationContext, password)) {
                    authViewModel.register(
                        email = email,
                        pass = password,
                        User(username = username, email = email)
                    )
                    authViewModel.loading.observe(this@RegisterActivity){
                        showLoading(it)
                    }
                    showToast("Register Success")
                    finish()
                } else {
                    showToast(resources.getString(R.string.empty_field))
                }
            }
            tvMoveRegister.setOnClickListener {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(applicationContext, AuthActivity::class.java))
            finish()
        }
    }

    private fun showToast(messages: String?) {
        Toast.makeText(this, messages!!, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}