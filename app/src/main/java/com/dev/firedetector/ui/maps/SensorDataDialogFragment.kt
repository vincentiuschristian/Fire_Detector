package com.dev.firedetector.ui.maps

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import com.dev.firedetector.R
import com.dev.firedetector.core.data.source.remote.response.SensorDataResponse
import com.dev.firedetector.databinding.FragmentSensorDataDialogBinding
import com.dev.firedetector.ui.home.HomeViewModel
import com.dev.firedetector.util.Result
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SensorDataDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSensorDataDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var macAddress: String
    private val viewModel: HomeViewModel by activityViewModels()

    companion object {
        private const val ARG_MAC = "mac_address"

        fun newInstance(sensor: SensorDataResponse): SensorDataDialogFragment {
            val fragment = SensorDataDialogFragment()
            val args = Bundle()
            args.putString(ARG_MAC, sensor.macAddress)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSensorDataDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        macAddress = arguments?.getString(ARG_MAC) ?: run {
            Toast.makeText(requireContext(), "MAC Address tidak ditemukan", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }

        viewModel.getSensorListLiveData().observe(viewLifecycleOwner) { result ->
            if (result is Result.Success) {
                val data = result.data.find { it.macAddress == macAddress }
                if (data != null) {
                    showSensorData(data)
                }
            }
        }
    }

    private fun showSensorData(data: SensorDataResponse) {
        binding.apply {
            txtTemperature.text = getString(R.string.text_suhu, data.temperature.toString())
            txtHumidity.text = getString(R.string.text_kelembapan, data.humidity.toString())
            txtGasStatus.text = data.mqStatus.ifEmpty { "Tidak tersedia" }
            txtFireStatus.text = data.flameStatus.ifEmpty { "Tidak tersedia" }
            txtTimestamp.text = data.timestamp

            btnOpenInMaps.setOnClickListener {
                val lat = data.latitude
                val lng = data.longitude
                val uri = "geo:$lat,$lng?q=$lat,$lng(Lokasi Sensor)".toUri()
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage("com.google.android.apps.maps")
                }
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Google Maps tidak tersedia", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
