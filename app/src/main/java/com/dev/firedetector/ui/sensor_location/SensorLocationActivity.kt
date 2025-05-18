package com.dev.firedetector.ui.sensor_location

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dev.firedetector.R
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.data.response.DeviceLocationResponse
import com.dev.firedetector.data.response.DeviceLocationUpdate
import com.dev.firedetector.databinding.ActivitySensorLocationBinding
import com.dev.firedetector.util.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar

class SensorLocationActivity : AppCompatActivity() {
    private val binding: ActivitySensorLocationBinding by lazy {
        ActivitySensorLocationBinding.inflate(layoutInflater)
    }
    private val viewModel: SensorLocationViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentDevice = 1

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

        setupToolbar()
        setupLocationButtons()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDeviceLocations()
    }

    private fun setupObservers() {
        viewModel.locationsState.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    updateUI(result.data)
                }

                is Result.Error -> {
                    showLoading(false)
                    showSnackbar(result.error)
                }

            }
        }

        viewModel.updateState.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    showSnackbar(result.data)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                is Result.Error -> {
                    showLoading(false)
                    showSnackbar(result.error)
                }
            }
        }
    }


    private fun setupToolbar() {
        binding.topBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.topBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_save -> {
                    saveLocations()
                    true
                }

                else -> false
            }
        }
    }

    private fun updateUI(locations: List<DeviceLocationResponse>) {
        locations.forEach { location ->
            when (location.deviceNumber) {
                1 -> {
                    binding.etNamaZona1.setText(location.zoneName)
                    binding.etLokasiPerangkat1.setText(location.deviceLocation)
                }

                2 -> {
                    binding.etNamaZona2.setText(location.zoneName)
                    binding.etLokasiPerangkat2.setText(location.deviceLocation)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setupLocationButtons() {
        binding.btnLokasi1.setOnClickListener {
            currentDevice = 1
            getMyLocation()
        }

        binding.btnLokasi2.setOnClickListener {
            currentDevice = 2
            getMyLocation()
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
                        val coordinates = "${location.latitude},${location.longitude}"
                        when (currentDevice) {
                            1 -> binding.etLokasiPerangkat1.setText(coordinates)
                            2 -> binding.etLokasiPerangkat2.setText(coordinates)
                        }
                    } else {
                        showSnackbar(getString(R.string.location_not_found))
                    }
                }.addOnFailureListener {
                    showLoading(false)
                    showSnackbar("Failed to get location: ${it.message}")
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

    private fun saveLocations() {
        val locations = listOf(
            DeviceLocationUpdate(
                deviceNumber = 1,
                zoneName = binding.etNamaZona1.text.toString(),
                deviceLocation = binding.etLokasiPerangkat1.text.toString()
            ),
            DeviceLocationUpdate(
                deviceNumber = 2,
                zoneName = binding.etNamaZona2.text.toString(),
                deviceLocation = binding.etLokasiPerangkat2.text.toString()
            )
        )

        viewModel.updateDeviceLocations(locations)
    }

    private fun showSnackbar(message: String) =
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}