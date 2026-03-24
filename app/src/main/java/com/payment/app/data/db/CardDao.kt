package com.payment.app.data.db

import androidx.room.*
import com.payment.app.data.db.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM cards ORDER BY dueDate ASC, cardId ASC")
    fun getAllCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards ORDER BY dueDate ASC, cardId ASC")
    suspend fun getAllCardsOnce(): List<CardEntity>

    @Query("SELECT * FROM cards WHERE dueDate = :dueDate ORDER BY cardId ASC")
    fun getCardsByDueDate(dueDate: Int): Flow<List<CardEntity>>

    @Query("SELECT DISTINCT dueDate FROM cards ORDER BY dueDate ASC")
    fun getDistinctDueDates(): Flow<List<Int>>

    @Insert
    suspend fun insertCard(card: CardEntity): Long

    @Insert
    suspend fun insertCards(cards: List<CardEntity>)

    @Delete
    suspend fun deleteCard(card: CardEntity)

    @Query("SELECT COUNT(*) FROM cards")
    suspend fun getCardCount(): Int
}
