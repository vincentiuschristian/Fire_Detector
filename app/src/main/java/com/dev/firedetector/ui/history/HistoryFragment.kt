package com.dev.firedetector.ui.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.firedetector.data.ViewModelFactory
import com.dev.firedetector.data.model.DataAlatModel
import com.dev.firedetector.databinding.FragmentHistoryBinding
import com.dev.firedetector.ui.adapter.HistoryAdapter
import com.google.android.material.snackbar.Snackbar

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var adapter: HistoryAdapter
    private val historyList = ArrayList<DataAlatModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = HistoryAdapter(historyList)
        binding.apply {
            rvHistory.adapter = adapter
            rvHistory.layoutManager = LinearLayoutManager(requireContext())
            rvHistory.setHasFixedSize(true)
        }
        showLoading(true)
        getData()
        viewModel.getId().observe(viewLifecycleOwner) { userModel ->
            Log.d("LoginActivity ID", "Saved ID Perangkat: ${userModel.idPerangkat}")
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDataHistory()
    }

    private fun getData() {
        viewModel.dataHistory.observe(viewLifecycleOwner) { result ->
            showLoading(false)
            Log.d("Data History", "Data History: $result")
            if (result.isEmpty()) {
                binding.tvEmptyHistory.visibility = View.VISIBLE
            } else {
                adapter.updateData(result)
            }
        }
        viewModel.dataHistory.observe(viewLifecycleOwner) { data ->
            adapter.updateData(data)
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSnackbar(message: String?) {
        Snackbar.make(binding.root, message ?: "Unknown Error", Snackbar.LENGTH_SHORT).show()
    }


}