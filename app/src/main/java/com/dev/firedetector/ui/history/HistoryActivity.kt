package com.dev.firedetector.ui.history

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.firedetector.R
import com.dev.firedetector.core.data.source.remote.response.SensorDataResponse
import com.dev.firedetector.databinding.ActivityHistoryBinding
import com.dev.firedetector.ui.adapter.HistoryAdapter
import com.dev.firedetector.util.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {
    private val binding: ActivityHistoryBinding by lazy {
        ActivityHistoryBinding.inflate(layoutInflater)
    }
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var adapter: HistoryAdapter
    private var history: List<SensorDataResponse    > = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.topBar)

        adapter = HistoryAdapter()

        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter

        val macAddress = intent.getStringExtra("mac_address")
        if (macAddress != null) {
            observeHistory(macAddress)
        } else {
            Toast.makeText(this, "MAC Address tidak tersedia", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_history, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                showFilterDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeHistory(macAddress: String){
        viewModel.getHistory(macAddress).observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    history = result.data.data
                    updateList(history)
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showFilterDialog() {
        val options = arrayOf("Hari Ini", "Kemarin", "Minggu Lalu", "Bulan Lalu")
        val apiValues = arrayOf("today", "yesterday", "last_week", "last_month")

        AlertDialog.Builder(this)
            .setTitle("Filter Histori Waktu")
            .setItems(options) { _, which ->
                val selectedRange = apiValues[which]
                val mac = intent.getStringExtra("mac_address") ?: return@setItems
                fetchFilteredHistory(mac, selectedRange)
            }
            .show()
    }

    private fun fetchFilteredHistory(mac: String, range: String) {
        viewModel.getFilteredHistory(mac, range).observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)

                is Result.Success -> {
                    showLoading(false)
                    history = result.data.data
                    updateList(history)
                    binding.rvHistory.scrollToPosition(0)
                }

                is Result.Error -> {
                    showLoading(false)
                    history = emptyList()
                    updateList(history)
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateList(data: List<SensorDataResponse>) {
        adapter.submitList(null)
        adapter.submitList(data)
        if (data.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvHistory.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvHistory.visibility = View.VISIBLE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}