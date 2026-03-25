package com.payment.app.data.db

import androidx.room.*
import com.payment.app.data.db.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE yearMonth = :yearMonth")
    fun getPaymentsByMonth(yearMonth: String): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE yearMonth = :yearMonth")
    suspend fun getPaymentsByMonthOnce(yearMonth: String): List<PaymentEntity>

    @Query("SELECT * FROM payments WHERE cardId = :cardId AND yearMonth = :yearMonth LIMIT 1")
    suspend fun getPaymentByCardIdAndMonth(cardId: Long, yearMonth: String): PaymentEntity?

    @Query("SELECT * FROM payments WHERE paymentId = :paymentId LIMIT 1")
    suspend fun getPaymentById(paymentId: Long): PaymentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePayment(payment: PaymentEntity)

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
}
