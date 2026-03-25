package com.payment.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subscriptions",
    foreignKeys = [
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["cardId"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cardId")]
)
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true) val subscriptionId: Long = 0,
    val cardId: Long,
    val serviceName: String,
    val amount: Long,
    val billingDay: Int,
    val isActive: Boolean = true
)
