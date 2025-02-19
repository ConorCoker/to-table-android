package com.dining.totable.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.dining.totable.R

object NotificationUtils {

    fun createOrderNotificationChannel(context: Context) {
        // Create channel to show notifications.
        val channelId = getString(context, R.string.default_notification_channel_id)
        val channelName = getString(context, R.string.default_notification_channel_name)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.createNotificationChannel(
            NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH,
            ),
        )
    }
}