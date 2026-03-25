package com.payment.app.domain.usecase

import android.content.Context
import com.payment.app.data.repository.PaymentRepository
import com.payment.app.util.exportPaymentsToCsv
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class ExportPaymentsUseCase @Inject constructor(
    private val repository: PaymentRepository,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(yearMonth: String): File? {
        val data = repository.getCardPaymentsOnce(yearMonth)
        return exportPaymentsToCsv(context, yearMonth, data)
    }
}
