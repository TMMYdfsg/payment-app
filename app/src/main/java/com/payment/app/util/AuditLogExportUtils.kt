package com.payment.app.util

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter
import org.json.JSONArray
import org.json.JSONObject

fun exportAuditLogsToCsv(context: Context, entries: List<String>): File? {
    if (entries.isEmpty()) return null
    val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
    val file = File(dir, "member_audit_logs.csv")
    FileWriter(file).use { writer ->
        writer.appendLine("index,entry")
        entries.forEachIndexed { index, entry ->
            val escaped = entry.replace("\"", "\"\"")
            writer.appendLine("${index + 1},\"$escaped\"")
        }
    }
    return file
}

fun exportAuditLogsToJson(context: Context, entries: List<String>): File? {
    if (entries.isEmpty()) return null
    val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
    val file = File(dir, "member_audit_logs.json")
    val root = JSONObject().apply {
        put("exportedAt", System.currentTimeMillis())
        put(
            "entries",
            JSONArray().apply {
                entries.forEachIndexed { index, entry ->
                    put(
                        JSONObject().apply {
                            put("index", index + 1)
                            put("entry", entry)
                        }
                    )
                }
            }
        )
    }
    file.writeText(root.toString(2))
    return file
}
