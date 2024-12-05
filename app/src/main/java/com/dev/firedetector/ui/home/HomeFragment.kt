package com.dev.firedetector.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dev.firedetector.R
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.databinding.FragmentHomeBinding
import com.dev.firedetector.ui.profile.ProfileViewModel
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

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        binding.apply {
            topBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_history -> {
                        findNavController().navigate(R.id.action_navigation_home_to_historyFragment)
                        true
                    }

                    else -> false
                }

            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.sensorData.observe(viewLifecycleOwner) { data ->
/*            if (data != null) {
                binding.apply {
                    txtTemperature.text = getString(R.string.text_suhu, data.temp)
                    txtHumidity.text = getString(R.string.text_kelembapan, data.hum.toString())
                    txtAirQuality.text = getString(R.string.text_kualitas_udara, data.gasLevel)
                    txtFireDetection.text =
                        if (data.flameDetected == true) getString(R.string.api_terdeteksi) else getString(
                            R.string.api_tidak_terdeteksi
                        )
                    cvIsFire.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (data.flameDetected == true) R.color.red else R.color.black
                        )
                    )
                }
            }*/
        }

        profileViewModel.dataUserModelData.observe(viewLifecycleOwner) { data ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSnackbar(message: String?) {
        Snackbar.make(binding.root, message ?: "Unknown Error", Snackbar.LENGTH_SHORT).show()
    }
}