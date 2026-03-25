package com.payment.app.domain.usecase

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.payment.app.data.repository.PaymentRepository
import com.payment.app.util.backupSnapshotToJson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExportBackupJsonUseCase @Inject constructor(
    private val repository: PaymentRepository,
    @ApplicationContext private val context: Context
) {
    suspend fun buildJson(): String {
        val snapshot = repository.getBackupSnapshot()
        return backupSnapshotToJson(snapshot)
    }

    suspend fun saveToDocument(uri: Uri, json: String): Boolean = withContext(Dispatchers.IO) {
        val doc = DocumentFile.fromSingleUri(context, uri) ?: return@withContext false
        val output = context.contentResolver.openOutputStream(doc.uri) ?: return@withContext false
        output.use { it.write(json.toByteArray(Charsets.UTF_8)) }
        true
    }
}

