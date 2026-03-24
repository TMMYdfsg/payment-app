package com.payment.app.data.db

import androidx.room.*
import com.payment.app.data.db.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE cardId = :cardId LIMIT 1")
    suspend fun getPaymentByCardId(cardId: Long): PaymentEntity?

    @Query("SELECT * FROM payments WHERE cardId = :cardId LIMIT 1")
    fun getPaymentByCardIdFlow(cardId: Long): Flow<PaymentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePayment(payment: PaymentEntity)

    @Query("UPDATE payments SET amount = :amount, updatedAt = :updatedAt WHERE cardId = :cardId")
    suspend fun updateAmount(cardId: Long, amount: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM payments WHERE cardId = :cardId")
    suspend fun deletePaymentByCardId(cardId: Long)

    @Query("UPDATE payments SET amount = 0")
    suspend fun resetAllAmounts()
}
