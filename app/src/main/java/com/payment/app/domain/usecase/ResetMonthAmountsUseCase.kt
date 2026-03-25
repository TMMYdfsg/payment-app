package com.payment.app.domain.usecase

import com.payment.app.data.repository.PaymentRepository
import javax.inject.Inject

class ResetMonthAmountsUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(yearMonth: String) = repository.resetMonthAmounts(yearMonth)
}
