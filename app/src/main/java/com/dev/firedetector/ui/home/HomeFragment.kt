package com.dev.firedetector.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.firedetector.R
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.data.response.SensorDataResponse
import com.dev.firedetector.databinding.FragmentHomeBinding
import com.dev.firedetector.ui.adapter.ListSensorAdapter
import com.dev.firedetector.ui.maps.SensorMapActivity
import com.dev.firedetector.ui.profile.ProfileViewModel
import com.dev.firedetector.util.Result
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var adapter: ListSensorAdapter

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

        setupRecyclerView()
        setupEmergencyButtons()
        showLoading(true)
        observeViewModel()

    }

    override fun onResume() {
        super.onResume()
        profileViewModel.loadUserProfile()
    }

    private fun setupRecyclerView() {
        adapter = ListSensorAdapter { sensor ->
            val intent = Intent(requireContext(), SensorMapActivity::class.java).apply {
                putExtra("sensor_data", sensor)
            }
            startActivity(intent)
        }
        binding.rvList.adapter = adapter
        binding.rvList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupEmergencyButtons() {
        binding.btnPolisi.setOnClickListener {
            val police = getString(R.string.notelp_polisi)
            showConfirmationDialog(police)
        }

        binding.btnCallAmbulance.setOnClickListener {
            val ambulance = getString(R.string.notelp_ambulans)
            showConfirmationDialog(ambulance)
        }
    }

    private fun observeViewModel() {
        viewModel.getSensorListLiveData().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    showLoading(false)
                    updateList(result.data)
                }
                is Result.Loading -> showLoading(true)
                is Result.Error -> {
                    showLoading(false)
                    showToast(result.error)
                }
            }
        }

        profileViewModel.userResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    result.data.let { data ->
                        binding.apply {
                            tvLocation.text = data.location
                            tvName.text = getString(R.string.name, data.username)
                        }
                    }
                    showLoading(false)
                }

                is Result.Loading -> showLoading(true)
                is Result.Error -> showToast(result.error)
            }
        }

    }

    private fun updateList(data: List<SensorDataResponse>) {
        binding.apply {
            if (data.isNotEmpty()) {
                adapter.submitList(data)
                progressBar.visibility = View.GONE
            } else {
                adapter.submitList(emptyList())
                progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun calling(number: String) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startActivity(Intent(Intent.ACTION_CALL, number.toUri()))
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
