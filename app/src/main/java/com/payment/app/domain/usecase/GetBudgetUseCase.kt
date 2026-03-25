package com.payment.app.domain.usecase

import com.payment.app.data.db.entity.BudgetEntity
import com.payment.app.data.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBudgetUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    operator fun invoke(yearMonth: String, category: String? = null): Flow<BudgetEntity?> =
        repository.observeBudget(yearMonth, category)
}
