package com.payment.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

const val CHANNEL_ID_REMINDER = "payments_reminder"

fun ensureNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID_REMINDER,
            "支払いリマインダー",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        mgr.createNotificationChannel(channel)
    }
}
