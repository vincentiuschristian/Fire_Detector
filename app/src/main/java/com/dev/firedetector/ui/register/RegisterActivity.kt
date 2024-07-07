package com.dev.firedetector.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dev.firedetector.databinding.ActivityRegisterBinding
import com.dev.firedetector.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnRegister.setOnClickListener {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
            tvMoveRegister.setOnClickListener {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
        }


    }
}