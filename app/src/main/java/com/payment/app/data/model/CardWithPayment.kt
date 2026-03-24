package com.payment.app.data.model

data class CardWithPayment(
    val cardId: Long,
    val cardName: String,
    val dueDate: Int,
    val yearMonth: String,
    val amount: Long = 0,
    val isPaid: Boolean = false,
    val accountId: Long? = null,
    val accountName: String? = null,
    val completedAt: Long? = null
)
