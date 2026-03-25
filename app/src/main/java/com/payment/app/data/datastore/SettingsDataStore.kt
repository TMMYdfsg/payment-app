package com.payment.app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
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

    private object Keys {
        val themeMode = stringPreferencesKey("theme_mode")
        val themeAccent = stringPreferencesKey("theme_accent")
        val lockEnabled = booleanPreferencesKey("lock_enabled")
        val reminderLeadDays = intPreferencesKey("reminder_lead_days")
        val budgetAlertThreshold = intPreferencesKey("budget_alert_threshold")
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.themeMode] ?: "system"
    }

    val lockEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.lockEnabled] ?: false
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

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[Keys.themeMode] = mode }
    }

    suspend fun setLockEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.lockEnabled] = enabled }
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
}
