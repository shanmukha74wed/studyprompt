package com.example.data

import kotlinx.coroutines.flow.Flow

class PromptRepository(private val promptDao: PromptDao) {
    val allPrompts: Flow<List<SavedPromptEntity>> = promptDao.getAllPrompts()
    val favoritePrompts: Flow<List<SavedPromptEntity>> = promptDao.getFavoritePrompts()

    suspend fun getPromptById(id: Long): SavedPromptEntity? {
        return promptDao.getPromptById(id)
    }

    suspend fun savePrompt(prompt: SavedPromptEntity): Long {
        return promptDao.insertPrompt(prompt)
    }

    suspend fun updatePrompt(prompt: SavedPromptEntity) {
        promptDao.updatePrompt(prompt)
    }

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        promptDao.updateFavoriteStatus(id, isFavorite)
    }

    suspend fun deletePrompt(id: Long) {
        promptDao.deletePromptById(id)
    }

    suspend fun clearAllPrompts() {
        promptDao.clearAllPrompts()
    }
}
