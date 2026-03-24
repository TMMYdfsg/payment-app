package com.payment.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payment.app.data.model.CardWithPayment
import com.payment.app.data.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val cardsByDueDate: Map<Int, List<CardWithPayment>> = emptyMap(),
    val subtotalByDueDate: Map<Int, Long> = emptyMap(),
    val totalAmount: Long = 0L,
    val dueDates: List<Int> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PaymentRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.allCards,
        repository.allPayments
    ) { cards, payments ->
        val paymentMap = payments.associateBy { it.cardId }
        val cardWithPayments = cards.map { card ->
            CardWithPayment(
                cardId = card.cardId,
                cardName = card.cardName,
                dueDate = card.dueDate,
                amount = paymentMap[card.cardId]?.amount ?: 0L
            )
        }
        val grouped = cardWithPayments.groupBy { it.dueDate }
        val subtotals = grouped.mapValues { (_, list) -> list.sumOf { it.amount } }
        HomeUiState(
            cardsByDueDate = grouped,
            subtotalByDueDate = subtotals,
            totalAmount = subtotals.values.sum(),
            dueDates = grouped.keys.sorted(),
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun resetAll() {
        viewModelScope.launch {
            repository.resetAllAmounts()
        }
    }

    init {
        viewModelScope.launch {
            repository.initializeDefaultCards()
        }
    }
}
