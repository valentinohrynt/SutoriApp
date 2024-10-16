package com.inoo.sutoriapp.ui.story.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.inoo.sutoriapp.R
import com.inoo.sutoriapp.data.remote.response.story.ListStoryItem
import com.inoo.sutoriapp.ui.story.ui.detailstory.DetailStoryActivity

@Suppress("DEPRECATION")
class ListItemAdapter() : RecyclerView.Adapter<ListItemAdapter.ViewHolder>() {

    private val stories = mutableListOf<ListStoryItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun setStories(newStories: List<ListStoryItem>) {
        val sortedStories = newStories.sortedByDescending { it.createdAt }
        stories.clear()
        stories.addAll(sortedStories)
        notifyDataSetChanged()
    }

    fun addStories(newStories: List<ListStoryItem>) {
        val sortedStories = newStories.sortedByDescending { it.createdAt }
        val startPosition = stories.size
        stories.addAll(sortedStories)
        notifyItemRangeInserted(startPosition, newStories.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = stories[position]
        holder.bind(story)
    }

    override fun getItemCount(): Int {
        return stories.size
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val context = view.context

        private val photoImageView: ImageView = view.findViewById(R.id.iv_item_photo)
        private val nameTextView: TextView = view.findViewById(R.id.tv_item_name)

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

            view.alpha = 0f
            view.animate().alpha(1f).setDuration(500).start()

            view.setOnClickListener {
                animateClick(view) {
                    val intent = Intent(context, DetailStoryActivity::class.java)
                    intent.putExtra("storyId", story.id)
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
}
