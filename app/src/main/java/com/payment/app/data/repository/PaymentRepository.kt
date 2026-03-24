package com.payment.app.data.repository

import com.payment.app.data.db.CardDao
import com.payment.app.data.db.PaymentDao
import com.payment.app.data.db.entity.CardEntity
import com.payment.app.data.db.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(
    private val cardDao: CardDao,
    private val paymentDao: PaymentDao
) {
    val allCards: Flow<List<CardEntity>> = cardDao.getAllCards()
    val allPayments: Flow<List<PaymentEntity>> = paymentDao.getAllPayments()
    val distinctDueDates: Flow<List<Int>> = cardDao.getDistinctDueDates()

    fun getCardsByDueDate(dueDate: Int): Flow<List<CardEntity>> =
        cardDao.getCardsByDueDate(dueDate)

    suspend fun addCard(cardName: String, dueDate: Int): Long =
        cardDao.insertCard(CardEntity(cardName = cardName, dueDate = dueDate))

    suspend fun deleteCard(card: CardEntity) {
        cardDao.deleteCard(card)
    }

    suspend fun upsertPayment(cardId: Long, amount: Long) {
        val existing = paymentDao.getPaymentByCardId(cardId)
        if (existing != null) {
            paymentDao.updateAmount(cardId, amount)
        } else {
            paymentDao.insertOrUpdatePayment(
                PaymentEntity(cardId = cardId, amount = amount)
            )
        }
    }

    suspend fun resetAllAmounts() {
        paymentDao.resetAllAmounts()
    }

    suspend fun initializeDefaultCards() {
        if (cardDao.getCardCount() == 0) {
            val defaults = listOf(
                CardEntity(cardName = "PayPay", dueDate = 26),
                CardEntity(cardName = "NL", dueDate = 26),
                CardEntity(cardName = "Amazon", dueDate = 26),
                CardEntity(cardName = "楽天", dueDate = 27),
                CardEntity(cardName = "EPOS", dueDate = 27),
                CardEntity(cardName = "メルカード", dueDate = 31)
            )
            cardDao.insertCards(defaults)
        }
    }

    suspend fun getAllCardsOnce(): List<CardEntity> = cardDao.getAllCardsOnce()
}
