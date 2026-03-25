package com.payment.app.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payment.app.data.db.entity.NotificationSettingEntity
import com.payment.app.domain.usecase.GetNotificationSettingsUseCase
import com.payment.app.domain.usecase.UpsertNotificationSettingUseCase
import com.payment.app.notifications.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationUiState(
    val settings: NotificationSettingEntity? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    getSettingsUseCase: GetNotificationSettingsUseCase,
    private val upsertNotificationSettingUseCase: UpsertNotificationSettingUseCase,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    val uiState: StateFlow<NotificationUiState> = getSettingsUseCase()
        .map { NotificationUiState(settings = it, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotificationUiState())

    fun save(settings: NotificationSettingEntity) {
        viewModelScope.launch {
            upsertNotificationSettingUseCase(settings)
            reminderScheduler.scheduleDailyChecks()
            reminderScheduler.scheduleMonthlyReminder(settings.monthlyReminderDay)
        }
    }
}
