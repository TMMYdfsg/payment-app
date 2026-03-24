package com.payment.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["cardId"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BankAccountEntity::class,
            parentColumns = ["accountId"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("cardId"),
        Index("yearMonth"),
        Index("accountId"),
        Index(value = ["cardId", "yearMonth"], unique = true)
    ]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val paymentId: Long = 0,
    val cardId: Long,
    val yearMonth: String,
    val amount: Long = 0,
    val isPaid: Boolean = false,
    val accountId: Long? = null,
    val completedAt: Long? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
