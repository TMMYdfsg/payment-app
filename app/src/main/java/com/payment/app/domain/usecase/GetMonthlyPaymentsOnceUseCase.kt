package com.payment.app.domain.usecase

import com.payment.app.data.model.CardWithPayment
import com.payment.app.data.repository.PaymentRepository
import javax.inject.Inject

class GetMonthlyPaymentsOnceUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(yearMonth: String): List<CardWithPayment> =
        repository.getCardPaymentsOnce(yearMonth)
}
