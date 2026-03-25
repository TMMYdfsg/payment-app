package com.payment.app.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payment.app.domain.usecase.GetBudgetUseCase
import com.payment.app.domain.usecase.GetMonthlyPaymentsOnceUseCase
import com.payment.app.util.asDisplayLabel
import com.payment.app.util.asStorageKey
import com.payment.app.util.currentYearMonth
import com.payment.app.util.parseYearMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AnalyticsUiState(
    val selectedYearMonth: String = currentYearMonth().asStorageKey(),
    val monthLabel: String = currentYearMonth().asDisplayLabel(),
    val monthlyTotals: List<Pair<String, Long>> = emptyList(),
    val cardBreakdown: List<Pair<String, Long>> = emptyList(),
    val categoryBreakdown: List<Pair<String, Long>> = emptyList(),
    val budgetAmount: Long? = null,
    val budgetUsagePercent: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getMonthlyPaymentsOnceUseCase: GetMonthlyPaymentsOnceUseCase,
    private val getBudgetUseCase: GetBudgetUseCase
) : ViewModel() {

    private val selectedMonth = MutableStateFlow(currentYearMonth())
    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun changeMonth(offset: Long) {
        selectedMonth.value = selectedMonth.value.plusMonths(offset)
        refresh()
    }

    fun setMonth(value: String?) {
        selectedMonth.value = parseYearMonth(value)
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val month = selectedMonth.value
            val monthKey = month.asStorageKey()
            val cards = getMonthlyPaymentsOnceUseCase(monthKey)
            val budget = getBudgetUseCase(monthKey, null).first()?.amount

            val monthlyTotals = (11 downTo 0).map { back ->
                val ym = month.minusMonths(back.toLong())
                val sum = getMonthlyPaymentsOnceUseCase(ym.asStorageKey()).sumOf { it.amount }
                ym.asStorageKey() to sum
            }
            val cardBreakdown = cards
                .groupBy { it.cardName }
                .mapValues { (_, list) -> list.sumOf { it.amount } }
                .toList()
                .sortedByDescending { it.second }
            val categoryBreakdown = cards
                .groupBy { it.category.ifBlank { "未分類" } }
                .mapValues { (_, list) -> list.sumOf { it.amount } }
                .toList()
                .sortedByDescending { it.second }
            val total = cards.sumOf { it.amount }
            val usagePercent = if (budget != null && budget > 0L) ((total * 100) / budget).toInt() else 0

            _uiState.value = AnalyticsUiState(
                selectedYearMonth = monthKey,
                monthLabel = month.asDisplayLabel(),
                monthlyTotals = monthlyTotals,
                cardBreakdown = cardBreakdown,
                categoryBreakdown = categoryBreakdown,
                budgetAmount = budget,
                budgetUsagePercent = usagePercent,
                isLoading = false
            )
        }
    }
}
