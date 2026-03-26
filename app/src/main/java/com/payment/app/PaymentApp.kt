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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltAndroidApp
class PaymentApp : Application() {

    @Inject lateinit var reminderScheduler: ReminderScheduler
    @Inject lateinit var repository: PaymentRepository
    @Inject lateinit var widgetUpdater: WidgetUpdater
    @Inject lateinit var settingsDataStore: SettingsDataStore
    @Inject lateinit var downloadSharedBackupFromDriveUseCase: DownloadSharedBackupFromDriveUseCase
    @Inject lateinit var importBackupJsonUseCase: ImportBackupJsonUseCase
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
        appScope.launch {
            val token = settingsDataStore.driveAccessToken.first()
            val folderId = settingsDataStore.driveFolderId.first()
            val groupName = settingsDataStore.driveGroupName.first()
            if (token.isNotBlank() && folderId.isNotBlank() && groupName.isNotBlank()) {
                downloadSharedBackupFromDriveUseCase(
                    accessToken = token,
                    folderId = folderId,
                    fileName = buildSharedFileName(groupName)
                ).onSuccess { json ->
                    importBackupJsonUseCase(json)
                    widgetUpdater.refresh()
                }
            }
        }
    }

    private fun buildSharedFileName(groupName: String): String {
        val sanitized = groupName.ifBlank { "payment_group" }
            .replace(Regex("[^A-Za-z0-9_-]"), "_")
        return "${sanitized}_latest.json"
    }
}
