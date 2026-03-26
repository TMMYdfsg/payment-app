package com.payment.app.domain.usecase

import com.payment.app.data.repository.PaymentRepository
import javax.inject.Inject

class UpdateCardCategoryUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(cardId: Long, category: String) =
        repository.updateCardCategory(cardId, category)
}
