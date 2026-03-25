package com.payment.app.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun initialize() {
        scheduleDailyChecks()
        scheduleMonthlyReminder(1)
    }

    fun scheduleDailyChecks() {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayToHour(8), TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "payment_daily_checks",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun scheduleMonthlyReminder(day: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MonthlyReminderReceiver::class.java).apply {
            putExtra(EXTRA_DAY, day.coerceIn(1, 28))
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_MONTHLY,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        val triggerAt = nextMonthlyTrigger(day.coerceIn(1, 28))
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }

    private fun initialDelayToHour(hour: Int): Long {
        val now = LocalDateTime.now()
        val next = now.withHour(hour).withMinute(0).withSecond(0).withNano(0).let {
            if (it.isAfter(now)) it else it.plusDays(1)
        }
        val nowMillis = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val nextMillis = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return (nextMillis - nowMillis).coerceAtLeast(1_000)
    }

    private fun nextMonthlyTrigger(day: Int): Long {
        val now = LocalDateTime.now()
        val thisMonthDay = day.coerceAtMost(now.toLocalDate().lengthOfMonth())
        val thisMonth = now.withDayOfMonth(thisMonthDay).withHour(9).withMinute(0).withSecond(0).withNano(0)
        val target = if (thisMonth.isAfter(now)) {
            thisMonth
        } else {
            val nextMonth = now.plusMonths(1)
            nextMonth.withDayOfMonth(day.coerceAtMost(nextMonth.toLocalDate().lengthOfMonth()))
                .withHour(9)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
        }
        return target.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    companion object {
        const val EXTRA_DAY = "monthly_day"
        private const val REQUEST_MONTHLY = 5001
    }
}
