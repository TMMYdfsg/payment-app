package com.payment.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val cardId: Long = 0,
    val cardName: String,
    val dueDate: Int,
    val category: String = ""
)
