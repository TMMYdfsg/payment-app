package com.payment.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.payment.app.data.db.entity.BankAccountEntity
import com.payment.app.data.db.entity.CardEntity
import com.payment.app.data.db.entity.PaymentEntity

@Database(
    entities = [CardEntity::class, PaymentEntity::class, BankAccountEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun paymentDao(): PaymentDao
    abstract fun accountDao(): AccountDao
}
