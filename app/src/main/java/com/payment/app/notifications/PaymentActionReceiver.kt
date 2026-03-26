package com.payment.app.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.payment.app.domain.usecase.UpdatePaymentPaidUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking

class PaymentActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val yearMonth = intent.getStringExtra(EXTRA_YEAR_MONTH).orEmpty()
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
        if (yearMonth.isBlank()) return

        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            PaymentActionReceiverEntryPoint::class.java
        )

        runBlocking {
            when (action) {
                ACTION_MARK_PAID -> {
                    val cardId = intent.getLongExtra(EXTRA_CARD_ID, -1L)
                    if (cardId > 0L) {
                        entryPoint.updatePaymentPaidUseCase()(cardId, yearMonth, true)
                    }
                }
                ACTION_MARK_PAID_BATCH -> {
                    val ids = intent.getLongArrayExtra(EXTRA_CARD_IDS)?.toList().orEmpty()
                    ids.forEach { cardId ->
                        if (cardId > 0L) entryPoint.updatePaymentPaidUseCase()(cardId, yearMonth, true)
                    }
                }
            }
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationId > 0) {
            manager.cancel(notificationId)
        }
        ensureNotificationChannel(context)
        manager.notify(
            9_001,
            NotificationCompat.Builder(context, CHANNEL_ID_REMINDER)
                .setSmallIcon(android.R.drawable.checkbox_on_background)
                .setContentTitle("引落完了を記録しました")
                .setContentText(
                    if (action == ACTION_MARK_PAID_BATCH) "通知から複数件を支払済みに更新しました"
                    else "通知から支払済みに更新しました"
                )
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)
                .build()
        )
    }

    companion object {
        private const val ACTION_MARK_PAID = "com.payment.app.action.MARK_PAID"
        private const val ACTION_MARK_PAID_BATCH = "com.payment.app.action.MARK_PAID_BATCH"
        private const val EXTRA_CARD_ID = "extra_card_id"
        private const val EXTRA_CARD_IDS = "extra_card_ids"
        private const val EXTRA_YEAR_MONTH = "extra_year_month"
        private const val EXTRA_NOTIFICATION_ID = "extra_notification_id"

        fun createMarkPaidIntent(
            context: Context,
            cardId: Long,
            yearMonth: String,
            notificationId: Int
        ): Intent = Intent(context, PaymentActionReceiver::class.java).apply {
            action = ACTION_MARK_PAID
            putExtra(EXTRA_CARD_ID, cardId)
            putExtra(EXTRA_YEAR_MONTH, yearMonth)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }

        fun createMarkPaidBatchIntent(
            context: Context,
            cardIds: LongArray,
            yearMonth: String,
            notificationId: Int
        ): Intent = Intent(context, PaymentActionReceiver::class.java).apply {
            action = ACTION_MARK_PAID_BATCH
            putExtra(EXTRA_CARD_IDS, cardIds)
            putExtra(EXTRA_YEAR_MONTH, yearMonth)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PaymentActionReceiverEntryPoint {
    fun updatePaymentPaidUseCase(): UpdatePaymentPaidUseCase
}
