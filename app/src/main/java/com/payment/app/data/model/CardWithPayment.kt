package com.payment.app.data.model

data class CardWithPayment(
    val cardId: Long,
    val cardName: String,
    val dueDate: Int,
    val amount: Long = 0
)
