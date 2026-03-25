package com.payment.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.payment.app.data.db.entity.BankAccountEntity
import com.payment.app.data.db.entity.BudgetEntity
import com.payment.app.data.db.entity.CardEntity
import com.payment.app.data.db.entity.InstallmentEntity
import com.payment.app.data.db.entity.NotificationSettingEntity
import com.payment.app.data.db.entity.PaymentEntity
import com.payment.app.data.db.entity.SubscriptionEntity

@Database(
    entities = [
        CardEntity::class,
        PaymentEntity::class,
        BankAccountEntity::class,
        BudgetEntity::class,
        SubscriptionEntity::class,
        InstallmentEntity::class,
        NotificationSettingEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun paymentDao(): PaymentDao
    abstract fun accountDao(): AccountDao
    abstract fun budgetDao(): BudgetDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun installmentDao(): InstallmentDao
    abstract fun notificationSettingDao(): NotificationSettingDao
}
