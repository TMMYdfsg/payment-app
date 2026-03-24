package com.payment.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.payment.app.data.db.entity.CardEntity
import com.payment.app.data.db.entity.PaymentEntity

@Database(
    entities = [CardEntity::class, PaymentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun paymentDao(): PaymentDao
}
