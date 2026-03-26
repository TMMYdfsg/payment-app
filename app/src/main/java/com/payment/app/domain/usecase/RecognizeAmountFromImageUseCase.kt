package com.payment.app.domain.usecase

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.android.gms.tasks.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class RecognizedAmount(
    val amount: Long,
    val rawText: String,
    val candidateCount: Int,
    val candidates: List<Long>,
    val savedImagePath: String?
)

class RecognizeAmountFromImageUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val recognizer by lazy {
        TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
    }

    suspend operator fun invoke(
        uri: Uri,
        cardNameHint: String? = null,
        templateHints: List<String> = emptyList()
    ): Result<RecognizedAmount> = withContext(Dispatchers.IO) {
        runCatching {
            val saved = copyToAppStorage(uri)
            val image = InputImage.fromFilePath(context, Uri.fromFile(saved))
            val text = recognizer.process(image).awaitResult()
            val scored = collectScoredAmounts(text, cardNameHint, templateHints)
            val amount = scored.maxWith(compareBy<Pair<Int, Long>> { it.first }.thenBy { it.second }).second
            val candidates = scored
                .sortedWith(compareByDescending<Pair<Int, Long>> { it.first }.thenByDescending { it.second })
                .map { it.second }
                .distinct()
                .take(6)
            RecognizedAmount(
                amount = amount,
                rawText = text.text,
                candidateCount = candidates.size,
                candidates = candidates,
                savedImagePath = saved.absolutePath
            )
        }
    }

    private fun copyToAppStorage(uri: Uri): File {
        val folder = File(context.filesDir, "receipts").apply { mkdirs() }
        val target = File(folder, "receipt_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        } ?: error("画像ファイルを開けませんでした")
        return target
    }

    private fun collectScoredAmounts(
        text: Text,
        cardNameHint: String?,
        templateHints: List<String>
    ): List<Pair<Int, Long>> {
        val scored = mutableListOf<Pair<Int, Long>>()
        text.textBlocks.forEach { block ->
            block.lines.forEach { line ->
                val numbers = parseAmounts(line.text)
                if (numbers.isNotEmpty()) {
                    val score = scoreLine(line.text, cardNameHint, templateHints)
                    numbers.forEach { amount -> scored += score to amount }
                }
            }
        }
        if (scored.isEmpty()) {
            error("金額候補を認識できませんでした")
        }
        return scored
    }

    private fun scoreLine(line: String, cardNameHint: String?, templateHints: List<String>): Int {
        var score = 0
        val normalized = normalizeForMatch(line)
        if (normalized.contains("請求") || normalized.contains("支払") || normalized.contains("金額")) score += 5
        if (normalized.contains("合計") || normalized.contains("総額") || normalized.contains("今回")) score += 4
        if (normalized.contains("利用額") || normalized.contains("確定")) score += 3
        if (normalized.contains("paypay") || normalized.contains("カード")) score += 1
        if (!cardNameHint.isNullOrBlank()) {
            val hint = normalizeForMatch(cardNameHint)
            if (hint.isNotBlank() && normalized.contains(hint)) score += 3
            score += brandHints(cardNameHint).count { normalized.contains(it) } * 2
        }
        val normalizedHints = templateHints.map { normalizeForMatch(it) }.filter { it.length >= 2 }.distinct()
        score += normalizedHints.count { normalized.contains(it) } * 2
        return score
    }

    private fun parseAmounts(text: String): List<Long> {
        val normalized = normalizeForParse(text)
        if (shouldSkipLine(normalized)) return emptyList()
        val regex = Regex("(?:[¥￥]\\s*)?([0-9]{1,3}(?:,[0-9]{3})+|[0-9]{4,})")
        return regex.findAll(normalized)
            .mapNotNull { match ->
                match.groupValues.getOrNull(1)
                    ?.replace(",", "")
                    ?.toLongOrNull()
                    ?.takeIf { it in 100L..9_999_999L }
            }
            .toList()
    }

    private fun shouldSkipLine(line: String): Boolean {
        val lowered = line.lowercase()
        if (listOf("tel", "電話", "会員番号", "カード番号", "問い合わせ", "お問い合わせ", "有効期限").any { lowered.contains(it) }) {
            return true
        }
        val hasDate = Regex("\\d{4}[/-]\\d{1,2}[/-]\\d{1,2}").containsMatchIn(lowered)
        val hasCurrencyWord = lowered.contains("円") || lowered.contains("¥") || lowered.contains("￥") ||
            lowered.contains("請求") || lowered.contains("支払") || lowered.contains("合計")
        if (hasDate && !hasCurrencyWord) return true
        val looksLikeCardNumber = Regex("\\d{4}[ -]\\d{4}[ -]\\d{4}[ -]\\d{4}").containsMatchIn(lowered)
        return looksLikeCardNumber && !hasCurrencyWord
    }

    private fun brandHints(cardNameHint: String): List<String> {
        val normalized = normalizeForMatch(cardNameHint)
        return when {
            normalized.contains("paypay") -> listOf("paypay", "ypj")
            normalized.contains("rakuten") || normalized.contains("楽天") -> listOf("rakuten", "楽天")
            normalized.contains("epos") -> listOf("epos", "エポス")
            normalized.contains("amazon") -> listOf("amazon")
            normalized.contains("mercard") || normalized.contains("メルカード") -> listOf("mercard", "メルカード", "merpay")
            normalized.contains("jcb") -> listOf("jcb")
            normalized.contains("visa") -> listOf("visa")
            normalized.contains("master") -> listOf("mastercard", "master")
            else -> emptyList()
        }
    }

    private fun normalizeForMatch(text: String): String =
        normalizeForParse(text).replace(" ", "").lowercase()

    private fun normalizeForParse(text: String): String {
        val sb = StringBuilder(text.length)
        text.forEach { ch ->
            val converted = when (ch) {
                in '０'..'９' -> ('0'.code + (ch.code - '０'.code)).toChar()
                '，' -> ','
                '．' -> '.'
                '￥' -> '¥'
                else -> ch
            }
            sb.append(converted)
        }
        return sb.toString()
    }
}

private suspend fun <T> Task<T>.awaitResult(): T = suspendCancellableCoroutine { cont ->
    addOnSuccessListener { cont.resume(it) }
    addOnFailureListener { cont.resumeWithException(it) }
}
