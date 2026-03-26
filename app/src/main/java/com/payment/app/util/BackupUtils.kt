package com.payment.app.util

import com.payment.app.data.db.entity.BankAccountEntity
import com.payment.app.data.db.entity.BudgetEntity
import com.payment.app.data.db.entity.CardEntity
import com.payment.app.data.db.entity.InstallmentEntity
import com.payment.app.data.db.entity.NotificationSettingEntity
import com.payment.app.data.db.entity.PaymentEntity
import com.payment.app.data.db.entity.SubscriptionEntity
import com.payment.app.data.model.BackupSnapshot
import org.json.JSONArray
import org.json.JSONObject

fun backupSnapshotToJson(snapshot: BackupSnapshot): String {
    val root = JSONObject()
    root.put("schemaVersion", 1)
    root.put("exportedAt", System.currentTimeMillis())

    root.put("cards", JSONArray().apply {
        snapshot.cards.forEach { card ->
            put(
                JSONObject()
                    .put("cardId", card.cardId)
                    .put("cardName", card.cardName)
                    .put("dueDate", card.dueDate)
                    .put("category", card.category)
                    .put("rewardRate", card.rewardRate)
                    .put("annualFee", card.annualFee)
            )
        }
    })

    root.put("payments", JSONArray().apply {
        snapshot.payments.forEach { payment ->
            put(
                JSONObject()
                    .put("paymentId", payment.paymentId)
                    .put("cardId", payment.cardId)
                    .put("yearMonth", payment.yearMonth)
                    .put("amount", payment.amount)
                    .put("isPaid", payment.isPaid)
                    .put("accountId", payment.accountId)
                    .put("completedAt", payment.completedAt)
                    .put("updatedAt", payment.updatedAt)
            )
        }
    })

    root.put("accounts", JSONArray().apply {
        snapshot.accounts.forEach { account ->
            put(
                JSONObject()
                    .put("accountId", account.accountId)
                    .put("accountName", account.accountName)
                    .put("bankName", account.bankName)
            )
        }
    })

    root.put("budgets", JSONArray().apply {
        snapshot.budgets.forEach { budget ->
            put(
                JSONObject()
                    .put("budgetId", budget.budgetId)
                    .put("yearMonth", budget.yearMonth)
                    .put("category", budget.category)
                    .put("amount", budget.amount)
            )
        }
    })

    root.put("subscriptions", JSONArray().apply {
        snapshot.subscriptions.forEach { sub ->
            put(
                JSONObject()
                    .put("subscriptionId", sub.subscriptionId)
                    .put("cardId", sub.cardId)
                    .put("serviceName", sub.serviceName)
                    .put("amount", sub.amount)
                    .put("billingDay", sub.billingDay)
                    .put("isActive", sub.isActive)
            )
        }
    })

    root.put("installments", JSONArray().apply {
        snapshot.installments.forEach { installment ->
            put(
                JSONObject()
                    .put("installmentId", installment.installmentId)
                    .put("paymentId", installment.paymentId)
                    .put("totalAmount", installment.totalAmount)
                    .put("totalMonths", installment.totalMonths)
                    .put("startYearMonth", installment.startYearMonth)
            )
        }
    })

    root.put(
        "notificationSetting",
        snapshot.notificationSetting?.let { setting ->
            JSONObject()
                .put("id", setting.id)
                .put("enabled", setting.enabled)
                .put("reminderLeadDays", setting.reminderLeadDays)
                .put("budgetAlertThreshold", setting.budgetAlertThreshold)
                .put("monthlyReminderDay", setting.monthlyReminderDay)
        } ?: JSONObject.NULL
    )

    return root.toString(2)
}

fun backupSnapshotFromJson(json: String): BackupSnapshot {
    val root = JSONObject(json)
    val cards = root.optJSONArray("cards").toCardList()
    val payments = root.optJSONArray("payments").toPaymentList()
    val accounts = root.optJSONArray("accounts").toAccountList()
    val budgets = root.optJSONArray("budgets").toBudgetList()
    val subscriptions = root.optJSONArray("subscriptions").toSubscriptionList()
    val installments = root.optJSONArray("installments").toInstallmentList()
    val notification = root.optJSONObject("notificationSetting")?.toNotificationSetting()
    return BackupSnapshot(
        cards = cards,
        payments = payments,
        accounts = accounts,
        budgets = budgets,
        subscriptions = subscriptions,
        installments = installments,
        notificationSetting = notification
    )
}

private fun JSONArray?.toCardList(): List<CardEntity> = buildList {
    if (this@toCardList == null) return@buildList
    for (i in 0 until length()) {
        val item = optJSONObject(i) ?: continue
        add(
            CardEntity(
                cardId = item.optLong("cardId", 0L),
                cardName = item.optString("cardName"),
                dueDate = item.optInt("dueDate"),
                category = item.optString("category"),
                rewardRate = item.optDouble("rewardRate", 1.0).toFloat(),
                annualFee = item.optLong("annualFee", 0L)
            )
        )
    }
}

private fun JSONArray?.toPaymentList(): List<PaymentEntity> = buildList {
    if (this@toPaymentList == null) return@buildList
    for (i in 0 until length()) {
        val item = optJSONObject(i) ?: continue
        add(
            PaymentEntity(
                paymentId = item.optLong("paymentId", 0L),
                cardId = item.optLong("cardId"),
                yearMonth = item.optString("yearMonth"),
                amount = item.optLong("amount", 0L),
                isPaid = item.optBoolean("isPaid", false),
                accountId = item.optLong("accountId").takeIf { it > 0L },
                completedAt = item.optLong("completedAt").takeIf { it > 0L },
                updatedAt = item.optLong("updatedAt", System.currentTimeMillis())
            )
        )
    }
}

private fun JSONArray?.toAccountList(): List<BankAccountEntity> = buildList {
    if (this@toAccountList == null) return@buildList
    for (i in 0 until length()) {
        val item = optJSONObject(i) ?: continue
        add(
            BankAccountEntity(
                accountId = item.optLong("accountId", 0L),
                accountName = item.optString("accountName"),
                bankName = item.optString("bankName")
            )
        )
    }
}

private fun JSONArray?.toBudgetList(): List<BudgetEntity> = buildList {
    if (this@toBudgetList == null) return@buildList
    for (i in 0 until length()) {
        val item = optJSONObject(i) ?: continue
        add(
            BudgetEntity(
                budgetId = item.optLong("budgetId", 0L),
                yearMonth = item.optString("yearMonth"),
                category = item.optString("category").takeIf { it.isNotBlank() },
                amount = item.optLong("amount", 0L)
            )
        )
    }
}

private fun JSONArray?.toSubscriptionList(): List<SubscriptionEntity> = buildList {
    if (this@toSubscriptionList == null) return@buildList
    for (i in 0 until length()) {
        val item = optJSONObject(i) ?: continue
        add(
            SubscriptionEntity(
                subscriptionId = item.optLong("subscriptionId", 0L),
                cardId = item.optLong("cardId"),
                serviceName = item.optString("serviceName"),
                amount = item.optLong("amount", 0L),
                billingDay = item.optInt("billingDay", 1),
                isActive = item.optBoolean("isActive", true)
            )
        )
    }
}

private fun JSONArray?.toInstallmentList(): List<InstallmentEntity> = buildList {
    if (this@toInstallmentList == null) return@buildList
    for (i in 0 until length()) {
        val item = optJSONObject(i) ?: continue
        add(
            InstallmentEntity(
                installmentId = item.optLong("installmentId", 0L),
                paymentId = item.optLong("paymentId"),
                totalAmount = item.optLong("totalAmount", 0L),
                totalMonths = item.optInt("totalMonths", 1),
                startYearMonth = item.optString("startYearMonth")
            )
        )
    }
}

private fun JSONObject.toNotificationSetting(): NotificationSettingEntity {
    return NotificationSettingEntity(
        id = optLong("id", 0L),
        enabled = optBoolean("enabled", false),
        reminderLeadDays = optInt("reminderLeadDays", 3),
        budgetAlertThreshold = optInt("budgetAlertThreshold", 80),
        monthlyReminderDay = optInt("monthlyReminderDay", 1)
    )
}
