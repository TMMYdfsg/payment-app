package com.payment.app.domain.usecase

import com.payment.app.data.model.CardWithPayment
import com.payment.app.data.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMonthlyPaymentsUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    operator fun invoke(yearMonth: String): Flow<List<CardWithPayment>> =
        repository.observeCardPayments(yearMonth)
}
