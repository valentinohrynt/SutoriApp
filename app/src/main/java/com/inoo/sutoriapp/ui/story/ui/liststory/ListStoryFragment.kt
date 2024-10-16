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
import com.inoo.sutoriapp.data.pref.SessionViewModelFactory
import com.inoo.sutoriapp.data.pref.SessionViewModel
import com.inoo.sutoriapp.data.pref.SutoriAppPreferences
import com.inoo.sutoriapp.data.pref.dataStore
import com.inoo.sutoriapp.data.remote.response.story.ListStoryItem
import com.inoo.sutoriapp.databinding.FragmentListStoryBinding
import com.inoo.sutoriapp.ui.customview.CustomButton
import com.inoo.sutoriapp.ui.story.adapter.ListItemAdapter
import com.inoo.sutoriapp.ui.story.ui.StoryViewModel
import com.inoo.sutoriapp.utils.Utils.showToast
import java.util.Stack

class ListStoryFragment : Fragment() {
    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!

    private var currentPage = 1
    private val pageSize = 10
    private val location = 0
    private var isLoadingMoreStories = false
    private var allPagesLoaded = false
    private var isInitialLoad = true
    private var isSessionInitialized = false
    private var needsRefresh = false

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

    private val storyViewModel: StoryViewModel by viewModels()
    private val sessionViewModel: SessionViewModel by viewModels{
        SessionViewModelFactory.getInstance(requireContext(), pref)
    }
    private val previousPages = Stack<List<ListStoryItem>>()

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
        setupObservers()
        setupScrollListener()

        if (isInitialLoad) {
            fetchStories()
            isInitialLoad = false
        }
    }

    private fun initializeVariables() {
        val dataStore = requireContext().applicationContext.dataStore
        pref = SutoriAppPreferences.getInstance(dataStore)

        sessionViewModel.getToken().observe(viewLifecycleOwner) { token ->
            this.token = token
            checkSessionInitialization()
        }

        sessionViewModel.getName().observe(viewLifecycleOwner) { name ->
            this.username = name
            checkSessionInitialization()
        }
    }

    private fun checkSessionInitialization() {
        if (token != null && username != null) {
            isSessionInitialized = true
            onSessionInitialized()
        }
    }

    private fun onSessionInitialized() {
        if (isSessionInitialized) {
            fetchStories()
        }
        setupViews()
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
        storyAdapter = ListItemAdapter()
        storyRecyclerView.adapter = storyAdapter
        storyRecyclerView.itemAnimator = DefaultItemAnimator()

        swipeRefreshLayout.setOnRefreshListener {

            refreshStories()
        }
    }

    private fun setupObservers() {
        observeLoading()
        observeError()
        observeStories()
    }

    private fun observeLoading() {
        storyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                swipeRefreshLayout.isRefreshing = false
            } else {
                progressBar.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun observeError() {
        storyViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                errorContainer.visibility = View.VISIBLE
                tvErrorMsg.text = getString(R.string.fetch_story_failed)
                btnRetry.setOnClickListener {
                    errorContainer.visibility = View.GONE
                    storyViewModel.clearError()
                    fetchStories()
                }
            } else {
                errorContainer.visibility = View.GONE
            }
        }
    }

    private fun observeStories() {
        storyViewModel.storyList.observe(viewLifecycleOwner) { storyList ->
            if (storyList != null) {
                if (storyList.isEmpty() && !allPagesLoaded) {
                    allPagesLoaded = true
                    showToast(requireContext(), getString(R.string.no_more_page))
                } else if (storyList.isEmpty()){
                    errorContainer.visibility = View.VISIBLE
                    tvErrorMsg.text = getString(R.string.no_story)
                    btnRetry.setOnClickListener {
                        errorContainer.visibility = View.GONE
                        fetchStories()
                    }
                }
                else {
                    if (currentPage == 1) {
                        storyAdapter.setStories(storyList)
                    } else {
                        storyAdapter.addStories(storyList)
                    }
                    isLoadingMoreStories = false
                }
            }
        }
    }

    private fun fetchStories() {
        token?.let { storyViewModel.fetchStories(it, currentPage, pageSize, location) }
    }

    private fun setupScrollListener() {
        storyRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = storyRecyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (dy > 0 && !isLoadingMoreStories && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 1) {
                    loadMoreStories()
                }

                if (dy < 0 && firstVisibleItemPosition == 0 && !isLoadingMoreStories) {
                    goBackToPreviousPage()
                }
            }
        })
    }

    private fun refreshStories(){
        errorContainer.visibility = View.GONE
        storyViewModel.clearError()
        currentPage = 1
        previousPages.clear()
        storyAdapter.setStories(emptyList())
        fetchStories()
    }

    private fun loadMoreStories() {
        if (allPagesLoaded || isLoadingMoreStories) return

        isLoadingMoreStories = true
        currentPage++
        fetchStories()
    }

    private fun goBackToPreviousPage() {
        if (previousPages.isNotEmpty()) {
            val previousStories = previousPages.pop()
            storyAdapter.setStories(previousStories)
            currentPage--
        }
    }

    override fun onResume() {
        super.onResume()
        allPagesLoaded = false
        isLoadingMoreStories = false
        if (needsRefresh) {
            previousPages.clear()
            storyAdapter.setStories(emptyList())
            allPagesLoaded = false
            isLoadingMoreStories = false
            currentPage = 1
            fetchStories()
            setupScrollListener()
            needsRefresh = false
        }
    }

    override fun onPause() {
        super.onPause()
        needsRefresh = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
