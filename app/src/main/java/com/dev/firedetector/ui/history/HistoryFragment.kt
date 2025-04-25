package com.dev.firedetector.ui.history

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.firedetector.R
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.databinding.FragmentHistoryBinding
import com.dev.firedetector.ui.adapter.HistoryAdapter
import com.dev.firedetector.util.Result
import com.google.android.material.tabs.TabLayout

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
        binding.tabLayout.tabTextColors = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_text))

        setupAdapter()
        setupTabLayout()
        observeViewModel()
    }

    private fun setupAdapter() {
        adapter = HistoryAdapter()
        binding.rvHistory.adapter = adapter
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Ruang Tamu"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Kamar"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.getHistoryRuangTamu()
                    1 -> viewModel.getHistoryKamar()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        viewModel.getHistoryRuangTamu()
    }

    private fun observeViewModel() {
        viewModel.historyRuangTamu.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    showLoading(false)
                    result.data.let {
                        adapter.updateData(it)
                        binding.tvEmptyHistory.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
                is Result.Error -> {
                    showLoading(false)
                    showToast(result.error)
                }
                is Result.Loading -> showLoading(true)
            }
        }

        viewModel.historyKamar.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    showLoading(false)
                    result.data.let {
                        adapter.updateData(it)
                        binding.tvEmptyHistory.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
                is Result.Error -> {
                    showLoading(false)
                    showToast(result.error)
                }
                is Result.Loading -> showLoading(true)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
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