package com.payment.app.domain.usecase

import com.payment.app.data.db.entity.SubscriptionEntity
import com.payment.app.data.repository.PaymentRepository
import javax.inject.Inject

class UpsertSubscriptionUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(entity: SubscriptionEntity) =
        repository.upsertSubscription(entity)
}
