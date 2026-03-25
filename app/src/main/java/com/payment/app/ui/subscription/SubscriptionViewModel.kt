package com.payment.app.ui.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payment.app.data.db.entity.CardEntity
import com.payment.app.data.db.entity.InstallmentEntity
import com.payment.app.data.db.entity.SubscriptionEntity
import com.payment.app.data.repository.PaymentRepository
import com.payment.app.domain.usecase.GetInstallmentsUseCase
import com.payment.app.domain.usecase.GetSubscriptionsUseCase
import com.payment.app.domain.usecase.UpsertInstallmentUseCase
import com.payment.app.domain.usecase.UpsertSubscriptionUseCase
import com.payment.app.util.asStorageKey
import com.payment.app.util.currentYearMonth
import com.payment.app.util.parseYearMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class InstallmentStatus(
    val installment: InstallmentEntity,
    val remainingMonths: Int,
    val remainingAmount: Long
)

data class SubscriptionUiState(
    val cards: List<CardEntity> = emptyList(),
    val subscriptions: List<SubscriptionEntity> = emptyList(),
    val installments: List<InstallmentStatus> = emptyList(),
    val monthlySubscriptionTotal: Long = 0L,
    val isLoading: Boolean = true
)

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val repository: PaymentRepository,
    getSubscriptionsUseCase: GetSubscriptionsUseCase,
    getInstallmentsUseCase: GetInstallmentsUseCase,
    private val upsertSubscriptionUseCase: UpsertSubscriptionUseCase,
    private val upsertInstallmentUseCase: UpsertInstallmentUseCase
) : ViewModel() {

    val uiState: StateFlow<SubscriptionUiState> = combine(
        repository.allCards,
        getSubscriptionsUseCase(),
        getInstallmentsUseCase()
    ) { cards, subscriptions, installments ->
        val now = currentYearMonth()
        val statuses = installments.map { installment ->
            val start = parseYearMonth(installment.startYearMonth)
            val elapsed = ((now.year - start.year) * 12 + (now.monthValue - start.monthValue)).coerceAtLeast(0)
            val remainingMonths = (installment.totalMonths - elapsed).coerceAtLeast(0)
            val monthly = if (installment.totalMonths > 0) installment.totalAmount / installment.totalMonths else 0L
            InstallmentStatus(
                installment = installment,
                remainingMonths = remainingMonths,
                remainingAmount = (monthly * remainingMonths).coerceAtLeast(0L)
            )
        }
        SubscriptionUiState(
            cards = cards,
            subscriptions = subscriptions,
            installments = statuses,
            monthlySubscriptionTotal = subscriptions.sumOf { it.amount },
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SubscriptionUiState())

    fun addSubscription(cardId: Long, name: String, amount: Long, billingDay: Int) {
        if (cardId <= 0L || name.isBlank() || billingDay !in 1..31) return
        viewModelScope.launch {
            upsertSubscriptionUseCase(
                SubscriptionEntity(
                    cardId = cardId,
                    serviceName = name.trim(),
                    amount = amount.coerceAtLeast(0L),
                    billingDay = billingDay
                )
            )
        }
    }

    fun addInstallment(cardId: Long, totalAmount: Long, totalMonths: Int, startYearMonth: String) {
        if (cardId <= 0L || totalAmount <= 0L || totalMonths <= 0) return
        viewModelScope.launch {
            val normalized = parseYearMonth(startYearMonth).asStorageKey()
            val paymentId = repository.ensurePaymentRecord(cardId, normalized)
            if (paymentId <= 0L) return@launch
            upsertInstallmentUseCase(
                InstallmentEntity(
                    paymentId = paymentId,
                    totalAmount = totalAmount,
                    totalMonths = totalMonths,
                    startYearMonth = normalized
                )
            )
        }
    }
}
