package com.payment.app.domain.usecase

import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class DownloadSharedBackupFromDriveUseCase @Inject constructor() {

    suspend operator fun invoke(
        accessToken: String,
        folderId: String,
        fileName: String
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            require(accessToken.isNotBlank()) { "アクセストークンが未入力です" }
            require(folderId.isNotBlank()) { "共有フォルダIDが未入力です" }
            require(fileName.isNotBlank()) { "ファイル名が未入力です" }

            val query = "'${folderId.trim()}' in parents and name='${fileName.trim()}' and trashed=false"
            val encoded = java.net.URLEncoder.encode(query, Charsets.UTF_8.name())
            val searchConnection = (
                URL("https://www.googleapis.com/drive/v3/files?q=$encoded&fields=files(id,name,modifiedTime)").openConnection()
                    as HttpURLConnection
                ).apply {
                requestMethod = "GET"
                connectTimeout = 15_000
                readTimeout = 20_000
                setRequestProperty("Authorization", "Bearer ${accessToken.trim()}")
            }
            val searchResponse = searchConnection.readText()
            if (searchConnection.responseCode !in 200..299) {
                error("Drive file query failed (${searchConnection.responseCode}): $searchResponse")
            }
            val files = JSONObject(searchResponse).optJSONArray("files") ?: JSONArray()
            val fileId = files.optJSONObject(0)?.optString("id")?.ifBlank { null }
                ?: error("共有バックアップが見つかりません")

            val downloadConnection = (
                URL("https://www.googleapis.com/drive/v3/files/${fileId.trim()}?alt=media").openConnection()
                    as HttpURLConnection
                ).apply {
                requestMethod = "GET"
                connectTimeout = 15_000
                readTimeout = 20_000
                setRequestProperty("Authorization", "Bearer ${accessToken.trim()}")
            }
            val body = downloadConnection.readText()
            if (downloadConnection.responseCode !in 200..299) {
                error("Drive download failed (${downloadConnection.responseCode}): $body")
            }
            body
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )
    }
}
