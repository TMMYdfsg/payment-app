package com.payment.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_settings")
data class NotificationSettingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reminderLeadDays: Int = 3,
    val budgetAlertThreshold: Int = 80,
    val monthlyReminderDay: Int = 1,
    val enabled: Boolean = false
)
