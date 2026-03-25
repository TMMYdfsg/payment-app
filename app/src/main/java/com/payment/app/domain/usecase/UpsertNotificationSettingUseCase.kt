package com.payment.app.domain.usecase

import com.payment.app.data.db.entity.NotificationSettingEntity
import com.payment.app.data.repository.PaymentRepository
import javax.inject.Inject

class UpsertNotificationSettingUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(setting: NotificationSettingEntity) =
        repository.upsertNotificationSetting(setting)
}
