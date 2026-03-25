package com.payment.app.domain.usecase

import com.payment.app.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBudgetAlertThresholdUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    operator fun invoke(): Flow<Int> = settings.budgetAlertThreshold
}

class SetBudgetAlertThresholdUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke(threshold: Int) = settings.setBudgetAlertThreshold(threshold)
}
