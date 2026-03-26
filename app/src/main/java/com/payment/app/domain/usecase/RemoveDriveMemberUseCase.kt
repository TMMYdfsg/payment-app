package com.payment.app.domain.usecase

import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class RemoveDriveMemberUseCase @Inject constructor() {

    suspend operator fun invoke(
        accessToken: String,
        folderId: String,
        permissionId: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            require(accessToken.isNotBlank()) { "アクセストークンが未入力です" }
            require(folderId.isNotBlank()) { "共有フォルダIDが未入力です" }
            require(permissionId.isNotBlank()) { "権限IDが未入力です" }

            val connection = (
                URL("https://www.googleapis.com/drive/v3/files/${folderId.trim()}/permissions/${permissionId.trim()}")
                    .openConnection() as HttpURLConnection
                ).apply {
                requestMethod = "DELETE"
                connectTimeout = 15_000
                readTimeout = 20_000
                setRequestProperty("Authorization", "Bearer ${accessToken.trim()}")
                setRequestProperty("Accept", "application/json")
            }

            val response = connection.readText()
            if (connection.responseCode !in 200..299) {
                error(parseRemoveError(connection.responseCode, response))
            }
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { Result.failure(it) }
        )
    }

    private fun parseRemoveError(code: Int, response: String): String {
        val apiMessage = runCatching {
            JSONObject(response).optJSONObject("error")?.optString("message").orEmpty()
        }.getOrDefault("")
        val readable = when {
            code == 401 -> "認証が期限切れです。Google接続をやり直してください。"
            code == 403 -> "このメンバーを解除する権限がありません。"
            code == 404 -> "対象メンバーが見つかりません。"
            apiMessage.isNotBlank() -> apiMessage
            else -> "メンバー解除に失敗しました (HTTP $code)"
        }
        return "Drive member remove failed ($code): $readable"
    }
}

