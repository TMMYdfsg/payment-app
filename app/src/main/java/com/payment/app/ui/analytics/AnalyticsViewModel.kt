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
import java.time.LocalDate
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
    val forecastMonthEndTotal: Long = 0L,
    val forecastDelta: Long = 0L,
    val anomalyCards: List<Pair<String, Long>> = emptyList(),
    val optimizationSuggestions: List<String> = emptyList(),
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
            val forecastTotal = calculateForecast(month, total)
            val anomalyCards = detectAnomalyCards(month, cards)
            val optimizationSuggestions = buildOptimizationSuggestions(
                total = total,
                budget = budget,
                usagePercent = usagePercent,
                forecastTotal = forecastTotal,
                cardBreakdown = cardBreakdown,
                categoryBreakdown = categoryBreakdown,
                anomalyCards = anomalyCards,
                monthlyTotals = monthlyTotals
            )

            _uiState.value = AnalyticsUiState(
                selectedYearMonth = monthKey,
                monthLabel = month.asDisplayLabel(),
                monthlyTotals = monthlyTotals,
                cardBreakdown = cardBreakdown,
                categoryBreakdown = categoryBreakdown,
                budgetAmount = budget,
                budgetUsagePercent = usagePercent,
                forecastMonthEndTotal = forecastTotal,
                forecastDelta = forecastTotal - total,
                anomalyCards = anomalyCards,
                optimizationSuggestions = optimizationSuggestions,
                isLoading = false
            )
        }
    }

    private fun calculateForecast(month: YearMonth, currentTotal: Long): Long {
        val currentMonth = currentYearMonth()
        if (month != currentMonth) return currentTotal
        val today = LocalDate.now().dayOfMonth.coerceAtLeast(1)
        val length = month.lengthOfMonth().coerceAtLeast(today)
        val dailyAverage = currentTotal.toDouble() / today.toDouble()
        return (dailyAverage * length.toDouble()).toLong()
    }

    private suspend fun detectAnomalyCards(month: YearMonth, currentCards: List<com.payment.app.data.model.CardWithPayment>): List<Pair<String, Long>> {
        val currentByCard = currentCards.groupBy { it.cardName }.mapValues { it.value.sumOf { card -> card.amount } }
        val history = (1..3).map { month.minusMonths(it.toLong()) }
        val historyByCard = mutableMapOf<String, MutableList<Long>>()
        history.forEach { ym ->
            val monthlyCards = getMonthlyPaymentsOnceUseCase(ym.asStorageKey())
            monthlyCards
                .groupBy { it.cardName }
                .mapValues { it.value.sumOf { card -> card.amount } }
                .forEach { (name, amount) ->
                    historyByCard.getOrPut(name) { mutableListOf() }.add(amount)
                }
        }
        return currentByCard.mapNotNull { (cardName, currentAmount) ->
            val samples = historyByCard[cardName].orEmpty().filter { it > 0L }
            if (samples.isEmpty() || currentAmount <= 0L) return@mapNotNull null
            val avg = samples.average()
            val isSpike = currentAmount >= (avg * 1.8).toLong() && (currentAmount - avg.toLong()) >= 5_000L
            if (isSpike) cardName to currentAmount else null
        }.sortedByDescending { it.second }
    }

    private fun buildOptimizationSuggestions(
        total: Long,
        budget: Long?,
        usagePercent: Int,
        forecastTotal: Long,
        cardBreakdown: List<Pair<String, Long>>,
        categoryBreakdown: List<Pair<String, Long>>,
        anomalyCards: List<Pair<String, Long>>,
        monthlyTotals: List<Pair<String, Long>>
    ): List<String> {
        val suggestions = mutableListOf<String>()

        if (budget != null && budget > 0L) {
            when {
                forecastTotal > budget -> {
                    val over = forecastTotal - budget
                    suggestions += "月末予測が予算を${over}円超過しています。可変費の上限を先に決めると抑制しやすくなります。"
                }
                usagePercent >= 85 -> {
                    suggestions += "予算消化率が${usagePercent}%です。残り期間は固定費以外の購入を優先度順に整理してください。"
                }
            }
        }

        val topCategory = categoryBreakdown.firstOrNull()
        if (topCategory != null && total > 0L) {
            val ratio = ((topCategory.second * 100) / total).toInt()
            if (ratio >= 35) {
                suggestions += "カテゴリ「${topCategory.first}」が支出の${ratio}%を占めています。ここを1割削減できると効果が大きいです。"
            }
        }

        val topCard = cardBreakdown.firstOrNull()
        if (topCard != null && total > 0L) {
            val ratio = ((topCard.second * 100) / total).toInt()
            if (ratio >= 40) {
                suggestions += "カード「${topCard.first}」への集中度が${ratio}%です。引落日分散で資金繰りリスクを下げられます。"
            }
        }

        if (anomalyCards.isNotEmpty()) {
            suggestions += "急増検知カードがあります。単発支出か継続支出かを確認して、継続なら予算再配分を検討してください。"
        }

        if (monthlyTotals.size >= 6) {
            val recent3Avg = monthlyTotals.takeLast(3).map { it.second }.average()
            val previous3Avg = monthlyTotals.dropLast(3).takeLast(3).map { it.second }.average()
            if (previous3Avg > 0.0 && recent3Avg >= previous3Avg * 1.15) {
                suggestions += "直近3ヶ月平均がその前の3ヶ月より15%以上増加しています。固定費化した項目を優先して見直してください。"
            }
        }

        if (suggestions.isEmpty()) {
            suggestions += "支出推移は安定しています。今月は高頻度カテゴリの単価見直しで小さく最適化するのが効果的です。"
        }

        return suggestions.take(4)
    }
}
