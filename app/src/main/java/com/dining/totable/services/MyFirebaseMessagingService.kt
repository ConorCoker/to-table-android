package com.dining.totable.services

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.dining.totable.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received from: ${remoteMessage.from}")

        remoteMessage.notification?.let {
            val title = it.title ?: "New Order"
            val body = it.body ?: "A new order has been received"
            Log.d(TAG, "Notification: $title - $body")
            sendNotification(title, body)
        }

        // Handle data payload (if any)
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Data payload: ${remoteMessage.data}")
            // I can use this to pass additional order details if needed
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private fun sendNotification(title: String, body: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(
            this,
            ContextCompat.getString(this, R.string.default_notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            1,
            notification
        )
    }

    override fun handleIntent(intent: Intent?) {
        super.handleIntent(intent)
    }

    override fun getStartCommandIntent(originalIntent: Intent?): Intent {
        return super.getStartCommandIntent(originalIntent)
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}