package com.payment.app.domain.usecase

import com.payment.app.data.repository.PaymentRepository
import javax.inject.Inject

class UpdatePaymentPaidUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(cardId: Long, yearMonth: String, isPaid: Boolean) =
        repository.updatePaymentPaid(cardId, yearMonth, isPaid)
}
