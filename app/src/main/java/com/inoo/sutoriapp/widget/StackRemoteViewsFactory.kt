package com.inoo.sutoriapp.widget

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.lifecycle.ViewModelProvider
import com.inoo.sutoriapp.R
import com.inoo.sutoriapp.data.remote.response.story.ListStoryItem
import kotlinx.coroutines.runBlocking
import java.net.HttpURLConnection
import java.net.URL

class StackRemoteViewsFactory(private val mContext: Context, private var widgetViewModel: WidgetViewModel) : RemoteViewsService.RemoteViewsFactory {

    private val stories = mutableListOf<ListStoryItem>()
    private lateinit var token: String
    private var page: Int = 1
    private var pageSize: Int = 10
    private var location: Int = 0

    companion object {
        const val PREFS_NAME = "SutoriAppPreferences"
    }

    override fun onCreate() {
        widgetViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(mContext.applicationContext as android.app.Application)
            .create(WidgetViewModel::class.java)

        val sharedPreferences = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", null).toString()
    }

    override fun onDataSetChanged() {
        runBlocking {
            val fetchedStories = widgetViewModel.fetchStories(token, page, pageSize, location)
            stories.clear()
            if (fetchedStories != null) {
                stories.addAll(fetchedStories)
            }
        }
    }

    override fun getViewAt(position: Int): RemoteViews {
        if (position == stories.size - 1) {
            page++
            onDataSetChanged()
        }
        val sortedData = stories.sortedByDescending { it.createdAt }
        val story = sortedData[position]
        val views = RemoteViews(mContext.packageName, R.layout.widget_item).apply {
            setTextViewText(R.id.user_name, story.name)
            val bitmap = story.photoUrl?.let { loadImageIntoWidget(it) }
            bitmap?.let {
                setImageViewBitmap(R.id.image_view, it)
            }
        }

        val fillInIntent = Intent().apply {
            putExtra(ListStoryWidget.EXTRA_ITEM, story.id)
        }
        views.setOnClickFillInIntent(R.id.widget_item, fillInIntent)

        return views
    }

    override fun getCount(): Int = stories.size
    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true
    override fun onDestroy() {}

    private fun loadImageIntoWidget(imageUrl: String): android.graphics.Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
