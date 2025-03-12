package com.dev.firedetector.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.databinding.FragmentProfileBinding
import com.dev.firedetector.ui.register.RegisterActivity
import com.dev.firedetector.util.Result

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            viewModel.clearIdSaved()
            startActivity(Intent(requireActivity(), RegisterActivity::class.java))
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.userData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    binding.apply {
                        tvIdPerangkat.text = result.data.deviceId
                        tvUserName.text = result.data.username
                        tvUserEmail.text = result.data.email
                        tvUserFullName.text = result.data.username
                        tvUserLocation.text = result.data.location
                    }
                    Log.d("Data Profile", "ID alat : ${result.data.deviceId}")
                }

                is Result.Loading -> showLoading(true)
                is Result.Error -> showToast(result.error)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showToast(message: String?) {
        Toast.makeText(requireContext(), message!!, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}