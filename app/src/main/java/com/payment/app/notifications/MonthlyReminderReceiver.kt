package com.payment.app.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.payment.app.navigation.AppLaunchRoute
import com.payment.app.navigation.createLaunchIntent
import com.payment.app.data.repository.PaymentRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MonthlyReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        ensureNotificationChannel(context)
        val entry = EntryPointAccessors.fromApplication(context, MonthlyReceiverEntryPoint::class.java)
        val repository = entry.repository()
        val scheduler = entry.scheduler()
        val day = intent.getIntExtra(ReminderScheduler.EXTRA_DAY, 1)

        runBlocking {
            val settings = repository.observeNotificationSetting().first()
            if (settings?.enabled == true) {
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val contentIntent = PendingIntent.getActivity(
                    context,
                    30_001,
                    createLaunchIntent(context, AppLaunchRoute.input()),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDER)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("月次入力催促")
                    .setContentText("今月の入力を開始してください")
                    .setContentIntent(contentIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build()
                manager.notify(2001, notification)
            }
        }

        scheduler.scheduleMonthlyReminder(day)
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MonthlyReceiverEntryPoint {
    fun repository(): PaymentRepository
    fun scheduler(): ReminderScheduler
}
