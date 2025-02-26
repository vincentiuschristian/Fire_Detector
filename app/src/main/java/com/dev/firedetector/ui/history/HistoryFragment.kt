package com.dev.firedetector.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.databinding.FragmentHistoryBinding
import com.dev.firedetector.ui.adapter.HistoryAdapter
import com.dev.firedetector.util.Result
import com.google.android.material.snackbar.Snackbar

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSensorHistory()
    }

    private fun setupAdapter() {
        adapter = HistoryAdapter()
        binding.rvHistory.adapter = adapter
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewModel.sensorHistory.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    result.data?.let { adapter.updateData(it) }
                }
                is Result.Error -> {
                    showSnackbar(result.message)
                }
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
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
