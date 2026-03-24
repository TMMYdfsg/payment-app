package com.payment.app.ui.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payment.app.data.db.entity.CardEntity
import com.payment.app.data.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CardManageUiState(
    val cardsByDueDate: Map<Int, List<CardEntity>> = emptyMap(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CardManageViewModel @Inject constructor(
    private val repository: PaymentRepository
) : ViewModel() {

    val uiState: StateFlow<CardManageUiState> = repository.allCards
        .map { cards ->
            CardManageUiState(
                cardsByDueDate = cards.groupBy { it.dueDate },
                isLoading = false
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CardManageUiState())

    fun addCard(cardName: String, dueDate: Int, category: String) {
        if (cardName.isBlank() || dueDate !in 1..31) return
        viewModelScope.launch {
            repository.addCard(cardName.trim(), dueDate, category.trim())
        }
    }

    fun deleteCard(card: CardEntity) {
        viewModelScope.launch {
            repository.deleteCard(card)
        }
    }
}
