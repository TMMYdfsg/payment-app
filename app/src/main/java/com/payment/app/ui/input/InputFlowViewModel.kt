package com.payment.app.ui.input

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payment.app.data.db.entity.BankAccountEntity
import com.payment.app.data.model.CardWithPayment
import com.payment.app.domain.usecase.GetOcrProfilesJsonUseCase
import com.payment.app.domain.usecase.GetMonthlyPaymentsOnceUseCase
import com.payment.app.domain.usecase.RecognizeAmountFromImageUseCase
import com.payment.app.domain.usecase.SetOcrProfilesJsonUseCase
import com.payment.app.domain.usecase.UpdateCardCategoryUseCase
import com.payment.app.domain.usecase.UpdatePaymentAccountUseCase
import com.payment.app.domain.usecase.UpdatePaymentAmountUseCase
import com.payment.app.domain.usecase.UpdatePaymentPaidUseCase
import com.payment.app.data.repository.PaymentRepository
import com.payment.app.util.asStorageKey
import com.payment.app.util.currentYearMonth
import com.payment.app.util.parseYearMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

data class InputFlowUiState(
    val cards: List<CardWithPayment> = emptyList(),
    val accounts: List<BankAccountEntity> = emptyList(),
    val selectedYearMonth: String = currentYearMonth().asStorageKey(),
    val currentIndex: Int = 0,
    val inputText: String = "",
    val isCompleted: Boolean = false,
    val showSummary: Boolean = false,
    val isRecognizing: Boolean = false,
    val recognizedMessage: String? = null,
    val ocrCandidates: List<Long> = emptyList(),
    val showOcrCandidateDialog: Boolean = false,
    val ocrSavedImagePath: String? = null,
    val lastOcrRawText: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class InputFlowViewModel @Inject constructor(
    private val repository: PaymentRepository,
    private val getMonthlyPaymentsOnceUseCase: GetMonthlyPaymentsOnceUseCase,
    private val recognizeAmountFromImageUseCase: RecognizeAmountFromImageUseCase,
    private val getOcrProfilesJsonUseCase: GetOcrProfilesJsonUseCase,
    private val setOcrProfilesJsonUseCase: SetOcrProfilesJsonUseCase,
    private val updateCardCategoryUseCase: UpdateCardCategoryUseCase,
    private val updatePaymentAmountUseCase: UpdatePaymentAmountUseCase,
    private val updatePaymentPaidUseCase: UpdatePaymentPaidUseCase,
    private val updatePaymentAccountUseCase: UpdatePaymentAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InputFlowUiState())
    val uiState: StateFlow<InputFlowUiState> = _uiState.asStateFlow()

    fun initializeForDueDate(dueDate: Int?, yearMonthValue: String?) {
        viewModelScope.launch {
            repository.initializeDefaultAccounts()
            repository.initializeDefaultCards()
            val yearMonth = parseYearMonth(yearMonthValue).asStorageKey()
            val allCards = getMonthlyPaymentsOnceUseCase(yearMonth)
            val cards = if (dueDate != null) {
                allCards.filter { it.dueDate == dueDate }
            } else {
                allCards
            }
            _uiState.value = InputFlowUiState(
                cards = cards,
                accounts = repository.getAllAccountsOnce(),
                selectedYearMonth = yearMonth,
                currentIndex = 0,
                inputText = cards.firstOrNull()?.amount?.takeIf { it > 0 }?.toString().orEmpty(),
                isCompleted = cards.isEmpty(),
                showSummary = cards.isEmpty(),
                lastOcrRawText = "",
                isLoading = false
            )
        }
    }

    fun onInputChange(text: String) {
        if (text.all { it.isDigit() }) {
            _uiState.update { it.copy(inputText = text, recognizedMessage = null) }
        }
    }

    fun importAmountFromScreenshot(uri: Uri) {
        viewModelScope.launch {
            val baseState = _uiState.value
            val currentCard = baseState.cards.getOrNull(baseState.currentIndex)
            val profiles = loadOcrProfiles()
            val cardProfile = currentCard?.let { findCardProfile(it.cardName, profiles) }

            _uiState.update {
                it.copy(
                    isRecognizing = true,
                    recognizedMessage = "OCRで金額を解析中...",
                    ocrCandidates = emptyList(),
                    showOcrCandidateDialog = false,
                    ocrSavedImagePath = null,
                    lastOcrRawText = ""
                )
            }
            val result = recognizeAmountFromImageUseCase(
                uri = uri,
                cardNameHint = currentCard?.cardName,
                templateHints = cardProfile?.keywords.orEmpty()
            )

            result.fold(
                onSuccess = { recognized ->
                    val candidates = if (recognized.candidates.isEmpty()) {
                        listOf(recognized.amount)
                    } else {
                        recognized.candidates
                    }

                    var updatedCard = currentCard
                    val autoMessages = mutableListOf<String>()
                    if (currentCard != null) {
                        val autoProfile = resolveAutoClassificationProfile(
                            rawText = recognized.rawText,
                            currentCardName = currentCard.cardName,
                            profiles = profiles,
                            currentProfile = cardProfile
                        )
                        if (autoProfile?.accountId != null && autoProfile.accountId != currentCard.accountId) {
                            val matchedAccount = baseState.accounts.firstOrNull { it.accountId == autoProfile.accountId }
                            if (matchedAccount != null) {
                                updatePaymentAccountUseCase(
                                    currentCard.cardId,
                                    baseState.selectedYearMonth,
                                    matchedAccount.accountId
                                )
                                updatedCard = (updatedCard ?: currentCard).copy(
                                    accountId = matchedAccount.accountId,
                                    accountName = matchedAccount.accountName
                                )
                                autoMessages += "自動仕分け: 口座を「${matchedAccount.accountName}」に設定"
                            }
                        }

                        val targetCategory = autoProfile?.category?.trim().orEmpty()
                        if (targetCategory.isNotBlank() && targetCategory != currentCard.category) {
                            updateCardCategoryUseCase(currentCard.cardId, targetCategory)
                            updatedCard = (updatedCard ?: currentCard).copy(category = targetCategory)
                            autoMessages += "自動仕分け: カテゴリを「$targetCategory」に設定"
                        }
                    }

                    _uiState.update { state ->
                        val nextCards = state.cards.toMutableList()
                        if (updatedCard != null && state.currentIndex in nextCards.indices) {
                            nextCards[state.currentIndex] = updatedCard
                        }
                        val baseMessage = buildString {
                            append(if (candidates.size <= 1) {
                                "OCR完了: ${recognized.amount}円 を自動入力しました"
                            } else {
                                "OCR候補を選択してください"
                            })
                            if (autoMessages.isNotEmpty()) {
                                append("\n")
                                append(autoMessages.joinToString("\n"))
                            }
                            if (!recognized.savedImagePath.isNullOrBlank()) {
                                append("\nスクショ保存: ${recognized.savedImagePath}")
                            }
                        }
                        state.copy(
                            cards = nextCards,
                            inputText = recognized.amount.toString(),
                            isRecognizing = false,
                            recognizedMessage = baseMessage,
                            ocrCandidates = candidates,
                            showOcrCandidateDialog = candidates.size > 1,
                            ocrSavedImagePath = recognized.savedImagePath,
                            lastOcrRawText = recognized.rawText
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isRecognizing = false,
                            recognizedMessage = "OCR失敗: ${error.message ?: "金額を認識できませんでした"}",
                            ocrCandidates = emptyList(),
                            showOcrCandidateDialog = false,
                            ocrSavedImagePath = null,
                            lastOcrRawText = ""
                        )
                    }
                }
            )
        }
    }

    fun applyVoiceInput(recognizedText: String) {
        val amount = parseAmountFromVoice(recognizedText)
        if (amount == null) {
            _uiState.update {
                it.copy(recognizedMessage = "音声から金額を抽出できませんでした。数字を含めて話してください。")
            }
            return
        }
        _uiState.update {
            it.copy(
                inputText = amount.toString(),
                recognizedMessage = "音声入力: ${amount}円 を反映しました"
            )
        }
    }

    fun selectOcrCandidate(amount: Long) {
        _uiState.update { state ->
            state.copy(
                inputText = amount.toString(),
                showOcrCandidateDialog = false,
                recognizedMessage = buildString {
                    append("OCR候補を適用: ${amount}円")
                    if (!state.ocrSavedImagePath.isNullOrBlank()) {
                        append("\nスクショ保存: ${state.ocrSavedImagePath}")
                    }
                }
            )
        }
    }

    fun dismissOcrCandidateDialog() {
        _uiState.update { it.copy(showOcrCandidateDialog = false) }
    }

    fun onConfirm() {
        val state = _uiState.value
        val currentCard = state.cards.getOrNull(state.currentIndex) ?: return
        val amount = state.inputText.toLongOrNull() ?: 0L
        val resolvedAccount = state.accounts.firstOrNull { it.accountId == currentCard.accountId }
            ?: state.accounts.firstOrNull()
        val updatedCard = currentCard.copy(
            amount = amount,
            isPaid = if (amount == 0L) false else currentCard.isPaid,
            accountId = currentCard.accountId ?: resolvedAccount?.accountId,
            accountName = currentCard.accountName ?: resolvedAccount?.accountName,
            completedAt = if (amount == 0L) null else currentCard.completedAt
        )

        viewModelScope.launch {
            updatePaymentAmountUseCase(currentCard.cardId, state.selectedYearMonth, amount)
            applyCardUpdate(updatedCard)
            learnCurrentOcrProfile(state, updatedCard)
            moveNextOrShowSummary()
        }
    }

    fun onSkip() {
        moveNextOrShowSummary()
    }

    fun selectCurrentAccount(accountId: Long?) {
        val state = _uiState.value
        val currentCard = state.cards.getOrNull(state.currentIndex) ?: return
        val account = state.accounts.firstOrNull { it.accountId == accountId }
        viewModelScope.launch {
            updatePaymentAccountUseCase(currentCard.cardId, state.selectedYearMonth, accountId)
            applyCardUpdate(
                currentCard.copy(
                    accountId = account?.accountId,
                    accountName = account?.accountName
                )
            )
        }
    }

    fun toggleCurrentPaid() {
        val state = _uiState.value
        val currentCard = state.cards.getOrNull(state.currentIndex) ?: return
        val nextPaid = !currentCard.isPaid
        viewModelScope.launch {
            updatePaymentPaidUseCase(currentCard.cardId, state.selectedYearMonth, nextPaid)
            applyCardUpdate(
                currentCard.copy(
                    isPaid = nextPaid,
                    completedAt = if (nextPaid) System.currentTimeMillis() else null
                )
            )
        }
    }

    fun clearRecognizedMessage() {
        _uiState.update { it.copy(recognizedMessage = null) }
    }

    private suspend fun learnCurrentOcrProfile(state: InputFlowUiState, currentCard: CardWithPayment) {
        val rawText = state.lastOcrRawText.trim()
        if (rawText.isBlank()) return

        val profiles = loadOcrProfiles().toMutableList()
        val key = normalizeCardKey(currentCard.cardName)
        val extractedKeywords = extractKeywords(rawText)
        val existing = profiles.firstOrNull { it.cardKey == key }
        val merged = if (existing == null) {
            OcrCardProfile(
                cardKey = key,
                cardName = currentCard.cardName,
                accountId = currentCard.accountId,
                category = currentCard.category.takeIf { it.isNotBlank() },
                keywords = extractedKeywords,
                updatedAt = System.currentTimeMillis()
            )
        } else {
            existing.copy(
                cardName = currentCard.cardName,
                accountId = currentCard.accountId ?: existing.accountId,
                category = currentCard.category.takeIf { it.isNotBlank() } ?: existing.category,
                keywords = (extractedKeywords + existing.keywords)
                    .map { it.trim() }
                    .filter { it.length >= 2 }
                    .distinct()
                    .take(MAX_PROFILE_KEYWORDS),
                updatedAt = System.currentTimeMillis()
            )
        }
        val nextProfiles = profiles
            .filterNot { it.cardKey == key }
            .plus(merged)
            .sortedByDescending { it.updatedAt }
            .take(MAX_PROFILE_COUNT)
        saveOcrProfiles(nextProfiles)
    }

    private fun applyCardUpdate(updatedCard: CardWithPayment) {
        _uiState.update { state ->
            val nextCards = state.cards.toMutableList()
            if (state.currentIndex in nextCards.indices) {
                nextCards[state.currentIndex] = updatedCard
            }
            state.copy(cards = nextCards)
        }
    }

    private fun moveNextOrShowSummary() {
        _uiState.update { state ->
            val nextIndex = state.currentIndex + 1
            if (nextIndex >= state.cards.size) {
                state.copy(showSummary = true, isCompleted = true, lastOcrRawText = "")
            } else {
                val nextCard = state.cards[nextIndex]
                state.copy(
                    currentIndex = nextIndex,
                    inputText = nextCard.amount.takeIf { it > 0 }?.toString().orEmpty(),
                    lastOcrRawText = ""
                )
            }
        }
    }

    private suspend fun loadOcrProfiles(): List<OcrCardProfile> {
        val raw = getOcrProfilesJsonUseCase().trim()
        if (raw.isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val item = array.optJSONObject(i) ?: continue
                    val cardKey = item.optString("cardKey").ifBlank {
                        normalizeCardKey(item.optString("cardName"))
                    }
                    val keywords = item.optJSONArray("keywords")
                        ?.let { keywordsArray ->
                            buildList {
                                for (k in 0 until keywordsArray.length()) {
                                    val keyword = keywordsArray.optString(k).trim().lowercase(Locale.getDefault())
                                    if (keyword.length >= 2) add(keyword)
                                }
                            }
                        }
                        .orEmpty()
                        .distinct()
                        .take(MAX_PROFILE_KEYWORDS)
                    add(
                        OcrCardProfile(
                            cardKey = cardKey,
                            cardName = item.optString("cardName"),
                            accountId = item.optLong("accountId").takeIf { it > 0L },
                            category = item.optString("category").takeIf { it.isNotBlank() },
                            keywords = keywords,
                            updatedAt = item.optLong("updatedAt", 0L)
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private suspend fun saveOcrProfiles(profiles: List<OcrCardProfile>) {
        val array = JSONArray().apply {
            profiles.forEach { profile ->
                put(
                    JSONObject().apply {
                        put("cardKey", profile.cardKey)
                        put("cardName", profile.cardName)
                        put("accountId", profile.accountId ?: JSONObject.NULL)
                        put("category", profile.category ?: JSONObject.NULL)
                        put("updatedAt", profile.updatedAt)
                        put(
                            "keywords",
                            JSONArray().apply {
                                profile.keywords.forEach { put(it) }
                            }
                        )
                    }
                )
            }
        }
        setOcrProfilesJsonUseCase(array.toString())
    }

    private fun findCardProfile(cardName: String, profiles: List<OcrCardProfile>): OcrCardProfile? {
        val key = normalizeCardKey(cardName)
        return profiles.firstOrNull { it.cardKey == key }
    }

    private fun resolveAutoClassificationProfile(
        rawText: String,
        currentCardName: String,
        profiles: List<OcrCardProfile>,
        currentProfile: OcrCardProfile?
    ): OcrCardProfile? {
        if (currentProfile != null && (currentProfile.accountId != null || !currentProfile.category.isNullOrBlank())) {
            return currentProfile
        }

        val normalizedText = normalizeText(rawText)
        return profiles
            .asSequence()
            .filter { it.cardKey != normalizeCardKey(currentCardName) }
            .filter { it.accountId != null || !it.category.isNullOrBlank() }
            .map { profile ->
                val score = profile.keywords.count { normalizedText.contains(it) }
                profile to score
            }
            .filter { (_, score) -> score >= 2 }
            .sortedByDescending { (_, score) -> score }
            .firstOrNull()
            ?.first
    }

    private fun extractKeywords(rawText: String): List<String> {
        val words = Regex("[A-Za-zぁ-んァ-ヶ一-龠]{2,}")
            .findAll(normalizeText(rawText))
            .map { it.value.lowercase(Locale.getDefault()) }
            .filter { token -> token !in OCR_STOP_WORDS }
            .toList()
        if (words.isEmpty()) return emptyList()
        return words
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .map { it.key }
            .take(MAX_PROFILE_KEYWORDS)
    }

    private fun parseAmountFromVoice(text: String): Long? {
        val normalized = normalizeText(text)
            .replace("，", ",")
            .replace("．", ".")
        val candidates = Regex("([0-9]{1,3}(?:,[0-9]{3})+|[0-9]+)")
            .findAll(normalized)
            .mapNotNull { match ->
                match.groupValues
                    .getOrNull(1)
                    ?.replace(",", "")
                    ?.toLongOrNull()
                    ?.takeIf { it in 0L..9_999_999L }
            }
            .toList()
        return candidates.maxOrNull()
    }

    private fun normalizeText(input: String): String {
        val builder = StringBuilder(input.length)
        input.forEach { ch ->
            val converted = when (ch) {
                in '０'..'９' -> ('0'.code + (ch.code - '０'.code)).toChar()
                else -> ch
            }
            builder.append(converted)
        }
        return builder.toString().lowercase(Locale.getDefault())
    }

    private fun normalizeCardKey(cardName: String): String =
        cardName.trim().lowercase(Locale.getDefault()).replace(Regex("\\s+"), "")

    private data class OcrCardProfile(
        val cardKey: String,
        val cardName: String,
        val accountId: Long?,
        val category: String?,
        val keywords: List<String>,
        val updatedAt: Long
    )

    private companion object {
        const val MAX_PROFILE_COUNT = 30
        const val MAX_PROFILE_KEYWORDS = 12
        val OCR_STOP_WORDS = setOf(
            "請求",
            "支払",
            "支払い",
            "合計",
            "金額",
            "今回",
            "利用",
            "利用額",
            "カード",
            "円",
            "税込",
            "税抜",
            "明細",
            "日付",
            "年",
            "月"
        )
    }
}
