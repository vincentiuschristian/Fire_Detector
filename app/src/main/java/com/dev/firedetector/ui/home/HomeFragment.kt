package com.dev.firedetector.ui.home

import android.Manifest
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
import com.dev.firedetector.R
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.databinding.FragmentHomeBinding
import com.dev.firedetector.ui.profile.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private val profileViewModel: ProfileViewModel by viewModels {
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
                val ambulance = "tel:0895600560600"
                showConfirmationDialog(ambulance)
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        viewModel.loading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.getId().observe(viewLifecycleOwner){
            binding.tvIdPerangkat.text = it.idPerangkat
        }

        viewModel.sensorData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                binding.apply {
                    txtTemperature.text = getString(R.string.text_suhu, data.temp.toString())
                    Log.d("Data Home", "Data Home: ${data.temp}")
                    txtHumidity.text = getString(R.string.text_kelembapan, data.hum.toString())
                    txtAirQuality.text = getString(R.string.text_kualitas_udara, data.mqValue)
                    txtFireDetection.text =
                        if (data.flameDetected == "Api Terdeteksi") getString(R.string.api_terdeteksi) else getString(
                            R.string.api_tidak_terdeteksi
                        )
                    cvIsFire.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (data.flameDetected.toString() == "Api Terdeteksi") R.color.red else R.color.cardview_color
                        )
                    )
                }
            }
        }

        profileViewModel.userModelData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                binding.apply {
                    tvName.text = data.username
                }
            } else {
                showSnackbar(profileViewModel.error.toString())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchLatestSensorData()
        profileViewModel.fetchData()
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

    private fun showSnackbar(message: String?) {
        Snackbar.make(binding.root, message ?: "Unknown Error", Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
