package com.payment.app.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payment.app.data.db.entity.BankAccountEntity
import com.payment.app.data.model.CardWithPayment
import com.payment.app.data.model.PaymentHistoryItem
import com.payment.app.domain.usecase.GetMonthlyPaymentsUseCase
import com.payment.app.domain.usecase.DeletePaymentUseCase
import com.payment.app.domain.usecase.UpdatePaymentAccountUseCase
import com.payment.app.domain.usecase.UpdatePaymentAmountUseCase
import com.payment.app.domain.usecase.UpdatePaymentPaidUseCase
import com.payment.app.data.repository.PaymentRepository
import com.payment.app.util.asDisplayLabel
import com.payment.app.util.asStorageKey
import com.payment.app.util.currentYearMonth
import com.payment.app.util.parseYearMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListUiState(
    val selectedYearMonth: String = currentYearMonth().asStorageKey(),
    val monthLabel: String = currentYearMonth().asDisplayLabel(),
    val accounts: List<BankAccountEntity> = emptyList(),
    val cardsByDueDate: Map<Int, List<CardWithPayment>> = emptyMap(),
    val subtotalByDueDate: Map<Int, Long> = emptyMap(),
    val totalAmount: Long = 0L,
    val paidAmount: Long = 0L,
    val unpaidAmount: Long = 0L,
    val searchQuery: String = "",
    val historyResults: List<PaymentHistoryItem> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ListViewModel @Inject constructor(
    getMonthlyPaymentsUseCase: GetMonthlyPaymentsUseCase,
    private val deletePaymentUseCase: DeletePaymentUseCase,
    private val updatePaymentAmountUseCase: UpdatePaymentAmountUseCase,
    private val updatePaymentPaidUseCase: UpdatePaymentPaidUseCase,
    private val updatePaymentAccountUseCase: UpdatePaymentAccountUseCase,
    private val repository: PaymentRepository
) : ViewModel() {

    private val selectedMonth = MutableStateFlow(currentYearMonth())
    private val searchQuery = MutableStateFlow("")

    val uiState: StateFlow<ListUiState> = selectedMonth
        .flatMapLatest { yearMonth ->
            combine(
                getMonthlyPaymentsUseCase(yearMonth.asStorageKey()),
                repository.allAccounts,
                searchQuery
            ) { cards, accounts, query ->
                val grouped = cards.groupBy { it.dueDate }
                val subtotals = grouped.mapValues { (_, list) -> list.sumOf { it.amount } }
                val paidAmount = cards.filter { it.isPaid }.sumOf { it.amount }
                val results = repository.searchPaymentHistory(query)
                ListUiState(
                    selectedYearMonth = yearMonth.asStorageKey(),
                    monthLabel = yearMonth.asDisplayLabel(),
                    accounts = accounts,
                    cardsByDueDate = grouped,
                    subtotalByDueDate = subtotals,
                    totalAmount = subtotals.values.sum(),
                    paidAmount = paidAmount,
                    unpaidAmount = subtotals.values.sum() - paidAmount,
                    searchQuery = query,
                    historyResults = results,
                    isLoading = false
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListUiState())

    init {
        viewModelScope.launch {
            repository.initializeDefaultAccounts()
            repository.initializeDefaultCards()
        }
    }

    fun setYearMonth(value: String?) {
        selectedMonth.value = parseYearMonth(value)
    }

    fun changeMonth(offset: Long) {
        selectedMonth.update { it.plusMonths(offset) }
    }

    fun setSearchQuery(value: String) {
        searchQuery.value = value
    }

    fun updateAmount(cardId: Long, amount: Long) {
        viewModelScope.launch {
            updatePaymentAmountUseCase(cardId, selectedMonth.value.asStorageKey(), amount)
        }
    }

    fun updatePaid(cardId: Long, isPaid: Boolean) {
        viewModelScope.launch {
            updatePaymentPaidUseCase(cardId, selectedMonth.value.asStorageKey(), isPaid)
        }
    }

    fun updateAccount(cardId: Long, accountId: Long?) {
        viewModelScope.launch {
            updatePaymentAccountUseCase(cardId, selectedMonth.value.asStorageKey(), accountId)
        }
    }

    fun deletePayment(cardId: Long) {
        viewModelScope.launch {
            deletePaymentUseCase(cardId, selectedMonth.value.asStorageKey())
        }
    }
}
