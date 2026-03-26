package com.payment.app.domain.usecase

import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class CreateDriveFolderUseCase @Inject constructor() {

    suspend operator fun invoke(accessToken: String, folderName: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            require(accessToken.isNotBlank()) { "アクセストークンが未入力です" }
            require(folderName.isNotBlank()) { "グループ名が未入力です" }

            val body = JSONObject()
                .put("name", folderName.trim())
                .put("mimeType", "application/vnd.google-apps.folder")
                .toString()
                .toByteArray(Charsets.UTF_8)

            val connection = (URL("https://www.googleapis.com/drive/v3/files").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                connectTimeout = 15_000
                readTimeout = 20_000
                setRequestProperty("Authorization", "Bearer ${accessToken.trim()}")
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            }

            connection.outputStream.use { it.write(body) }
            val response = connection.readText()
            if (connection.responseCode !in 200..299) {
                error("Drive folder create failed (${connection.responseCode}): $response")
            }
            JSONObject(response).optString("id").ifBlank { error("folderId を取得できませんでした") }
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )
    }
}

internal fun HttpURLConnection.readText(): String {
    val stream = if (responseCode in 200..299) inputStream else errorStream
    return stream?.use { input ->
        ByteArrayOutputStream().also { output -> input.copyTo(output) }.toString(Charsets.UTF_8)
    }.orEmpty()
}
