package com.dev.firedetector.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dev.firedetector.MainActivity
import com.dev.firedetector.databinding.ActivityLoginBinding
import com.dev.firedetector.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnLogin.setOnClickListener {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }

            tvMoveRegister.setOnClickListener {
                startActivity(Intent(applicationContext, RegisterActivity::class.java))
            }
        }

    }
}