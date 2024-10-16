package com.inoo.sutoriapp.widget

import android.content.Intent
import android.widget.RemoteViewsService
import androidx.lifecycle.ViewModelProvider

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        val viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(WidgetViewModel::class.java)

        return StackRemoteViewsFactory(this.applicationContext, viewModel)
    }
}
