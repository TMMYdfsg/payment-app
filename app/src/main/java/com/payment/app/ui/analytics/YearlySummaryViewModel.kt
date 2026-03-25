package com.payment.app.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payment.app.domain.usecase.GetMonthlyPaymentsOnceUseCase
import com.payment.app.util.asDisplayLabel
import com.payment.app.util.asStorageKey
import com.payment.app.util.currentYearMonth
import com.payment.app.util.parseYearMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class YearMonthSummary(
    val yearMonth: String,
    val label: String,
    val totalAmount: Long,
    val paidAmount: Long,
    val unpaidCount: Int,
    val totalCount: Int
)

data class YearlySummaryUiState(
    val baseYearMonth: String = currentYearMonth().asStorageKey(),
    val baseMonthLabel: String = currentYearMonth().asDisplayLabel(),
    val monthly: List<YearMonthSummary> = emptyList(),
    val yearlyTotal: Long = 0L,
    val yearlyPaid: Long = 0L,
    val isLoading: Boolean = true
)

@HiltViewModel
class YearlySummaryViewModel @Inject constructor(
    private val getMonthlyPaymentsOnceUseCase: GetMonthlyPaymentsOnceUseCase
) : ViewModel() {
    private val baseMonth = MutableStateFlow(currentYearMonth())
    private val _uiState = MutableStateFlow(YearlySummaryUiState())
    val uiState: StateFlow<YearlySummaryUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun setBaseMonth(yearMonth: String?) {
        baseMonth.value = parseYearMonth(yearMonth)
        refresh()
    }

    fun changeBaseMonth(offset: Long) {
        baseMonth.value = baseMonth.value.plusMonths(offset)
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val month = baseMonth.value
            val items = (11 downTo 0).map { back ->
                val target = month.minusMonths(back.toLong())
                val cards = getMonthlyPaymentsOnceUseCase(target.asStorageKey())
                val total = cards.sumOf { it.amount }
                val paid = cards.filter { it.isPaid }.sumOf { it.amount }
                YearMonthSummary(
                    yearMonth = target.asStorageKey(),
                    label = target.asDisplayLabel(),
                    totalAmount = total,
                    paidAmount = paid,
                    unpaidCount = cards.count { !it.isPaid },
                    totalCount = cards.size
                )
            }
            _uiState.value = YearlySummaryUiState(
                baseYearMonth = month.asStorageKey(),
                baseMonthLabel = month.asDisplayLabel(),
                monthly = items,
                yearlyTotal = items.sumOf { it.totalAmount },
                yearlyPaid = items.sumOf { it.paidAmount },
                isLoading = false
            )
        }
    }
}

