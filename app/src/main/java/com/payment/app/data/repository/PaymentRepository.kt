package com.payment.app.data.repository

import com.payment.app.data.db.AccountDao
import com.payment.app.data.db.BudgetDao
import com.payment.app.data.db.CardDao
import com.payment.app.data.db.PaymentDao
import com.payment.app.data.db.InstallmentDao
import com.payment.app.data.db.NotificationSettingDao
import com.payment.app.data.db.SubscriptionDao
import com.payment.app.data.db.entity.BankAccountEntity
import com.payment.app.data.db.entity.BudgetEntity
import com.payment.app.data.db.entity.CardEntity
import com.payment.app.data.db.entity.InstallmentEntity
import com.payment.app.data.db.entity.NotificationSettingEntity
import com.payment.app.data.db.entity.PaymentEntity
import com.payment.app.data.db.entity.SubscriptionEntity
import com.payment.app.data.model.CardWithPayment
import com.payment.app.data.model.PaymentHistoryItem
import com.payment.app.data.model.BackupSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(
    private val cardDao: CardDao,
    private val paymentDao: PaymentDao,
    private val accountDao: AccountDao,
    private val budgetDao: BudgetDao,
    private val subscriptionDao: SubscriptionDao,
    private val installmentDao: InstallmentDao,
    private val notificationSettingDao: NotificationSettingDao
) {
    val allCards: Flow<List<CardEntity>> = cardDao.getAllCards()
    val allAccounts: Flow<List<BankAccountEntity>> = accountDao.getAllAccounts()
    val distinctDueDates: Flow<List<Int>> = cardDao.getDistinctDueDates()

    fun getCardsByDueDate(dueDate: Int): Flow<List<CardEntity>> =
        cardDao.getCardsByDueDate(dueDate)

    fun observeCardPayments(yearMonth: String): Flow<List<CardWithPayment>> = combine(
        allCards,
        paymentDao.getPaymentsByMonth(yearMonth),
        allAccounts
    ) { cards, payments, accounts ->
        mergeCardsWithPayments(cards, payments, accounts, yearMonth)
    }

    fun observeBudget(yearMonth: String, category: String?): Flow<BudgetEntity?> =
        budgetDao.getBudget(yearMonth, category?.takeUnless { it.isBlank() })

    fun observeBudgets(yearMonth: String): Flow<List<BudgetEntity>> =
        budgetDao.getBudgetsByMonth(yearMonth)

    fun observeSubscriptions(): Flow<List<SubscriptionEntity>> = subscriptionDao.getActiveSubscriptions()
    fun observeInstallments(): Flow<List<InstallmentEntity>> = installmentDao.getAllInstallments()
    fun observeNotificationSetting(): Flow<NotificationSettingEntity?> = notificationSettingDao.getSettings()

    suspend fun addCard(cardName: String, dueDate: Int, category: String): Long =
        cardDao.insertCard(CardEntity(cardName = cardName, dueDate = dueDate, category = category))

    suspend fun deleteCard(card: CardEntity) {
        cardDao.deleteCard(card)
    }

    suspend fun addAccount(accountName: String, bankName: String): Long =
        accountDao.insertAccount(
            BankAccountEntity(
                accountName = accountName.trim(),
                bankName = bankName.trim()
            )
        )

    suspend fun deleteAccount(account: BankAccountEntity) {
        accountDao.deleteAccount(account)
    }

    suspend fun upsertPayment(cardId: Long, yearMonth: String, amount: Long) {
        upsertMonthlyPayment(cardId, yearMonth) { existing, defaultAccountId ->
            (existing ?: PaymentEntity(cardId = cardId, yearMonth = yearMonth, accountId = defaultAccountId)).copy(
                paymentId = existing?.paymentId ?: 0,
                cardId = cardId,
                yearMonth = yearMonth,
                amount = amount,
                isPaid = if (amount == 0L) false else existing?.isPaid ?: false,
                accountId = existing?.accountId ?: defaultAccountId,
                completedAt = if (amount == 0L) null else existing?.completedAt,
                updatedAt = System.currentTimeMillis()
            )
        }
    }

    suspend fun updatePaymentPaid(cardId: Long, yearMonth: String, isPaid: Boolean) {
        upsertMonthlyPayment(cardId, yearMonth) { existing, defaultAccountId ->
            (existing ?: PaymentEntity(cardId = cardId, yearMonth = yearMonth, accountId = defaultAccountId)).copy(
                paymentId = existing?.paymentId ?: 0,
                cardId = cardId,
                yearMonth = yearMonth,
                amount = existing?.amount ?: 0L,
                isPaid = isPaid,
                accountId = existing?.accountId ?: defaultAccountId,
                completedAt = if (isPaid) System.currentTimeMillis() else null,
                updatedAt = System.currentTimeMillis()
            )
        }
    }

    suspend fun updatePaymentAccount(cardId: Long, yearMonth: String, accountId: Long?) {
        upsertMonthlyPayment(cardId, yearMonth) { existing, defaultAccountId ->
            (existing ?: PaymentEntity(cardId = cardId, yearMonth = yearMonth, accountId = defaultAccountId)).copy(
                paymentId = existing?.paymentId ?: 0,
                cardId = cardId,
                yearMonth = yearMonth,
                amount = existing?.amount ?: 0L,
                isPaid = existing?.isPaid ?: false,
                accountId = accountId ?: defaultAccountId,
                completedAt = existing?.completedAt,
                updatedAt = System.currentTimeMillis()
            )
        }
    }

    suspend fun deletePayment(cardId: Long, yearMonth: String) {
        paymentDao.deleteByCardAndMonth(cardId, yearMonth)
    }

    suspend fun resetMonthAmounts(yearMonth: String) {
        paymentDao.resetMonthAmounts(yearMonth)
    }

    suspend fun markAllPaid(yearMonth: String) {
        paymentDao.markAllPaid(yearMonth)
    }

    suspend fun initializeDefaultCards() {
        if (cardDao.getCardCount() == 0) {
            val defaults = listOf(
                CardEntity(cardName = "PayPay", dueDate = 26, rewardRate = 1.0f),
                CardEntity(cardName = "NL", dueDate = 26, rewardRate = 1.0f),
                CardEntity(cardName = "Amazon", dueDate = 26, rewardRate = 1.0f),
                CardEntity(cardName = "楽天", dueDate = 27, rewardRate = 1.0f),
                CardEntity(cardName = "EPOS", dueDate = 27, rewardRate = 1.0f),
                CardEntity(cardName = "メルカード", dueDate = 31, rewardRate = 1.0f)
            )
            cardDao.insertCards(defaults)
        }
    }

    suspend fun initializeDefaultAccounts() {
        if (accountDao.getAccountCount() == 0) {
            accountDao.insertAccount(BankAccountEntity(accountName = "メイン口座", bankName = "メインバンク"))
        }
    }

    suspend fun getAllCardsOnce(): List<CardEntity> = cardDao.getAllCardsOnce()

    suspend fun getAllAccountsOnce(): List<BankAccountEntity> = accountDao.getAllAccountsOnce()

    suspend fun getCardPaymentsOnce(yearMonth: String): List<CardWithPayment> {
        val cards = cardDao.getAllCardsOnce()
        val payments = paymentDao.getPaymentsByMonthOnce(yearMonth)
        val accounts = accountDao.getAllAccountsOnce()
        return mergeCardsWithPayments(cards, payments, accounts, yearMonth)
    }

    suspend fun upsertBudget(yearMonth: String, category: String?, amount: Long) {
        budgetDao.upsertBudget(
            BudgetEntity(
                yearMonth = yearMonth,
                category = category?.takeUnless { it.isBlank() },
                amount = amount
            )
        )
    }

    suspend fun upsertSubscription(entity: SubscriptionEntity) = subscriptionDao.upsert(entity)
    suspend fun upsertInstallment(entity: InstallmentEntity) = installmentDao.upsert(entity)
    suspend fun upsertNotificationSetting(setting: NotificationSettingEntity) {
        val targetId = setting.id.takeIf { it > 0L } ?: 1L
        val savedId = notificationSettingDao.upsert(setting.copy(id = targetId))
        val keepId = if (savedId > 0L) savedId else targetId
        notificationSettingDao.deleteOthers(keepId)
    }

    suspend fun ensurePaymentRecord(cardId: Long, yearMonth: String): Long {
        val existing = paymentDao.getPaymentByCardIdAndMonth(cardId, yearMonth)
        if (existing != null) return existing.paymentId
        val defaultAccountId = accountDao.getFirstAccountId()
        paymentDao.insertOrUpdatePayment(
            PaymentEntity(
                cardId = cardId,
                yearMonth = yearMonth,
                amount = 0L,
                isPaid = false,
                accountId = defaultAccountId,
                completedAt = null
            )
        )
        return paymentDao.getPaymentByCardIdAndMonth(cardId, yearMonth)?.paymentId ?: 0L
    }

    suspend fun getPaymentById(paymentId: Long): PaymentEntity? = paymentDao.getPaymentById(paymentId)

    suspend fun searchPaymentHistory(query: String): List<PaymentHistoryItem> =
        if (query.isBlank()) emptyList() else paymentDao.searchPaymentHistory(query.trim())

    suspend fun getBackupSnapshot(): BackupSnapshot {
        return BackupSnapshot(
            cards = cardDao.getAllCardsOnce(),
            payments = paymentDao.getAllPaymentsOnce(),
            accounts = accountDao.getAllAccountsOnce(),
            budgets = budgetDao.getAllBudgetsOnce(),
            subscriptions = subscriptionDao.getAllSubscriptionsOnce(),
            installments = installmentDao.getAllInstallmentsOnce(),
            notificationSetting = notificationSettingDao.getSettingsOnce()
        )
    }

    private suspend fun upsertMonthlyPayment(
        cardId: Long,
        yearMonth: String,
        builder: (PaymentEntity?, Long?) -> PaymentEntity
    ) {
        val existing = paymentDao.getPaymentByCardIdAndMonth(cardId, yearMonth)
        val defaultAccountId = existing?.accountId ?: accountDao.getFirstAccountId()
        paymentDao.insertOrUpdatePayment(builder(existing, defaultAccountId))
    }

    private fun mergeCardsWithPayments(
        cards: List<CardEntity>,
        payments: List<PaymentEntity>,
        accounts: List<BankAccountEntity>,
        yearMonth: String
    ): List<CardWithPayment> {
        val paymentMap = payments.associateBy { it.cardId }
        val accountMap = accounts.associateBy { it.accountId }
        return cards
            .sortedWith(compareBy({ it.dueDate }, { it.cardId }))
            .map { card ->
                val payment = paymentMap[card.cardId]
                CardWithPayment(
                    cardId = card.cardId,
                    cardName = card.cardName,
                    dueDate = card.dueDate,
                    category = card.category,
                    yearMonth = yearMonth,
                    amount = payment?.amount ?: 0L,
                    isPaid = payment?.isPaid ?: false,
                    accountId = payment?.accountId,
                    accountName = payment?.accountId?.let(accountMap::get)?.accountName,
                    completedAt = payment?.completedAt
                )
            }
    }
}
