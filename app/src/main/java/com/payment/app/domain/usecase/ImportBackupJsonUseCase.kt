package com.payment.app.domain.usecase

import com.payment.app.data.repository.PaymentRepository
import com.payment.app.util.backupSnapshotFromJson
import javax.inject.Inject

class ImportBackupJsonUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(json: String) {
        val snapshot = backupSnapshotFromJson(json)
        repository.replaceBackupSnapshot(snapshot)
    }
}
