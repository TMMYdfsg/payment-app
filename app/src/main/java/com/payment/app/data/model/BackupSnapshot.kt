package com.payment.app.data.model

import com.payment.app.data.db.entity.BankAccountEntity
import com.payment.app.data.db.entity.BudgetEntity
import com.payment.app.data.db.entity.CardEntity
import com.payment.app.data.db.entity.InstallmentEntity
import com.payment.app.data.db.entity.NotificationSettingEntity
import com.payment.app.data.db.entity.PaymentEntity
import com.payment.app.data.db.entity.SubscriptionEntity

data class BackupSnapshot(
    val cards: List<CardEntity>,
    val payments: List<PaymentEntity>,
    val accounts: List<BankAccountEntity>,
    val budgets: List<BudgetEntity>,
    val subscriptions: List<SubscriptionEntity>,
    val installments: List<InstallmentEntity>,
    val notificationSetting: NotificationSettingEntity?
)

