package com.dev.firedetector.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dev.firedetector.AuthActivity
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.databinding.FragmentProfileBinding

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
           viewModel.logout()
            startActivity(Intent(requireActivity(), AuthActivity::class.java))
        }

        viewModel.loading.observe(viewLifecycleOwner){
            showLoading(it)
        }

        viewModel.userData.observe(viewLifecycleOwner){data ->
            if (data != null){
                binding.tvUserName.text = data.username
                binding.tvUserEmail.text = data.email
                binding.tvUserFullName.text = data.username
                binding.tvUserLocation.text = data.location
            } else {
                showToast(viewModel.error.toString())
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