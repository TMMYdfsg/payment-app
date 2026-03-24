package com.payment.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_accounts")
data class BankAccountEntity(
    @PrimaryKey(autoGenerate = true) val accountId: Long = 0,
    val accountName: String,
    val bankName: String = ""
)
