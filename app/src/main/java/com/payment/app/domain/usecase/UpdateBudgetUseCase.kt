package com.payment.app.domain.usecase

import com.payment.app.data.repository.PaymentRepository
import javax.inject.Inject

class UpdateBudgetUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(yearMonth: String, category: String?, amount: Long) =
        repository.upsertBudget(yearMonth, category, amount)
}
