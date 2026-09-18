package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PromptDao {
    @Query("SELECT * FROM saved_prompts ORDER BY createdAt DESC")
    fun getAllPrompts(): Flow<List<SavedPromptEntity>>

    @Query("SELECT * FROM saved_prompts WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoritePrompts(): Flow<List<SavedPromptEntity>>

    @Query("SELECT * FROM saved_prompts WHERE id = :id LIMIT 1")
    suspend fun getPromptById(id: Long): SavedPromptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompt(prompt: SavedPromptEntity): Long

    @Update
    suspend fun updatePrompt(prompt: SavedPromptEntity)

    @Query("UPDATE saved_prompts SET isFavorite = :isFavorite, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM saved_prompts WHERE id = :id")
    suspend fun deletePromptById(id: Long)

    @Query("DELETE FROM saved_prompts")
    suspend fun clearAllPrompts()
}
