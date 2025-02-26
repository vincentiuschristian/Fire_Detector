package com.dev.firedetector

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.databinding.ActivityAuthBinding
import com.dev.firedetector.ui.register.AuthViewModel

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(applicationContext)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.loading.observe(this){
            showLoading(it)
        }

       /* viewModel.loggedInUser.observe(this) {
            if (it != null) {
                startActivity(Intent(this, MainActivity::class.java).apply {
                    addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                })
            } else {
                startActivity(Intent(this, RegisterActivity::class.java).apply {
                    finish()
                })
            }
        }*/
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}