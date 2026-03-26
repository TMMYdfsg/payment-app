package com.payment.app.domain.usecase

import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class UpsertSharedBackupToDriveUseCase @Inject constructor(
    private val uploadBackupToDriveUseCase: UploadBackupToDriveUseCase
) {

    data class Request(
        val accessToken: String,
        val folderId: String,
        val fileName: String,
        val jsonBody: String
    )

    suspend operator fun invoke(request: Request): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            require(request.accessToken.isNotBlank()) { "アクセストークンが未入力です" }
            require(request.folderId.isNotBlank()) { "共有フォルダIDが未入力です" }
            require(request.fileName.isNotBlank()) { "ファイル名が未入力です" }

            val existingId = findFileId(
                accessToken = request.accessToken,
                folderId = request.folderId,
                fileName = request.fileName
            )
            if (existingId == null) {
                createFile(request)
            } else {
                updateFile(request.accessToken, existingId, request.jsonBody)
                existingId
            }
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )
    }

    private fun findFileId(accessToken: String, folderId: String, fileName: String): String? {
        val query = "'${folderId.trim()}' in parents and name='${fileName.trim()}' and trashed=false"
        val encoded = java.net.URLEncoder.encode(query, Charsets.UTF_8.name())
        val connection = (
            URL("https://www.googleapis.com/drive/v3/files?q=$encoded&fields=files(id,name)").openConnection()
                as HttpURLConnection
            ).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 20_000
            setRequestProperty("Authorization", "Bearer ${accessToken.trim()}")
        }
        val response = connection.readText()
        if (connection.responseCode !in 200..299) {
            error("Drive file query failed (${connection.responseCode}): $response")
        }
        val files = JSONObject(response).optJSONArray("files") ?: JSONArray()
        return files.optJSONObject(0)?.optString("id")?.ifBlank { null }
    }

    private suspend fun createFile(request: Request): String {
        return uploadBackupToDriveUseCase(
            UploadBackupToDriveUseCase.Request(
                accessToken = request.accessToken,
                fileName = request.fileName,
                jsonBody = request.jsonBody,
                folderId = request.folderId
            )
        ).getOrThrow()
    }

    private fun updateFile(accessToken: String, fileId: String, jsonBody: String) {
        val body = jsonBody.toByteArray(Charsets.UTF_8)
        val connection = (
            URL("https://www.googleapis.com/upload/drive/v3/files/${fileId.trim()}?uploadType=media").openConnection()
                as HttpURLConnection
            ).apply {
            requestMethod = "PATCH"
            doOutput = true
            connectTimeout = 15_000
            readTimeout = 20_000
            setRequestProperty("Authorization", "Bearer ${accessToken.trim()}")
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        }
        connection.outputStream.use { it.write(body) }
        val response = connection.readText()
        if (connection.responseCode !in 200..299) {
            error("Drive file update failed (${connection.responseCode}): $response")
        }
    }
}
