package com.payment.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payment.app.data.model.CardWithPayment
import com.payment.app.data.repository.PaymentRepository
import com.payment.app.util.asDisplayLabel
import com.payment.app.util.asStorageKey
import com.payment.app.util.calculateBillingDate
import com.payment.app.util.currentYearMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class ScheduleGroup(
    val scheduledDate: LocalDate,
    val requestedDate: LocalDate,
    val subtotal: Long,
    val cardNames: List<String>,
    val adjustedByHoliday: Boolean
)

data class HomeUiState(
    val selectedYearMonth: String = currentYearMonth().asStorageKey(),
    val monthLabel: String = currentYearMonth().asDisplayLabel(),
    val cardsByDueDate: Map<Int, List<CardWithPayment>> = emptyMap(),
    val subtotalByDueDate: Map<Int, Long> = emptyMap(),
    val totalAmount: Long = 0L,
    val paidAmount: Long = 0L,
    val unpaidAmount: Long = 0L,
    val paidCount: Int = 0,
    val totalCount: Int = 0,
    val dueDates: List<Int> = emptyList(),
    val scheduleGroups: List<ScheduleGroup> = emptyList(),
    val accountTotals: Map<String, Long> = emptyMap(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PaymentRepository
) : ViewModel() {

    private val selectedMonth = MutableStateFlow(currentYearMonth())

    val uiState: StateFlow<HomeUiState> = selectedMonth
        .flatMapLatest { yearMonth ->
            repository.observeCardPayments(yearMonth.asStorageKey()).map { cards ->
                buildUiState(yearMonth, cards)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun changeMonth(offset: Long) {
        selectedMonth.update { it.plusMonths(offset) }
    }

    fun resetSelectedMonth() {
        viewModelScope.launch {
            repository.resetMonthAmounts(selectedMonth.value.asStorageKey())
        }
    }

    init {
        viewModelScope.launch {
            repository.initializeDefaultAccounts()
            repository.initializeDefaultCards()
        }
    }

    private fun buildUiState(yearMonth: YearMonth, cards: List<CardWithPayment>): HomeUiState {
        val grouped = cards.groupBy { it.dueDate }
        val subtotals = grouped.mapValues { (_, list) -> list.sumOf { it.amount } }
        val paidAmount = cards.filter { it.isPaid }.sumOf { it.amount }
        val scheduleGroups = cards
            .groupBy { calculateBillingDate(yearMonth, it.dueDate).scheduledDate }
            .entries
            .sortedBy { it.key }
            .map { (scheduledDate, scheduledCards) ->
                val earliestRequestedDate = scheduledCards
                    .map { calculateBillingDate(yearMonth, it.dueDate).requestedDate }
                    .minOrNull()
                    ?: scheduledDate
                ScheduleGroup(
                    scheduledDate = scheduledDate,
                    requestedDate = earliestRequestedDate,
                    subtotal = scheduledCards.sumOf { it.amount },
                    cardNames = scheduledCards.map { it.cardName },
                    adjustedByHoliday = scheduledCards.any {
                        val info = calculateBillingDate(yearMonth, it.dueDate)
                        info.requestedDate != info.scheduledDate
                    }
                )
            }
        val accountTotals = cards
            .groupBy { it.accountName ?: "未設定口座" }
            .mapValues { (_, list) -> list.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
            .toMap()

        return HomeUiState(
            selectedYearMonth = yearMonth.asStorageKey(),
            monthLabel = yearMonth.asDisplayLabel(),
            cardsByDueDate = grouped,
            subtotalByDueDate = subtotals,
            totalAmount = subtotals.values.sum(),
            paidAmount = paidAmount,
            unpaidAmount = subtotals.values.sum() - paidAmount,
            paidCount = cards.count { it.isPaid },
            totalCount = cards.size,
            dueDates = grouped.keys.sorted(),
            scheduleGroups = scheduleGroups,
            accountTotals = accountTotals,
            isLoading = false
        )
    }
}
