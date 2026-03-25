package com.payment.app.domain.usecase

import com.payment.app.data.db.entity.NotificationSettingEntity
import com.payment.app.data.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationSettingsUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    operator fun invoke(): Flow<NotificationSettingEntity?> = repository.observeNotificationSetting()
}
