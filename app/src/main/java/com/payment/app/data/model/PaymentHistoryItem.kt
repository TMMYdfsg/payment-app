package com.payment.app.data.model

data class PaymentHistoryItem(
    val paymentId: Long,
    val cardId: Long,
    val cardName: String,
    val dueDate: Int,
    val category: String,
    val yearMonth: String,
    val amount: Long,
    val isPaid: Boolean,
    val accountId: Long?,
    val accountName: String?,
    val completedAt: Long?
)

