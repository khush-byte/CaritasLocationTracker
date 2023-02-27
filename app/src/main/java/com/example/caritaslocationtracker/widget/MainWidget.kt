package com.example.caritaslocationtracker.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.example.caritaslocationtracker.R
import com.example.caritaslocationtracker.main.MainActivity

/**
 * Implementation of App Widget functionality.
 */
class MainWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

@RequiresApi(Build.VERSION_CODES.M)
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    var widgetText = context.getString(R.string.appwidget_text)
    val sharedPreference = context.getSharedPreferences("LocalMemory", Context.MODE_PRIVATE)
    val views = RemoteViews(context.packageName, R.layout.main_widget)

    if(sharedPreference.getInt("state", 0)!=0) {
        widgetText = "tracking in progress..."
        views.setTextColor(R.id.appwidget_text, Color.WHITE)
    }else{
        widgetText = "service is off..."
        views.setTextColor(R.id.appwidget_text, Color.RED)
    }

    views.setTextViewText(R.id.appwidget_text, widgetText)

    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT);
    views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent)
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}