package com.payment.app.domain.usecase

import com.payment.app.data.repository.PaymentRepository
import javax.inject.Inject

class UpdatePaymentAccountUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(cardId: Long, yearMonth: String, accountId: Long?) =
        repository.updatePaymentAccount(cardId, yearMonth, accountId)
}
