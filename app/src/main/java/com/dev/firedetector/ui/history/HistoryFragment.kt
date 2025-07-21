package com.dev.firedetector.ui.history

import androidx.fragment.app.Fragment

class HistoryFragment : Fragment() {

/*    private var _binding: FragmentHistoryBinding? = null
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
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Zona 1"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Zona 2"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.getHistoryZona1()
                    1 -> viewModel.getHistoryZona2()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        viewModel.getHistoryZona1()
    }

    private fun observeViewModel() {
        viewModel.historyZona1.observe(viewLifecycleOwner) { result ->
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

        viewModel.historyZona2.observe(viewLifecycleOwner) { result ->
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
    }*/
}