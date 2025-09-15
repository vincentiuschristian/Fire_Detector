package com.dev.firedetector.ui.maps

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.dev.firedetector.R
import com.dev.firedetector.core.data.source.remote.response.SensorDataResponse
import com.dev.firedetector.databinding.ActivitySensorMapBinding
import com.dev.firedetector.ui.home.HomeViewModel
import com.dev.firedetector.util.Result
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SensorMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivitySensorMapBinding
    private var currentMarker: Marker? = null
    private lateinit var selectedSensor: SensorDataResponse
    private val viewModel: HomeViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedSensor = intent.getParcelableExtra("sensor_data", SensorDataResponse::class.java)
            ?: error("Sensor data tidak diterima")

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        updateMarker(selectedSensor)

        mMap.setOnMarkerClickListener { marker ->
            (marker.tag as? SensorDataResponse)?.let {
                SensorDataDialogFragment.newInstance(it)
                    .show(supportFragmentManager, "DialogSensor")
            }
            true
        }

        viewModel.getSensorListLiveData().observe(this) { result ->
            if (result is Result.Success) {
                result.data.find { it.macAddress == selectedSensor.macAddress }?.let { updatedData ->
                    selectedSensor = updatedData
                    updateMarker(updatedData)
                }
            } else if (result is Result.Error) {
                Toast.makeText(this, "Gagal memuat data sensor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMarker(data: SensorDataResponse) {
        val latLng = LatLng(data.latitude, data.longitude)
        currentMarker?.remove()
        currentMarker = mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Sensor ${data.macAddress}")
        )?.apply {
            tag = data
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
    }
}