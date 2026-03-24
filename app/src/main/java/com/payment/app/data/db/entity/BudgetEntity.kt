package com.payment.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "budgets",
    indices = [Index(value = ["yearMonth", "category"], unique = true)]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val budgetId: Long = 0,
    val yearMonth: String,
    val category: String?, // null または空で全体予算
    val amount: Long
)
