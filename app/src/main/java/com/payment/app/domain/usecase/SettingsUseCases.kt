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

class GetPinHashUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    operator fun invoke(): Flow<String> = settings.pinHash
}

class SetPinHashUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke(hash: String) = settings.setPinHash(hash)
}

class ClearPinHashUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke() = settings.clearPinHash()
}

class GetUnlockGraceEnabledUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    operator fun invoke(): Flow<Boolean> = settings.unlockGraceEnabled
}

class SetUnlockGraceEnabledUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke(enabled: Boolean) = settings.setUnlockGraceEnabled(enabled)
}

class GetUnlockGraceUntilUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    operator fun invoke(): Flow<Long> = settings.unlockGraceUntil
}

class SetUnlockGraceUntilUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke(timestamp: Long) = settings.setUnlockGraceUntil(timestamp)
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

class GetDriveAccessTokenUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    operator fun invoke(): Flow<String> = settings.driveAccessToken
}

class SetDriveAccessTokenUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke(value: String) = settings.setDriveAccessToken(value)
}

class GetDriveFolderIdUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    operator fun invoke(): Flow<String> = settings.driveFolderId
}

class SetDriveFolderIdUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke(value: String) = settings.setDriveFolderId(value)
}

class GetDriveGroupNameUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    operator fun invoke(): Flow<String> = settings.driveGroupName
}

class SetDriveGroupNameUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke(value: String) = settings.setDriveGroupName(value)
}

class GetSyncAccountEmailUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    operator fun invoke(): Flow<String> = settings.syncAccountEmail
}

class SetSyncAccountEmailUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke(value: String) = settings.setSyncAccountEmail(value)
}

class GetCloudSyncPrefsUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    operator fun invoke(): Flow<SettingsDataStore.CloudSyncPrefs> = settings.cloudSyncPrefs
}

class SetCloudSyncEnabledUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke(enabled: Boolean) = settings.setCloudSyncEnabled(enabled)
}

class SetCloudGroupFileIdUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke(fileId: String) = settings.setCloudGroupFileId(fileId)
}

class SetCloudLastSyncUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke(status: String) = settings.setCloudLastSync(status)
}

class GetOcrProfilesJsonUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke(): String = settings.getOcrProfilesJsonOnce()
}

class SetOcrProfilesJsonUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke(value: String) = settings.setOcrProfilesJson(value)
}
