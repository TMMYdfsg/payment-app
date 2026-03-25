package com.payment.app.domain.usecase

import com.payment.app.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLockEnabledUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    operator fun invoke(): Flow<Boolean> = settings.lockEnabled
}

class SetLockEnabledUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke(enabled: Boolean) = settings.setLockEnabled(enabled)
}

class GetThemeModeUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    operator fun invoke(): Flow<String> = settings.themeMode
}

class SetThemeModeUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke(mode: String) = settings.setThemeMode(mode)
}

class GetThemeAccentUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    operator fun invoke(): Flow<String> = settings.themeAccent
}

class SetThemeAccentUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke(accent: String) = settings.setThemeAccent(accent)
}

class GetReminderLeadDaysUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    operator fun invoke(): Flow<Int> = settings.reminderLeadDays
}

class SetReminderLeadDaysUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke(days: Int) = settings.setReminderLeadDays(days)
}
