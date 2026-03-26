package com.payment.app.domain.usecase

import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class InviteDriveMemberUseCase @Inject constructor() {

    suspend operator fun invoke(
        accessToken: String,
        folderId: String,
        inviteEmail: String,
        role: String = "writer"
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            require(accessToken.isNotBlank()) { "アクセストークンが未入力です" }
            require(folderId.isNotBlank()) { "共有フォルダIDが未入力です" }
            val normalizedEmail = inviteEmail.trim().lowercase()
            require(normalizedEmail.isNotBlank()) { "招待先メールが未入力です" }
            require(EMAIL_REGEX.matches(normalizedEmail)) { "招待先メール形式が不正です: $normalizedEmail" }

            val body = JSONObject()
                .put("type", "user")
                .put("role", role)
                .put("emailAddress", normalizedEmail)
                .toString()
                .toByteArray(Charsets.UTF_8)

            val connection = (
                URL(
                    "https://www.googleapis.com/drive/v3/files/${folderId.trim()}/permissions" +
                        "?sendNotificationEmail=true&emailMessage=Payment%20App%20%E3%81%8B%E3%82%89%E5%85%B1%E6%9C%89%E6%8B%9B%E5%BE%85%E3%81%8C%E5%B1%8A%E3%81%84%E3%81%A6%E3%81%84%E3%81%BE%E3%81%99"
                ).openConnection() as HttpURLConnection
                ).apply {
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
                error(parseDriveInviteError(connection.responseCode, response))
            }
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { Result.failure(it) }
        )
    }

    private fun parseDriveInviteError(code: Int, response: String): String {
        val apiMessage = runCatching {
            JSONObject(response).optJSONObject("error")?.optString("message").orEmpty()
        }.getOrDefault("")

        val normalized = apiMessage.lowercase()
        val readable = when {
            code == 401 -> "認証が期限切れです。Google接続をやり直してください。"
            code == 403 && normalized.contains("insufficient") ->
                "Google Drive 権限が不足しています。再認証してください。"
            code == 403 && normalized.contains("sharing") ->
                "共有設定で外部招待が制限されています。Drive 側設定を確認してください。"
            code == 404 -> "共有フォルダIDが見つかりません。フォルダIDを確認してください。"
            code == 409 || normalized.contains("already") ->
                "このメールアドレスは既に共有済みです。"
            apiMessage.isNotBlank() -> apiMessage
            else -> "Drive招待送信に失敗しました (HTTP $code)"
        }
        return "Drive invite failed ($code): $readable"
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}
