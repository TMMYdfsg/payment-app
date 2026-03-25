package com.payment.app.domain.usecase

import com.payment.app.data.repository.PaymentRepository
import com.payment.app.widget.WidgetUpdater
import javax.inject.Inject

class DeletePaymentUseCase @Inject constructor(
    private val repository: PaymentRepository,
    private val widgetUpdater: WidgetUpdater
) {
    suspend operator fun invoke(cardId: Long, yearMonth: String) {
        repository.deletePayment(cardId, yearMonth)
        widgetUpdater.refresh()
    }
}
