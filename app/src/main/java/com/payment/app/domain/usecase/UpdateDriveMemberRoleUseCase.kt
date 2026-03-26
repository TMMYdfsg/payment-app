package com.payment.app.domain.usecase

import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class UpdateDriveMemberRoleUseCase @Inject constructor() {

    suspend operator fun invoke(
        accessToken: String,
        folderId: String,
        permissionId: String,
        role: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            require(accessToken.isNotBlank()) { "アクセストークンが未入力です" }
            require(folderId.isNotBlank()) { "共有フォルダIDが未入力です" }
            require(permissionId.isNotBlank()) { "権限IDが未入力です" }
            require(role in setOf("reader", "writer")) { "未対応の権限です: $role" }

            val body = JSONObject().put("role", role).toString().toByteArray(Charsets.UTF_8)
            val connection = (
                URL("https://www.googleapis.com/drive/v3/files/${folderId.trim()}/permissions/${permissionId.trim()}")
                    .openConnection() as HttpURLConnection
                ).apply {
                requestMethod = "PATCH"
                doOutput = true
                connectTimeout = 15_000
                readTimeout = 20_000
                setRequestProperty("Authorization", "Bearer ${accessToken.trim()}")
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                setRequestProperty("Accept", "application/json")
            }

            connection.outputStream.use { it.write(body) }
            val response = connection.readText()
            if (connection.responseCode !in 200..299) {
                error(parseRoleUpdateError(connection.responseCode, response))
            }
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { Result.failure(it) }
        )
    }

    private fun parseRoleUpdateError(code: Int, response: String): String {
        val apiMessage = runCatching {
            JSONObject(response).optJSONObject("error")?.optString("message").orEmpty()
        }.getOrDefault("")
        val readable = when {
            code == 401 -> "認証が期限切れです。Google接続をやり直してください。"
            code == 403 -> "このメンバーの権限を変更する権限がありません。"
            code == 404 -> "対象メンバーが見つかりません。"
            apiMessage.isNotBlank() -> apiMessage
            else -> "メンバー権限の更新に失敗しました (HTTP $code)"
        }
        return "Drive member role update failed ($code): $readable"
    }
}

