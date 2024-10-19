package com.inoo.sutoriapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.net.toUri
import com.inoo.sutoriapp.R
import com.inoo.sutoriapp.ui.story.ui.detailstory.DetailStoryActivity

@Suppress("DEPRECATION")
class ListStoryWidget : AppWidgetProvider() {

    companion object {
        private const val STORY_ACTION = "com.inoo.sutoriapp.STORY_ACTION"
        const val EXTRA_ITEM = "com.inoo.sutoriapp.EXTRA_ITEM"

        private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val intent = Intent(context, StackWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = toUri(Intent.URI_INTENT_SCHEME).toUri()
            }

            val views = RemoteViews(context.packageName, R.layout.list_story_widget).apply {
                setRemoteAdapter(R.id.stack_view, intent)
                setEmptyView(R.id.stack_view, R.id.empty_view)
            }

            val clickIntent = Intent(context, ListStoryWidget::class.java).apply {
                action = STORY_ACTION
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }

            val clickPendingIntent = PendingIntent.getBroadcast(
                context, 0, clickIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                else PendingIntent.FLAG_UPDATE_CURRENT
            )

            views.setPendingIntentTemplate(R.id.stack_view, clickPendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == STORY_ACTION) {
            val storyId = intent.getStringExtra(EXTRA_ITEM)
            val detailIntent = Intent(context, DetailStoryActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("storyId", storyId)
            }
            context.startActivity(detailIntent)
        }
    }
}
