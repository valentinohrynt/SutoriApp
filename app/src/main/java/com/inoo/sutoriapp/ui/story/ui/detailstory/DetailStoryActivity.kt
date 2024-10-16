package com.inoo.sutoriapp.ui.story.ui.detailstory

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.inoo.sutoriapp.R
import com.inoo.sutoriapp.data.pref.SessionViewModelFactory
import com.inoo.sutoriapp.data.pref.SessionViewModel
import com.inoo.sutoriapp.data.pref.SutoriAppPreferences
import com.inoo.sutoriapp.data.pref.dataStore
import com.inoo.sutoriapp.databinding.ActivityDetailStoryBinding
import com.inoo.sutoriapp.ui.customview.CustomButton
import com.inoo.sutoriapp.ui.story.ui.StoryViewModel

@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {
    private var _binding: ActivityDetailStoryBinding? = null
    private val binding get() = _binding!!

    private val storyViewModel: StoryViewModel by viewModels()
    private val sessionViewModel: SessionViewModel by viewModels{
        SessionViewModelFactory.getInstance(this, pref)
    }
    private lateinit var pref: SutoriAppPreferences
    private lateinit var token: String

    private lateinit var photoImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var errorContainer: ConstraintLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var retryButton: CustomButton
    private lateinit var backButton: ImageButton

    private lateinit var storyId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        photoImageView = binding.ivDetailPhoto
        nameTextView = binding.tvDetailName
        descriptionTextView = binding.tvDetailDescription
        errorContainer = binding.errorContainer
        progressBar = binding.progressBar
        errorTextView = binding.tvErrorMessage
        retryButton = binding.btnRetry

        pref = SutoriAppPreferences.getInstance(dataStore)

        backButton = binding.btnBack

        retryButton.text = getString(R.string.retry)

        storyId = intent.getStringExtra("storyId") ?: ""

        backButton.setOnClickListener {
            onBackPressed()
            this.overridePendingTransition(R.anim.exit, R.anim.enter)
        }

        sessionViewModel.getToken().observe(this) { token ->
            this.token = token
            setupView()
        }
    }

    private fun setupView(){
        supportActionBar?.hide()

        observeStoryDetail()
        observeError()
        observeLoading()
        fetchStoryDetail(token, storyId)
    }

    private fun fetchStoryDetail(token: String, id: String){
        storyViewModel.fetchStoryDetail(token, id)
    }

    private fun observeStoryDetail(){
        storyViewModel.story.observe(this) { story ->
            if (story != null) {
                nameTextView.text = story.name
                descriptionTextView.text = story.description

                Glide.with(this)
                    .load(story.photoUrl)
                    .into(photoImageView)
            }
            else {
                errorContainer.visibility = View.VISIBLE
                progressBar.visibility = View.GONE

                retryButton.setOnClickListener {
                    fetchStoryDetail(token, storyId)
                    errorContainer.visibility = View.GONE
                }
            }
        }
    }

    private fun observeError(){
        storyViewModel.error.observe(this) { errorMessage ->
            if (errorMessage != null) {
                errorTextView.text = getString(R.string.fetch_story_failed)
                errorContainer.visibility = View.VISIBLE
                progressBar.visibility = View.GONE

                retryButton.setOnClickListener {
                    fetchStoryDetail(token, storyId)
                    errorContainer.visibility = View.GONE
                }
            }
        }
    }

    private fun observeLoading(){
        storyViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}