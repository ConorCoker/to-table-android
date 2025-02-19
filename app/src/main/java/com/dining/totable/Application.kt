package com.dining.totable

import android.app.Application
import com.dining.totable.utils.NotificationUtils

class Application : Application() {
    override fun onCreate() {
        NotificationUtils.createOrderNotificationChannel(this)
        super.onCreate()
    }
}