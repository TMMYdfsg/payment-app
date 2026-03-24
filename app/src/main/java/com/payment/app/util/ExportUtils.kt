package com.payment.app.util

import android.content.Context
import android.os.Environment
import com.payment.app.data.model.CardWithPayment
import java.io.File
import java.io.FileWriter

fun exportPaymentsToCsv(context: Context, yearMonth: String, items: List<CardWithPayment>): File? {
    if (items.isEmpty()) return null
    val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
    val file = File(dir, "payments_$yearMonth.csv")
    FileWriter(file).use { writer ->
        writer.appendLine("カード名,カテゴリ,支払日,金額,口座,完了,完了日時")
        items.forEach { item ->
            writer.appendLine(
                listOf(
                    item.cardName,
                    item.category,
                    item.dueDate.toString(),
                    item.amount.toString(),
                    item.accountName ?: "",
                    if (item.isPaid) "済" else "未",
                    item.completedAt?.let { ts -> ts.toString() } ?: ""
                ).joinToString(",")
            )
        }
    }
    return file
}
