package com.payment.app.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.payment.app.data.repository.PaymentRepository
import com.payment.app.util.asStorageKey
import com.payment.app.util.calculateBillingDate
import com.payment.app.util.currentYearMonth
import com.payment.app.util.parseYearMonth
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.time.LocalDate
import kotlinx.coroutines.flow.first

class ReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        ensureNotificationChannel(applicationContext)
        val repository = entryPoint().repository()
        val settings = repository.observeNotificationSetting().first() ?: return Result.success()
        if (!settings.enabled) return Result.success()

        val yearMonth = currentYearMonth().asStorageKey()
        val today = LocalDate.now()
        val cards = repository.getCardPaymentsOnce(yearMonth)
        val budget = repository.observeBudget(yearMonth, null).first()?.amount
        val unpaid = cards.filter { !it.isPaid }

        val notifications = mutableListOf<Pair<String, String>>()

        val dueSoon = unpaid.filter { card ->
            val scheduled = calculateBillingDate(parseYearMonth(yearMonth), card.dueDate).scheduledDate
            scheduled.minusDays(settings.reminderLeadDays.toLong()) == today
        }
        if (dueSoon.isNotEmpty()) {
            notifications += "引落${settings.reminderLeadDays}日前" to "今日チェック: ${dueSoon.size}件"
        }

        val overdue = unpaid.filter { card ->
            val scheduled = calculateBillingDate(parseYearMonth(yearMonth), card.dueDate).scheduledDate
            !scheduled.isAfter(today)
        }
        if (overdue.isNotEmpty()) {
            notifications += "未完了リマインダー" to "未完了 ${overdue.size}件 / ¥${overdue.sumOf { it.amount }}"
        }

        if (today.dayOfMonth == settings.monthlyReminderDay) {
            val enteredCount = cards.count { it.amount > 0 }
            if (enteredCount < cards.size) {
                notifications += "月次入力催促" to "入力済み $enteredCount / ${cards.size}"
            }
        }

        if (budget != null && budget > 0L) {
            val total = cards.sumOf { it.amount }
            val ratio = (total * 100 / budget).toInt()
            if (ratio >= 100) {
                notifications += "予算100%超過" to "使用率 ${ratio}% (¥$total / ¥$budget)"
            } else if (ratio >= settings.budgetAlertThreshold) {
                notifications += "予算${settings.budgetAlertThreshold}%到達" to "使用率 ${ratio}% (¥$total / ¥$budget)"
            }
        }

        val state = applicationContext.getSharedPreferences("notification_state", Context.MODE_PRIVATE)
        val dateKey = today.toString()
        notifications.forEachIndexed { index, (title, body) ->
            val key = "$dateKey:$title"
            if (!state.getBoolean(key, false)) {
                notify(index + 1, title, body)
                state.edit().putBoolean(key, true).apply()
            }
        }
        return Result.success()
    }

    private fun notify(id: Int, title: String, body: String) {
        val mgr = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID_REMINDER)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        mgr.notify(1000 + id, notification)
    }

    private fun entryPoint(): WorkerEntryPoint {
        return EntryPointAccessors.fromApplication(applicationContext, WorkerEntryPoint::class.java)
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WorkerEntryPoint {
    fun repository(): PaymentRepository
}
