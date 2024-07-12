package com.dev.firedetector.ui.emergency

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dev.firedetector.R
import com.dev.firedetector.databinding.FragmentEmergencyBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class EmergencyFragment : Fragment() {

    private var _binding: FragmentEmergencyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmergencyBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnCallPemadamKebakaran.setOnClickListener {
                val phoneNumber = "+6285379654250"
                val message = "Halo, ini pesan automatise dari aplikasi saya."
                sendMessage(phoneNumber, message)
            }

            btnPolisi.setOnClickListener {
                val police = "tel:089605958454"
                showConfirmationDialog(police)
            }

            btnAmbulance.setOnClickListener {
                val ambulance = "tel:0895600560600"
                showConfirmationDialog(ambulance)
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

    private fun showConfirmationDialog(number: String){
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

    private fun sendMessage(number: String, message: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://api.whatsapp.com/send?phone=$number&text=$message")
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}