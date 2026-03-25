package com.payment.app.util

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

