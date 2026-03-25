package com.payment.app

import android.app.Application
import com.payment.app.data.repository.PaymentRepository
import com.payment.app.notifications.ReminderScheduler
import com.payment.app.widget.WidgetUpdater
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltAndroidApp
class PaymentApp : Application() {

    @Inject lateinit var reminderScheduler: ReminderScheduler
    @Inject lateinit var repository: PaymentRepository
    @Inject lateinit var widgetUpdater: WidgetUpdater
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        reminderScheduler.scheduleDailyChecks()
        appScope.launch {
            val day = repository.observeNotificationSetting().first()?.monthlyReminderDay ?: 1
            reminderScheduler.scheduleMonthlyReminder(day)
        }
        appScope.launch {
            widgetUpdater.refresh()
        }
    }
}
