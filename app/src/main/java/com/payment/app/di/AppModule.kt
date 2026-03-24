package com.payment.app.di

import android.content.Context
import androidx.room.Room
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

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "payment_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideCardDao(db: AppDatabase): CardDao = db.cardDao()

    @Provides
    @Singleton
    fun providePaymentDao(db: AppDatabase): PaymentDao = db.paymentDao()
}
