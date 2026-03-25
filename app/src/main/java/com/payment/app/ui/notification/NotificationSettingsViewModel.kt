package com.payment.app.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payment.app.data.db.entity.NotificationSettingEntity
import com.payment.app.domain.usecase.GetNotificationSettingsUseCase
import com.payment.app.domain.usecase.GetLockEnabledUseCase
import com.payment.app.domain.usecase.GetThemeAccentUseCase
import com.payment.app.domain.usecase.GetThemeModeUseCase
import com.payment.app.domain.usecase.SetLockEnabledUseCase
import com.payment.app.domain.usecase.SetThemeAccentUseCase
import com.payment.app.domain.usecase.SetThemeModeUseCase
import com.payment.app.domain.usecase.UpsertNotificationSettingUseCase
import com.payment.app.notifications.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationUiState(
    val settings: NotificationSettingEntity? = null,
    val themeMode: String = "system",
    val themeAccent: String = "ocean",
    val lockEnabled: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    getSettingsUseCase: GetNotificationSettingsUseCase,
    getThemeModeUseCase: GetThemeModeUseCase,
    getThemeAccentUseCase: GetThemeAccentUseCase,
    getLockEnabledUseCase: GetLockEnabledUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    private val setThemeAccentUseCase: SetThemeAccentUseCase,
    private val setLockEnabledUseCase: SetLockEnabledUseCase,
    private val upsertNotificationSettingUseCase: UpsertNotificationSettingUseCase,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    val uiState: StateFlow<NotificationUiState> = combine(
        getSettingsUseCase(),
        getThemeModeUseCase(),
        getThemeAccentUseCase(),
        getLockEnabledUseCase()
    ) { settings, themeMode, themeAccent, lockEnabled ->
        NotificationUiState(
            settings = settings,
            themeMode = themeMode,
            themeAccent = themeAccent,
            lockEnabled = lockEnabled,
            isLoading = false
        )
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotificationUiState())

    fun save(
        settings: NotificationSettingEntity,
        themeMode: String,
        themeAccent: String,
        lockEnabled: Boolean,
        onSaved: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            upsertNotificationSettingUseCase(settings)
            setThemeModeUseCase(themeMode)
            setThemeAccentUseCase(themeAccent)
            setLockEnabledUseCase(lockEnabled)
            reminderScheduler.scheduleDailyChecks()
            reminderScheduler.scheduleMonthlyReminder(settings.monthlyReminderDay)
            onSaved?.invoke()
        }
    }
}
