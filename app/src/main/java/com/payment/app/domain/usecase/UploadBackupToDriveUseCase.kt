package com.payment.app.domain.usecase

import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class UploadBackupToDriveUseCase @Inject constructor() {

    data class Request(
        val accessToken: String,
        val fileName: String,
        val jsonBody: String,
        val folderId: String? = null
    )

    suspend operator fun invoke(request: Request): Result<String> = withContext(Dispatchers.IO) {
        if (request.accessToken.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("アクセストークンが未入力です"))
        }
        val boundary = "----paymentapp-${System.currentTimeMillis()}"
        val metadata = JSONObject().apply {
            put("name", request.fileName)
            if (!request.folderId.isNullOrBlank()) {
                put("parents", org.json.JSONArray().put(request.folderId))
            }
        }.toString()
        val multipart = buildString {
            append("--").append(boundary).append("\r\n")
            append("Content-Type: application/json; charset=UTF-8\r\n\r\n")
            append(metadata).append("\r\n")
            append("--").append(boundary).append("\r\n")
            append("Content-Type: application/json; charset=UTF-8\r\n\r\n")
            append(request.jsonBody).append("\r\n")
            append("--").append(boundary).append("--\r\n")
        }.toByteArray(Charsets.UTF_8)

        val connection =
            (URL("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                connectTimeout = 15_000
                readTimeout = 20_000
                setRequestProperty("Authorization", "Bearer ${request.accessToken.trim()}")
                setRequestProperty("Content-Type", "multipart/related; boundary=$boundary")
                setRequestProperty("Content-Length", multipart.size.toString())
            }

        runCatching {
            connection.outputStream.use { it.write(multipart) }
            val stream = if (connection.responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }
            val body = stream?.use { input ->
                ByteArrayOutputStream().also { output -> input.copyTo(output) }.toString(Charsets.UTF_8)
            }.orEmpty()
            if (connection.responseCode !in 200..299) {
                error("Drive upload failed (${connection.responseCode}): $body")
            }
            val id = JSONObject(body).optString("id")
            id.ifBlank { "uploaded" }
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )
    }
}

