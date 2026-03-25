package com.payment.app.domain.usecase

import android.content.Context
import com.payment.app.data.model.CardWithPayment
import com.payment.app.data.repository.PaymentRepository
import com.payment.app.util.exportPaymentsToCsv
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ExportPaymentsUseCase @Inject constructor(
    private val repository: PaymentRepository,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(yearMonth: String): String? {
        val data = repository.getCardPaymentsOnce(yearMonth)
        val file = exportPaymentsToCsv(context, yearMonth, data)
        return file?.absolutePath
    }
}
