package com.payment.app.ui.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payment.app.data.db.entity.BankAccountEntity
import com.payment.app.data.model.CardWithPayment
import com.payment.app.data.repository.PaymentRepository
import com.payment.app.util.asStorageKey
import com.payment.app.util.currentYearMonth
import com.payment.app.util.parseYearMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InputFlowUiState(
    val cards: List<CardWithPayment> = emptyList(),
    val accounts: List<BankAccountEntity> = emptyList(),
    val selectedYearMonth: String = currentYearMonth().asStorageKey(),
    val currentIndex: Int = 0,
    val inputText: String = "",
    val isCompleted: Boolean = false,
    val showSummary: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class InputFlowViewModel @Inject constructor(
    private val repository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InputFlowUiState())
    val uiState: StateFlow<InputFlowUiState> = _uiState.asStateFlow()

    fun initializeForDueDate(dueDate: Int?, yearMonthValue: String?) {
        viewModelScope.launch {
            repository.initializeDefaultAccounts()
            repository.initializeDefaultCards()
            val yearMonth = parseYearMonth(yearMonthValue).asStorageKey()
            val allCards = repository.getCardPaymentsOnce(yearMonth)
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
                isLoading = false
            )
        }
    }

    fun onInputChange(text: String) {
        if (text.all { it.isDigit() }) {
            _uiState.update { it.copy(inputText = text) }
        }
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
            repository.upsertPayment(currentCard.cardId, state.selectedYearMonth, amount)
            applyCardUpdate(updatedCard)
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
            repository.updatePaymentAccount(currentCard.cardId, state.selectedYearMonth, accountId)
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
            repository.updatePaymentPaid(currentCard.cardId, state.selectedYearMonth, nextPaid)
            applyCardUpdate(
                currentCard.copy(
                    isPaid = nextPaid,
                    completedAt = if (nextPaid) System.currentTimeMillis() else null
                )
            )
        }
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
                state.copy(showSummary = true, isCompleted = true)
            } else {
                val nextCard = state.cards[nextIndex]
                state.copy(
                    currentIndex = nextIndex,
                    inputText = nextCard.amount.takeIf { it > 0 }?.toString().orEmpty()
                )
            }
        }
    }
}
