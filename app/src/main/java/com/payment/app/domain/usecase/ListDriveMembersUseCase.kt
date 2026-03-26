package com.payment.app.domain.usecase

import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class DriveMember(
    val permissionId: String,
    val emailAddress: String,
    val displayName: String,
    val role: String,
    val type: String
) {
    val isOwner: Boolean
        get() = role.equals("owner", ignoreCase = true)
}

class ListDriveMembersUseCase @Inject constructor() {

    suspend operator fun invoke(accessToken: String, folderId: String): Result<List<DriveMember>> = withContext(Dispatchers.IO) {
        runCatching {
            require(accessToken.isNotBlank()) { "アクセストークンが未入力です" }
            require(folderId.isNotBlank()) { "共有フォルダIDが未入力です" }

            val connection = (
                URL(
                    "https://www.googleapis.com/drive/v3/files/${folderId.trim()}/permissions" +
                        "?fields=permissions(id,emailAddress,displayName,role,type)"
                ).openConnection() as HttpURLConnection
                ).apply {
                requestMethod = "GET"
                connectTimeout = 15_000
                readTimeout = 20_000
                setRequestProperty("Authorization", "Bearer ${accessToken.trim()}")
                setRequestProperty("Accept", "application/json")
            }

            val response = connection.readText()
            if (connection.responseCode !in 200..299) {
                error(parseDriveMembersError(connection.responseCode, response))
            }

            val json = JSONObject(response)
            val permissions = json.optJSONArray("permissions")
            buildList {
                if (permissions != null) {
                    for (i in 0 until permissions.length()) {
                        val item = permissions.optJSONObject(i) ?: continue
                        add(
                            DriveMember(
                                permissionId = item.optString("id").orEmpty(),
                                emailAddress = item.optString("emailAddress").orEmpty(),
                                displayName = item.optString("displayName").orEmpty(),
                                role = item.optString("role").orEmpty(),
                                type = item.optString("type").orEmpty()
                            )
                        )
                    }
                }
            }.sortedWith(
                compareByDescending<DriveMember> { it.isOwner }
                    .thenBy { it.emailAddress.ifBlank { it.displayName } }
            )
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )
    }

    private fun parseDriveMembersError(code: Int, response: String): String {
        val apiMessage = runCatching {
            JSONObject(response).optJSONObject("error")?.optString("message").orEmpty()
        }.getOrDefault("")
        val readable = when {
            code == 401 -> "認証が期限切れです。Google接続をやり直してください。"
            code == 403 -> "Driveアクセス権限が不足しています。"
            code == 404 -> "共有フォルダIDが見つかりません。"
            apiMessage.isNotBlank() -> apiMessage
            else -> "共有メンバーの取得に失敗しました (HTTP $code)"
        }
        return "Drive members list failed ($code): $readable"
    }
}

