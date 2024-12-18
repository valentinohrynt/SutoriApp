package com.inoo.sutoriapp.ui.story.ui.liststory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.inoo.sutoriapp.R
import com.inoo.sutoriapp.data.pref.SutoriAppPreferences
import com.inoo.sutoriapp.data.pref.dataStore
import com.inoo.sutoriapp.databinding.FragmentListStoryBinding
import com.inoo.sutoriapp.ui.customview.CustomButton
import com.inoo.sutoriapp.ui.story.adapter.ListItemAdapter
import com.inoo.sutoriapp.ui.story.adapter.LoadingStateAdapter

class ListStoryFragment : Fragment() {
    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var pref: SutoriAppPreferences
    private lateinit var username: String

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var errorContainer: ConstraintLayout
    private lateinit var tvErrorMsg: TextView
    private lateinit var btnRetry: CustomButton
    private lateinit var storyRecyclerView: RecyclerView
    private lateinit var storyAdapter: ListItemAdapter

    private val listStoryViewModel: ListStoryViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = SutoriAppPreferences.getInstance(requireContext().dataStore)
        val intent = requireActivity().intent
        username = intent.getStringExtra("name") ?: ""
        setupViews()
    }

    private fun setupViews() {
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.welcome) + " " + username

        swipeRefreshLayout = binding.swipeRefreshLayout
        progressBar = binding.progressBar
        storyRecyclerView = binding.listStoryRecyclerView
        errorContainer = binding.errorContainer
        tvErrorMsg = binding.tvErrorMessage
        btnRetry = binding.btnRetry
        btnRetry.text = getString(R.string.retry)
        storyRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        storyRecyclerView.setHasFixedSize(true)
        storyRecyclerView.itemAnimator = DefaultItemAnimator()

        swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

        setupRecyclerViewAdapter()
    }

    private fun setupRecyclerViewAdapter() {
        storyAdapter = ListItemAdapter()
        storyRecyclerView.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )
        getData()
        storyAdapter.addOnPagesUpdatedListener {
            swipeRefreshLayout.isRefreshing = false
            storyRecyclerView.smoothScrollToPosition(0)
        }
    }

    private fun getData() {
        listStoryViewModel.listStory.observe(viewLifecycleOwner) { pagingData ->
            storyAdapter.submitData(lifecycle, pagingData)
        }
    }

    fun refreshData() {
        storyAdapter.refresh()
        storyAdapter.addOnPagesUpdatedListener {
            swipeRefreshLayout.isRefreshing = false
            storyRecyclerView.smoothScrollToPosition(0)
        }
    }

    fun refresh() {
        swipeRefreshLayout.isRefreshing = true
        refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
