package com.payment.app.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.payment.app.data.model.CardWithPayment
import com.payment.app.data.repository.PaymentRepository
import com.payment.app.navigation.AppLaunchRoute
import com.payment.app.navigation.Screen
import com.payment.app.navigation.createLaunchIntent
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

private data class ReminderNotification(
    val title: String,
    val body: String,
    val contentRoute: String,
    val quickCompleteCard: CardWithPayment? = null,
    val quickCompleteCardIds: LongArray = longArrayOf()
)

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
        val unpaid = cards.filter { !it.isPaid && it.amount > 0L }

        val notifications = mutableListOf<ReminderNotification>()

        val dueSoon = unpaid.filter { card ->
            val scheduled = calculateBillingDate(parseYearMonth(yearMonth), card.dueDate).scheduledDate
            scheduled.minusDays(settings.reminderLeadDays.toLong()) == today
        }
        if (dueSoon.isNotEmpty()) {
            val single = dueSoon.singleOrNull()
            notifications += ReminderNotification(
                title = "引落${settings.reminderLeadDays}日前",
                body = single?.let { "${it.cardName} ¥${it.amount}" } ?: "今日チェック: ${dueSoon.size}件",
                contentRoute = single?.let { Screen.InputFlow.createRoute(yearMonth, it.dueDate) }
                    ?: Screen.List.createRoute(yearMonth),
                quickCompleteCard = single
            )
        }

        val overdue = unpaid.filter { card ->
            val scheduled = calculateBillingDate(parseYearMonth(yearMonth), card.dueDate).scheduledDate
            !scheduled.isAfter(today)
        }
        if (overdue.isNotEmpty()) {
            val single = overdue.singleOrNull()
            notifications += ReminderNotification(
                title = "未完了リマインダー",
                body = single?.let { "${it.cardName} ¥${it.amount}" }
                    ?: "未完了 ${overdue.size}件 / ¥${overdue.sumOf { it.amount }}",
                contentRoute = Screen.List.createRoute(yearMonth, unpaidOnly = true),
                quickCompleteCard = single,
                quickCompleteCardIds = overdue.map { it.cardId }.toLongArray()
            )
        }

        if (today.dayOfMonth == settings.monthlyReminderDay) {
            val enteredCount = cards.count { it.amount > 0 }
            if (enteredCount < cards.size) {
                notifications += ReminderNotification(
                    title = "月次入力催促",
                    body = "入力済み $enteredCount / ${cards.size}",
                    contentRoute = AppLaunchRoute.input()
                )
            }
        }

        if (budget != null && budget > 0L) {
            val total = cards.sumOf { it.amount }
            val ratio = (total * 100 / budget).toInt()
            if (ratio >= 100) {
                notifications += ReminderNotification(
                    title = "予算100%超過",
                    body = "使用率 ${ratio}% (¥$total / ¥$budget)",
                    contentRoute = AppLaunchRoute.analytics()
                )
            } else if (ratio >= settings.budgetAlertThreshold) {
                notifications += ReminderNotification(
                    title = "予算${settings.budgetAlertThreshold}%到達",
                    body = "使用率 ${ratio}% (¥$total / ¥$budget)",
                    contentRoute = AppLaunchRoute.analytics()
                )
            }
        }

        val state = applicationContext.getSharedPreferences("notification_state", Context.MODE_PRIVATE)
        val dateKey = today.toString()
        notifications.forEachIndexed { index, item ->
            val key = "$dateKey:${item.title}:${item.body}"
            if (!state.getBoolean(key, false)) {
                notify(index + 1, yearMonth, item)
                state.edit().putBoolean(key, true).apply()
            }
        }
        return Result.success()
    }

    private fun notify(id: Int, yearMonth: String, payload: ReminderNotification) {
        val mgr = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val contentIntent = PendingIntent.getActivity(
            applicationContext,
            10_000 + id,
            createLaunchIntent(applicationContext, payload.contentRoute),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID_REMINDER)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(payload.title)
            .setContentText(payload.body)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        when {
            payload.quickCompleteCard != null -> {
                val actionIntent = PaymentActionReceiver.createMarkPaidIntent(
                    context = applicationContext,
                    cardId = payload.quickCompleteCard.cardId,
                    yearMonth = yearMonth,
                    notificationId = 1000 + id
                )
                val actionPendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    20_000 + id,
                    actionIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                builder.addAction(0, "引落完了", actionPendingIntent)
            }
            payload.quickCompleteCardIds.isNotEmpty() -> {
                val actionIntent = PaymentActionReceiver.createMarkPaidBatchIntent(
                    context = applicationContext,
                    cardIds = payload.quickCompleteCardIds,
                    yearMonth = yearMonth,
                    notificationId = 1000 + id
                )
                val actionPendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    25_000 + id,
                    actionIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                builder.addAction(0, "未完了一括完了", actionPendingIntent)
            }
        }

        mgr.notify(1000 + id, builder.build())
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
