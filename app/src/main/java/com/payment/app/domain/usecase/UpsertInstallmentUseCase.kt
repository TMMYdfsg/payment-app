package com.payment.app.domain.usecase

import com.payment.app.data.db.entity.InstallmentEntity
import com.payment.app.data.repository.PaymentRepository
import javax.inject.Inject

class UpsertInstallmentUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(entity: InstallmentEntity) =
        repository.upsertInstallment(entity)
}
