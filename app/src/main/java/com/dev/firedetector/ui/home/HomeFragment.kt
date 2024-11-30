package com.dev.firedetector.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dev.firedetector.R
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels {
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
                when(menuItem.itemId){
                    R.id.menu_history -> {
                        findNavController().navigate(R.id.action_navigation_home_to_historyFragment)
                        true
                    }
                    else -> false
                }

            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showLoading(true)
            } else {
               showLoading(false)
            }
        }

        viewModel.sensorData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                binding.apply {
                    txtTemperature.text = getString(R.string.text_suhu, data.temp)
                    txtHumidity.text = getString(R.string.text_kelembapan, data.hum.toString())
                    txtAirQuality.text = getString(R.string.text_kualitas_udara, data.gasLevel)
                    txtFireDetection.text = if (data.flameDetected == true) "Detected" else "Not Detected"
                }
                Log.d("DataKu", "Data : ${data.temp}")
            }
        }

        viewModel.fetchLatestSensorData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}