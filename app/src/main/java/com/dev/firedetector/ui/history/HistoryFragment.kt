package com.dev.firedetector.ui.history

import android.os.Bundle
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
    }

    override fun onResume() {
        super.onResume()
        getDataHistory()

        viewModel.getDataHistory()
    }

    private fun getDataHistory(){
//        viewModel.getDataHistory()
//        viewModel.dataHistory.observe(viewLifecycleOwner){result ->
//            showLoading(false)
//            if (result.isEmpty()){
//                binding.tvEmptyHistory.visibility = View.VISIBLE
//            } else {
//                adapter.updateData(result)
//            }
//        }
        viewModel.dataHistory.observe(viewLifecycleOwner) { data ->
            adapter.updateData(data)
        }

        viewModel.loading.observe(viewLifecycleOwner) {
          showLoading(it)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                showSnackbar(error)
            }
        }


    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSnackbar(message: String?) {
        Snackbar.make(binding.root, message ?: "Unknown Error", Snackbar.LENGTH_SHORT).show()
    }


}