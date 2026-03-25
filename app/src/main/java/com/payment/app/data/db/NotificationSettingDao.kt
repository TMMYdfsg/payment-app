package com.payment.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.payment.app.data.db.entity.NotificationSettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationSettingDao {
    @Query("SELECT * FROM notification_settings LIMIT 1")
    fun getSettings(): Flow<NotificationSettingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(setting: NotificationSettingEntity): Long
}
