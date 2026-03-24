package com.payment.app.di

import android.content.Context
import androidx.room.migration.Migration
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.payment.app.data.db.AccountDao
import com.payment.app.data.db.AppDatabase
import com.payment.app.data.db.CardDao
import com.payment.app.data.db.PaymentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS bank_accounts (
                    accountId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    accountName TEXT NOT NULL,
                    bankName TEXT NOT NULL DEFAULT ''
                )
                """.trimIndent()
            )
            database.execSQL(
                """
                INSERT INTO bank_accounts (accountName, bankName)
                VALUES ('メイン口座', 'メインバンク')
                """.trimIndent()
            )
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS payments_new (
                    paymentId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    cardId INTEGER NOT NULL,
                    yearMonth TEXT NOT NULL,
                    amount INTEGER NOT NULL,
                    isPaid INTEGER NOT NULL DEFAULT 0,
                    accountId INTEGER,
                    completedAt INTEGER,
                    updatedAt INTEGER NOT NULL,
                    FOREIGN KEY(cardId) REFERENCES cards(cardId) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(accountId) REFERENCES bank_accounts(accountId) ON UPDATE NO ACTION ON DELETE SET NULL
                )
                """.trimIndent()
            )
            database.execSQL(
                """
                INSERT INTO payments_new (paymentId, cardId, yearMonth, amount, isPaid, accountId, completedAt, updatedAt)
                SELECT paymentId, cardId, strftime('%Y-%m', 'now', 'localtime'), amount, 0, 1, NULL, updatedAt
                FROM payments
                """.trimIndent()
            )
            database.execSQL("DROP TABLE payments")
            database.execSQL("ALTER TABLE payments_new RENAME TO payments")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_payments_cardId ON payments(cardId)")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_payments_yearMonth ON payments(yearMonth)")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_payments_accountId ON payments(accountId)")
            database.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS index_payments_cardId_yearMonth ON payments(cardId, yearMonth)"
            )
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "payment_db")
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    @Singleton
    fun provideCardDao(db: AppDatabase): CardDao = db.cardDao()

    @Provides
    @Singleton
    fun providePaymentDao(db: AppDatabase): PaymentDao = db.paymentDao()

    @Provides
    @Singleton
    fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()
}
