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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.inoo.sutoriapp.R
import com.inoo.sutoriapp.data.pref.SessionViewModelFactory
import com.inoo.sutoriapp.data.pref.SessionViewModel
import com.inoo.sutoriapp.data.pref.SutoriAppPreferences
import com.inoo.sutoriapp.data.pref.dataStore
import com.inoo.sutoriapp.databinding.FragmentListStoryBinding
import com.inoo.sutoriapp.ui.customview.CustomButton
import com.inoo.sutoriapp.ui.story.adapter.ListItemAdapter
import com.inoo.sutoriapp.ui.story.adapter.LoadingStateAdapter

class ListStoryFragment : Fragment() {
    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var pref : SutoriAppPreferences
    private var token: String? = null
    private var username: String? = null

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var errorContainer: ConstraintLayout
    private lateinit var tvErrorMsg: TextView
    private lateinit var btnRetry: CustomButton
    private lateinit var storyRecyclerView: RecyclerView
    private lateinit var storyAdapter: ListItemAdapter

    private lateinit var listStoryViewModel: ListStoryViewModel
    private val sessionViewModel: SessionViewModel by viewModels{
        SessionViewModelFactory.getInstance(pref)
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

        initializeVariables()
        setupViews()
    }

    private fun initializeVariables() {
        val dataStore = requireContext().applicationContext.dataStore
        pref = SutoriAppPreferences.getInstance(dataStore)

        sessionViewModel.getToken().observe(viewLifecycleOwner) { token ->
            this.token = token
            if (token != null) {
                listStoryViewModel = ViewModelProvider(this@ListStoryFragment, ViewModelFactory(requireContext(), token))[ListStoryViewModel::class.java]
                getData()
            }
        }

        sessionViewModel.getName().observe(viewLifecycleOwner) { name ->
            this.username = name
            setupViews()
        }
    }

    private fun setupViews() {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.welcome) + " " + username

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
            getData()
            storyAdapter.refresh()
        }
    }

    private fun getData() {
        storyAdapter = ListItemAdapter()
        storyRecyclerView.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )

        listStoryViewModel.listStory.observe(viewLifecycleOwner) { pagingData ->
            swipeRefreshLayout.isRefreshing = false
            storyAdapter.submitData(lifecycle, pagingData)

            storyRecyclerView.scrollToPosition(0)
        }
    }

    override fun onResume() {
        super.onResume()
        if (token != null) {
            listStoryViewModel = ViewModelProvider(this@ListStoryFragment, ViewModelFactory(requireContext(),
                token!!
            ))[ListStoryViewModel::class.java]
            getData()
            storyAdapter.refresh()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
