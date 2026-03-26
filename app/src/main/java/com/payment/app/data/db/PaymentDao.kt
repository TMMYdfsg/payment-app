package com.payment.app.data.db

import androidx.room.*
import com.payment.app.data.model.PaymentHistoryItem
import com.payment.app.data.db.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE yearMonth = :yearMonth")
    fun getPaymentsByMonth(yearMonth: String): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE yearMonth = :yearMonth")
    suspend fun getPaymentsByMonthOnce(yearMonth: String): List<PaymentEntity>

    @Query("SELECT * FROM payments")
    suspend fun getAllPaymentsOnce(): List<PaymentEntity>

    @Query("SELECT * FROM payments WHERE cardId = :cardId AND yearMonth = :yearMonth LIMIT 1")
    suspend fun getPaymentByCardIdAndMonth(cardId: Long, yearMonth: String): PaymentEntity?

    @Query("SELECT * FROM payments WHERE paymentId = :paymentId LIMIT 1")
    suspend fun getPaymentById(paymentId: Long): PaymentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePayment(payment: PaymentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePayments(payments: List<PaymentEntity>)

    @Query("DELETE FROM payments WHERE cardId = :cardId AND yearMonth = :yearMonth")
    suspend fun deleteByCardAndMonth(cardId: Long, yearMonth: String)

    @Query(
        """
        SELECT
            p.paymentId AS paymentId,
            c.cardId AS cardId,
            c.cardName AS cardName,
            c.dueDate AS dueDate,
            c.category AS category,
            p.yearMonth AS yearMonth,
            p.amount AS amount,
            p.isPaid AS isPaid,
            p.accountId AS accountId,
            a.accountName AS accountName,
            p.completedAt AS completedAt
        FROM payments p
        INNER JOIN cards c ON c.cardId = p.cardId
        LEFT JOIN bank_accounts a ON a.accountId = p.accountId
        WHERE
            c.cardName LIKE '%' || :query || '%'
            OR c.category LIKE '%' || :query || '%'
            OR COALESCE(a.accountName, '') LIKE '%' || :query || '%'
            OR p.yearMonth LIKE '%' || :query || '%'
            OR CAST(p.amount AS TEXT) LIKE '%' || :query || '%'
        ORDER BY p.yearMonth DESC, c.dueDate ASC, c.cardId ASC
        LIMIT :limit
        """
    )
    suspend fun searchPaymentHistory(query: String, limit: Int = 200): List<PaymentHistoryItem>

    @Query(
        """
        UPDATE payments
        SET amount = 0,
            isPaid = 0,
            completedAt = NULL,
            updatedAt = :updatedAt
        WHERE yearMonth = :yearMonth
        """
    )
    suspend fun resetMonthAmounts(yearMonth: String, updatedAt: Long = System.currentTimeMillis())

    @Query(
        """
        UPDATE payments
        SET isPaid = 1,
            completedAt = :completedAt,
            updatedAt = :completedAt
        WHERE yearMonth = :yearMonth
        """
    )
    suspend fun markAllPaid(yearMonth: String, completedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM payments")
    suspend fun clearAll()
}
