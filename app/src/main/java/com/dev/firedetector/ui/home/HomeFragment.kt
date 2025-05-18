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
import com.dev.firedetector.util.Result
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
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
                    binding.txtHumidityRuangtamu.text =
                        getString(R.string.text_kelembapan, result.data.humidity.toString())
                    binding.txtTemperatureRuangtamu.text =
                        getString(R.string.text_suhu, result.data.temperature.toString())

                    val flameStatus = result.data.flameStatus
                    binding.txtFireDetectionRuangtamu.text = flameStatus

                    binding.txtAirQualityRuangtamu.text = result.data.mqStatus
                    binding.cvIsFire.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (flameStatus == "Api Terdeteksi") R.color.red else R.color.cardview_color
                        )
                    )
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
                    binding.txtHumidityKamar.text =
                        getString(R.string.text_kelembapan, resultSensor.data.humidity.toString())
                    binding.txtTemperatureKamar.text =
                        getString(R.string.text_suhu, resultSensor.data.temperature.toString())

                    val flameStatus = resultSensor.data.flameStatus
                    binding.txtFireDetectionKamar.text = flameStatus
                    binding.txtAirQualityKamar.text = resultSensor.data.mqStatus
                    binding.cvIsFire2.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (flameStatus == "Api Terdeteksi") R.color.red else R.color.cardview_color
                        )
                    )
                }

                is Result.Loading -> showLoading(true)
                is Result.Error -> showToast("Error Sensor Kamar : ${resultSensor.error}")

            }
        }
    }

    private fun fetchDeviceLocations() {
        locationSensorViewModel.locationsState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    updateZoneNames(result.data)
                    showLoading(false)
                }
                is Result.Error -> {
                    Log.e("HomeFragment", "Error fetching device locations: ${result.error}")
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


    private fun autoRefresh() {
        viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                viewModel.getLatestDataZona1()
                viewModel.getLatestDataZona2()
                delay(2000)
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
