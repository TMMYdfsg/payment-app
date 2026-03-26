package com.payment.app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val SETTINGS_NAME = "settings"

private val Context.dataStore by preferencesDataStore(name = SETTINGS_NAME)

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    data class CloudSyncPrefs(
        val enabled: Boolean = false,
        val accessToken: String = "",
        val groupId: String = "",
        val ownerEmail: String = "",
        val groupFileId: String = "",
        val lastSyncStatus: String = "",
        val lastSyncAt: Long = 0L
    )

    private object Keys {
        val themeMode = stringPreferencesKey("theme_mode")
        val themeAccent = stringPreferencesKey("theme_accent")
        val lockEnabled = booleanPreferencesKey("lock_enabled")
        val pinHash = stringPreferencesKey("pin_hash")
        val unlockGraceEnabled = booleanPreferencesKey("unlock_grace_enabled")
        val unlockGraceUntil = longPreferencesKey("unlock_grace_until")
        val reminderLeadDays = intPreferencesKey("reminder_lead_days")
        val budgetAlertThreshold = intPreferencesKey("budget_alert_threshold")
        val driveAccessToken = stringPreferencesKey("drive_access_token")
        val driveFolderId = stringPreferencesKey("drive_folder_id")
        val driveGroupName = stringPreferencesKey("drive_group_name")
        val syncAccountEmail = stringPreferencesKey("sync_account_email")
        val cloudSyncEnabled = booleanPreferencesKey("cloud_sync_enabled")
        val cloudGroupFileId = stringPreferencesKey("cloud_group_file_id")
        val cloudLastSyncStatus = stringPreferencesKey("cloud_last_sync_status")
        val cloudLastSyncAt = longPreferencesKey("cloud_last_sync_at")
        val ocrProfilesJson = stringPreferencesKey("ocr_profiles_json")
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.themeMode] ?: "system"
    }

    val lockEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.lockEnabled] ?: false
    }
    val pinHash: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.pinHash].orEmpty()
    }
    val unlockGraceEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.unlockGraceEnabled] ?: true
    }
    val unlockGraceUntil: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[Keys.unlockGraceUntil] ?: 0L
    }

    val themeAccent: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.themeAccent] ?: "ocean"
    }

    val reminderLeadDays: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.reminderLeadDays] ?: 3
    }
    val budgetAlertThreshold: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.budgetAlertThreshold] ?: 80
    }
    val driveAccessToken: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.driveAccessToken].orEmpty()
    }
    val driveFolderId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.driveFolderId].orEmpty()
    }
    val driveGroupName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.driveGroupName] ?: "Payment App Shared Group"
    }
    val syncAccountEmail: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.syncAccountEmail].orEmpty()
    }
    val cloudSyncPrefs: Flow<CloudSyncPrefs> = context.dataStore.data.map { prefs ->
        CloudSyncPrefs(
            enabled = prefs[Keys.cloudSyncEnabled] ?: false,
            accessToken = prefs[Keys.driveAccessToken].orEmpty(),
            groupId = prefs[Keys.driveGroupName] ?: "Payment App Shared Group",
            ownerEmail = prefs[Keys.syncAccountEmail].orEmpty(),
            groupFileId = prefs[Keys.cloudGroupFileId].orEmpty(),
            lastSyncStatus = prefs[Keys.cloudLastSyncStatus].orEmpty(),
            lastSyncAt = prefs[Keys.cloudLastSyncAt] ?: 0L
        )
    }
    val ocrProfilesJson: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.ocrProfilesJson].orEmpty()
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[Keys.themeMode] = mode }
    }

    suspend fun setLockEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.lockEnabled] = enabled }
    }

    suspend fun setPinHash(hash: String) {
        context.dataStore.edit { it[Keys.pinHash] = hash }
    }

    suspend fun clearPinHash() {
        context.dataStore.edit { it.remove(Keys.pinHash) }
    }

    suspend fun setUnlockGraceEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.unlockGraceEnabled] = enabled }
    }

    suspend fun setUnlockGraceUntil(timestamp: Long) {
        context.dataStore.edit { it[Keys.unlockGraceUntil] = timestamp }
    }

    suspend fun setThemeAccent(accent: String) {
        context.dataStore.edit { it[Keys.themeAccent] = accent }
    }

    suspend fun setReminderLeadDays(days: Int) {
        context.dataStore.edit { it[Keys.reminderLeadDays] = days.coerceAtLeast(0) }
    }

    suspend fun setBudgetAlertThreshold(threshold: Int) {
        context.dataStore.edit { it[Keys.budgetAlertThreshold] = threshold.coerceIn(10, 200) }
    }

    suspend fun setDriveAccessToken(value: String) {
        context.dataStore.edit { it[Keys.driveAccessToken] = value.trim() }
    }

    suspend fun setDriveFolderId(value: String) {
        context.dataStore.edit { it[Keys.driveFolderId] = value.trim() }
    }

    suspend fun setDriveGroupName(value: String) {
        context.dataStore.edit { it[Keys.driveGroupName] = value.trim() }
    }

    suspend fun setSyncAccountEmail(value: String) {
        context.dataStore.edit { it[Keys.syncAccountEmail] = value.trim() }
    }

    suspend fun setCloudSyncEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.cloudSyncEnabled] = enabled }
    }

    suspend fun setCloudGroupFileId(value: String) {
        context.dataStore.edit { it[Keys.cloudGroupFileId] = value.trim() }
    }

    suspend fun setCloudLastSync(status: String, timestamp: Long = System.currentTimeMillis()) {
        context.dataStore.edit { prefs ->
            prefs[Keys.cloudLastSyncStatus] = status
            prefs[Keys.cloudLastSyncAt] = timestamp
        }
    }

    suspend fun getOcrProfilesJsonOnce(): String {
        return context.dataStore.data.first()[Keys.ocrProfilesJson].orEmpty()
    }

    suspend fun setOcrProfilesJson(value: String) {
        context.dataStore.edit { it[Keys.ocrProfilesJson] = value }
    }
}
