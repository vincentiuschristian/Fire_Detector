package com.dev.firedetector.ui.register

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dev.firedetector.R
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.databinding.ActivityRegisterBinding
import com.dev.firedetector.ui.login.LoginActivity
import com.dev.firedetector.util.Reference.isEmailValid
import com.dev.firedetector.util.Reference.isPasswordValid
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {

    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.apply {
            btnRegister.setOnClickListener {
                val idPerangkat = etIdPerangkat.text.toString().trim()
                val username = etUsername.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val location = etLokasi.text.toString()

                if (username.isNotEmpty() && isEmailValid(applicationContext, email) &&
                    isPasswordValid(
                        applicationContext,
                        password
                    ) && location.isNotEmpty() && idPerangkat.isNotEmpty()
                ) {
                    authViewModel.registerUser(idPerangkat, username, email, password, location)
                    showSnackbar("Register")

                } else {
                    showSnackbar(resources.getString(R.string.empty_field))
                }
            }

            tvMoveRegister.setOnClickListener {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }

            btnLokasi.setOnClickListener {
                getMyLocation()
            }
        }
    }


    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {

            showLoading(true)

            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    showLoading(false)
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        binding.etLokasi.setText(
                            getString(
                                R.string.location_format,
                                latitude,
                                longitude
                            )
                        )
                        showSnackbar("Lokasi berhasil didapatkan!")
                    } else {
                        showSnackbar(getString(R.string.location_not_found))
                    }
                }.addOnFailureListener {
                    showLoading(false)
                    showSnackbar("Gagal mendapatkan lokasi: ${it.message}")
                }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                getMyLocation()
            } else {
                showSnackbar(getString(R.string.permission_denied))
            }
        }

    private fun showSnackbar(message: String?) {
        Snackbar.make(binding.root, message ?: "Unknown Error", Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
