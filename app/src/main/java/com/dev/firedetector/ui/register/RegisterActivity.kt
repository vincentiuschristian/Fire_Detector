package com.dev.firedetector.ui.register

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dev.firedetector.R
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.databinding.ActivityRegisterBinding
import com.dev.firedetector.ui.login.LoginActivity
import com.dev.firedetector.util.Reference.isEmailValid
import com.dev.firedetector.util.Reference.isPasswordValid
import com.dev.firedetector.util.Result
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.apply {
            tvMoveRegister.setOnClickListener {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }

        }
        setupListeners()
        observeRegisterResult()
    }

    private fun observeRegisterResult(){
        authViewModel.registerResult.observe(this){result ->
            when(result){
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    showSnackbar("Registrasi Berhasil!")
                    navigateToLogin()
                }
                is Result.Error -> showSnackbar(result.error)
            }
        }
    }

    private fun setupListeners(){
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
                } else {
                    showSnackbar(resources.getString(R.string.empty_field))
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
                        Log.d("RegisterActivity", "Lokasi diperoleh: $latitude, $longitude")
                        getAddressFromLocation(latitude, longitude)
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        try {
            val geocoder = Geocoder(this)
            geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val addressString = address.getAddressLine(0)

                        binding.etLokasi.setText(addressString)
                        showSnackbar("Alamat berhasil didapatkan: $addressString")
                    } else {
                        showSnackbar("Alamat tidak ditemukan untuk koordinat tersebut.")
                    }
                }

                override fun onError(errorMessage: String?) {
                    showSnackbar("Gagal mendapatkan alamat: $errorMessage")
                }
            })
        } catch (e: Exception) {
            showSnackbar("Gagal mendapatkan alamat: ${e.message}")
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun showSnackbar(message: String?) =
        Snackbar.make(binding.root, message ?: "Unknown Error", Snackbar.LENGTH_SHORT).show()


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
