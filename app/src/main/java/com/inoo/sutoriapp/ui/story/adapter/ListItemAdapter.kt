package com.inoo.sutoriapp.ui.story.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.inoo.sutoriapp.R
import com.inoo.sutoriapp.data.remote.response.story.ListStoryItem
import com.inoo.sutoriapp.databinding.ItemListBinding
import com.inoo.sutoriapp.ui.story.ui.detailstory.DetailStoryActivity

@Suppress("DEPRECATION")
class ListItemAdapter : PagingDataAdapter<ListStoryItem, ListItemAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    inner class ViewHolder(binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        private val view = binding.root
        private val context = view.context
        private val photoImageView: ImageView = binding.ivItemPhoto
        private val nameTextView: TextView = binding.tvItemName

        fun bind(story: ListStoryItem) {
            nameTextView.text = story.name

            Glide.with(context)
                .load(story.photoUrl)
                .preload()

            Glide.with(context)
                .load(story.photoUrl)
                .thumbnail(0.1f)
                .override(600, 400)
                .placeholder(R.drawable.image_preloader_sutoriapp)
                .error(R.drawable.image_broken_sutoriapp)
                .into(photoImageView)

            view.setOnClickListener {
                animateClick(view) {
                    val intent = Intent(context, DetailStoryActivity::class.java)
                    intent.putExtra(DetailStoryActivity.EXTRA_STORY_ID, story.id)
                    context.startActivity(intent)

                    (context as Activity).overridePendingTransition(R.anim.enter, R.anim.exit)
                }
            }
        }

        private fun animateClick(view: View, onAnimationEnd: () -> Unit) {
            val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0.95f)
            val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.95f)
            val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 1f)
            val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 1f)

            val scaleDown = AnimatorSet().apply {
                play(scaleDownX).with(scaleDownY)
                duration = 100
            }

            val scaleUp = AnimatorSet().apply {
                play(scaleUpX).with(scaleUpY)
                duration = 100
            }

            AnimatorSet().apply {
                play(scaleDown).before(scaleUp)
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        onAnimationEnd()
                    }
                })
                start()
            }
        }
    }
    
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
