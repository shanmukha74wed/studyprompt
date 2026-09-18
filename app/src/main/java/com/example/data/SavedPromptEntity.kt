package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_prompts")
data class SavedPromptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String,
    val topic: String,
    val academicLevel: String,
    val studyGoal: String,
    val currentKnowledge: String,
    val availableTime: String,
    val learningStyles: String, // Comma separated
    val examOrUniversity: String = "",
    val additionalInstructions: String = "",
    val language: String = "English",
    val generatedPrompt: String,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
