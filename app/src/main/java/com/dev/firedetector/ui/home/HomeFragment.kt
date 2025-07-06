package com.dev.firedetector.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dev.firedetector.R
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.data.response.DeviceLocationResponse
import com.dev.firedetector.databinding.FragmentHomeBinding
import com.dev.firedetector.ui.profile.ProfileViewModel
import com.dev.firedetector.ui.sensor_location.SensorLocationActivity
import com.dev.firedetector.ui.sensor_location.SensorLocationViewModel
import com.dev.firedetector.util.DangerConditions
import com.dev.firedetector.util.Result
import com.dev.firedetector.util.setDangerState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var deviceLocations: List<DeviceLocationResponse> = emptyList()
    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private val locationSensorViewModel: SensorLocationViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnPolisi.setOnClickListener {
                val police = getString(R.string.notelp_polisi)
                showConfirmationDialog(police)
            }

            btnCallAmbulance.setOnClickListener {
                val ambulance = getString(R.string.notelp_ambulans)
                showConfirmationDialog(ambulance)
            }
            btnMapsZona1.setOnClickListener {
                openMapsForZone(1)
            }
            btnMapsZona2.setOnClickListener {
                openMapsForZone(2)
            }
            tvEdit.setOnClickListener {
                sensorLocationResultLauncher.launch(
                    Intent(requireContext(), SensorLocationActivity::class.java)
                )
            }
        }

        fetchSensorZona1()
        fetchSensorZona2()
        fetchUserData()
        fetchDeviceLocations()
        autoRefresh()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getLatestDataZona1()
        viewModel.getLatestDataZona2()
        profileViewModel.fetchData()
        locationSensorViewModel.getDeviceLocations()
    }

    private val sensorLocationResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            locationSensorViewModel.getDeviceLocations()
        }
    }

    private fun fetchSensorZona1() {
        viewModel.latestSensorDataZona1.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val data = result.data
                    binding.apply {
                        txtHumidityRuangtamu.text = getString(R.string.text_kelembapan, data.humidity.toString())
                        txtTemperatureRuangtamu.text = getString(R.string.text_suhu, data.temperature.toString())
                        txtFireDetectionRuangtamu.text = data.flameStatus
                        txtAirQualityRuangtamu.text = data.mqStatus

                        val conditions = DangerConditions.fromSensorData(
                            data.temperature,
                            data.mqStatus,
                            data.flameStatus
                        )
                        updateCardColors(conditions, isZona1 = true)
                    }
                }

                is Result.Loading -> showLoading(true)
                is Result.Error -> {
                    showToast("Error Sensor Ruang Tamu : ${result.error}")
                    Log.d("ERROR HOME = ", result.error)
                }
            }
        }
    }

    private fun fetchSensorZona2() {
        viewModel.latestSensorDataZona2.observe(viewLifecycleOwner) { resultSensor ->
            when (resultSensor) {
                is Result.Success -> {
                    val data = resultSensor.data
                    binding.apply {
                        txtHumidityKamar.text = getString(R.string.text_kelembapan, data.humidity.toString())
                        txtTemperatureKamar.text = getString(R.string.text_suhu, data.temperature.toString())
                        txtFireDetectionKamar.text = data.flameStatus
                        txtAirQualityKamar.text = data.mqStatus

                        val conditions = DangerConditions.fromSensorData(
                            data.temperature,
                            data.mqStatus,
                            data.flameStatus
                        )
                        updateCardColors(conditions, isZona1 = false)
                    }
                }

                is Result.Loading -> showLoading(true)
                is Result.Error -> showToast("Error Sensor Kamar : ${resultSensor.error}")

            }
        }
    }

    private fun updateCardColors(conditions: DangerConditions, isZona1: Boolean) {
        val context = requireContext()

        if (isZona1) {
            binding.cvTemperature.setDangerState(conditions.isHighTemperature, context)
            binding.cvAirQuality.setDangerState(conditions.isGasDetected, context)
            binding.cvIsFire.setDangerState(conditions.isFireDetected, context)
        } else {
            binding.cvTemperature2.setDangerState(conditions.isHighTemperature, context)
            binding.cvAirQuality2.setDangerState(conditions.isGasDetected, context)
            binding.cvIsFire2.setDangerState(conditions.isFireDetected, context)
        }
    }

    private fun fetchDeviceLocations() {
        locationSensorViewModel.locationsState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    deviceLocations = result.data
                    updateZoneNames(result.data)
                    showLoading(false)
                }
                is Result.Error -> {
                    binding.tvZona1.text = getString(R.string.zona_1)
                    binding.tvZona2.text = getString(R.string.zona_2)
                    showLoading(false)
                }
                is Result.Loading -> {
                    if (locationSensorViewModel.locationsState.value !is Result.Success) {
                        showLoading(true)
                    }
                }
            }
        }
    }

    private fun updateZoneNames(locations: List<DeviceLocationResponse>) {
        val zona1 = locations.find { it.deviceNumber == 1 }?.zoneName
            ?: getString(R.string.zona_1)
        val zona2 = locations.find { it.deviceNumber == 2 }?.zoneName
            ?: getString(R.string.zona_2)

        binding.tvZona1.text = zona1
        binding.tvZona2.text = zona2
    }

    private fun openMapsForZone(deviceNumber: Int) {
        val location = deviceLocations.find { it.deviceNumber == deviceNumber }

        if (location?.deviceLocation.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Lokasi belum tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val coordinates = location!!.deviceLocation!!.split(",")
            if (coordinates.size != 2) {
                throw IllegalArgumentException("Format lokasi tidak valid")
            }

            val latitude = coordinates[0].trim().toDouble()
            val longitude = coordinates[1].trim().toDouble()

            val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${location.zoneName})")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            } else {
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
                )
                startActivity(webIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Gagal membuka peta: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("Maps", "Error opening maps", e)
        }
    }



    private fun autoRefresh() {
        viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                viewModel.getLatestDataZona1()
                viewModel.getLatestDataZona2()
                delay(1000)
            }
        }
    }

    private fun fetchUserData() {
        profileViewModel.userData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    binding.apply {
                        tvLocation.text = result.data.location
                        tvName.text =  getString(R.string.name, result.data.username)
                    }
                    showLoading(false)
                }

                is Result.Loading -> showLoading(true)
                is Result.Error -> showToast(result.error)
            }
        }

    }

    private fun calling(number: String) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse(number)))
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }

    private fun showConfirmationDialog(number: String) {
        MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_rounded)
            .setTitle(resources.getString(R.string.title))
            .setMessage(resources.getString(R.string.supporting_text))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->
                calling(number)
                dialog.dismiss()
            }
            .show()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(requireContext(), "Permission denied to make calls", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String?) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
