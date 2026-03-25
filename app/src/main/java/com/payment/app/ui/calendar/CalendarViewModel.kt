package com.payment.app.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payment.app.domain.usecase.GetMonthlyPaymentsUseCase
import com.payment.app.util.BillingDateInfo
import com.payment.app.util.asDisplayLabel
import com.payment.app.util.asStorageKey
import com.payment.app.util.calculateBillingDate
import com.payment.app.util.currentYearMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class CalendarPaymentItem(
    val cardName: String,
    val amount: Long,
    val requestedDate: LocalDate,
    val scheduledDate: LocalDate
)

data class CalendarUiState(
    val selectedMonth: YearMonth = currentYearMonth(),
    val selectedDate: LocalDate = currentYearMonth().atDay(1),
    val monthLabel: String = currentYearMonth().asDisplayLabel(),
    val daysInGrid: List<LocalDate?> = emptyList(),
    val paymentsByDate: Map<LocalDate, List<CalendarPaymentItem>> = emptyMap(),
    val selectedDateItems: List<CalendarPaymentItem> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getMonthlyPaymentsUseCase: GetMonthlyPaymentsUseCase
) : ViewModel() {

    private val selectedMonth = MutableStateFlow(currentYearMonth())
    private val selectedDate = MutableStateFlow(currentYearMonth().atDay(1))

    val uiState: StateFlow<CalendarUiState> = selectedMonth
        .flatMapLatest { month ->
            combine(
                getMonthlyPaymentsUseCase(month.asStorageKey()),
                selectedDate
            ) { cards, date ->
                val paymentsByDate = cards
                    .groupBy { calculateBillingDate(month, it.dueDate).scheduledDate }
                    .mapValues { (_, list) ->
                        list.map { card ->
                            val billing = calculateBillingDate(month, card.dueDate)
                            CalendarPaymentItem(
                                cardName = card.cardName,
                                amount = card.amount,
                                requestedDate = billing.requestedDate,
                                scheduledDate = billing.scheduledDate
                            )
                        }
                    }
                CalendarUiState(
                    selectedMonth = month,
                    selectedDate = date,
                    monthLabel = month.asDisplayLabel(),
                    daysInGrid = buildMonthGrid(month),
                    paymentsByDate = paymentsByDate,
                    selectedDateItems = paymentsByDate[date].orEmpty(),
                    isLoading = false
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalendarUiState())

    fun changeMonth(offset: Long) {
        selectedMonth.update { current ->
            val next = current.plusMonths(offset)
            selectedDate.value = next.atDay(1)
            next
        }
    }

    fun selectDate(date: LocalDate) {
        selectedDate.value = date
    }

    private fun buildMonthGrid(month: YearMonth): List<LocalDate?> {
        val firstDate = month.atDay(1)
        val firstDayOffset = (firstDate.dayOfWeek.value - DayOfWeek.MONDAY.value + 7) % 7
        val cells = mutableListOf<LocalDate?>()
        repeat(firstDayOffset) { cells += null }
        repeat(month.lengthOfMonth()) { day ->
            cells += month.atDay(day + 1)
        }
        while (cells.size % 7 != 0) {
            cells += null
        }
        return cells
    }
}
