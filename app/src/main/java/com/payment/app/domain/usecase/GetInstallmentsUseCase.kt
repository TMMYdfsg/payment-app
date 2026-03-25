package com.payment.app.domain.usecase

import com.payment.app.data.db.entity.InstallmentEntity
import com.payment.app.data.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInstallmentsUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    operator fun invoke(): Flow<List<InstallmentEntity>> = repository.observeInstallments()
}
