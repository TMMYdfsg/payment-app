package com.payment.app.ui.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payment.app.data.model.CardWithPayment
import com.payment.app.data.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InputFlowUiState(
    val cards: List<CardWithPayment> = emptyList(),
    val currentIndex: Int = 0,
    val inputText: String = "",
    val isCompleted: Boolean = false,
    val showSummary: Boolean = false,
    val isLoading: Boolean = true,
    val currentDueDateIndex: Int = 0,
    val allDueDates: List<Int> = emptyList(),
    val isSingleDueDate: Boolean = false,
    val showNextDueDatePrompt: Boolean = false
)

@HiltViewModel
class InputFlowViewModel @Inject constructor(
    private val repository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InputFlowUiState())
    val uiState: StateFlow<InputFlowUiState> = _uiState.asStateFlow()

    fun initializeForDueDate(dueDate: Int?) {
        viewModelScope.launch {
            val allCards = repository.getAllCardsOnce()
            val allPayments = repository.allPayments.first()
            val paymentMap = allPayments.associateBy { it.cardId }

            if (dueDate != null) {
                // Single due date mode
                val cards = allCards
                    .filter { it.dueDate == dueDate }
                    .map { card ->
                        CardWithPayment(
                            cardId = card.cardId,
                            cardName = card.cardName,
                            dueDate = card.dueDate,
                            amount = paymentMap[card.cardId]?.amount ?: 0L
                        )
                    }
                _uiState.update {
                    it.copy(
                        cards = cards,
                        currentIndex = 0,
                        inputText = cards.firstOrNull()?.amount?.takeIf { a -> a > 0 }?.toString() ?: "",
                        isCompleted = false,
                        showSummary = false,
                        isLoading = false,
                        isSingleDueDate = true,
                        allDueDates = listOf(dueDate),
                        currentDueDateIndex = 0
                    )
                }
            } else {
                // All cards mode
                val sortedDueDates = allCards.map { it.dueDate }.distinct().sorted()
                val cards = allCards
                    .sortedWith(compareBy({ it.dueDate }, { it.cardId }))
                    .map { card ->
                        CardWithPayment(
                            cardId = card.cardId,
                            cardName = card.cardName,
                            dueDate = card.dueDate,
                            amount = paymentMap[card.cardId]?.amount ?: 0L
                        )
                    }
                _uiState.update {
                    it.copy(
                        cards = cards,
                        currentIndex = 0,
                        inputText = cards.firstOrNull()?.amount?.takeIf { a -> a > 0 }?.toString() ?: "",
                        isCompleted = false,
                        showSummary = false,
                        isLoading = false,
                        isSingleDueDate = false,
                        allDueDates = sortedDueDates,
                        currentDueDateIndex = 0
                    )
                }
            }
        }
    }

    fun onInputChange(text: String) {
        if (text.all { it.isDigit() }) {
            _uiState.update { it.copy(inputText = text) }
        }
    }

    fun onConfirm() {
        val state = _uiState.value
        val amount = state.inputText.toLongOrNull() ?: 0L
        val currentCard = state.cards.getOrNull(state.currentIndex) ?: return

        viewModelScope.launch {
            repository.upsertPayment(currentCard.cardId, amount)

            val nextIndex = state.currentIndex + 1
            if (nextIndex >= state.cards.size) {
                // All done
                _uiState.update { it.copy(showSummary = true, isCompleted = true) }
            } else {
                val nextCard = state.cards[nextIndex]
                _uiState.update {
                    it.copy(
                        currentIndex = nextIndex,
                        inputText = nextCard.amount.takeIf { a -> a > 0 }?.toString() ?: ""
                    )
                }
            }
        }
    }

    fun onSkip() {
        val state = _uiState.value
        val nextIndex = state.currentIndex + 1
        if (nextIndex >= state.cards.size) {
            _uiState.update { it.copy(showSummary = true, isCompleted = true) }
        } else {
            val nextCard = state.cards[nextIndex]
            _uiState.update {
                it.copy(
                    currentIndex = nextIndex,
                    inputText = nextCard.amount.takeIf { a -> a > 0 }?.toString() ?: ""
                )
            }
        }
    }
}
