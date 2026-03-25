package com.payment.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "installments",
    foreignKeys = [
        ForeignKey(
            entity = PaymentEntity::class,
            parentColumns = ["paymentId"],
            childColumns = ["paymentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("paymentId")]
)
data class InstallmentEntity(
    @PrimaryKey(autoGenerate = true) val installmentId: Long = 0,
    val paymentId: Long,
    val totalAmount: Long,
    val totalMonths: Int,
    val startYearMonth: String
)
