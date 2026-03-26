package com.payment.app.domain.usecase

import com.payment.app.data.repository.PaymentRepository
import com.payment.app.widget.WidgetUpdater
import javax.inject.Inject

class ApplyPreviousMonthTemplateUseCase @Inject constructor(
    private val repository: PaymentRepository,
    private val widgetUpdater: WidgetUpdater
) {
    suspend operator fun invoke(yearMonth: String): Int {
        val count = repository.applyPreviousMonthTemplate(yearMonth)
        if (count > 0) {
            widgetUpdater.refresh()
        }
        return count
    }
}

