package com.payment.app

import android.app.Application
import com.payment.app.data.datastore.SettingsDataStore
import com.payment.app.data.repository.PaymentRepository
import com.payment.app.domain.usecase.DownloadSharedBackupFromDriveUseCase
import com.payment.app.domain.usecase.ImportBackupJsonUseCase
import com.payment.app.notifications.ReminderScheduler
import com.payment.app.widget.WidgetUpdater
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltAndroidApp
class PaymentApp : Application() {

    @Inject lateinit var reminderScheduler: ReminderScheduler
    @Inject lateinit var repository: PaymentRepository
    @Inject lateinit var widgetUpdater: WidgetUpdater
    @Inject lateinit var settingsDataStore: SettingsDataStore
    @Inject lateinit var downloadSharedBackupFromDriveUseCase: DownloadSharedBackupFromDriveUseCase
    @Inject lateinit var importBackupJsonUseCase: ImportBackupJsonUseCase
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            runCatching {
                reminderScheduler.scheduleDailyChecks()
                val day = repository.observeNotificationSetting().first()?.monthlyReminderDay ?: 1
                reminderScheduler.scheduleMonthlyReminder(day)
            }
        }
        appScope.launch {
            runCatching { widgetUpdater.refresh() }
        }
        appScope.launch {
            runCatching {
                val cloudPrefs = settingsDataStore.cloudSyncPrefs.first()
                if (!cloudPrefs.enabled) return@runCatching

                // 起動直後の負荷を抑えるため、直近同期済みならスキップ
                val now = System.currentTimeMillis()
                val elapsed = now - cloudPrefs.lastSyncAt
                if (cloudPrefs.lastSyncAt > 0L && elapsed in 0 until AUTO_SYNC_INTERVAL_MS) return@runCatching

                val token = settingsDataStore.driveAccessToken.first()
                val folderId = settingsDataStore.driveFolderId.first()
                val groupName = settingsDataStore.driveGroupName.first()
                if (token.isBlank() || folderId.isBlank() || groupName.isBlank()) return@runCatching

                val result = downloadSharedBackupFromDriveUseCase(
                    accessToken = token,
                    folderId = folderId,
                    fileName = buildSharedFileName(groupName)
                )
                result.onSuccess { json ->
                    runCatching {
                        importBackupJsonUseCase(json)
                        withContext(Dispatchers.IO) { widgetUpdater.refresh() }
                    }
                }
            }
        }
    }

    private fun buildSharedFileName(groupName: String): String {
        val sanitized = groupName.ifBlank { "payment_group" }
            .replace(Regex("[^A-Za-z0-9_-]"), "_")
        return "${sanitized}_latest.json"
    }

    private companion object {
        const val AUTO_SYNC_INTERVAL_MS = 6 * 60 * 60 * 1000L // 6h
    }
}
