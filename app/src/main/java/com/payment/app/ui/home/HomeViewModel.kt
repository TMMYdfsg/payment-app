package com.payment.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.net.Uri
import com.payment.app.data.db.entity.BudgetEntity
import com.payment.app.data.model.CardWithPayment
import com.payment.app.domain.usecase.ApplyPreviousMonthTemplateUseCase
import com.payment.app.domain.usecase.ExportPaymentsUseCase
import com.payment.app.domain.usecase.ExportBackupJsonUseCase
import com.payment.app.domain.usecase.GetBudgetUseCase
import com.payment.app.domain.usecase.GetMonthlyPaymentsOnceUseCase
import com.payment.app.domain.usecase.GetMonthlyPaymentsUseCase
import com.payment.app.domain.usecase.MarkAllPaidUseCase
import com.payment.app.domain.usecase.ResetMonthAmountsUseCase
import com.payment.app.domain.usecase.UploadBackupToDriveUseCase
import com.payment.app.domain.usecase.UpdateBudgetUseCase
import com.payment.app.notifications.ReminderScheduler
import com.payment.app.data.repository.PaymentRepository
import com.payment.app.util.asDisplayLabel
import com.payment.app.util.asStorageKey
import com.payment.app.util.calculateBillingDate
import com.payment.app.util.currentYearMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.io.File
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
    val budgetAmount: Long? = null,
    val budgetRemaining: Long? = null,
    val paidAmount: Long = 0L,
    val unpaidAmount: Long = 0L,
    val paidCount: Int = 0,
    val totalCount: Int = 0,
    val nextActions: List<NextActionItem> = emptyList(),
    val overdueActionCount: Int = 0,
    val dueDates: List<Int> = emptyList(),
    val scheduleGroups: List<ScheduleGroup> = emptyList(),
    val accountTotals: Map<String, Long> = emptyMap(),
    val isLoading: Boolean = true
)

data class NextActionItem(
    val cardId: Long,
    val cardName: String,
    val dueDate: Int,
    val scheduledDate: LocalDate,
    val amount: Long
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getMonthlyPaymentsUseCase: GetMonthlyPaymentsUseCase,
    getBudgetUseCase: GetBudgetUseCase,
    private val markAllPaidUseCase: MarkAllPaidUseCase,
    private val applyPreviousMonthTemplateUseCase: ApplyPreviousMonthTemplateUseCase,
    private val updateBudgetUseCase: UpdateBudgetUseCase,
    private val exportPaymentsUseCase: ExportPaymentsUseCase,
    private val exportBackupJsonUseCase: ExportBackupJsonUseCase,
    private val uploadBackupToDriveUseCase: UploadBackupToDriveUseCase,
    private val getMonthlyPaymentsOnceUseCase: GetMonthlyPaymentsOnceUseCase,
    private val resetMonthAmountsUseCase: ResetMonthAmountsUseCase,
    private val repository: PaymentRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    private val selectedMonth = MutableStateFlow(currentYearMonth())

    val uiState: StateFlow<HomeUiState> = selectedMonth
        .flatMapLatest { yearMonth ->
            combine(
                getMonthlyPaymentsUseCase(yearMonth.asStorageKey()),
                getBudgetUseCase(yearMonth.asStorageKey(), null)
            ) { cards, budget ->
                buildUiState(yearMonth, cards, budget)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun changeMonth(offset: Long) {
        selectedMonth.update { it.plusMonths(offset) }
    }

    fun resetSelectedMonth() {
        viewModelScope.launch {
            resetMonthAmountsUseCase(selectedMonth.value.asStorageKey())
        }
    }

    fun markAllPaid() {
        viewModelScope.launch {
            markAllPaidUseCase(selectedMonth.value.asStorageKey())
        }
    }

    suspend fun applyPreviousMonthTemplate(): Int {
        return applyPreviousMonthTemplateUseCase(selectedMonth.value.asStorageKey())
    }

    fun updateBudget(amount: Long) {
        viewModelScope.launch {
            updateBudgetUseCase(selectedMonth.value.asStorageKey(), null, amount)
        }
    }

    suspend fun getCurrentPayments(): List<CardWithPayment> =
        getMonthlyPaymentsOnceUseCase(selectedMonth.value.asStorageKey())

    suspend fun exportCurrentMonth(): File? =
        exportPaymentsUseCase(selectedMonth.value.asStorageKey())

    suspend fun buildBackupJson(): String =
        exportBackupJsonUseCase.buildJson()

    suspend fun saveBackupJson(uri: Uri, json: String): Boolean =
        exportBackupJsonUseCase.saveToDocument(uri, json)

    suspend fun uploadBackupToDrive(
        accessToken: String,
        folderId: String?,
        fileName: String,
        json: String
    ): Result<String> {
        return uploadBackupToDriveUseCase(
            UploadBackupToDriveUseCase.Request(
                accessToken = accessToken,
                folderId = folderId,
                fileName = fileName,
                jsonBody = json
            )
        )
    }

    fun rescheduleNotifications() {
        viewModelScope.launch {
            val day = repository.observeNotificationSetting().first()?.monthlyReminderDay ?: 1
            reminderScheduler.scheduleDailyChecks()
            reminderScheduler.scheduleMonthlyReminder(day)
        }
    }

    init {
        viewModelScope.launch {
            repository.initializeDefaultAccounts()
            repository.initializeDefaultCards()
        }
    }

    private fun buildUiState(yearMonth: YearMonth, cards: List<CardWithPayment>, budget: BudgetEntity?): HomeUiState {
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
        val budgetAmount = budget?.amount
        val budgetRemaining = budgetAmount?.let { it - subtotals.values.sum() }
        val today = LocalDate.now()
        val nextActions = cards
            .filter { !it.isPaid }
            .map { card ->
                val schedule = calculateBillingDate(yearMonth, card.dueDate).scheduledDate
                NextActionItem(
                    cardId = card.cardId,
                    cardName = card.cardName,
                    dueDate = card.dueDate,
                    scheduledDate = schedule,
                    amount = card.amount
                )
            }
            .sortedWith(
                compareBy<NextActionItem> { it.scheduledDate }
                    .thenByDescending { it.amount }
                    .thenBy { it.cardName }
            )
        val overdueActionCount = nextActions.count { it.scheduledDate.isBefore(today) }

        return HomeUiState(
            selectedYearMonth = yearMonth.asStorageKey(),
            monthLabel = yearMonth.asDisplayLabel(),
            cardsByDueDate = grouped,
            subtotalByDueDate = subtotals,
            totalAmount = subtotals.values.sum(),
            budgetAmount = budgetAmount,
            budgetRemaining = budgetRemaining,
            paidAmount = paidAmount,
            unpaidAmount = subtotals.values.sum() - paidAmount,
            paidCount = cards.count { it.isPaid },
            totalCount = cards.size,
            nextActions = nextActions.take(4),
            overdueActionCount = overdueActionCount,
            dueDates = grouped.keys.sorted(),
            scheduleGroups = scheduleGroups,
            accountTotals = accountTotals,
            isLoading = false
        )
    }
}
